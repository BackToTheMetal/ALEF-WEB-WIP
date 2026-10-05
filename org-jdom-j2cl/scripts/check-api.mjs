import fs from 'node:fs';
function read(file) {
  const types=new Map();
  for(const line of fs.readFileSync(file,'utf8').trim().split(/\r?\n/)) {
    const [name,kind,signature]=line.split('\t');
    if(!types.has(name))types.set(name,{declaration:null,members:[]});
    if(kind==='TYPE')types.get(name).declaration=signature;
    if(kind==='MEMBER')types.get(name).members.push(signature);
  }
  return types;
}
const reference=read('out/api-reference.tsv'),portable=read('out/api-portable.tsv');
const exclusions=name=>name==='org.jdom.input.DOMBuilder'||name.startsWith('org.jdom.xpath.')&&!['org.jdom.xpath.XPathFactory','org.jdom.xpath.XPathExpression','org.jdom.xpath.XPathDiagnostic'].includes(name);
const unexpected=[],types=[];
let memberCount=0;
for(const [name,original] of reference) {
  const current=portable.get(name);
  const excluded=exclusions(name);
  if(!current?.declaration&&!excluded)unexpected.push(`Missing type ${name}`);
  if(current?.declaration&&original.declaration!==current.declaration)unexpected.push(`Type declaration/ancestry changed: ${name}\n${original.declaration}\n${current.declaration}`);
  const members=original.members.map(signature=>{
    memberCount++;
    let status=current?.members.includes(signature)?'provided':'missing';
    let reason;
    if(status==='missing'&&excluded){status='excluded';reason='DOM/Jaxen/legacy XPath engines are outside the MPS browser profile';}
    if(status==='missing'&&/\bbuild\(java\.(io.File|net.URL)\)/.test(signature)){status='excluded';reason='File and URL are not owned by the pinned J2CL JRE; use Reader/InputStream/InputSource';}
    if(status==='provided'&&name==='org.jdom.xpath.XPathFactory'&&/\.(compile|elements)\(/.test(signature)){status='stub';reason='Default XPath factory is a stub; custom subclasses may implement evaluation; maintenance tool excluded';}
    if(status==='provided'&&(signature.includes('.clone(')||signature.includes('setEncoding(')||signature.includes('build(java.lang.String)')||name==='org.jdom.input.sax.XMLReaders'))status='profile-limited';
    if(['provided','profile-limited'].includes(status)&&/\bjava\.[\w.$]+\.[\w$]+\(/.test(signature)) {
      status='jre-owned';reason='Inherited Java member: availability belongs to the pinned J2CL JRE, not this replacement library';
      if(/java\.lang\.Object\.(wait|notify|notifyAll)\(/.test(signature)) {
        status='excluded';reason='Inherited on the reference JVM but absent from the pinned J2CL java.lang.Object source';
      }
    }
    if(status==='missing')unexpected.push(signature);
    return {signature,status,...(reason?{reason}:{})};
  });
  types.push({name,referenceDeclaration:original.declaration,portableDeclaration:current?.declaration??null,members,
    additions:current?.members.filter(s=>!original.members.includes(s))??[]});
}
const manifest={baseline:JSON.parse(fs.readFileSync('compatibility/upstream.json','utf8')).commit,
  method:'JVM reflection over matched source, effective public members and declared protected hooks; generic types and throws clauses retained. This is declaration compatibility, not proof every member behavior is exercised.',
  types:types.length,members:memberCount,unexpected,entries:types};
fs.writeFileSync('compatibility/jdom-api.json',JSON.stringify(manifest,null,2)+'\n');
if(unexpected.length)throw new Error(`Unclassified API differences:\n${unexpected.join('\n')}`);
console.log(`API gate passed: ${types.length} types, ${memberCount} effective member entries; explicit exclusions and stubs classified.`);
