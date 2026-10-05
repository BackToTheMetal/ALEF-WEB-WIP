#!/usr/bin/env python3
"""Real ALEF graphs with explicit synthetic edge cases through native persistence.
Never edits source-backed models. Output file-per-root corpus is an MPS-native
layout conversion of the existing six real persisted models, not a replacement.
"""
import copy,json,os,queue,signal,subprocess,threading,time,uuid
from pathlib import Path
from xml.etree import ElementTree as ET
ROOT=Path(__file__).resolve().parents[1];BACKEND=Path('/mnt/c/Users/niekh/Documents/ChatGPT/ALEFBackend')
state=Path.home()/'.cache/alef-backend/reference-edges';state.mkdir(parents=True,exist_ok=True);responses=queue.Queue()
def nodes(root):
    yield root
    for child in root['children']:yield from nodes(child['node'])
def semantic(graph):
    result={}
    for m in graph['models']:
        for root in m['roots']:
            value=copy.deepcopy(root)
            for n in nodes(value):
                for ref in n['references']:
                    ref.pop('resolvedModel',None);ref.pop('resolvedNodeId',None)
            result[(m['modelId'],root['id'])]=value
    return result
with (ROOT/'out/backend-native-edges-worker.log').open('w') as log:
    worker=subprocess.Popen(['python3',str(BACKEND/'tools/mps-runtime.py'),'--idea','--no-compile','--main','nl.alef.backend.runtime.mps.RuntimeWorker','--state',str(state)],stdin=subprocess.PIPE,stdout=subprocess.PIPE,stderr=log,text=True,bufsize=1,start_new_session=True)
    def reader():
        for line in worker.stdout:
            if line.startswith('{'):responses.put(json.loads(line))
        responses.put({'eof':True})
    threading.Thread(target=reader,daemon=True).start()
    def call(command,**args):
        worker.stdin.write(json.dumps(dict(id=str(uuid.uuid4()),command=command,**args))+'\n');worker.stdin.flush();reply=responses.get(timeout=120)
        assert reply.get('ok'),reply
        return reply['result']
    try:
        assert responses.get(timeout=90).get('event')=='worker.ready'
        corpus=json.loads((BACKEND/'.local/corpus.json').read_text());original=call('load',revision='real-source',models=corpus)
        converted=call('export',layout='FILE_PER_ROOT_XML9');assert all('.model' in m['nativeStreams'] for m in converted)
        file=ROOT/'out/backend-native-file-per-root-corpus.json';file.write_text(json.dumps(converted))
        loaded=call('load',revision='file-per-root',models=converted);assert semantic(loaded)==semantic(original)
        assert {m['modelId']:m['contextDigest'] for m in loaded['models']}=={m['modelId']:m['contextDigest'] for m in original['models']},'Imports, language versions and model metadata preserved'
        assert any(abs(int(n['id']))>9007199254740991 for m in original['models'] for r in m['roots'] for n in nodes(r)),'Real persisted corpus includes full 64-bit node identities'
        assert all('nativeStreams' in m for m in loaded['models']);assert len(loaded['standardModels'])==5
        selected=next((m,r,n,ref) for m in loaded['models'] for r in m['roots'] for n in nodes(r) for ref in n['references'] if ref['kind']=='STATIC' and ref.get('nodeId') and ref.get('resolveInfo'))
        model,root,source,edge=selected;changed=copy.deepcopy(root)
        changedSource=next(n for n in nodes(changed) if n['id']==source['id']);dynamic=next(r for r in changedSource['references'] if r['role']==edge['role'])
        dynamic.update(kind='DYNAMIC',model=None,nodeId=None)
        operation=str(uuid.uuid4());candidate=call('propose',expectedRevision='file-per-root',operationId=operation,roots=[dict(moduleId=model['moduleId'],modelId=model['modelId'],rootId=root['id'],root=changed)])
        assert candidate['validation']['engineFailures']==0
        active=call('activate',operationId=operation,revision='dynamic');roundtrip=call('load',revision='dynamic-reload',models=active['models']);assert semantic(active)==semantic(roundtrip)
        dynamicEdges=[r for m in roundtrip['models'] for root in m['roots'] for n in nodes(root) for r in n['references'] if r['kind']=='DYNAMIC'];assert dynamicEdges and dynamicEdges[0]['resolveInfo']==edge['resolveInfo']
        # Deliberately unresolved STATIC edge with a 64-bit identity beyond JS safe integers.
        baseline=call('load',revision='unresolved-base',models=converted);changed=copy.deepcopy(root);sourceChanged=next(n for n in nodes(changed) if n['id']==source['id']);missing=next(r for r in sourceChanged['references'] if r['role']==edge['role']);missing['nodeId']='9007199254740993';missing['resolveInfo']='Synthetic unresolved target'
        operation=str(uuid.uuid4());candidate=call('propose',expectedRevision='unresolved-base',operationId=operation,roots=[dict(moduleId=model['moduleId'],modelId=model['modelId'],rootId=root['id'],root=changed)])
        active=call('activate',operationId=operation,revision='unresolved');again=call('load',revision='unresolved-reload',models=active['models']);assert semantic(active)==semantic(again)
        assert any(ref.get('nodeId')=='9007199254740993' for m in again['models'] for r in m['roots'] for n in nodes(r) for ref in n['references'])
        report={'status':'PASS','realModels':len(corpus),'realNodes':original['nodeCount'],'standardModels':len(loaded['standardModels']),'filePerRootStreams':sum(len(m['nativeStreams']) for m in converted),'dynamicResolved':dynamicEdges[0].get('resolvedNodeId') is not None,'cases':['native file-per-root conversion/load/save/reload','static and dynamic associations','unresolved 64-bit association identity','global containment order','five native standard models'],'syntheticEdgeCases':True}
        (ROOT/'out/backend-native-edges.json').write_text(json.dumps(report,indent=2));print(json.dumps(report))
    finally:
        worker.stdin.close()
        try:worker.wait(15)
        except subprocess.TimeoutExpired:os.killpg(worker.pid,signal.SIGTERM);worker.wait(10)
