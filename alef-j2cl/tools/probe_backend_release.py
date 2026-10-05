"""Read-only native release-evidence compatibility probe; no model publication."""
import json, subprocess, pathlib
backend=pathlib.Path('/mnt/c/Users/niekh/Documents/ChatGPT/ALEFBackend')
output=pathlib.Path('/mnt/c/Codex/alef-j2cl/out')
with (output/'backend-release-worker.log').open('w') as log:
    worker=subprocess.Popen(['python3',str(backend/'tools/mps-runtime.py'),'--idea','--no-compile','--main','nl.alef.backend.runtime.mps.RuntimeWorker','--state',str(pathlib.Path.home()/'.cache/alef-backend/release-probe')],stdin=subprocess.PIPE,stdout=subprocess.PIPE,stderr=log,text=True)
    def receive():
        while True:
            line=worker.stdout.readline()
            if not line: raise RuntimeError('Native worker exited')
            if line.startswith('{'): return json.loads(line)
    def rpc(command,**arguments):
        worker.stdin.write(json.dumps(dict(id=command,command=command,**arguments))+'\n');worker.stdin.flush()
        reply=receive()
        if not reply.get('ok'): raise RuntimeError(reply)
        return reply['result']
    try:
        receive()
        rpc('load',revision='read-only-compatibility',models=json.loads((backend/'.local/corpus.json').read_text()))
        evidence=rpc('releaseEvidence')
        (output/'backend-release-evidence.json').write_text(json.dumps(evidence,indent=2))
        print(json.dumps({k:v for k,v in evidence.items() if k!='standardModels'},indent=2),flush=True)
    finally:
        worker.stdin.close();worker.wait(timeout=30)
