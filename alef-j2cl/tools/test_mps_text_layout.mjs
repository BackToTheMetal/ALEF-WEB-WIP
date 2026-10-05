import {createRequire} from 'node:module';
import fs from 'node:fs';
import path from 'node:path';
import assert from 'node:assert/strict';
const root=path.resolve(import.meta.dirname,'..');
const {chromium}=createRequire(path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const scaleFactor=Number(process.env.ALEF_TEST_DPR||1);
const output=path.join(root,'out/text-layout'+(scaleFactor===1?'':'-dpr'+scaleFactor));fs.mkdirSync(output,{recursive:true});
const browser=await chromium.launch({headless:true}),page=await browser.newPage({viewport:{width:1440,height:950},deviceScaleFactor:scaleFactor}),errors=[];
page.on('pageerror',e=>errors.push(String(e)));
// Use the real language's standalone models. Never acquire a lease or save live data.
await page.route('**/backend-mode.js',r=>r.fulfill({contentType:'text/javascript',body:'globalThis.alefBackendMode=false;'}));
await page.route('**/api/**',()=>{throw new Error('No backend requests allowed in this layout fixture');});
await page.addInitScript(()=>{
  window.paints=[];
  const draw=CanvasRenderingContext2D.prototype.fillText;
  CanvasRenderingContext2D.prototype.fillText=function(text,x,y,...args){
    const matrix=this.getTransform(),measure=this.measureText(text);
    window.paints.push({text,font:this.font,color:this.fillStyle,canvas:this.canvas,x:matrix.a*x+matrix.c*y+matrix.e,y:matrix.b*x+matrix.d*y+matrix.f,width:measure.width*matrix.a});
    if(window.paints.length>20000)window.paints.splice(0,10000);
    return draw.call(this,text,x,y,...args);
  };
});
const findPainted=text=>{
  const paints=window.paints;
  for(let i=paints.length-1;i>=0;i--){
    const p=paints[i];
    if(!p.text||!text.startsWith(p.text)||!document.querySelector('#alef-editor').contains(p.canvas))continue;
    // MPS retains the caret position even after blur/undo, so a label may be
    // painted as adjacent runs. Join those runs for subsequent mouse clicks.
    let joined=p.text,right=p.x+p.width;
    for(let j=i+1;joined!==text&&j<Math.min(i+8,paints.length);j++){
      const next=paints[j];
      if(!next.text)continue;
      const pixelScale=p.canvas.width/p.canvas.getBoundingClientRect().width;
      if(next.canvas!==p.canvas||Math.abs(next.y-p.y)>1||Math.abs(next.x-right)>1.1*pixelScale)break;
      joined+=next.text;right=next.x+next.width;
    }
    if(joined!==text)continue;
    const box=p.canvas.getBoundingClientRect(),scale=box.width/p.canvas.width;
    return {text,x:box.left+p.x*scale,y:box.top+p.y*scale,width:(right-p.x)*scale,font:p.font};
  }
  return null;
};
const painted=async text=>{
  await page.waitForFunction(findPainted,text);
  return page.evaluate(findPainted,text);
};
const click=async text=>{const p=await painted(text);await page.mouse.click(p.x+Math.min(12,p.width/2),p.y-5);};
try{
  await page.goto(process.env.ALEF_EXAMPLE_URL||'http://localhost:8080/#model');
  await page.waitForFunction(()=>window.alefExample?.status()==='READY',null,{timeout:120000});
  await page.locator('mps-project-view').getByRole('treeitem',{name:'Huishouden',exact:true}).click();
  const original=await page.evaluate(()=>window.alefExample.editorText(0));
  const results=[];
  for(const name of ['bisddssddsfsdfsdfsdfsdfsdfsdfsdfsdfsdfsddam','ssssssssssssssssssssssssssssssssssssssss','AVATAR_fi_é_naam_AVATAR_fi_é_naam']){
    const point=await painted('naam');
    await page.mouse.dblclick(point.x+Math.min(12,point.width/2),point.y-5);
    // Native text-input events (used by Playwright for composed characters) are
    // queued separately from key events. Commit each before sending the next.
    let typed='';
    for(const character of name){
      await page.keyboard.type(character);typed+=character;
      await page.waitForFunction(text=>window.alefExample.editorText(0).includes('de '+text+' Tekst'),typed);
    }
    await click('Objectmodel');
    const label=await painted(name);
    const type=await page.evaluate(label=>{
      for(const p of [...window.paints].reverse()){
        if(p.text!=='Tekst'||!document.querySelector('#alef-editor').contains(p.canvas))continue;
        const b=p.canvas.getBoundingClientRect(),scale=b.width/p.canvas.width,x=b.left+p.x*scale,y=b.top+p.y*scale;
        if(Math.abs(y-label.y)<1&&x>label.x)return {x,y};
      }
    },label);
    assert.ok(type,'The type cell must be drawn beside the attribute');
    const gap=type.x-label.x-label.width;
    results.push({name,font:label.font,width:label.width,gap});
    fs.writeFileSync(path.join(output,'metrics.json'),JSON.stringify(results,null,2));
    await page.screenshot({path:path.join(output,`long-name-${results.length}.png`)});
    assert.ok(gap>=2,`Text overlaps or has no readable gap: ${gap.toFixed(2)}px for ${name}`);
    // A click at a measured text boundary must place the caret there too.
    // This exercises the substring-width path used by MPS hit testing.
    const index=name.startsWith('AVATAR')?3:7,prefix=name.slice(0,index);
    const prefixWidth=await page.evaluate(({prefix,font})=>{
      const context=document.createElement('canvas').getContext('2d');
      context.font=font;return context.measureText(prefix).width;
    },{prefix,font:label.font});
    await page.mouse.click(label.x+prefixWidth,label.y-5);
    await page.keyboard.type('Z');
    await page.waitForFunction(expected=>window.alefExample.editorText(0).includes(expected),prefix+'Z'+name.slice(index));
    await painted(prefix+'Z');
    const suffix=await painted(name.slice(index));
    const activeGap=await page.evaluate(suffix=>{
      const p=[...window.paints].reverse().find(p=>p.text==='Tekst'&&document.querySelector('#alef-editor').contains(p.canvas)&&Math.abs(p.canvas.getBoundingClientRect().top+p.y*p.canvas.getBoundingClientRect().width/p.canvas.width-suffix.y)<1);
      const box=p.canvas.getBoundingClientRect();
      return box.left+p.x*box.width/p.canvas.width-suffix.x-suffix.width;
    },suffix);
    assert.ok(activeGap>=1,`Text overlaps while the caret is inside the name: ${activeGap.toFixed(2)}px`);
    results.at(-1).activeGap=activeGap;
    fs.writeFileSync(path.join(output,'metrics.json'),JSON.stringify(results,null,2));
    for(let undo=0;undo<3;undo++){
      const previous=await page.evaluate(()=>window.alefExample.editorText(0));
      if(previous===original)break;
      await page.getByRole('button',{name:'Undo',exact:true}).click();
      await page.waitForFunction(previous=>window.alefExample.editorText(0)!==previous,previous);
    }
    assert.equal(await page.evaluate(()=>window.alefExample.editorText(0)),original,'Undo restores the original model');
    await click('Objectmodel');
  }
  assert.deepEqual(errors,[]);
  console.log(JSON.stringify({status:'PASS',checks:'native canvas text bounds, long names, fractional advances, kerning, accented characters, caret placement and undo',results,liveWorkspaceWrites:false}));
}catch(error){
  await page.screenshot({path:path.join(output,'failure.png')});
  console.error(errors,await page.evaluate(()=>window.alefExample?.editorText(0)));
  throw error;
}finally{await browser.close();}
