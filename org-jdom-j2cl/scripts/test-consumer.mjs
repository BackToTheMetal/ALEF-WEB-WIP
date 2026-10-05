import fs from 'node:fs';
import path from 'node:path';
import {chromium,firefox,webkit} from 'playwright';
const root=path.resolve(import.meta.dirname,'..');
const results=[];
for(const [name,type] of Object.entries({chromium,firefox,webkit})) {
  const browser=await type.launch({headless:true});
  try {
    const page=await browser.newPage();
    await page.addScriptTag({path:path.join(root,'out/fresh-consumer/bazel-bin/browser.js')});
    const value=await page.evaluate(()=>window.runConsumer());
    if(value!== '<model><node id="hook-2" /></model>\n')throw new Error(`Unexpected consumer output ${JSON.stringify(value)}`);
    results.push({browser:name,output:value});console.log(`${name}: independent optimized consumer passed`);
  }finally{await browser.close();}
}
fs.writeFileSync(path.join(root,'out/consumer-results.json'),JSON.stringify(results,null,2)+'\n');
