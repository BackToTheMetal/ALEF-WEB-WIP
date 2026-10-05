// The real Windows application and Keycloak remain authoritative. Linux Firefox
// uses loopback endpoints carried over private wsl.exe pipes, not a LAN listener.
import {spawn} from 'node:child_process';import http from 'node:http';import readline from 'node:readline';
import {createGzip} from 'node:zlib';
const directory='/mnt/c/Codex/alef-j2cl',backend='/mnt/c/Users/niekh/Documents/ChatGPT/ALEFBackend';
const script=process.env.ALEF_TEST_SCRIPT||'test_backend_editor.mjs';
if(!['test_backend_editor.mjs','test_backend_structure.mjs'].includes(script))throw new Error('Select an existing backend browser test');
const relay=spawn('wsl.exe',['-d','Ubuntu','--','python3',`${directory}/tools/wsl_loopback_relay.py`],{windowsHide:true,stdio:['pipe','pipe','inherit']});
const lines=readline.createInterface({input:relay.stdout});let startResolve,startReject;
const started=new Promise((resolve,reject)=>{startResolve=resolve;startReject=reject;});
relay.on('error',startReject);relay.on('exit',code=>{if(code)startReject(new Error(`Loopback relay exited ${code}`));});
lines.on('line',line=>{
 const request=JSON.parse(line);if(request.event==='relay.ready'){startResolve();return;}
 if(![8080,8180].includes(request.port))throw new Error('Unexpected relay target');
 const headers={};for(const [key,value]of request.headers)if(!['connection','transfer-encoding','content-length'].includes(key.toLowerCase()))headers[key]=value;
 const body=Buffer.from(request.body,'base64');headers['Content-Length']=String(body.length);
 const outgoing=http.request({hostname:'127.0.0.1',port:request.port,path:request.path,method:request.method,headers},response=>{
   const chunks=[];let length=0;
   // The development bundle is over 200 MiB. Standard gzip transport avoids
   // materializing its base64 representation on both sides of the private pipe.
   const compress=request.path==='/app.js'&&!response.headers['content-encoding']&&String(headers['Accept-Encoding']||headers['accept-encoding']).includes('gzip');
   const stream=compress?response.pipe(createGzip()):response;
   stream.on('data',chunk=>{length+=chunk.length;if(length>128*1024*1024)outgoing.destroy();else chunks.push(chunk);});
   stream.on('end',()=>{const pairs=[];for(let i=0;i<response.rawHeaders.length;i+=2)pairs.push(response.rawHeaders.slice(i,i+2));
     if(compress)pairs.push(['Content-Encoding','gzip'],['Vary','Accept-Encoding']);
     relay.stdin.write(JSON.stringify({id:request.id,status:response.statusCode,headers:pairs,body:Buffer.concat(chunks).toString('base64')})+'\n');});
 });
 outgoing.setTimeout(140000,()=>outgoing.destroy());
 outgoing.on('error',()=>relay.stdin.write(JSON.stringify({id:request.id,status:502,headers:[],body:Buffer.from('Local test transport unavailable').toString('base64')})+'\n'));
 outgoing.end(body);
});
try{
 await Promise.race([started,new Promise((_,reject)=>setTimeout(()=>reject(new Error('Relay startup timeout')),15000).unref())]);
 const env=['ALEF_BACKEND_HOME='+backend,'ALEF_BROWSERS=firefox'];
 if(process.env.ALEF_VERIFY_RESTART)env.push('ALEF_VERIFY_RESTART='+process.env.ALEF_VERIFY_RESTART);
 const test=spawn('wsl.exe',['-d','Ubuntu','--','env',...env,'node',`${directory}/tools/${script}`],{windowsHide:true,stdio:'inherit'});
 process.exitCode=await new Promise((resolve,reject)=>{test.on('error',reject);test.on('exit',resolve);});
}finally{relay.stdin.end();lines.close();}
