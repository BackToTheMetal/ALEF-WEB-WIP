const fs = require('node:fs');
const path = require('node:path');
const http = require('node:http');
const {chromium, firefox, webkit} = require('playwright');
const root = path.resolve(__dirname, '..');
const workspace = path.resolve(process.env.TIME_WORKSPACE || root);
const execroot = fs.realpathSync(path.join(workspace, 'bazel-' + path.basename(workspace)));
const bin = fs.realpathSync(path.join(workspace, 'bazel-bin'));
const output = path.join(root, 'out', process.env.TIME_RESULT || 'browser-results.json');
fs.rmSync(output, {force: true});
const expected = fs.readFileSync(path.join(root, 'out/jdk-reference.txt'), 'utf8');
async function bounded(promise) {
  let timer;
  try {
    return await Promise.race([promise, new Promise((_, reject) => {
      timer = setTimeout(() => reject(Error('java.time browser contracts timed out')), 60000);
    })]);
  } finally { clearTimeout(timer); }
}
const server = http.createServer((req, res) => {
  const url = new URL(req.url, 'http://localhost');
  if (url.pathname === '/') {
    res.setHeader('Content-Type', 'text/html');
    res.end(url.searchParams.get('mode') === 'optimized'
      ? '<script src="/bin/tests/browser.js"></script>'
      : '<script src="/bin/tests/browser_dev_config.js"></script><script src="/bin/tests/browser_dev.js"></script>');
    return;
  }
  let decoded;
  try { decoded = decodeURIComponent(url.pathname); } catch { res.writeHead(400).end(); return; }
  const base = decoded.startsWith('/bin/') ? bin : execroot;
  const file = path.resolve(base, decoded.startsWith('/bin/') ? decoded.slice(5) : decoded.slice(1));
  if (!file.startsWith(base + path.sep)) { res.writeHead(403).end(); return; }
  fs.readFile(file, (err, bytes) => {
    if (err) { res.writeHead(404).end(); return; }
    res.setHeader('Content-Type', file.endsWith('.js') ? 'text/javascript' : 'application/octet-stream');
    res.end(bytes);
  });
});
(async () => {
  const results = [];
  await new Promise(resolve => server.listen(0, '127.0.0.1', resolve));
  try {
    for (const [engine, launcher] of Object.entries({chromium, firefox, webkit})) {
      if (process.env.TIME_ENGINE && process.env.TIME_ENGINE !== engine) continue;
      const browser = await launcher.launch({headless: true});
      try {
        for (const mode of (process.env.TIME_MODE ? [process.env.TIME_MODE] : ['development', 'optimized'])) {
          const page = await browser.newPage();
          const errors = [];
          page.on('pageerror', e => errors.push(e.message));
          await page.goto(`http://127.0.0.1:${server.address().port}/?mode=${mode}`);
          await page.waitForFunction(() => typeof window.runJavaTimeContracts === 'function', null, {timeout: 60000}).catch(e => { throw Error(e.message + '\n' + errors.join('\n')); });
          const actual = await bounded(page.evaluate(() => window.runJavaTimeContracts()));
          fs.writeFileSync(path.join(root, 'out', `${engine}-${mode}.txt`), actual);
          if (actual !== expected) {
            const left = expected.split('\n'), right = actual.split('\n');
            const differences = left.flatMap((line, i) => line === right[i] ? [] : [`expected: ${line}\nactual:   ${right[i]}`]);
            throw Error(engine + ' ' + mode + ' JDK mismatch:\n' + differences.slice(0, 20).join('\n'));
          }
          if (errors.length) throw Error(errors.join('\n'));
          const result = {engine, version: browser.version(), mode, summary: actual.split('\n')[0]};
          results.push(result);
          console.log(result);
          await page.close();
        }
      } finally { await browser.close(); }
    }
    fs.writeFileSync(output, JSON.stringify(results, null, 2) + '\n');
  } finally { server.close(); }
})().catch(e => { console.error(e); process.exitCode = 1; });
