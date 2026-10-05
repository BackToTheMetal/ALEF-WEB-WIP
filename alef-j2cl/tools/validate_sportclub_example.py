"""Validate the example in an isolated native MPS worker; never open live storage."""
import json, queue, subprocess, threading, uuid
from pathlib import Path
from xml.etree import ElementTree as ET

ROOT=Path(__file__).resolve().parents[1]
OUTPUT=ROOT/'out/workspace-sportclub-2026-09-29'
BACKEND=Path('/mnt/c/Users/niekh/Documents/ChatGPT/ALEFBackend')
fixture=json.loads((ROOT/'docs/examples/sportclub/workspace.json').read_text())
responses=queue.Queue()
def nodes(node):
    yield node
    for child in node['children']:yield from nodes(child['node'])
with (OUTPUT/'native-worker.log').open('w') as log:
    worker=subprocess.Popen(['python3',str(BACKEND/'tools/mps-runtime.py'),'--idea','--no-compile','--main','nl.alef.backend.runtime.mps.RuntimeWorker','--state','/tmp/alef-sportclub-'+str(uuid.uuid4())],stdin=subprocess.PIPE,stdout=subprocess.PIPE,stderr=log,text=True,bufsize=1)
    def reader():
        for line in worker.stdout:
            if line.startswith('{'):responses.put(json.loads(line))
        responses.put({'eof':True})
    threading.Thread(target=reader,daemon=True).start()
    def call(command,**fields):
        worker.stdin.write(json.dumps(dict(id=str(uuid.uuid4()),command=command,**fields))+'\n');worker.stdin.flush()
        response=responses.get(timeout=150)
        if not response.get('ok'):raise RuntimeError(response)
        return response['result']
    try:
        assert responses.get(timeout=120).get('event')=='worker.ready'
        corpus=[];proposals=[]
        for model in fixture['models']:
            xml=ET.Element('model',ref=model['reference']);ET.SubElement(xml,'persistence',version='9')
            languages=ET.SubElement(xml,'languages');ET.SubElement(languages,'devkit',ref='d07fa9c5-678d-4a9b-9eaf-b1b8c569b820(alef.devkit)')
            imports=ET.SubElement(xml,'imports')
            targets=sorted({r['model'] for root in model['roots'] for node in nodes(root) for r in node['references'] if r.get('model') and r['model']!=model['reference']})
            for i,target in enumerate(targets):ET.SubElement(imports,'import',index=str(i),ref=target)
            document={key:model[key] for key in ['moduleId','moduleName','modelId','name']};document['nativeXml']=ET.tostring(xml,encoding='unicode');corpus.append(document)
            proposals.extend(dict(moduleId=model['moduleId'],modelId=model['modelId'],rootId=root['id'],mode='CREATE',root=root) for root in model['roots'])
        call('load',models=corpus,revision='sportclub-example')
        operation=str(uuid.uuid4());call('propose',expectedRevision='sportclub-example',operationId=operation,roots=proposals)
        active=call('activate',operationId=operation,revision='sportclub-validated')
        evidence=call('releaseEvidence')
        (OUTPUT/'validation.json').write_text(json.dumps(evidence,indent=2))
        assert evidence['sourceUnchanged'] and evidence['engineFailures']==0,evidence
        assert not any(d['severity']=='ERROR' for d in evidence['diagnostics']),evidence['diagnostics']
        assert len(evidence['tests'])==len(fixture['cases']),evidence
        assert all(t['status']=='PASSED' for t in evidence['tests']),evidence['tests']
        (OUTPUT/'validated-native-models.json').write_text(json.dumps(active['models'],indent=2))
        print(json.dumps({'result':'PASS','tests':evidence['tests'],'diagnostics':evidence['diagnostics']},indent=2),flush=True)
    finally:
        worker.stdin.close()
        try:worker.wait(20)
        except subprocess.TimeoutExpired:worker.terminate();worker.wait(10)
