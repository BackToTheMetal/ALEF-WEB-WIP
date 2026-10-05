#!/usr/bin/env python3
"""Copy only served application assets to the backend's static-resource directory."""
from pathlib import Path
import argparse,shutil,json,hashlib,os
ROOT=Path(__file__).resolve().parents[1]
parser=argparse.ArgumentParser(description=__doc__)
parser.add_argument('--optimized',action='store_true')
args=parser.parse_args()
target=ROOT/'out/backend-web'
target.mkdir(parents=True,exist_ok=True)
sources={name:ROOT/'example'/name for name in ['index.html','style.css','page.js','project-view.js','project-tree.js','backend.js','backend-workflows.js','backend-mode.js','interface.js','interface-ui.js','interface.css','package-checks.js','model-diff.js','mps-diff.js']}
sources['saved-workspace.js']=ROOT/'example/saved-workspace.js'
sources['app.js']=ROOT/'bazel-bin'/('editor_example_browser'+('' if args.optimized else '_dev')+'.js')
sources['vendor/vue.runtime.esm-browser.prod.js']=ROOT/'node_modules/vue/dist/vue.runtime.esm-browser.prod.js'
for name,source in sources.items():
    if not source.is_file():raise SystemExit('Missing built browser asset: '+str(source))
    destination=target/name
    destination.parent.mkdir(parents=True,exist_ok=True)
    temporary=destination.with_name(destination.name+'.preparing')
    shutil.copyfile(source,temporary)
    os.replace(temporary,destination)
manifest={'profile':'optimized' if args.optimized else 'development','assets':{name:hashlib.sha256((target/name).read_bytes()).hexdigest() for name in sources}}
(ROOT/'out/backend-browser-artifacts.json').write_text(json.dumps(manifest,indent=2)+'\n')
print('Backend browser assets:',target)
