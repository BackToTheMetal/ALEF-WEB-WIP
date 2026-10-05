// Save completes by acknowledging a revision, without navigating the page.
export async function saveWorkspace(page){
  await page.evaluate(()=>{
    window.saveTestCompleted=false;
    document.addEventListener('alef-workspace-saved',()=>{window.saveTestCompleted=true;},{once:true});
  });
  await page.getByRole('button',{name:'Save',exact:true}).click();
  await page.waitForFunction(()=>window.saveTestCompleted,null,{timeout:90000});
}
