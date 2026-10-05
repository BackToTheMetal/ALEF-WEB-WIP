#!/usr/bin/env python3
"""Serve current bundle, export real edited browser graphs, compare native MPS."""
import os,signal,socket,subprocess,time,urllib.request
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
with socket.socket() as probe:
    if probe.connect_ex(('127.0.0.1',18810))==0:raise SystemExit('Verification port is occupied; existing service retained')
with (ROOT/'out/backend-parity-server.log').open('w') as log:
    server=subprocess.Popen(['python3',str(ROOT/'tools/serve_example.py'),'--optimized','--port','18810'],stdout=log,stderr=log,start_new_session=True)
    try:
        for attempt in range(50):
            try:
                with urllib.request.urlopen('http://127.0.0.1:18810/',timeout=1) as response:assert response.status==200
                break
            except OSError:time.sleep(.2)
        env=dict(os.environ,ALEF_EXAMPLE_URL='http://127.0.0.1:18810/',ALEF_EXAMPLE_VARIANT='optimized',ALEF_BROWSERS='chromium,firefox,webkit',ALEF_EXPORT_NATIVE_PARITY='1')
        subprocess.run(['node',str(ROOT/'tools/test_service_test.mjs')],cwd=ROOT,env=env,check=True,timeout=480)
        subprocess.run(['python3',str(ROOT/'tools/test_backend_allowance_parity.py'),str(ROOT/'out/service-test-optimized')],cwd=ROOT,check=True,timeout=480)
    finally:
        os.killpg(server.pid,signal.SIGTERM);server.wait(10)
