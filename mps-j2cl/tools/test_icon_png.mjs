import assert from 'node:assert/strict';
import fs from 'node:fs';
import path from 'node:path';
import http from 'node:http';
import crypto from 'node:crypto';
import {inflateSync} from 'node:zlib';
import {createRequire} from 'node:module';

const root = path.resolve(import.meta.dirname, '..');
const builds = ['icon_creation_browser_dev', 'icon_creation_browser'];
const expectedResult = 'IconCreationUtil: synchronous 16x16 PNG output and drawing error propagation passed; fixture pixels require independent decoder validation';
const crcTable = Array.from({length: 256}, (_, byte) => {
  let value = byte;
  for (let bit = 0; bit < 8; bit++) value = value & 1 ? 0xedb88320 ^ (value >>> 1) : value >>> 1;
  return value >>> 0;
});

function crc32(bytes) {
  let value = 0xffffffff;
  for (const byte of bytes) value = crcTable[(value ^ byte) & 255] ^ (value >>> 8);
  return (value ^ 0xffffffff) >>> 0;
}

function paeth(left, above, upperLeft) {
  const estimate = left + above - upperLeft;
  const a = Math.abs(estimate - left);
  const b = Math.abs(estimate - above);
  const c = Math.abs(estimate - upperLeft);
  return a <= b && a <= c ? left : b <= c ? above : upperLeft;
}

/** Independent PNG parser: standard Node zlib verifies the DEFLATE stream and Adler32. */
function verifyPng(png, empty) {
  assert.ok(png.length >= 8, 'PNG signature is present');
  assert.deepEqual(png.subarray(0, 8), Buffer.from([137, 80, 78, 71, 13, 10, 26, 10]), 'PNG signature');
  const compressed = [];
  let offset = 8;
  let width;
  let height;
  let stage = 0;
  let chunks = 0;
  while (offset < png.length) {
    assert.ok(offset + 12 <= png.length, 'Complete chunk header and CRC');
    const length = png.readUInt32BE(offset);
    assert.ok(length <= png.length - offset - 12, 'Chunk length fits input');
    const type = png.toString('ascii', offset + 4, offset + 8);
    const data = png.subarray(offset + 8, offset + 8 + length);
    assert.equal(crc32(png.subarray(offset + 4, offset + 8 + length)), png.readUInt32BE(offset + 8 + length), type + ' CRC32');
    chunks++;
    if (type === 'IHDR') {
      assert.equal(stage, 0, 'IHDR occurs first and once');
      assert.equal(length, 13, 'IHDR length');
      width = data.readUInt32BE(0);
      height = data.readUInt32BE(4);
      assert.equal(width, 16, 'Icon width');
      assert.equal(height, 16, 'Icon height');
      assert.deepEqual([...data.subarray(8)], [8, 6, 0, 0, 0], 'RGBA8, DEFLATE, standard filter, no interlace');
      stage = 1;
    } else if (type === 'IDAT') {
      assert.ok(stage === 1 || stage === 2, 'Contiguous IDAT chunks follow IHDR');
      compressed.push(data);
      stage = 2;
    } else if (type === 'IEND') {
      assert.equal(stage, 2, 'IEND follows image data');
      assert.equal(length, 0, 'IEND has no payload');
      assert.equal(offset + 12, png.length, 'No trailing bytes after IEND');
      stage = 3;
    } else {
      throw new Error('Unexpected chunk in this icon profile: ' + type);
    }
    offset += length + 12;
  }
  assert.equal(stage, 3, 'Complete PNG image');
  const stride = width * 4;
  const expectedLength = height * (stride + 1);
  const scanlines = inflateSync(Buffer.concat(compressed), {maxOutputLength: expectedLength});
  assert.equal(scanlines.length, expectedLength, 'Decompressed pixel count');
  const pixels = Buffer.alloc(width * height * 4);
  for (let y = 0; y < height; y++) {
    const input = y * (stride + 1);
    const filter = scanlines[input];
    assert.ok(filter >= 0 && filter <= 4, 'Standard PNG filter');
    for (let byte = 0; byte < stride; byte++) {
      const index = y * stride + byte;
      const left = byte >= 4 ? pixels[index - 4] : 0;
      const above = y > 0 ? pixels[index - stride] : 0;
      const upperLeft = y > 0 && byte >= 4 ? pixels[index - stride - 4] : 0;
      const predictor = filter === 0 ? 0 : filter === 1 ? left : filter === 2 ? above
        : filter === 3 ? Math.floor((left + above) / 2) : paeth(left, above, upperLeft);
      pixels[index] = (scanlines[input + 1 + byte] + predictor) & 255;
    }
  }
  for (let y = 0; y < height; y++) {
    for (let x = 0; x < width; x++) {
      const alpha = empty ? 0 : ((x + y) % 4) * 85;
      const expected = alpha === 0 ? [0, 0, 0, 0] : [x * 17, y * 17, (x ^ y) * 17, alpha];
      const offset = (y * width + x) * 4;
      assert.deepEqual([...pixels.subarray(offset, offset + 4)], expected, 'RGBA pixel ' + x + ',' + y);
    }
  }
  return {width, height, chunks, pixels: width * height, bytes: png.length,
    sha256: crypto.createHash('sha256').update(png).digest('hex'),
    rgbaSha256: crypto.createHash('sha256').update(pixels).digest('hex')};
}

assert.equal(crc32(Buffer.from('123456789')), 0xcbf43926, 'Independent standard CRC32 check vector');
if (process.argv[2] === '--verify-files') {
  assert.equal(process.argv.length, 5, 'Usage: node tools/test_icon_png.mjs --verify-files gradient.png empty.png');
  console.log(JSON.stringify({gradient: verifyPng(fs.readFileSync(process.argv[3]), false),
    empty: verifyPng(fs.readFileSync(process.argv[4]), true)}, null, 2));
} else {
  const selectedEngine = process.argv[2];
  if (selectedEngine && !['chromium', 'firefox', 'webkit'].includes(selectedEngine)) throw new Error('Unknown browser: ' + selectedEngine);
  const siblingRequire = createRequire(path.join(root, '../org-jdom-j2cl/package.json'));
  const {chromium, firefox, webkit} = siblingRequire('playwright');
  const assets = new Map(builds.map(build => {
    const file = path.join(root, 'bazel-bin', build + '.js');
    if (!fs.existsSync(file)) throw new Error('Missing built JavaScript: ' + file);
    return ['/' + build + '.js', fs.readFileSync(file)];
  }));
  const server = http.createServer((request, response) => {
    const script = assets.get(request.url);
    response.setHeader('Content-Type', script ? 'application/javascript' : 'text/html');
    response.end(script ?? '<!doctype html><meta charset="utf-8"><title>MPS icon PNG verification</title>');
  });
  await new Promise(resolve => server.listen(0, '127.0.0.1', resolve));
  const results = [];
  try {
    for (const [engine, type] of Object.entries({chromium, firefox, webkit})) {
      if (selectedEngine && selectedEngine !== engine) continue;
      let browser;
      try {
        browser = await type.launch({headless: true});
        for (const build of builds) {
          const started = Date.now();
          const result = {engine, version: browser.version(), build, passed: false,
            sha256: crypto.createHash('sha256').update(assets.get('/' + build + '.js')).digest('hex'), pageErrors: []};
          const page = await browser.newPage();
          let reportPageError;
          const pageError = new Promise(resolve => { reportPageError = resolve; });
          page.on('pageerror', error => { result.pageErrors.push(String(error)); reportPageError(); });
          try {
            await page.goto(`http://127.0.0.1:${server.address().port}/`);
            await page.addScriptTag({url: '/' + build + '.js'});
            await Promise.race([page.waitForFunction(() => globalThis.emptyIconPngBase64 !== undefined, null, {timeout: 15000}), pageError]);
            const actual = await page.evaluate(() => ({result: globalThis.iconCreationResult,
              gradient: globalThis.iconPngBase64, empty: globalThis.emptyIconPngBase64}));
            result.output = actual.result;
            assert.deepEqual(result.pageErrors, [], 'No browser runtime errors');
            assert.equal(actual.result, expectedResult, 'Java drawing and exception regression');
            result.fixtures = {};
            for (const kind of ['gradient', 'empty']) {
              assert.equal(typeof actual[kind], 'string', 'Browser fixture is available');
              const png = Buffer.from(actual[kind], 'base64');
              assert.equal(png.toString('base64'), actual[kind], 'Canonical complete base64');
              result.fixtures[kind] = verifyPng(png, kind === 'empty');
              const relative = path.join('out', 'icon-png-fixtures', engine + '-' + build + '-' + kind + '.png');
              fs.mkdirSync(path.dirname(path.join(root, relative)), {recursive: true});
              fs.writeFileSync(path.join(root, relative), png);
              result.fixtures[kind].artifact = relative;
            }
            result.passed = true;
          } catch (error) {
            result.error = String(error.stack ?? error);
          } finally {
            result.elapsedMs = Date.now() - started;
            results.push(result);
            console.log(engine + '/' + build + ': ' + (result.passed ? 'PASS (512 decoded pixels, zlib/Adler and all CRCs)' : 'FAIL'));
            if (!result.passed) console.error(result.error);
            await page.close();
          }
        }
      } catch (error) {
        results.push({engine, passed: false, error: String(error.stack ?? error)});
      } finally {
        if (browser) await browser.close();
      }
    }
  } finally {
    await new Promise(resolve => server.close(resolve));
    fs.mkdirSync(path.join(root, 'out'), {recursive: true});
    fs.writeFileSync(path.join(root, 'out/icon-png-browser-results.json'), JSON.stringify(results, null, 2) + '\n');
  }
  if (!results.length || results.some(result => !result.passed)) throw new Error('PNG browser verification failed; see out/icon-png-browser-results.json');
}
