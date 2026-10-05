// End-to-end Linux filesystem/Git providers use the real local PostgreSQL and
// Keycloak over bounded private pipes. The normal Windows application stays up.
import {spawn} from 'node:child_process';import net from 'node:net';import readline from 'node:readline';import fs from 'node:fs';import path from 'node:path';
const root=path.resolve(import.meta.dirname,'..'),directory='/mnt/c/Codex/alef-j2cl';
let restored=fs.readFileSync(path.join(root,'out/backend-last-restore.txt'),'utf8').trim().replaceAll('\\','/').replace(/^C:/,'/mnt/c');
let verification='verify_linux_providers.py';
if(process.argv.includes('--capacity')){
 const backend='C:/Users/niekh/Documents/ChatGPT/ALEFBackend',db=JSON.parse(fs.readFileSync(path.join(backend,'.local/database.json'),'utf8').replace(/^\uFEFF/,'')),stamp=Date.now(),state=path.join(backend,`.local/capacity-${stamp}`);fs.mkdirSync(state);
 for(const count of [10000,100000,1000000]){
  const name=`alef_capacity_${stamp}_${count}`,child=spawn(path.join(backend,'.local/postgres-clients-18.6/createdb.exe'),['-h','127.0.0.1','-p','54329','-U',db.username,name],{windowsHide:true,env:{...process.env,PGPASSWORD:db.password},stdio:['ignore','inherit','inherit']});
  const code=await new Promise((resolve,reject)=>{child.on('error',reject);child.on('exit',resolve);});if(code!==0)throw new Error('Cannot create isolated capacity database');
  fs.writeFileSync(path.join(state,`database-${count}.json`),JSON.stringify({...db,url:`jdbc:postgresql://127.0.0.1:54329/${name}`}));
 }
 fs.writeFileSync(path.join(root,'out/backend-capacity-databases.txt'),state);restored=state.replaceAll('\\','/').replace(/^C:/,'/mnt/c');verification='measure_backend_application_capacity.py';
}
const relay=spawn('wsl.exe',['-d','Ubuntu','--','python3',`${directory}/tools/wsl_private_tcp_relay.py`],{windowsHide:true,stdio:['pipe','pipe','inherit']});
const sockets=new Map();let resolveReady,rejectReady;const ready=new Promise((resolve,reject)=>{resolveReady=resolve;rejectReady=reject;});
const send=message=>{if(!relay.stdin.destroyed)relay.stdin.write(JSON.stringify(message)+'\n');};
readline.createInterface({input:relay.stdout}).on('line',line=>{
 const message=JSON.parse(line);if(message.event==='relay.ready'){resolveReady();return;}const key=message.id;
 if(message.type==='open'){
  if(![54329,8180].includes(message.port))throw new Error('Unexpected test transport endpoint');
  const socket=net.createConnection({host:'127.0.0.1',port:message.port},()=>send({type:'ack',id:key}));sockets.set(key,socket);
  socket.on('data',data=>{socket.pause();send({type:'data',id:key,data:data.toString('base64')});});
  socket.on('end',()=>send({type:'end',id:key}));socket.on('error',()=>{});socket.on('close',()=>{send({type:'close',id:key});sockets.delete(key);});
 }else{const socket=sockets.get(key);if(message.type==='data'){if(socket)socket.write(Buffer.from(message.data,'base64'),()=>send({type:'ack',id:key}));else send({type:'close',id:key});}else if(message.type==='ack')socket?.resume();else if(message.type==='end')socket?.end();else if(message.type==='close')socket?.destroy();}
});
relay.on('error',rejectReady);relay.on('exit',code=>{if(code)rejectReady(new Error('Private test transport failed'));});
async function run(args){const process=spawn('wsl.exe',['-d','Ubuntu','--',...args],{windowsHide:true,stdio:'inherit'});const code=await new Promise((resolve,reject)=>{process.on('error',reject);process.on('exit',resolve);});if(code!==0)throw new Error(`Linux verification exited ${code}`);}
try{
 await Promise.race([ready,new Promise((_,reject)=>setTimeout(()=>reject(new Error('Relay not ready')),15000).unref())]);
 await run(['flock','/tmp/j2cl-build.lock','python3',`${directory}/tools/${verification}`,restored,...process.argv.slice(2)]);
}finally{for(const socket of sockets.values())socket.destroy();relay.stdin.end();}
