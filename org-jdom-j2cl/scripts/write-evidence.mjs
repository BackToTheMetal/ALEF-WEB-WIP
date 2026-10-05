import fs from 'node:fs';
import {createHash} from 'node:crypto';
const hash=file=>createHash('sha256').update(fs.readFileSync(file)).digest('hex');
const upstream=JSON.parse(fs.readFileSync('compatibility/upstream.json','utf8'));
for(const file of upstream.files)if(hash('out/upstream/'+file.path)!==file.sha256)throw new Error(`Upstream source hash mismatch: ${file.path}`);
const imports=fs.readFileSync('analysis/mps-jdom-imports.txt','utf8').trim().split(/\r?\n/);
const files=[...new Set(imports.map(line=>line.split(':')[0]))].sort();
const consumers=JSON.parse(fs.readFileSync('compatibility/mps-consumers.json','utf8'));
const bytecode=fs.readFileSync('out/mps-consumer-bytecode.txt','utf8');
const calls=[...new Set([...bytecode.matchAll(/(?:InterfaceMethodref|Methodref)\s+.*\/\/ (org\/jdom\/[^\r\n]+)/g)].map(match=>match[1]))].sort();
if(calls.length===0)throw new Error('Missing resolved MPS consumer calls');
const usage={revision:consumers.revision,method:'Textual inventory plus executable consumer source probes; bytecode identifies resolved JDOM calls from the imported MPS classes.',
  limitation:'Not a whole-program reachability analysis. No unknown caller is labeled unused.',resolvedProbeCalls:calls,
  consumers:files.map(path=>({path,status:consumers.consumers.some(c=>c.source===path)?'executable-browser-probe':'requires-integration-audit'}))};
fs.writeFileSync('compatibility/mps-jdom-usage.json',JSON.stringify(usage,null,2)+'\n');
const fixtureDir='tests/fixtures/mps-2025.3';
const provenance={producer:'Installed JetBrains MPS 2025.3, MPS-253.28294.432, ModelWriter9; synthetic model created through MPS Java APIs',
  generator:'tests/reference/MpsFixtureGenerator.java',scope:'One property-bearing root, node/concept identity, metadata, single-file XML9 and file-per-root header/root documents; not a complete persistence corpus',
  streams:{'stream-0.xml':'1234.mpsr','stream-1.xml':'.model'},
  files:fs.readdirSync(fixtureDir).filter(f=>f.endsWith('.xml')).map(path=>({path,sha256:hash(fixtureDir+'/'+path)}))};
fs.writeFileSync(fixtureDir+'/provenance.json',JSON.stringify(provenance,null,2)+'\n');
console.log(`Verified ${upstream.files.length} upstream hashes; recorded ${calls.length} resolved MPS JDOM calls and ${files.length} consumer audit statuses.`);
