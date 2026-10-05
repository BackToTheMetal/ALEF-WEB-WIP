#!/usr/bin/env python3
"""Test transport: real Windows loopback PostgreSQL/OIDC over private WSL pipes.
No LAN listener, identity substitution, SQL interception or application responses.
Each direction permits one 64 KiB frame per connection until acknowledged.
"""
import asyncio,base64,itertools,json,sys
connections={};acks={};sequence=itertools.count(1)
def emit(value):
    sys.stdout.write(json.dumps(value,separators=(',',':'))+'\n');sys.stdout.flush()
async def client(reader,writer,port):
    key=next(sequence);connections[key]=writer;acks[key]=asyncio.Event();emit({'type':'open','id':key,'port':port})
    try:
        await asyncio.wait_for(acks[key].wait(),15)
        while data:=await reader.read(65536):
            acks[key].clear();emit({'type':'data','id':key,'data':base64.b64encode(data).decode()});await acks[key].wait()
        emit({'type':'end','id':key})
    except (ConnectionError,asyncio.TimeoutError):emit({'type':'close','id':key})
async def receive():
    while line:=await asyncio.to_thread(sys.stdin.readline):
        message=json.loads(line);key=message['id'];writer=connections.get(key)
        if message['type']=='ack':
            if key in acks:acks[key].set()
        elif message['type']=='data':
            if writer:
                try:writer.write(base64.b64decode(message['data']));await writer.drain()
                except ConnectionError:pass
            emit({'type':'ack','id':key})
        elif message['type'] in ('end','close'):
            if writer:
                if message['type']=='end' and writer.can_write_eof():writer.write_eof()
                else:writer.close();connections.pop(key,None)
            if key in acks:acks[key].set()
async def main():
    servers=[]
    try:
        for port in (54329,8180):servers.append(await asyncio.start_server(lambda r,w,p=port:client(r,w,p),'127.0.0.1',port))
        emit({'event':'relay.ready'});await receive()
    finally:
        for server in servers:server.close();await server.wait_closed()
        for writer in connections.values():writer.close()
asyncio.run(main())
