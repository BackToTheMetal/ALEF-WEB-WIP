#!/usr/bin/env python3
"""Synthetic scaling corpus derived from real native ALEF XML9 exports.

This is NOT a production corpus or independent language compatibility evidence.
The original four-profile sample stays intact. Added ObjectModels contain copied
native ObjectType/fact/role structures, unique IDs, and remapped local references.
Native MPS must subsequently load/check/serialize these inputs.
"""
import argparse,copy,hashlib,json,uuid
from pathlib import Path
from xml.etree import ElementTree as ET

ALPHABET='0123456789abcdefghijklmnopqrstuvwxyz$_ABCDEFGHIJKLMNOPQRSTUVWXYZ'
def node_id(value):
    encoded=''
    while value:encoded=ALPHABET[value&63]+encoded;value>>=6
    return encoded or '0'
def word(value):
    result=''
    while True:
        result=chr(97+value%26)+result;value=value//26-1
        if value<0:return result

def generate(source,total):
    corpus=json.loads(source.read_text(encoding='utf-8-sig'))
    originals=sum(sum(1 for _ in ET.fromstring(m['nativeXml']).iter('node')) for m in corpus)
    if total<originals:raise ValueError('Requested total cannot omit the original real sample')
    template=next(m for m in corpus if m['name'].endswith('.Gegevens'))
    document=ET.fromstring(template['nativeXml']);root=document.find('node')
    children=list(root.findall('node'));remaining=total-originals;serial=8_100_000_000_000_000_000
    root_number=0;model_number=0;added_roots=[];references=0
    while remaining:
        metadata=copy.deepcopy(template);model=copy.deepcopy(document)
        for old in model.findall('node'):model.remove(old)
        model_number+=1
        model_id='r:'+str(uuid.uuid5(uuid.NAMESPACE_URL,f'alef-capacity-v1/{total}/{model_number}'))
        name='capacity.synthetic.'+word(model_number)
        metadata.update(modelId=model_id,name=name);model.set('ref',model_id+'('+name+')')
        metadata['reference']=model.attrib['ref']
        for _ in range(10):
            if not remaining:break
            size=min(1000,remaining);remaining-=size;root_number+=1
            serial+=1;root_id=serial
            created=ET.SubElement(model,'node',concept=root.attrib['concept'],id=node_id(root_id))
            ET.SubElement(created,'property',role='TrG5h',value='Capacity '+word(root_number))
            added_roots.append(dict(modelId=model_id,nodeId=str(root_id),nodes=size))
            capacity=size-1;group=0
            while capacity>=16:
                group+=1;suffix=' '+word(root_number)+' '+word(group)
                clones=[copy.deepcopy(c) for c in children];mapping={};names={}
                for clone in clones:
                    for node in clone.iter('node'):
                        serial+=1;mapping[node.attrib['id']]=node_id(serial);node.set('id',node_id(serial))
                        for prop in node.findall('property'):
                            if prop.attrib['role']=='TrG5h':
                                original=prop.attrib['value'];names[original]=original+suffix;prop.set('value',original+suffix)
                for clone in clones:
                    for ref in clone.iter('ref'):
                        if ref.get('node') in mapping:ref.set('node',mapping[ref.attrib['node']])
                        if ref.get('resolve') in names:ref.set('resolve',names[ref.attrib['resolve']])
                        references+=1
                    created.append(clone)
                capacity-=16
            # The small remainder is explicit whitespace, never invalid partial types.
            for _ in range(capacity):
                serial+=1;ET.SubElement(created,'node',concept='1uxNW$',id=node_id(serial),role='2bv6Cn')
        metadata['nativeXml']=ET.tostring(model,encoding='unicode');corpus.append(metadata)
    return corpus,dict(kind='SYNTHETIC_NATIVE_DERIVED',requestedNodes=total,originalNodes=originals,
                       models=len(corpus),addedRoots=added_roots,addedReferences=references,
                       generator='alef-capacity-v1',sourceSha256=hashlib.sha256(source.read_bytes()).hexdigest())

if __name__=='__main__':
    parser=argparse.ArgumentParser();parser.add_argument('source',type=Path);parser.add_argument('destination',type=Path);parser.add_argument('--nodes',type=int,required=True);args=parser.parse_args()
    corpus,manifest=generate(args.source,args.nodes);args.destination.parent.mkdir(parents=True,exist_ok=True)
    args.destination.write_text(json.dumps(corpus,separators=(',',':')))
    manifest['bytes']=args.destination.stat().st_size;manifest['sha256']=hashlib.sha256(args.destination.read_bytes()).hexdigest()
    args.destination.with_suffix('.manifest.json').write_text(json.dumps(manifest,indent=2))
    print(json.dumps({k:v for k,v in manifest.items() if k!='addedRoots'}))
