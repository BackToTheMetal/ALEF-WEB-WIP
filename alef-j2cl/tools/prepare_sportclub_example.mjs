import fs from 'node:fs';
import path from 'node:path';
import assert from 'node:assert/strict';
import {root,output} from './workspace-example-api.mjs';

const source=JSON.parse(fs.readFileSync(path.join(root,'out/service-test-optimized/chromium-native-case-0.json'))).models[0];
const before=JSON.parse(fs.readFileSync(path.join(output,'before.json')));
const models=before.graph.models.filter(m=>!m.readOnly);
const NAME='ceab5195-25ea-4f22-9b92-103b95ca8c0c/1169194658468/1169194664001';
const PACKAGE='ceab5195-25ea-4f22-9b92-103b95ca8c0c/1133920641626/1193676396447';
const kind=(node,name)=>node.conceptName.endsWith('.'+name);
function nodes(node){return[node,...node.children.flatMap(c=>nodes(c.node))];}
const find=(node,name)=>nodes(node).find(n=>kind(n,name));
const prop=(node,end,value)=>{const key=Object.keys(node.properties).find(k=>k.endsWith('/'+end));assert.ok(key,end);node.properties[key]=String(value);};
let counter=900029000000000000n;
function clone(node){const copy=structuredClone(node),ids=new Map();for(const n of nodes(copy)){const id=String(counter++);ids.set(n.id,id);n.id=id;}for(const n of nodes(copy))for(const ref of n.references)if(ids.has(ref.nodeId))ref.nodeId=ids.get(ref.nodeId);return copy;}

const [object, rules, service, tests]=structuredClone(source.roots);
const objectWhitespace=structuredClone(object.children.find(c=>kind(c.node,'Witruimte')));
const ruleWhitespace=structuredClone(rules.children.find(c=>kind(c.node,'Witruimte')));
object.properties[NAME]='Sportclub';
object.children=object.children.filter(c=>kind(c.node,'ObjectType')&&c.node.properties[NAME]==='Persoon');
const member=object.children[0].node;
member.properties[NAME]='Deelnemer';prop(member,'8989128614612178055','deelnemers');
member.children=member.children.filter(c=>c.node.properties[NAME]!=='geboortedatum');
const contribution=member.children.find(c=>c.node.properties[NAME]==='toeslag').node;contribution.properties[NAME]='jaarcontributie';
object.children.push(objectWhitespace);

rules.properties[NAME]='Contributieregels';delete rules.properties[PACKAGE];
const adult=find(rules,'Regel');adult.properties[NAME]='Volwassenencontributie';
const assignment=find(adult,'Gelijkstelling'),literal=clone(find(adult,'NumeriekeLiteral'));
prop(literal,'558527188465081158','125');assignment.children[1].node=literal;
const youth=clone(adult);youth.properties[NAME]='Jeugdcontributie';
prop(find(youth,'Gelijkstelling').children[1].node,'558527188465081158','75');
// MPS enum values include the member identity as well as its readable name.
prop(find(youth,'Vergelijking'),'6747529342263127133','5brrC35IcXt/LT');
const ruleRole=rules.children[0].role;rules.children=[{role:ruleRole,node:youth},{role:ruleRole,node:adult},ruleWhitespace];

delete service.properties[PACKAGE];service.properties[NAME]='Contributieservice';
const replacements={hhd:'sportclub',toeslag:'contributie',TS:'SC',ts:'sc',Toeslag:'Contributie',ToeslagRequest:'ContributieAanvraag',ToeslagResponse:'ContributieAntwoord',BerekenToeslag:'BerekenContributie',berekenToeslag:'berekenContributie',BT:'BC',persoon:'deelnemer',Aanvraag:'Deelnemergegevens',Besluit:'Contributieberekening'};
for(const n of nodes(service))for(const[k,v]of Object.entries(n.properties))n.properties[k]=replacements[v]||v.replace('https://example.org/toeslag','https://example.org/sportclub').replace('berekenToeslag','berekenContributie');
prop(service,'8879350159213016767','scb');prop(service,'8879350159213016781','leden');
find(service,'DirectUitvoerAttribuut').properties[NAME]='jaarcontributie';
const mapping=find(service,'Invoerberichtmapping'),nameField=clone(find(service,'DirectInvoerAttribuut'));
nameField.properties[NAME]='naam';nameField.references[0].nodeId=member.children.find(c=>c.node.properties[NAME]==='naam').node.id;
mapping.children.unshift({role:mapping.children[0].role,node:nameField});

delete tests.properties[PACKAGE];prop(tests,'8803452945676232903','Contributie controleren');
const template=find(tests,'ServiceTest'),testRole=tests.children[0].role;
const cases=[['Noor',10,75,'Jeugdlid van 10 jaar'],['Sam',17,75,'Laatste jeugdjaar: 17 jaar'],['Mila',18,125,'Vanaf de 18e verjaardag'],['Daan',35,125,'Volwassene van 35 jaar'],['Ada',70,125,'Volwassen tarief bij 70 jaar']];
tests.children=cases.map(([name,age,expected,title])=>{const test=clone(template);test.properties[NAME]=title;
  const input=find(test,'ElementairTestBerichtVeld'),expectedField=find(test,'ElementaireVeldVerwachting');
  prop(input,'8803452945675844818',age);prop(expectedField,'6404605531307076166',expected);
  const inputName=clone(input);prop(inputName,'8803452945675844818',name);inputName.references[0].nodeId=nameField.id;
  const inputs=find(test,'InvoerSubBericht');inputs.children.unshift({role:inputs.children[0].role,node:inputName});
  return{role:testRole,node:test};
});

const slots=[['.Gegevens','ObjectModel',object],['.Regels','Regelgroep',rules],['.Service','Service',service],['.Service','ServiceTestSet',tests]];
const rootIds=new Map(),locations=new Map(),updates=[];
for(const[suffix,type,content]of slots){const model=models.find(m=>m.name.endsWith(suffix)),original=model.roots.find(r=>kind(r,type));assert.ok(original);rootIds.set(content.id,original.id);content.id=original.id;updates.push({modelKey:model.moduleId+'/'+model.modelId,model,original,content});}
for(const u of updates)for(const n of nodes(u.content))locations.set(n.id,{node:n,model:u.model});
for(const u of updates)for(const n of nodes(u.content))for(const ref of n.references){if(rootIds.has(ref.nodeId))ref.nodeId=rootIds.get(ref.nodeId);const target=locations.get(ref.nodeId);assert.ok(target,'Unresolved example reference '+ref.nodeId);ref.model=target.model.reference;ref.resolveInfo=target.node.properties[NAME]||ref.resolveInfo;}
const finalModels=models.map(model=>({...model,roots:updates.filter(u=>u.model.reference===model.reference).map(u=>u.content)}));
const proposal={description:'Sportclub: jeugd tot 18 jaar betaalt 75 euro per jaar, volwassenen 125 euro. Fictieve voorbeeldtarieven.',basedOnRevision:before.graph.revision,cases:cases.map(([name,age,expected,title])=>({name,age,expected,title})),models:finalModels,rootUpdates:models.flatMap(m=>m.roots.map(r=>({rootId:r.rootId,expectedVersion:r.version,expectedModelContext:m.contextDigest,root:updates.find(u=>u.original.rootId===r.rootId)?.content||null}))),structuralOperations:models.flatMap(m=>[{type:'UPDATE_MODEL_CONTEXT',modelKey:m.moduleId+'/'+m.modelId,expectedModelContext:m.contextDigest},...m.roots.filter(r=>!updates.some(u=>u.original.rootId===r.rootId)).map(r=>({type:'DELETE_ROOT',rootId:r.rootId}))])};
fs.writeFileSync(path.join(output,'proposed.json'),JSON.stringify(proposal,null,2));
const destination=path.join(root,'docs/examples/sportclub');fs.mkdirSync(destination,{recursive:true});
fs.writeFileSync(path.join(destination,'workspace.json'),JSON.stringify({description:proposal.description,cases:proposal.cases,models:finalModels.filter(m=>m.roots.length).map(({reference,moduleId,moduleName,modelId,name,roots})=>({reference,moduleId,moduleName,modelId,name,roots}))},null,2));
console.log('Prepared four connected roots, two rules, five service tests. No workspace writes.');
