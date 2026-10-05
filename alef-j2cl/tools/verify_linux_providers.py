#!/usr/bin/env python3
"""Real isolated provider migration, served-browser saves and restart recovery.
Requires run_backend_linux_provider.mjs's private database/OIDC transport.
"""
import json,os,signal,socket,subprocess,sys,time,urllib.request
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
BACKEND=Path('/mnt/c/Users/niekh/Documents/ChatGPT/ALEFBackend')
restored=Path(sys.argv[1]).resolve(strict=True)
db=json.loads((restored/'database.json').read_text(encoding='utf-8-sig'))
identity=json.loads((BACKEND/'.local/development-identities.json').read_text(encoding='utf-8-sig'))
java=Path.home()/'.cache/alef-j2cl/96dd99ff70cb/alef/target/jbr-21.0.6-b631.39-linux-x64/bin/java'
state=Path.home()/'.cache/alef-backend/provider-verification'/time.strftime('%Y%m%d-%H%M%S')
state.mkdir(parents=True)
env=dict(os.environ,ALEF_DB_URL=db['url'],ALEF_DB_USER=db['username'],ALEF_DB_PASSWORD=db['password'],
         ALEF_OIDC_ISSUER='http://localhost:8180/realms/alef',ALEF_OIDC_CLIENT_ID='alef-browser',
         ALEF_OIDC_CLIENT_SECRET=identity['clientSecret'],ALEF_SECURE_COOKIE='false',
         ALEF_SESSION_ENCRYPTION_KEY=(BACKEND/'.local/session-encryption-key.txt').read_text().strip(),
         ALEF_RELEASE_TOOLCHAIN=str(BACKEND/'.local/release-toolchain.json'),
         ALEF_BROWSER_RESOURCES='file:'+str(ROOT/'out/backend-web')+'/',ALEF_BACKEND_HOME=str(BACKEND))
classpath=str(BACKEND/'alef-application/build/install/alef-application/lib/*')
def command(main,*args):return [str(java),'-Xmx512m','-cp',classpath,'nl.alef.backend.application.'+main,*map(str,args)]
def stop(process):
    if process and process.poll() is None:
        process.terminate()
        try:process.wait(25)
        except subprocess.TimeoutExpired:os.killpg(process.pid,signal.SIGKILL);process.wait()
def start(provider):
    with socket.socket() as check:
        if check.connect_ex(('127.0.0.1',8080))==0:raise RuntimeError('Linux application port is occupied; no existing service was stopped')
    worker=['python3',str(BACKEND/'tools/mps-runtime.py'),'--idea','--no-compile','--main','nl.alef.backend.runtime.mps.RuntimeWorker','--state',str(state/(provider+'-worker'))]
    env['ALEF_WORKER_COMMAND']=json.dumps(worker);env['ALEF_RELEASE_WORKER_COMMAND']=json.dumps(worker[:-1]+[str(state/(provider+'-release-worker'))])
    env['ALEF_WORKER_LOG']=str(ROOT/f'out/backend-linux-{provider}-worker.log')
    with (ROOT/f'out/backend-linux-{provider}-application.log').open('ab') as log:
        process=subprocess.Popen(command('AlefApplication'),cwd=BACKEND,env=env,stdin=subprocess.DEVNULL,stdout=log,stderr=log,start_new_session=True)
    try:
        for _ in range(90):
            if process.poll() is not None:raise RuntimeError('Linux application failed to start; inspect its retained log')
            try:
                with urllib.request.urlopen('http://127.0.0.1:8080/backend-mode.js',timeout=1) as response:
                    if response.status==200:return process
            except (OSError,urllib.error.URLError):pass
            time.sleep(1)
        raise RuntimeError('Linux application startup timed out')
    except BaseException:stop(process);raise
pointer=ROOT/'out/backend-linux-current-config.txt'
source=Path(pointer.read_text().strip()) if pointer.exists() else restored/'model-store.json'
(ROOT/'out/backend-linux-provider-state.txt').write_text(str(state))
with (ROOT/'out/backend-linux-provider-migrations.log').open('w') as log:
    subprocess.run(command('AlefApplication')[:-1]+['nl.alef.backend.persistence.DatabaseCli','migrate'],env=env,cwd=BACKEND,stdout=log,stderr=log,check=True)
for provider in (('current',) if '--current' in sys.argv[2:] else ('filesystem','git')):
    if provider=='current':config=source;provider=json.loads(config.read_text())['provider'].lower()
    else:
        config=state/(provider+'.json');receipt=state/(provider+'-aliases.json')
        definition={'provider':provider.upper(),'path':str(state/(provider+'-store'))}
        config.write_text(json.dumps(definition))
        with (ROOT/f'out/backend-linux-{provider}-migration.log').open('w') as log:
            subprocess.run(command('StorageMaintenanceCli','migrate',source,config,receipt),env=env,cwd=BACKEND,stdout=log,stderr=log,check=True)
        definition['aliases']=str(receipt);config.write_text(json.dumps(definition));pointer.write_text(str(config))
    env['ALEF_MODEL_STORE_CONFIG']=str(config)
    process=None
    try:
        process=start(provider)
        testenv=dict(env,ALEF_BROWSERS=os.environ.get('ALEF_LINUX_BROWSERS','chromium,firefox,webkit'))
        with (ROOT/f'out/backend-linux-{provider}-editor.log').open('w') as log:
            subprocess.run(['node',str(ROOT/'tools/test_backend_editor.mjs')],cwd=ROOT,env=testenv,stdout=log,stderr=log,check=True)
        stop(process);process=start(provider);testenv['ALEF_VERIFY_RESTART']='1'
        with (ROOT/f'out/backend-linux-{provider}-restart.log').open('w') as log:
            subprocess.run(['node',str(ROOT/'tools/test_backend_editor.mjs')],cwd=ROOT,env=testenv,stdout=log,stderr=log,check=True)
        with (ROOT/f'out/backend-linux-{provider}-provenance.log').open('w') as log:
            subprocess.run(['node',str(ROOT/'tools/test_backend_restored_provenance.mjs')],cwd=ROOT,env=env,stdout=log,stderr=log,check=True)
        print('PASS',provider,'real browser editing, restart, original revision aliases and approved release provenance',flush=True)
    finally:stop(process)
    source=config
(ROOT/'out/backend-linux-provider-state.txt').write_text(str(state))
