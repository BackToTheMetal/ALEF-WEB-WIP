#!/usr/bin/env python3
"""Test-only transport: Linux loopback HTTP through private stdio to Windows.
No public listener, credential storage, identity substitution or response mocking.
"""
import base64, http.server, json, queue, sys, threading, uuid
pending={}
lock=threading.Lock()
class Handler(http.server.BaseHTTPRequestHandler):
    protocol_version='HTTP/1.1'
    def log_message(self,*args): pass  # Never log OIDC codes, cookies or request bodies.
    def exchange(self):
        request_id=str(uuid.uuid4());response=queue.Queue(maxsize=1)
        length=int(self.headers.get('Content-Length','0'))
        if length>8*1024*1024:self.send_error(413);return
        body=self.rfile.read(length)
        with lock:
            pending[request_id]=response
            print(json.dumps(dict(id=request_id,port=self.server.server_port,method=self.command,path=self.path,
                headers=list(self.headers.items()),body=base64.b64encode(body).decode())),flush=True)
        try:
            data=response.get(timeout=150);payload=base64.b64decode(data['body'])
            self.send_response(data['status'])
            for key,value in data['headers']:
                if key.lower() not in ('connection','transfer-encoding','content-length'):self.send_header(key,value)
            self.send_header('Content-Length',str(len(payload)));self.send_header('Connection','close');self.end_headers()
            self.wfile.write(payload);self.close_connection=True
        except (queue.Empty,BrokenPipeError,ConnectionResetError): self.close_connection=True
        finally:
            with lock:pending.pop(request_id,None)
    do_GET=do_POST=do_PUT=do_DELETE=do_PATCH=do_HEAD=exchange
servers=[]
try:
    for port in (8080,8180):
        server=http.server.ThreadingHTTPServer(('127.0.0.1',port),Handler);servers.append(server)
        threading.Thread(target=server.serve_forever,daemon=True).start()
    print('{"event":"relay.ready"}',flush=True)
    for line in sys.stdin:
        data=json.loads(line)
        with lock:destination=pending.get(data.get('id'))
        if destination:destination.put(data)
finally:
    for server in servers:server.shutdown();server.server_close()
