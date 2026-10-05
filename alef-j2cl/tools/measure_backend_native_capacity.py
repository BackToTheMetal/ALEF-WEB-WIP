#!/usr/bin/env python3
"""Bounded real-MPS scaling probe. No publication, source edits or network service.
Run serially under /tmp/j2cl-build.lock. Retains failure and limit evidence.
"""
import argparse,copy,json,os,queue,signal,subprocess,threading,time,uuid
from pathlib import Path
from make_backend_capacity_corpus import generate
ROOT=Path(__file__).resolve().parents[1]
BACKEND=Path('/mnt/c/Users/niekh/Documents/ChatGPT/ALEFBackend')
parser=argparse.ArgumentParser();parser.add_argument('--nodes',nargs='+',type=int,default=[10000,100000,1000000]);parser.add_argument('--samples',type=int,default=5);args=parser.parse_args()
state=Path.home()/'.cache/alef-backend/capacity'/time.strftime('%Y%m%d-%H%M%S');state.mkdir(parents=True)
(ROOT/'out/backend-capacity-state.txt').write_text(str(state))
results={'hardware':{'cpu':next(l.split(':',1)[1].strip() for l in Path('/proc/cpuinfo').read_text().splitlines() if l.startswith('model name')),'logicalCpus':os.cpu_count(),'memory':Path('/proc/meminfo').read_text(),'kernel':os.uname().release},'workerHeapMiB':2048,'runs':[]}

def descendants(pid):
    children=Path(f'/proc/{pid}/task/{pid}/children')
    if not children.exists():return []
    direct=[int(p) for p in children.read_text().split()]
    return direct+[n for p in direct for n in descendants(p)]

for count in args.nodes:
    run={'requestedNodes':count,'samples':[]};results['runs'].append(run)
    corpus,manifest=generate(BACKEND/'.local/corpus.json',count)
    fixture=state/f'corpus-{count}.json';fixture.write_text(json.dumps(corpus,separators=(',',':')))
    fixture.with_suffix('.manifest.json').write_text(json.dumps(manifest,indent=2));run.update(inputBytes=fixture.stat().st_size,modelCount=len(corpus),referenceDensity=manifest['addedReferences']/max(1,count-128),fixture=str(fixture))
    response_queue=queue.Queue();stopped=threading.Event();process=None;jstat=None
    with (ROOT/f'out/backend-capacity-native-{count}.log').open('w') as log:
        try:
            process=subprocess.Popen(['python3',str(BACKEND/'tools/mps-runtime.py'),'--idea','--no-compile','--main','nl.alef.backend.runtime.mps.RuntimeWorker','--state',str(state/f'worker-{count}')],stdin=subprocess.PIPE,stdout=subprocess.PIPE,stderr=log,text=True,bufsize=1,start_new_session=True)
            def read():
                try:
                    for line in process.stdout:
                        if line.startswith('{'):response_queue.put(json.loads(line))
                except BaseException as error:response_queue.put({'readerFailure':type(error).__name__})
                finally:response_queue.put({'eof':True})
            threading.Thread(target=read,daemon=True).start()
            def sample():
                while not stopped.wait(.2):
                    rss=0
                    for pid in [process.pid,*descendants(process.pid)]:
                        try:rss+=int(next(l for l in Path(f'/proc/{pid}/status').read_text().splitlines() if l.startswith('VmRSS:')).split()[1])
                        except (OSError,StopIteration):pass
                    run['peakProcessTreeRssKiB']=max(run.get('peakProcessTreeRssKiB',0),rss)
                    if rss>4*1024*1024:
                        run['resourceStop']='4 GiB process-tree RSS safety budget';os.killpg(process.pid,signal.SIGTERM);return
            threading.Thread(target=sample,daemon=True).start()
            started=time.monotonic();ready=response_queue.get(timeout=90);assert ready.get('event')=='worker.ready',ready;run['startupSeconds']=time.monotonic()-started
            java_pid=next(p for p in descendants(process.pid) if Path(f'/proc/{p}/comm').read_text().strip()=='java')
            java=Path.home()/'.cache/alef-j2cl/96dd99ff70cb/alef/target/jbr-21.0.6-b631.39-linux-x64/bin'
            gc_log=(ROOT/f'out/backend-capacity-native-{count}-gc.log').open('w')
            jstat=subprocess.Popen([str(java/'jstat'),'-gc',str(java_pid),'250'],stdout=gc_log,stderr=subprocess.DEVNULL)
            def call(command,timeout=120,**fields):
                request=dict(id=str(uuid.uuid4()),command=command,**fields);start=time.monotonic()
                # Keep write blocking out of the timeout-owning thread.
                def send():
                    try:process.stdin.write(json.dumps(request,separators=(',',':'))+'\n');process.stdin.flush()
                    except BaseException as error:response_queue.put({'writeFailure':type(error).__name__})
                threading.Thread(target=send,daemon=True).start()
                answer=response_queue.get(timeout=timeout);elapsed=time.monotonic()-start
                if not answer.get('ok'):raise RuntimeError(str({k:v for k,v in answer.items() if k!='result'}))
                return answer['result'],elapsed
            initial,seconds=call('load',models=corpus,revision='capacity-initial');run['loadSeconds']=seconds
            assert initial['nodeCount']==count,(initial['nodeCount'],count)
            run['actualNodes']=initial['nodeCount'];run['actualRoots']=initial['rootCount']
            wanted=manifest['addedRoots'][0];model=next(m for m in initial['models'] if m['modelId']==wanted['modelId']);root=next(r for r in model['roots'] if r['id']==wanted['nodeId']);revision='capacity-initial'
            del initial,corpus
            for sample_index in range(args.samples):
                edited=copy.deepcopy(root);prop=next(iter(edited['properties']));edited['properties'][prop]='Capacity '+chr(97+sample_index)
                operation=str(uuid.uuid4());changed,proposal_time=call('propose',expectedRevision=revision,operationId=operation,roots=[dict(moduleId=model['moduleId'],modelId=model['modelId'],rootId=root['id'],root=edited)])
                assert changed['validation']['engineFailures']==0,changed['validation']
                assert not any(d['severity']=='ERROR' for d in changed['validation']['diagnostics']),changed['validation']
                del changed
                revision='capacity-'+str(sample_index);active,activation_time=call('activate',operationId=operation,revision=revision)
                actual=next(r for m in active['models'] for r in m['roots'] if r['id']==root['id']);assert actual==edited
                run['samples'].append({'nativeProposalSeconds':proposal_time,'nativeActivationSeconds':activation_time});root=edited;del active
            run['status']='PASS_NATIVE_ONLY'
        except BaseException as error:run['status']='CAPACITY_FAILURE';run['failure']=type(error).__name__+': '+str(error)[:700]
        finally:
            stopped.set()
            if process and process.poll() is None:
                os.killpg(process.pid,signal.SIGTERM)
                try:process.wait(10)
                except subprocess.TimeoutExpired:os.killpg(process.pid,signal.SIGKILL);process.wait()
            if jstat:jstat.terminate();jstat.wait();gc_log.close()
            gc_file=ROOT/f'out/backend-capacity-native-{count}-gc.log'
            if gc_file.exists():
                lines=gc_file.read_text().splitlines()
                if lines:
                    keys=lines[0].split();samples=[]
                    for line in lines[1:]:
                        try:samples.append(dict(zip(keys,map(float,line.split()))))
                        except ValueError:pass
                    if samples:
                        run['sampledPeakHeapKiB']=max(sum(s.get(k,0) for k in ('S0U','S1U','EU','OU')) for s in samples)
                        run['gcSeconds']=samples[-1].get('GCT');run['gcSamples']=len(samples)
            (ROOT/'out/backend-capacity-native.json').write_text(json.dumps(results,indent=2))
            print(json.dumps(run),flush=True)
