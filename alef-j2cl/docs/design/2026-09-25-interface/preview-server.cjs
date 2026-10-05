// Serves this isolated design artifact only. No connection to the ALEF API.
const http = require('node:http');
const fs = require('node:fs');
const path = require('node:path');
const mime = {'.html':'text/html; charset=utf-8','.css':'text/css; charset=utf-8','.js':'text/javascript; charset=utf-8','.jpg':'image/jpeg','.png':'image/png','.md':'text/plain; charset=utf-8'};
http.createServer((req,res)=>{
  let name; try { name=decodeURIComponent(new URL(req.url,'http://127.0.0.1').pathname).slice(1)||'index.html'; } catch { res.writeHead(400).end(); return; }
  if (name !== path.basename(name) || !mime[path.extname(name)]) { res.writeHead(404).end(); return; }
  fs.readFile(path.join(__dirname,name),(err,data)=>{
    if(err){res.writeHead(404).end();return;}
    res.writeHead(200,{'Content-Type':mime[path.extname(name)],'Cache-Control':'no-store','X-Content-Type-Options':'nosniff'});res.end(data);
  });
}).listen(18821,'127.0.0.1',()=>console.log('Design preview only: http://127.0.0.1:18821/'));
