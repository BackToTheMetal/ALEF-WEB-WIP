const fs = require('node:fs');
const path = require('node:path');
const http = require('node:http');
const {chromium, firefox, webkit} = require('playwright');

// Development output contains Closure dependency imports relative to execroot.
const root = path.resolve(__dirname, '..');
for (const name of ['browser-results.json', 'browser-regressions.json']) {
  fs.rmSync(path.join(root, 'out', name), {force: true});
}
const execroot = fs.realpathSync(path.join(root, 'bazel-' + path.basename(root)));
const bin = fs.realpathSync(path.join(root, 'bazel-bin'));
const seeds = JSON.parse(fs.readFileSync(path.resolve(root, process.env.TROVE_SEEDS_FILE || 'tests/seeds.json'), 'utf8'));
const results = [];
const regressions = [];
async function bounded(promise, milliseconds) {
  let timer;
  try {
    return await Promise.race([promise, new Promise((_, reject) => {
      timer = setTimeout(() => reject(new Error('Browser test timed out')), milliseconds);
    })]);
  } finally { clearTimeout(timer); }
}
const server = http.createServer((req, res) => {
  const url = new URL(req.url, 'http://localhost');
  if (url.pathname === '/') {
    const prod = url.searchParams.get('mode') === 'optimized';
    res.setHeader('Content-Type', 'text/html');
    res.end(prod ? '<script src="/bin/tests/browser_application.js"></script>' :
      '<script src="/bin/tests/browser_application_dev_config.js"></script><script src="/bin/tests/browser_application_dev.js"></script>');
    return;
  }
  let decoded;
  try { decoded = decodeURIComponent(url.pathname); } catch { res.writeHead(400).end(); return; }
  const base = decoded.startsWith('/bin/') ? bin : execroot;
  const relative = decoded.startsWith('/bin/') ? decoded.slice(5) : decoded.slice(1);
  const file = path.resolve(base, relative);
  if (!file.startsWith(base + path.sep)) { res.writeHead(403).end(); return; }
  fs.readFile(file, (err, data) => {
    if (err) { res.writeHead(404).end(file); return; }
    res.setHeader('Content-Type', file.endsWith('.js') ? 'text/javascript' : 'application/octet-stream');
    res.end(data);
  });
});

(async () => {
  await new Promise(resolve => server.listen(0, '127.0.0.1', resolve));
  try {
    for (const [engine, launcher] of Object.entries({chromium, firefox, webkit})) {
      const browser = await launcher.launch({headless: true});
      try {
        for (const mode of ['development', 'optimized']) {
          const page = await browser.newPage();
          const errors = [];
          page.on('pageerror', e => errors.push(e.message));
          await page.goto(`http://127.0.0.1:${server.address().port}/?mode=${mode}`);
          await page.waitForFunction(() => typeof window.runTroveContracts === 'function', null, {timeout: 60000});
          for (const seed of seeds) {
            const started = Date.now();
            const actual = await bounded(page.evaluate(seed => window.runTroveContracts(seed), seed), 60000);
            const expected = fs.readFileSync(path.join(root, 'out', `reference-${seed}.txt`), 'utf8').replace(/\r\n/g, '\n');
            fs.writeFileSync(path.join(root, 'out', `${engine}-${mode}-${seed}.txt`), actual);
            if (actual !== expected) throw new Error(`${engine} ${mode} ${seed}: differential mismatch\n${actual}\nexpected:\n${expected}`);
            const result = {engine, browser: browser.version(), mode, seed, milliseconds: Date.now() - started, summary: actual.split('\n')[0]};
            results.push(result); console.log(result);
          }
          const regression = await bounded(page.evaluate(() => window.runTroveRegressions()), 60000);
          if (regression !== 'PASS regression contracts') throw new Error('Unexpected regression result: ' + regression);
          regressions.push({engine, mode, summary: regression});
          console.log(engine, mode, regression);
          if (errors.length) throw new Error(errors.join('\n'));
          await page.close();
        }
      } finally { await browser.close(); }
    }
    fs.writeFileSync(path.join(root, 'out/browser-results.json'), JSON.stringify(results, null, 2) + '\n');
    fs.writeFileSync(path.join(root, 'out/browser-regressions.json'), JSON.stringify(regressions, null, 2) + '\n');
  } finally { server.close(); }
})().catch(e => { console.error(e); process.exitCode = 1; });
