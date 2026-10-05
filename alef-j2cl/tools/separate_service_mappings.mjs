import fs from 'node:fs';
import assert from 'node:assert/strict';
import {randomUUID} from 'node:crypto';
import {connect} from './workspace-example-api.mjs';

assert.equal(process.argv[2],'--apply','Use --apply for the authorized mapping extraction.');
const directory='out/separate-service-mappings';fs.mkdirSync(directory,{recursive:true});
const NAME='ceab5195-25ea-4f22-9b92-103b95ca8c0c/1169194658468/1169194664001';
const PACKAGE='ceab5195-25ea-4f22-9b92-103b95ca8c0c/1133920641626/1193676396447';
const kinds=new Set(['servicespraak.structure.Invoerberichtmapping','servicespraak.structure.Uitvoerberichtmapping']);
const nodes=n=>[n,...(n.children||[]).flatMap(c=>nodes(c.node))];
const clean=n=>({id:n.id,concept:n.concept,conceptName:n.conceptName,properties:n.properties,references:n.references,children:n.children.map(c=>({...c,node:clean(c.node)}))});
export function separate(models,serviceId,reservations){
 const model=models.find(m=>m.roots.some(r=>r.id===serviceId)),service=model.roots.find(r=>r.id===serviceId);
 const extracted=service.children.filter(c=>kinds.has(c.node.conceptName)).map(c=>c.node);
 assert.equal(extracted.length,2,'Exactly two embedded mappings must be present.');
 const remap=new Map();
 for(const mapping of extracted){const reservation=reservations.find(r=>r.name===mapping.properties[NAME]);assert.ok(reservation);remap.set(mapping.id,reservation.nodeId);mapping.id=reservation.nodeId;if(service.properties[PACKAGE])mapping.properties[PACKAGE]=service.properties[PACKAGE];else delete mapping.properties[PACKAGE];}
 service.children=service.children.filter(c=>!kinds.has(c.node.conceptName));model.roots.push(...extracted);
 for(const m of models)for(const root of m.roots)for(const node of nodes(root))for(const ref of node.references||[])if((ref.model||m.reference)===model.reference&&remap.has(ref.nodeId))ref.nodeId=remap.get(ref.nodeId);
 return {model,service,extracted,remap};
}

const api=await connect();let client,held=[];
try{
 const before=await api.snapshot();
 const model=before.graph.models.find(m=>m.roots.some(r=>r.properties[NAME]==='Contributieservice'));
 assert.ok(model&&!model.readOnly);const original=model.roots.find(r=>r.properties[NAME]==='Contributieservice');
 const mappings=original.children.filter(c=>kinds.has(c.node.conceptName));assert.equal(mappings.length,2);
 const modelKey=model.moduleId+'/'+model.modelId;
 const beforeFile=directory+'/before.json';assert.ok(!fs.existsSync(beforeFile),'A previous extraction exists; inspect it before repeating.');
 fs.writeFileSync(beforeFile,JSON.stringify(before,null,2));
 const packagesBefore=await api.request('GET','/api/v1/packages');fs.writeFileSync(directory+'/packages-before.json',JSON.stringify(packagesBefore,null,2));
 client=await api.request('POST','/api/editing-clients');
 held=(await api.request('POST','/api/v1/leases',{editingClientId:client.id,scopes:[{kind:'MODEL',key:modelKey}]})).leases;
 const fresh=await api.snapshot();assert.equal(fresh.graph.revision,before.graph.revision,'Workspace changed; do not overwrite newer content.');
 const reservations=[];
 for(const {node} of mappings){const reservation=await api.request('POST','/api/v1/root-reservations',{editingClientId:client.id,modelKey},{'Idempotency-Key':randomUUID()});reservations.push({...reservation,name:node.properties[NAME],oldNodeId:node.id});}
 fs.writeFileSync(directory+'/reservations.json',JSON.stringify(reservations,null,2));
 const proposal=structuredClone(fresh.graph),result=separate(proposal.models,original.id,reservations);
 // Only the service and the two extracted roots change. Mapping child identities,
 // tests, object model, rules, field values and reference targets stay intact.
 for(const m of fresh.graph.models)for(const root of m.roots){if(root.rootId===original.rootId)continue;assert.deepEqual(proposal.models.find(p=>p.reference===m.reference).roots.find(r=>r.id===root.id),root);}
 for(const {node} of mappings){const expected=clean(structuredClone(node));expected.id=result.remap.get(node.id);assert.deepEqual(clean(result.extracted.find(r=>r.id===expected.id)),expected);}
 fs.writeFileSync(directory+'/proposal.json',JSON.stringify(proposal,null,2));
 const rootUpdates=[{rootId:original.rootId,expectedVersion:original.version,expectedModelContext:model.contextDigest,root:clean(result.service)},...result.extracted.map(mapping=>({rootId:reservations.find(r=>r.nodeId===mapping.id).rootId,expectedModelContext:model.contextDigest,root:clean(mapping)}))];
 const structuralOperations=reservations.map(r=>({type:'CREATE_ROOT',rootId:r.rootId}));
 const operationId=randomUUID(),body={editingClientId:client.id,baseSnapshotId:fresh.manifest.snapshotId,languageFingerprint:fresh.manifest.languageFingerprint,reason:'Zet de invoer- en uitvoermapping van Contributieservice in aparte roots binnen hetzelfde Service-model.',leases:held.map(l=>l.token),rootUpdates,structuralOperations};
 fs.writeFileSync(directory+'/publication-request.json',JSON.stringify({operationId,body},null,2));
 let published=await api.request('POST','/api/v1/changes',body,{'Idempotency-Key':operationId});
 for(let i=0;published.state!=='ACTIVE'&&i<90;i++){await new Promise(resolve=>setTimeout(resolve,1000));published=await api.request('GET','/api/v1/operations/'+operationId);}
 assert.equal(published.state,'ACTIVE',JSON.stringify(published));fs.writeFileSync(directory+'/publication.json',JSON.stringify(published,null,2));
 const after=await api.snapshot();fs.writeFileSync(directory+'/after.json',JSON.stringify(after,null,2));
 for(const m of proposal.models){const saved=after.graph.models.find(p=>p.reference===m.reference);assert.ok(saved);assert.equal(saved.roots.length,m.roots.length);for(const expected of m.roots)assert.deepEqual(clean(saved.roots.find(r=>r.id===expected.id)),clean(expected));}
 const packagesAfter=await api.request('GET','/api/v1/packages');fs.writeFileSync(directory+'/packages-after.json',JSON.stringify(packagesAfter,null,2));
 for(const pkg of packagesBefore.filter(p=>p.finalized)){const saved=packagesAfter.find(p=>p.id===pkg.id);assert.equal(saved.revision,pkg.revision);assert.equal(saved.packageDigest,pkg.packageDigest);}
 // Keep the saved reusable example in the same layout, without copying unrelated
 // workspace edits into the example fixture.
 const examplePath='docs/examples/sportclub/workspace.json',example=JSON.parse(fs.readFileSync(examplePath,'utf8'));
 fs.copyFileSync(examplePath,directory+'/example-before.json');separate(example.models,original.id,reservations);fs.writeFileSync(examplePath,JSON.stringify(example,null,2)+'\n');
 console.log(JSON.stringify({state:published.state,revision:after.graph.revision,model:model.name,files:result.extracted.map(r=>({name:r.properties[NAME],concept:r.conceptName})),service:result.service.properties[NAME],savedRoots:after.graph.models.filter(m=>!m.readOnly).reduce((n,m)=>n+m.roots.length,0)},null,2));
}finally{
 for(const lease of held)await api.request('POST','/api/v1/leases/release',{editingClientId:client.id,token:lease.token}).catch(e=>console.error('Release:',e.message));
 if(client)await api.request('POST','/api/editing-clients/close',{editingClientId:client.id}).catch(()=>{});
 await api.browser.close();
}
