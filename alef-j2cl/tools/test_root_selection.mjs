import {createRequire} from 'node:module';
import path from 'node:path';
import fs from 'node:fs';
import assert from 'node:assert/strict';
const root = path.resolve(import.meta.dirname, '..');
const engines = createRequire(path.join(root, '../org-jdom-j2cl/package.json'))('playwright');
const directory = path.join(root,'out/root-selection'); fs.mkdirSync(directory,{recursive:true});
for(const name of (process.env.ALEF_BROWSERS || 'chromium').split(',')) {
  const browser=await engines[name].launch({headless:true});
  const page=await browser.newPage({viewport:{width:1500,height:950}});
  const errors=[];page.on('pageerror',error=>errors.push(String(error)));
  try {
    await page.goto(process.env.ALEF_EXAMPLE_URL || 'http://127.0.0.1:8812/');
    await page.getByRole('tab',{name:'Huishouden',exact:true}).waitFor({timeout:60000});
    const view=page.locator('mps-project-view');
    for(const title of ['Huishouden','Bereken toeslag','Toeslagservice','Toeslag voor volwassenen']) {
      await view.getByRole('treeitem',{name:title,exact:true}).click();
      await page.waitForTimeout(200);
      const selectedPixels=await page.locator('#alef-editor').evaluate(el=>{
        let count=0;
        for(const canvas of el.querySelectorAll('canvas')) {
          if(!canvas.getBoundingClientRect().width || !canvas.width || !canvas.height) continue;
          const bytes=canvas.getContext('2d').getImageData(0,0,canvas.width,canvas.height).data;
          for(let i=0;i<bytes.length;i+=4) if(bytes[i]===82 && bytes[i+1]===109 && bytes[i+2]===165 && bytes[i+3]>240) count++;
        }
        return count;
      });
      assert.ok(selectedPixels<1000, `${title}: opening must not paint a whole-root selection (${selectedPixels} blue pixels)`);
    }
    await page.screenshot({path:path.join(directory,`${name}-opened.png`)});
    await view.getByRole('treeitem',{name:'Huishouden',exact:true}).click();
    const bounds=await page.locator('#alef-editor').boundingBox();
    await page.mouse.click(bounds.x+230,bounds.y+55);
    await page.keyboard.press('End');await page.keyboard.press('ArrowLeft');
    await page.getByRole('tab',{name:'Toeslagservice',exact:true}).click();
    await page.getByRole('tab',{name:'Huishouden',exact:true}).click();
    // Return focus without moving the caret with a mouse click.
    await page.locator('#alef-editor').evaluate(el => {
      const inspectorTop = el.querySelector('.inspector-heading').getBoundingClientRect().top;
      const input = [...el.querySelectorAll('textarea[data-mps-input-method]')].find(input => {
        const rect = input.getBoundingClientRect();
        return rect.width && rect.top < inspectorTop;
      });
      if (!input) throw new Error('Missing main editor input');
      input.focus();
    });
    await page.keyboard.type('X');
    await view.getByRole('treeitem',{name:'HuishoudeXn',exact:true}).waitFor();
    await page.getByRole('button',{name:'Undo',exact:true}).click();
    await view.getByRole('treeitem',{name:'Huishouden',exact:true}).waitFor();
    assert.deepEqual(errors,[]);
    console.log(JSON.stringify({browser:name,passed:true,checks:'no whole-root highlight in all four languages; caret preserved across tabs; native editing and undo',errors}));
  }catch(error){await page.screenshot({path:path.join(directory,`${name}-failure.png`)});console.error(errors);throw error;}
  finally{await browser.close();}
}
