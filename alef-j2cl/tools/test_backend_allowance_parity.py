#!/usr/bin/env python3
"""Import exact browser example graphs into native MPS, then compare Testspraak.
The allowance example is synthetic. Real persisted ALEF compatibility is covered
separately; this test compares the JVM and J2CL semantics of identical inputs.
"""
import json,os,queue,signal,subprocess,sys,threading,time,uuid
from pathlib import Path
from xml.etree import ElementTree as ET
ROOT=Path(__file__).resolve().parents[1];BACKEND=Path('/mnt/c/Users/niekh/Documents/ChatGPT/ALEFBackend')
directory=Path(sys.argv[1]);state=Path.home()/'.cache/alef-backend/allowance-parity';state.mkdir(parents=True,exist_ok=True)
responses=queue.Queue();reports=[]
with (ROOT/'out/backend-allowance-native-worker.log').open('w') as log:
    process=subprocess.Popen(['python3',str(BACKEND/'tools/mps-runtime.py'),'--idea','--no-compile','--main','nl.alef.backend.runtime.mps.RuntimeWorker','--state',str(state)],stdin=subprocess.PIPE,stdout=subprocess.PIPE,stderr=log,text=True,bufsize=1,start_new_session=True)
    def reader():
        try:
            for line in process.stdout:
                if line.startswith('{'):responses.put(json.loads(line))
        finally:responses.put({'eof':True})
    threading.Thread(target=reader,daemon=True).start()
    def call(command,**fields):
        request=dict(id=str(uuid.uuid4()),command=command,**fields);process.stdin.write(json.dumps(request)+'\n');process.stdin.flush();response=responses.get(timeout=90)
        if not response.get('ok'):raise RuntimeError(response)
        return response['result']
    def all_nodes(root):
        yield root
        for child in root['children']:yield from all_nodes(child['node'])
    try:
        assert responses.get(timeout=90).get('event')=='worker.ready'
        fixtures=sorted(directory.glob('chromium-native-case-*.json'));assert len(fixtures)>=5
        for file in fixtures:
            fixture=json.loads(file.read_text());corpus=[];proposals=[]
            for model in fixture['models']:
                xml=ET.Element('model',ref=model['reference']);ET.SubElement(xml,'persistence',version='9');languages=ET.SubElement(xml,'languages');ET.SubElement(languages,'devkit',ref='d07fa9c5-678d-4a9b-9eaf-b1b8c569b820(alef.devkit)')
                imports=ET.SubElement(xml,'imports');targets=sorted({ref['model'] for root in model['roots'] for node in all_nodes(root) for ref in node['references'] if ref.get('model') and ref['model']!=model['reference']})
                for index,target in enumerate(targets):ET.SubElement(imports,'import',index=str(index),ref=target)
                document={key:model[key] for key in ['moduleId','moduleName','modelId','name']};document['nativeXml']=ET.tostring(xml,encoding='unicode');corpus.append(document)
                for root in model['roots']:proposals.append(dict(moduleId=model['moduleId'],modelId=model['modelId'],rootId=root['id'],mode='CREATE',root=root))
            call('load',models=corpus,revision='browser-fixture');operation=str(uuid.uuid4())
            changed=call('propose',expectedRevision='browser-fixture',operationId=operation,roots=proposals)
            assert changed['validation']['engineFailures']==0,changed['validation']
            assert not any(d['severity']=='ERROR' for d in changed['validation']['diagnostics']),changed['validation']
            active=call('activate',operationId=operation,revision='browser-fixture-imported')
            locations=[(m,n) for m in fixture['models'] for root in m['roots'] for n in all_nodes(root)]
            probes=[next((m,n) for m,n in locations if n['conceptName'].endswith('.Attribuut') and 'geboortedatum' in n['properties'].values()),
                    next((m,n) for m,n in locations if n['conceptName'].endswith('.Haakjes')),
                    next((m,n) for m,n in locations if n['conceptName'].endswith('.DirectInvoerAttribuut')),
                    next((m,n) for m,n in locations if n['conceptName'].endswith('.ElementairTestBerichtVeld'))]
            types=call('types',queries=[dict(node=m['reference']+'/'+n['id'],mode=mode) for (m,n),mode in zip(probes,['INFERRED','INFERRED','MAPPED_SERVICE','MAPPED_TEST'])])
            assert [t['type'] for t in types]==fixture['types'],dict(native=types,browser=fixture['types'])
            native=call('releaseEvidence');expected='PASSED' if fixture['expected']=='PASS' else 'FAILED'
            assert native['sourceUnchanged'] and native['engineFailures']==0,native
            assert len(native['tests'])==1 and native['tests'][0]['status']==expected,native
            assert not any(d['severity']=='ERROR' for d in native['diagnostics']),native
            original=[(n['id'],n['concept'],n['properties'],[c['node']['id'] for c in n['children']],[(r['role'],r['nodeId']) for r in n['references']]) for m in fixture['models'] for root in m['roots'] for n in all_nodes(root)]
            loaded=call('load',models=active['models'],revision='native-roundtrip')
            reloaded=[(n['id'],n['concept'],n['properties'],[c['node']['id'] for c in n['children']],[(r['role'],r['nodeId']) for r in n['references']]) for m in loaded['models'] for root in m['roots'] for n in all_nodes(root)]
            assert reloaded==original,'Native persistence changed browser identities, properties, associations or child order'
            reports.append({'fixture':file.name,'expected':fixture['expected'],'nativeTests':native['tests'],'nativeDiagnostics':native['diagnostics'],'browserDiagnostics':fixture['diagnostics'],'browserTypes':fixture['types'],'nativeTypes':types,'nodes':loaded['nodeCount'],'status':'PASS'})
            print('PASS',file.name,'exact browser graph/native Testspraak',expected,flush=True)
    finally:
        process.stdin.close()
        try:process.wait(15)
        except subprocess.TimeoutExpired:os.killpg(process.pid,signal.SIGTERM);process.wait(10)
        (ROOT/'out/backend-allowance-parity.json').write_text(json.dumps(reports,indent=2))
