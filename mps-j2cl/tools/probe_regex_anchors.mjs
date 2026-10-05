// Native-engine regression probe; the full Java oracle remains the release gate.
import {createRequire} from 'node:module';
const requireSibling = createRequire(new URL('../../org-jdom-j2cl/package.json', import.meta.url));
const {chromium, firefox, webkit} = requireSibling('playwright');
for (const [name, type] of Object.entries({chromium, firefox, webkit})) {
  const browser = await type.launch({headless: true});
  try {
    const page = await browser.newPage();
    const result = await page.evaluate(() => {
      const wrong = [];
      const absoluteEnd = String.raw`$(?![\s\S])`;
      const javaEnd = String.raw`(?=(?:\r\n|(?<!\r)\n|[\r\u0085\u2028\u2029])?$(?![\s\S]))`;
      for (const text of ['😀', '😀x', 'x😀', '😀\n', '😀\r\n', '😀\u0085', '😀\u2028']) {
        for (const [source, expected] of [[absoluteEnd, [text.length]], [javaEnd,
          [text.length - (text.endsWith('\r\n') ? 2 : /[\r\n\u0085\u2028\u2029]$/.test(text) ? 1 : 0), text.length]
            .filter((value, i, all) => i === 0 || all[i - 1] !== value)]]) {
          const regexp = new RegExp(source, 'gu');
          const offsets = [];
          let match;
          while ((match = regexp.exec(text)) !== null) {
            offsets.push(match.index);
            regexp.lastIndex++;
          }
          if (JSON.stringify(offsets) !== JSON.stringify(expected)) wrong.push({source, text, offsets, expected});
        }
      }
      return wrong;
    });
    if (result.length) throw new Error(name + ': ' + JSON.stringify(result));
    process.stdout.write(name + ': native regex end anchors PASS\n');
  } finally {
    await browser.close();
  }
}
