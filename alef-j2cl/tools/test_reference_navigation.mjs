import {createRequire} from 'node:module';
import path from 'node:path';
import fs from 'node:fs';
import assert from 'node:assert/strict';
const root=path.resolve(import.meta.dirname,'..');
const engines=createRequire(path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const directory=path.join(root,'out/reference-navigation');fs.mkdirSync(directory,{recursive:true});
for(const name of (process.env.ALEF_BROWSERS || 'chromium').split(',')) {
  const browser=await engines[name].launch({headless:true});
  const page=await browser.newPage({viewport:{width:1500,height:1000}});
  const errors=[];page.on('pageerror',error=>errors.push(String(error)));
  // Read rendered text positions so clicks exercise the real canvas editor,
  // without invoking the navigation implementation or hardcoding node IDs.
  await page.addInitScript(()=>{
    window.drawnLabels=[];
    const fill=CanvasRenderingContext2D.prototype.fillText;
    CanvasRenderingContext2D.prototype.fillText=function(text,x,y,...args){
      const t=this.getTransform(), metrics=this.measureText(text);
      window.drawnLabels.push({text,canvas:this.canvas,x:t.a*x+t.c*(y-5)+t.e,y:t.b*x+t.d*(y-5)+t.f,width:metrics.width*t.a});
      if(window.drawnLabels.length>30000) window.drawnLabels.splice(0,15000);
      return fill.call(this,text,x,y,...args);
    };
  });
  const clickLabel=async(text,options={})=>{
    let point;
    for(let attempt=0;attempt<10 && !point;attempt++) {
      point=await page.evaluate(text=>{
      const surface=document.getElementById('alef-editor').getBoundingClientRect();
      const bottom=document.querySelector('.inspector-heading').getBoundingClientRect().top;
      const labels=window.drawnLabels;
      for(let i=labels.length-1;i>=0;i--) {
        const item=labels[i];
        if(!item.canvas.isConnected || !item.text || !text.startsWith(item.text))continue;
        // MPS paints the portions on either side of a caret separately.
        let joined=item.text,right=item.x+item.width;
        for(let j=i+1;joined!==text && j<Math.min(i+8,labels.length);j++) {
          const next=labels[j];
          if(next.canvas!==item.canvas || Math.abs(next.y-item.y)>1 || Math.abs(next.x-right)>1)break;
          joined+=next.text;right=next.x+next.width;
        }
        if(joined!==text)continue;
        const rect=item.canvas.getBoundingClientRect();
        if(!rect.width || !rect.height)continue;
        const x=rect.left+(item.x+Math.min(item.width/2,15))*rect.width/item.canvas.width;
        const y=rect.top+item.y*rect.height/item.canvas.height;
        const hit=document.elementFromPoint(x,y);
        if(x>surface.left && x<surface.right && y>surface.top && y<bottom && hit && item.canvas.parentElement.contains(hit)) return {x,y,canvas:item.canvas.id};
      }
      return null;
      },text);
      if(!point) {
        if(options.noScroll)break;
        const bounds=await page.locator('#alef-editor').boundingBox();
        await page.mouse.move(bounds.x+400,bounds.y+200);
        await page.mouse.wheel(0,300);await page.waitForTimeout(150);
      }
    }
    assert.ok(point,'Rendered label not found: '+text);
    if(options.hover) await page.mouse.move(point.x,point.y);
    else if(options.drag) {
      await page.mouse.move(point.x,point.y);await page.mouse.down();
      await page.mouse.move(point.x+12,point.y,{steps:3});await page.mouse.up();
    } else await page.mouse.click(point.x,point.y,options);
    return point;
  };
  const ctrlClick=async text=>{
    // Find/scroll first: Ctrl+wheel is browser zoom, not editor scrolling.
    await clickLabel(text,{hover:true});
    await page.keyboard.down('Control');
    try {
      await page.waitForTimeout(100);
      await clickLabel(text,{noScroll:true});
    } finally { await page.keyboard.up('Control'); }
  };
  const pixels=point=>page.evaluate(point=>{
    const canvas=document.getElementById(point.canvas),rect=canvas.getBoundingClientRect();
    return Array.from(canvas.getContext('2d').getImageData(point.x-rect.left-5,point.y-rect.top-12,70,24).data);
  },point);
  const cursor=point=>page.evaluate(point=>getComputedStyle(document.elementFromPoint(point.x,point.y)).cursor,point);
  try {
    await page.goto(process.env.ALEF_EXAMPLE_URL || 'http://127.0.0.1:8812/');
    await page.getByRole('tab',{name:'Huishouden',exact:true}).waitFor({timeout:60000});
    const view=page.locator('mps-project-view');
    const open=async title=>{
      await page.evaluate(()=>{window.drawnLabels=[];});
      await view.getByRole('treeitem',{name:title,exact:true}).click();await page.waitForTimeout(250);
    };
    await open('Bereken toeslag');
    // Hover must work while the project tree still has keyboard focus, including
    // Ctrl pressed/released without moving the pointer. It must not select a cell.
    const hovered=await clickLabel('Persoon',{hover:true});
    const before=await pixels(hovered), inspectorBefore=await page.evaluate(()=>window.alefExample.inspectorState());
    await page.keyboard.down('Control');await page.waitForTimeout(100);
    assert.equal(await cursor(hovered),'pointer');
    assert.notDeepEqual(await pixels(hovered),before,'Ctrl-hover must visibly highlight the reference');
    assert.deepEqual(await page.evaluate(()=>window.alefExample.inspectorState()),inspectorBefore);
    await page.screenshot({path:path.join(directory,`${name}-ctrl-hover.png`)});
    await page.keyboard.up('Control');await page.waitForTimeout(100);
    assert.notEqual(await cursor(hovered),'pointer');
    assert.deepEqual(await pixels(hovered),before,'Releasing Ctrl must remove the highlight');
    await page.mouse.move(100,100);await page.keyboard.down('Control');
    await clickLabel('Persoon',{hover:true});await page.waitForTimeout(100);
    assert.equal(await cursor(hovered),'pointer','Holding Ctrl before entering must highlight too');
    await page.mouse.move(100,100);await page.waitForTimeout(100);
    assert.deepEqual(await pixels(hovered),before,'Leaving the reference must clear the highlight');
    await page.keyboard.up('Control');
    await clickLabel('Persoon');await page.waitForTimeout(100);
    assert.equal(await page.getByRole('tab',{name:'Bereken toeslag',exact:true}).getAttribute('aria-selected'),'true','Plain click must not navigate');
    await page.keyboard.down('Control');await clickLabel('Persoon',{hover:true});await page.waitForTimeout(100);
    assert.equal(await cursor(hovered),'pointer','Hover must also work with editor focus');
    await page.mouse.move(100,100);await page.keyboard.up('Control');await page.waitForTimeout(100);
    const tail={...hovered,x:hovered.x+25}, outside=await pixels(tail);
    await page.keyboard.down('Control');await page.waitForTimeout(100);
    assert.deepEqual(await pixels(tail),outside,'Pressing Ctrl outside must not restore an old reference highlight');
    await page.keyboard.up('Control');
    await clickLabel('Persoon',{button:'middle'});await page.waitForTimeout(100);
    assert.equal(await page.getByRole('tab',{name:'Bereken toeslag',exact:true}).getAttribute('aria-selected'),'true','Middle click without Ctrl must not navigate');
    await page.keyboard.down('Control');
    await clickLabel('Persoon',{drag:true});await page.waitForTimeout(150);
    await page.keyboard.up('Control');
    assert.equal(await page.getByRole('tab',{name:'Bereken toeslag',exact:true}).getAttribute('aria-selected'),'true');
    await ctrlClick('Persoon');
    await page.waitForFunction(()=>document.querySelector('[role=tab][aria-selected=true]')?.textContent==='Huishouden');
    await page.waitForFunction(()=>window.alefExample.inspectorState().concept==='ObjectType');
    assert.match(await page.evaluate(()=>window.alefExample.inspectorState().name),/^Persoon/);
    assert.equal(await view.getByRole('treeitem',{name:'Huishouden',exact:true}).getAttribute('aria-selected'),'true');
    await page.screenshot({path:path.join(directory,`${name}-target.png`)});
    // A property reference targets a descendant, not merely the root's first cell.
    await open('Bereken toeslag');await ctrlClick('leeftijd');
    await page.waitForFunction(()=>window.alefExample.inspectorState().concept==='Attribuut' && window.alefExample.inspectorState().name.startsWith('leeftijd'));
    assert.equal(await page.getByRole('tab',{name:'Huishouden',exact:true}).getAttribute('aria-selected'),'true');
    assert.equal(await page.getByRole('tab',{name:'Huishouden',exact:true}).count(),1);
    // Clicking an ordinary name still edits/selects that cell in this editor.
    await clickLabel('Huishouden');
    await page.keyboard.press('End');await page.keyboard.type('X');
    await view.getByRole('treeitem',{name:'HuishoudenX',exact:true}).waitFor();
    await page.getByRole('button',{name:'Undo',exact:true}).click();
    await view.getByRole('treeitem',{name:'Huishouden',exact:true}).waitFor();
    // Ctrl-click follows the same resolved reference without duplicate tabs.
    await open('Bereken toeslag');await page.keyboard.down('Control');await clickLabel('Persoon');await page.keyboard.up('Control');
    await page.waitForFunction(()=>window.alefExample.inspectorState().concept==='ObjectType');
    assert.equal(await page.getByRole('tab').count(),2);
    await open('Bereken toeslag');await page.keyboard.down('Shift');await clickLabel('Persoon');await page.keyboard.up('Shift');
    assert.equal(await page.getByRole('tab',{name:'Bereken toeslag',exact:true}).getAttribute('aria-selected'),'true');
    await page.keyboard.press('Control+b');
    await page.waitForFunction(()=>window.alefExample.inspectorState().concept==='ObjectType');
    assert.equal(await page.getByRole('tab',{name:'Huishouden',exact:true}).getAttribute('aria-selected'),'true');
    await open('Toeslagservice');await ctrlClick('Bereken toeslag');
    await page.waitForFunction(()=>window.alefExample.inspectorState().concept==='Regelgroep');
    assert.equal(await page.getByRole('tab',{name:'Bereken toeslag',exact:true}).getAttribute('aria-selected'),'true');
    await open('Toeslag voor volwassenen');await ctrlClick('BerekenToeslag');
    await page.waitForFunction(()=>window.alefExample.inspectorState().concept==='Entrypoint');
    assert.equal(await page.getByRole('tab',{name:'Toeslagservice',exact:true}).getAttribute('aria-selected'),'true');
    await ctrlClick('Aanvraag');
    await page.waitForFunction(()=>window.alefExample.inspectorState().concept==='Invoerberichtmapping');
    assert.equal(await page.getByRole('tab',{name:'Toeslagservice',exact:true}).count(),1);
    await page.screenshot({path:path.join(directory,`${name}-same-root.png`)});
    assert.deepEqual(errors,[]);assert.equal(await page.locator('#error').isVisible(),false);
    console.log(JSON.stringify({browser:name,passed:true,checks:'Ctrl-only click navigation; Ctrl-hover visual feedback without editor focus; clear on release and exit; descendant selection; tree, inspector and tab reuse; ordinary editing; Ctrl-drag; Ctrl+B; cross-language and same-root targets',errors}));
  }catch(error){await page.screenshot({path:path.join(directory,`${name}-failure.png`)});console.error(errors);throw error;}
  finally{await browser.close();}
}
