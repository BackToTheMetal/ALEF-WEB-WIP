import fs from 'node:fs';
import path from 'node:path';
const root=path.resolve(import.meta.dirname,'..');
const generated=path.resolve(root,'out/reference-src');
if(path.dirname(generated)!==path.join(root,'out'))throw new Error('Unsafe generated reference path');
fs.rmSync(generated,{recursive:true,force:true});
for(const rel of fs.readdirSync(path.join(root,'out/upstream'),{recursive:true})) {
  if(!rel.endsWith('.java'))continue;
  let text=fs.readFileSync(path.join(root,'out/upstream',rel),'utf8');
  text=text.replace(/^import org\.jetbrains\.annotations\..*;\r?\n/gm,'').replace(/@(NotNull|Nullable|ApiStatus\.[A-Za-z]+)(?:\([^)]*\))?\s*/g,'');
  const target=path.join(root,'out/reference-src',rel);
  fs.mkdirSync(path.dirname(target),{recursive:true});fs.writeFileSync(target,text);
}
