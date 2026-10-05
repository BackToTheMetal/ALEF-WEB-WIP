import fs from 'node:fs';
import path from 'node:path';
import assert from 'node:assert/strict';
import {randomUUID} from 'node:crypto';
import {connect,output} from './workspace-example-api.mjs';

assert.equal(process.argv[2],'--apply','Use --apply only for the authorized workspace replacement.');
const proposal=JSON.parse(fs.readFileSync(path.join(output,'proposed.json')));
const evidence=JSON.parse(fs.readFileSync(path.join(output,'validation.json')));
assert.equal(evidence.passed,true,'Native validation and every service test must pass before publication.');
assert.equal(evidence.tests.length,5);
const api=await connect();let client,held=[];
try{
  const before=await api.snapshot();
  assert.equal(before.graph.revision,proposal.basedOnRevision,'Workspace changed after backup; inspect instead of overwriting it.');
  client=await api.request('POST','/api/editing-clients');
  held=(await api.request('POST','/api/v1/leases',{editingClientId:client.id,scopes:proposal.models.map(m=>({kind:'MODEL',key:m.moduleId+'/'+m.modelId}))})).leases;
  const fresh=await api.snapshot();assert.equal(fresh.graph.revision,proposal.basedOnRevision);
  const operationId=randomUUID();
  const body={editingClientId:client.id,baseSnapshotId:fresh.manifest.snapshotId,languageFingerprint:fresh.manifest.languageFingerprint,reason:'Vervang proefmodellen door het complete sportclubvoorbeeld: objectmodel, contributieregels, service en vijf servicetests.',leases:held.map(l=>l.token),rootUpdates:proposal.rootUpdates,structuralOperations:proposal.structuralOperations};
  fs.writeFileSync(path.join(output,'publication-request.json'),JSON.stringify({operationId,body},null,2));
  let result=await api.request('POST','/api/v1/changes',body,{'Idempotency-Key':operationId});
  for(let i=0;result.state!=='ACTIVE'&&i<90;i++){await new Promise(resolve=>setTimeout(resolve,1000));result=await api.request('GET','/api/v1/operations/'+operationId);}
  assert.equal(result.state,'ACTIVE',JSON.stringify(result));
  fs.writeFileSync(path.join(output,'publication.json'),JSON.stringify(result,null,2));
  const after=await api.snapshot();fs.writeFileSync(path.join(output,'after.json'),JSON.stringify(after,null,2));
  const actual=after.graph.models.filter(m=>!m.readOnly).flatMap(m=>m.roots);
  assert.equal(actual.length,4);
  for(const update of proposal.rootUpdates.filter(u=>u.root)){
    const saved=actual.find(r=>r.rootId===update.rootId);assert.ok(saved);
    assert.deepEqual(saved.properties,update.root.properties);
    assert.equal(saved.children.length,update.root.children.length);
  }
  const packages=await api.request('GET','/api/v1/packages');fs.writeFileSync(path.join(output,'packages-after.json'),JSON.stringify(packages,null,2));
  console.log(JSON.stringify({state:result.state,revision:after.graph.revision,roots:actual.map(r=>({rootId:r.rootId,concept:r.conceptName,properties:r.properties})),packages:packages.map(p=>({id:p.id,revision:p.revision,finalized:p.finalized}))},null,2));
}finally{
  for(const lease of held)await api.request('POST','/api/v1/leases/release',{editingClientId:client.id,token:lease.token}).catch(e=>console.error('Release:',e.message));
  if(client)await api.request('POST','/api/editing-clients/close',{editingClientId:client.id}).catch(()=>{});
  await api.browser.close();
}
