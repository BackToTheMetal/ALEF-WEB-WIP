import {createRequire} from 'node:module';
import path from 'node:path';
import fs from 'node:fs';
import assert from 'node:assert/strict';
const root = path.resolve(import.meta.dirname, '..');
const engines = createRequire(path.join(root, '../org-jdom-j2cl/package.json'))('playwright');
const directory = path.join(root, 'out/ide'); fs.mkdirSync(directory, {recursive:true});
for (const name of (process.env.ALEF_BROWSERS || 'chromium').split(',')) {
  const browser = await engines[name].launch({headless:true});
  const page = await browser.newPage({viewport:{width:1500,height:950}});
  const errors=[]; page.on('pageerror', e=>errors.push(String(e)));
  try {
    await page.goto(process.env.ALEF_EXAMPLE_URL || 'http://127.0.0.1:8812/');
    await page.waitForFunction(()=>document.title==='Huishouden — ALEF',null,{timeout:60000});
    const view=page.locator('mps-project-view');
    const fit=async()=>{
      await page.waitForTimeout(250);
      const size=await page.evaluate(()=>({w:innerWidth,h:innerHeight,sw:document.documentElement.scrollWidth,sh:document.documentElement.scrollHeight,main:document.querySelector('main').getBoundingClientRect().toJSON(),surface:document.getElementById('alef-editor').getBoundingClientRect().toJSON(),editor:parseInt(document.getElementById('alef-editor').style.getPropertyValue('--editor-height'))}));
      assert.equal(size.sw,size.w); assert.equal(size.sh,size.h); assert.equal(size.main.height,size.h);
      assert.ok(size.surface.bottom<=size.h); assert.ok(size.editor>100); return size;
    };
    await fit();
    for(const id of ['edit-object','edit-expression','reset','inspect','show-data','show-services','run-test','sort-model']) assert.equal(await page.locator('#'+id).count(),0);
    await page.evaluate(()=>window.alefExample.focus(0));
    await page.keyboard.type('Persoon gewijzigd');
    await page.waitForFunction(()=>window.alefExample.value(0)==='Persoon gewijzigd');
    await page.getByRole('button',{name:'Undo',exact:true}).click();
    await page.waitForFunction(()=>window.alefExample.value(0)==='Persoon');
    await page.getByRole('button',{name:'Redo',exact:true}).click();
    await page.waitForFunction(()=>window.alefExample.value(0)==='Persoon gewijzigd');
    await page.getByRole('button',{name:'Undo',exact:true}).click();
    for(const title of ['Bereken toeslag','Toeslagservice','Toeslag voor volwassenen']) {
      await view.getByRole('treeitem',{name:title,exact:true}).click();
      await page.waitForFunction(t=>document.title===t+' — ALEF',title);
    }
    assert.equal(await page.getByRole('tab').count(),0);
    await view.getByRole('treeitem',{name:'Toeslagservice',exact:true}).click();
    await page.waitForFunction(()=>window.alefExample.inspectorState().concept==='Service');
    await page.screenshot({path:path.join(directory,`${name}-workspace.png`)});
    const before=await fit();
    await page.getByRole('button',{name:'Hide inspector',exact:true}).click();
    const hidden=await fit(); assert.ok(hidden.editor>before.editor);
    await page.getByRole('button',{name:'Show inspector',exact:true}).click();
    const splitter=page.getByRole('separator',{name:'Resize inspector',exact:true});
    await splitter.focus(); await page.keyboard.press('ArrowUp'); const expanded=await fit(); assert.ok(expanded.editor<before.editor);
    const project=page.getByRole('separator',{name:'Resize project view',exact:true});
    await project.focus(); await page.keyboard.press('ArrowRight'); assert.ok((await fit()).surface.width<before.surface.width);
    await page.setViewportSize({width:900,height:650}); await fit();
    await page.screenshot({path:path.join(directory,`${name}-compact.png`)});
    await view.getByRole('treeitem',{name:'Huishouden',exact:true}).focus(); await page.keyboard.press('Enter');
    await page.waitForFunction(()=>document.title==='Huishouden — ALEF');
    assert.equal(await page.getByRole('region',{name:'Node editor',exact:true}).count(),1);
    assert.equal(await page.locator('#empty-editor').isVisible(),false);
    assert.deepEqual(errors,[]); assert.equal(await page.locator('#error').isVisible(),false);
    console.log(JSON.stringify({browser:name,passed:true,checks:'viewport fill, demo controls removed, native edit/undo/redo, single editor, generated inspector, pane resizing, compact layout, keyboard root switching',errors}));
  } catch(error) {await page.screenshot({path:path.join(directory,`${name}-failure.png`)});console.error(errors);throw error;}
  finally {await browser.close();}
}
