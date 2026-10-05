import assert from 'node:assert/strict';

export async function testPackageCreation(page){
  const creations=()=>page.evaluate(()=>window.calls.filter(([url,method])=>url==='/api/v1/packages'&&method==='POST'));
  const before=(await creations()).length;
  await page.getByRole('button',{name:'+ New package',exact:true}).click();
  const dialog=page.getByRole('dialog'),version=dialog.getByRole('textbox',{name:'Package version',exact:true}),title=dialog.getByRole('textbox',{name:'Name',exact:true});
  const submit=dialog.getByRole('button',{name:'Create package for review',exact:true});
  await dialog.getByRole('button',{name:'Select filtered',exact:true}).click();
  await title.fill('Test package');
  assert.match(await dialog.locator('#package-version-format').textContent(),/major\.minor\.patch/);
  for(const invalid of ['1.41','','01.4.1','1.4.1 invalid']){
    await version.fill(invalid);await submit.click();
    await page.waitForFunction(()=>document.querySelector('dialog [role=alert]')?.textContent.includes('three numbers'));
    assert.equal(await version.inputValue(),invalid);
    assert.equal(await version.evaluate(input=>input===document.activeElement),true);
    assert.equal(await dialog.locator('.package-picker input:checked').count(),2);
    assert.equal((await creations()).length,before,'Invalid versions must not send a creation request');
  }
  await version.fill('1.4.1');
  for(const invalid of ['   ','x'.repeat(201)]){
    await title.fill(invalid);await submit.click();
    await page.waitForFunction(()=>document.querySelector('dialog [role=alert]')?.textContent.includes('1 to 200'));
    assert.equal(await title.evaluate(input=>input===document.activeElement),true);
    assert.equal((await creations()).length,before,'Invalid names must not send a creation request');
  }
  await title.fill(' Test package ');await version.fill(' v1.4.1-rc.1 ');await submit.click();
  await dialog.waitFor({state:'detached'});
  const requests=await creations();assert.equal(requests.length,before+1);
  assert.deepEqual(JSON.parse(requests.at(-1)[2]),{version:'1.4.1-rc.1',title:'Test package',baselineCandidateId:'base1',sourceRevision:'w1',versions:['v1','v2']});
  await page.getByRole('tab',{name:'Contents',exact:true}).waitFor();
  await page.locator('.primary-nav').getByRole('button',{name:'Packaging',exact:true}).click();
  await page.getByRole('heading',{name:'Packaging',exact:true}).waitFor();
  console.log('PASS: package version/name validation, preserved selections, no invalid POST, and corrected creation with normalized metadata.');
}
