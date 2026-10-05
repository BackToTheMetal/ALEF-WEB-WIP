#!/usr/bin/env python3
"""Isolated full application measurements; uses real DB, OIDC and MPS worker."""
import json,os,signal,socket,subprocess,sys,threading,time,urllib.request
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];BACKEND=Path('/mnt/c/Users/niekh/Documents/ChatGPT/ALEFBackend');databases=Path(sys.argv[1])
fixtures=Path((ROOT/'out/backend-capacity-state.txt').read_text().strip());state=Path.home()/'.cache/alef-backend/capacity-application'/str(int(time.time()));state.mkdir(parents=True)
identity=json.loads((BACKEND/'.local/development-identities.json').read_text(encoding='utf-8-sig'));java=Path.home()/'.cache/alef-j2cl/96dd99ff70cb/alef/target/jbr-21.0.6-b631.39-linux-x64/bin/java'
env=dict(os.environ,ALEF_OIDC_ISSUER='http://localhost:8180/realms/alef',ALEF_OIDC_CLIENT_ID='alef-browser',ALEF_OIDC_CLIENT_SECRET=identity['clientSecret'],ALEF_SECURE_COOKIE='false',ALEF_SESSION_ENCRYPTION_KEY=(BACKEND/'.local/session-encryption-key.txt').read_text().strip(),ALEF_RELEASE_TOOLCHAIN=str(BACKEND/'.local/release-toolchain.json'),ALEF_BROWSER_RESOURCES='file:'+str(ROOT/'out/backend-web')+'/',ALEF_BACKEND_HOME=str(BACKEND))
def command(main,*args):return [str(java),'-Xmx512m','-cp',str(BACKEND/'alef-application/build/install/alef-application/lib/*'),'nl.alef.backend.application.'+main,*map(str,args)]
def stop(process):
    if process and process.poll() is None:
        os.killpg(process.pid,signal.SIGTERM)
        try:process.wait(20)
        except subprocess.TimeoutExpired:os.killpg(process.pid,signal.SIGKILL);process.wait()
def children(pid):
    try:direct=[int(p) for p in Path(f'/proc/{pid}/task/{pid}/children').read_text().split()];return direct+[c for p in direct for c in children(p)]
    except OSError:return []
results=[]
heap=int(next((v.split('=',1)[1] for v in sys.argv[2:] if v.startswith('--api-heap=')),'512'))
sizes=[int(v) for v in next((v.split('=',1)[1] for v in sys.argv[2:] if v.startswith('--nodes=')),'10000,100000,1000000').split(',')]
original_command=command
def command(main,*args):
    result=original_command(main,*args);result[1]=f'-Xmx{heap}m';return result
for count in sizes:
    db=json.loads((databases/f'database-{count}.json').read_text());env.update(ALEF_DB_URL=db['url'],ALEF_DB_USER=db['username'],ALEF_DB_PASSWORD=db['password'],ALEF_CAPACITY_NODES=str(count))
    env['ALEF_MAX_WORKSPACE_NODES']=str(count)
    env['ALEF_CAPACITY_JOB_INTERFERENCE']='1' if '--job-interference' in sys.argv else '0'
    run={'requestedNodes':count,'apiHeapMiB':heap,'workerHeapMiB':2048,'state':str(state)};results.append(run);process=None;finished=threading.Event();samplers={}
    config=state/f'store-{count}.json';config.write_text(json.dumps({'provider':'GIT','path':str(state/f'workspace-{count}.git')}));env['ALEF_MODEL_STORE_CONFIG']=str(config)
    worker=['python3',str(BACKEND/'tools/mps-runtime.py'),'--idea','--no-compile','--main','nl.alef.backend.runtime.mps.RuntimeWorker','--state',str(state/f'worker-{count}')];env['ALEF_WORKER_COMMAND']=json.dumps(worker);env['ALEF_RELEASE_WORKER_COMMAND']=json.dumps(worker[:-1]+[str(state/f'release-{count}')]);env['ALEF_WORKER_LOG']=str(ROOT/f'out/backend-capacity-app-{count}-worker.log')
    try:
        with socket.socket() as probe:
            if probe.connect_ex(('127.0.0.1',8080))==0:raise RuntimeError('Linux port occupied; no unrelated listener was stopped')
        start=time.monotonic()
        with (ROOT/f'out/backend-capacity-app-{count}-bootstrap.log').open('w') as log:
            process=subprocess.Popen(command('BootstrapCli',BACKEND/'.local/development-subjects.json',fixtures/f'corpus-{count}.json',state/f'workspace-{count}.git'),cwd=BACKEND,env=env,stdout=log,stderr=log,start_new_session=True)
            try:code=process.wait(180)
            except subprocess.TimeoutExpired:raise RuntimeError('Bootstrap exceeded 180 seconds')
            if code!=0:raise RuntimeError('Bootstrap failed; inspect retained real native/API log')
        run['bootstrapSeconds']=time.monotonic()-start
        with (ROOT/f'out/backend-capacity-app-{count}.log').open('w') as log:process=subprocess.Popen(command('AlefApplication'),cwd=BACKEND,env=env,stdin=subprocess.DEVNULL,stdout=log,stderr=log,start_new_session=True)
        def monitor():
            while not finished.wait(.5):
                rss=0
                for pid in [process.pid,*children(process.pid)]:
                    try:
                        rss+=int(next(l for l in Path(f'/proc/{pid}/status').read_text().splitlines() if l.startswith('VmRSS:')).split()[1])
                        if Path(f'/proc/{pid}/comm').read_text().strip()=='java' and pid not in samplers:
                            output=(ROOT/f'out/backend-capacity-app-{count}-gc-{pid}.log').open('w');samplers[pid]=(subprocess.Popen([str(java.with_name('jstat')),'-gc',str(pid),'250'],stdout=output,stderr=subprocess.DEVNULL),output)
                    except (OSError,StopIteration):pass
                run['peakProcessTreeRssKiB']=max(run.get('peakProcessTreeRssKiB',0),rss)
                if rss>7*1024*1024:run['resourceStop']='7 GiB application tree RSS budget';os.killpg(process.pid,signal.SIGTERM);return
        threading.Thread(target=monitor,daemon=True).start()
        for attempt in range(120):
            if process.poll() is not None:raise RuntimeError('Application exited during startup')
            try:
                with urllib.request.urlopen('http://127.0.0.1:8080/backend-mode.js',timeout=1) as response:
                    if response.status==200:break
            except OSError:pass
            time.sleep(1)
        with (ROOT/f'out/backend-capacity-app-{count}-browser.log').open('w') as log:
            test=subprocess.Popen(['node',str(ROOT/'tools/measure_backend_browser_capacity.mjs')],cwd=ROOT,env=env,stdout=log,stderr=log,start_new_session=True)
            try:
                if test.wait(1200)!=0:raise RuntimeError('Served browser/API workload failed; inspect measurement JSON/log')
            finally:stop(test)
        run['status']='PASS'
    except BaseException as error:run['status']='CAPACITY_FAILURE';run['failure']=type(error).__name__+': '+str(error)
    finally:
        finished.set();stop(process)
        for sampler,log in samplers.values():sampler.terminate();sampler.wait();log.close()
        (ROOT/'out/backend-capacity-application.json').write_text(json.dumps(results,indent=2));print(json.dumps(run),flush=True)
