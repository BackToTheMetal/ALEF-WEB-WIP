import {chromium, firefox, webkit} from 'playwright';
import fs from 'node:fs';
import path from 'node:path';
import http from 'node:http';
const root=path.resolve(import.meta.dirname,'..');
const builds=['browser_dev','browser'];
const results=[];
const savedModels=path.resolve(root,'out/mps-browser');
if(path.dirname(savedModels)!==path.join(root,'out'))throw new Error('Unsafe fixture output path');
fs.rmSync(savedModels,{recursive:true,force:true});
const server=http.createServer((req,res)=>{
  const name=req.url.slice(1);
  if(builds.includes(name.replace('.js','')) && name.endsWith('.js')) {
    res.setHeader('Content-Type','application/javascript');res.end(fs.readFileSync(path.join(root,'bazel-bin/tests',name)));
  } else {res.setHeader('Content-Type','text/html');res.end('<!doctype html><title>XML contracts</title>');}
});
await new Promise(resolve=>server.listen(0,'127.0.0.1',resolve));
try {
  for(const [engine,type] of Object.entries({chromium,firefox,webkit})) {
    const browser=await type.launch({headless:true});
    try {
      for(const build of builds) {
        const page=await browser.newPage();const errors=[];page.on('pageerror',e=>errors.push(String(e)));
        await page.goto(`http://127.0.0.1:${server.address().port}/`);
        await page.addScriptTag({url:`/${build}.js`});
        const start=Date.now();const output=await page.evaluate(()=>window.runXmlContracts());
        const reference=fs.readFileSync(path.join(root,'out/reference-contracts.txt'),'utf8').trim().replaceAll('\r\n','\n');
        if(!output.startsWith(reference+'\n'))throw new Error(`${engine}/${build}: reference observations differ\n${output}`);
        if(errors.length)throw new Error(errors.join('\n'));
        const fixtures=path.join(root,'tests/fixtures/mps-2025.3');
        const destination=path.join(root,'out/mps-browser',engine,build);
        fs.mkdirSync(destination,{recursive:true});
        for(const name of fs.readdirSync(fixtures).filter(name=>name.endsWith('.xml'))) {
          const xml=fs.readFileSync(path.join(fixtures,name),'utf8');
          const saved=await page.evaluate(xml=>window.roundTripMpsXml(xml),xml);
          fs.writeFileSync(path.join(destination,name),saved);
        }
        results.push({engine,build,elapsedMs:Date.now()-start,output});
        console.log(`${engine}/${build}: passed (${Date.now()-start} ms)`);
        await page.close();
      }
    } finally {await browser.close();}
  }
} finally {
  server.close();fs.mkdirSync(path.join(root,'out'),{recursive:true});
  fs.writeFileSync(path.join(root,'out/browser-results.json'),JSON.stringify(results,null,2)+'\n');
}
