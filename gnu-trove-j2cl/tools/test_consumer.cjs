const path = require('node:path');
const fs = require('node:fs');
const {chromium, firefox, webkit} = require('playwright');
(async () => {
  const script = path.join(__dirname, '../out/consumer/bazel-bin/application.js');
  for (const [name, launcher] of Object.entries({chromium, firefox, webkit})) {
    const browser = await launcher.launch({headless: true});
    try {
      const page = await browser.newPage();
      await page.addScriptTag({content: fs.readFileSync(script, 'utf8')});
      const result = await page.evaluate(() => window.runConsumer());
      if (result !== 'PASS independent archive consumer') throw new Error(name + ': ' + result);
      console.log(name, result);
    } finally { await browser.close(); }
  }
})().catch(e => { console.error(e); process.exitCode = 1; });
