"""Build the editable implementation paper's PDF and vector diagrams.

No application state is read or changed. Source hashes exclude local secrets.
Run with Python + ReportLab; use Poppler/pypdf separately for output QA.
"""
from pathlib import Path
import hashlib
import html
import json
import math
import re
import textwrap

from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle
from reportlab.graphics.shapes import Drawing, Rect, String, Line, PolyLine, Polygon
from reportlab.graphics import renderSVG
from reportlab.platypus import (BaseDocTemplate, PageTemplate, Frame, Paragraph,
    Spacer, PageBreak, Table, TableStyle, KeepTogether, Preformatted, HRFlowable)

HERE=Path(__file__).resolve().parent
ROOT=HERE.parents[1]
BACKEND=Path('C:/Users/niekh/Documents/ChatGPT/ALEFBackend')
APP=BACKEND/'alef-application/src/main/java/nl/alef/backend/application'
PDF=ROOT/'output/pdf/alef-technical-paper.pdf'
DIAGRAMS=HERE/'diagrams'
PDF.parent.mkdir(parents=True,exist_ok=True)
DIAGRAMS.mkdir(parents=True,exist_ok=True)

for name,file in [('Body','arial.ttf'),('Body-Bold','arialbd.ttf'),('Body-Italic','ariali.ttf'),
                  ('Body-BoldItalic','arialbi.ttf'),('Title','georgia.ttf'),('Title-Bold','georgiab.ttf'),
                  ('Mono','consola.ttf')]:
    pdfmetrics.registerFont(TTFont(name,'C:/Windows/Fonts/'+file))
pdfmetrics.registerFontFamily('Body',normal='Body',bold='Body-Bold',italic='Body-Italic',boldItalic='Body-BoldItalic')
INK=colors.HexColor('#203342');MUTED=colors.HexColor('#536575');BLUE=colors.HexColor('#246884')
ORANGE=colors.HexColor('#c76529');PALE=colors.HexColor('#eef4f7');BORDER=colors.HexColor('#cad7df')
PAPER=colors.white
W=499.28

class Diagram:
    def __init__(self,height):
        self.height=height;self.d=Drawing(500,height);self.nodes={}
        self.d.add(Rect(0,0,500,height,fillColor=colors.HexColor('#fbfcfd'),strokeColor=BORDER,strokeWidth=.5,rx=4))
    def text(self,x,y,text,size=8,color=INK,anchor='middle',bold=False):
        self.d.add(String(x,self.height-y,text,fontName='Body-Bold' if bold else 'Body',fontSize=size,fillColor=color,textAnchor=anchor))
    def box(self,key,x,y,w,h,title,body='',accent=False):
        self.nodes[key]=(x,y,w,h)
        self.d.add(Rect(x,self.height-y-h,w,h,rx=5,strokeColor=ORANGE if accent else BLUE,
                        fillColor=colors.HexColor('#fff4ea') if accent else PALE,strokeWidth=.8))
        lines=[]
        for part in title.split('\n'):
            lines.extend([(v,9,True) for v in textwrap.wrap(part,max(10,int((w-16)/4.7)))])
        if body:
            for part in body.split('\n'):
                lines.extend([(v,7.7,False) for v in textwrap.wrap(part,max(10,int((w-16)/4)))])
        used=sum(size+3 for _,size,_ in lines)
        yy=y+(h-used)/2+9
        for line,size,bold in lines:
            self.text(x+w/2,yy,line,size,bold=bold);yy+=size+3
    def point(self,key,side):
        x,y,w,h=self.nodes[key]
        return {'t':(x+w/2,y),'b':(x+w/2,y+h),'l':(x,y+h/2),'r':(x+w,y+h/2)}[side]
    def arrow(self,points,label=None,labelpos=None,dashed=False,color=BLUE):
        native=[(x,self.height-y) for x,y in points]
        self.d.add(PolyLine([v for p in native for v in p],strokeColor=color,strokeWidth=.9,strokeDashArray=[3,2] if dashed else None))
        a,b=native[-2:];angle=math.atan2(b[1]-a[1],b[0]-a[0]);length=5
        pts=[b[0],b[1],b[0]-length*math.cos(angle-.5),b[1]-length*math.sin(angle-.5),b[0]-length*math.cos(angle+.5),b[1]-length*math.sin(angle+.5)]
        self.d.add(Polygon(pts,fillColor=color,strokeColor=color,strokeWidth=.3))
        if label:
            x,y=labelpos or ((points[0][0]+points[-1][0])/2,(points[0][1]+points[-1][1])/2-4)
            width=pdfmetrics.stringWidth(label,'Body',7.4)+6
            self.d.add(Rect(x-width/2,self.height-y-2,width,10,fillColor=colors.HexColor('#fbfcfd'),strokeColor=None))
            self.text(x,y,label,7.4,MUTED)
    def link(self,a,b,sa='b',sb='t',**kwargs):self.arrow([self.point(a,sa),self.point(b,sb)],**kwargs)
    def save(self,name):
        renderSVG.drawToFile(self.d,str(DIAGRAMS/name));return self.d

drawings={}
g=Diagram(276)
g.box('browser',28,15,197,54,'Browser application','Shell + resident MPS/J2CL editor')
g.box('idp',334,15,140,54,'Identity provider','OIDC login and freshness')
g.box('api',151,110,199,58,'Java application','APIs, authorization, coordination')
g.box('db',8,213,114,52,'PostgreSQL','Governance + artifacts')
g.box('store',133,213,108,52,'Revision store','Local Git in this setup')
g.box('worker',252,213,115,52,'Native MPS','Supervised workers')
g.box('target',378,213,114,52,'Target adapter','SIMULATED delivery',True)
g.link('browser','idp','r','l',label='login',labelpos=(278,36))
g.link('browser','api',label='Authenticated API',labelpos=(132,93))
g.link('idp','api',label='Identity checks',labelpos=(393,97),dashed=True)
for n in ['db','store','worker','target']:g.link('api',n)
drawings['01-system.svg']=g.save('01-system.svg')

g=Diagram(221)
g.box('alef',10,13,147,57,'Pinned ALEF / MPS','Language models + source')
g.box('generated',178,13,147,57,'Generated Java','Editors + language semantics')
g.box('overlay',346,13,144,57,'Browser adaptations','MPS, Swing, AWT, JRE')
g.link('alef','generated','r','l')
g.box('compile',102,110,296,42,'Bazel + J2CL + Closure','Explicit selection; one provider for each type')
g.link('generated','compile');g.link('overlay','compile')
g.box('bundle',102,177,296,32,'One optimized editor bundle')
g.link('compile','bundle')
drawings['02-build.svg']=g.save('02-build.svg')

def sequence(name,participants,steps,notes=None):
    h=60+len(steps)*23+(24 if notes else 0);g=Diagram(h)
    n=len(participants);xs=[52+i*(396/(n-1)) for i in range(n)]
    for i,p in enumerate(participants):
        g.box(str(i),xs[i]-47,12,94,33,p)
        g.d.add(Line(xs[i],h-49,xs[i],18,strokeColor=BORDER,strokeWidth=.8,strokeDashArray=[3,3]))
    for row,(start,end,text) in enumerate(steps):
        y=65+row*23
        g.text((xs[start]+xs[end])/2,y-5,text,7.5)
        g.arrow([(xs[start],y),(xs[end],y)],dashed=end<start)
    if notes:g.text(250,h-12,notes,7.5,MUTED)
    drawings[name]=g.save(name)

sequence('03-editor.svg',['Browser','Backend','Native worker'],[
 (0,1,'Request authorized snapshot'),(1,0,'Graph + versions + language fingerprint'),
 (0,1,'Acquire editing lease'),(1,0,'Confirmed client-bound lease'),
 (0,1,'Save captured dirty roots'),(1,2,'Represent and serialize proposed nodes'),
 (2,1,'Native proposal; durable publication follows'),(1,0,'Accepted operation + versions'),
],notes='Browser acknowledges metadata in place; existing editor and history remain.')

g=Diagram(242)
stages=[('1','Prepare retained resources','Revision store'),('2','Accept matching intent','PostgreSQL'),('3','Publish exact target','Revision store'),('4','Finalize governance','PostgreSQL'),('5','Activate / mark indexed','Native worker + database')]
for i,(num,title,where) in enumerate(stages):
    y=11+i*45;g.box(num,76,y,350,33,num+'. '+title+' | '+where)
    if i:g.link(str(i),num)
drawings['04-publication.svg']=g.save('04-publication.svg')

g=Diagram(228)
g.box('host',79,12,342,34,'One browser MPS host / loaded language runtime')
g.box('workspace',12,81,145,61,'Workspace models','Editable only under lease')
g.box('before',177,81,145,61,'Before models','Temporary model IDs; read-only')
g.box('after',342,81,145,61,'After models','Temporary model IDs; read-only')
for key in ['workspace','before','after']:g.link('host',key)
g.box('bview',177,180,145,32,'Generated before editor')
g.box('aview',342,180,145,32,'Generated after editor')
g.link('before','bview');g.link('after','aview')
g.text(84,180,'No fallback into',8);g.text(84,193,'writable workspace',8)
drawings['05-comparison.svg']=g.save('05-comparison.svg')

g=Diagram(245)
g.box('events',15,13,225,46,'Command model events','Normal incremental update')
g.box('runtime',260,13,225,46,'Transient / direct updates','Tracked writes and updater calls')
g.box('updater',129,97,241,34,'Build or synchronize editor cells')
g.link('events','updater');g.link('runtime','updater')
g.box('layout',20,173,222,49,'Coalesced completion','Layout, validate, repaint')
g.box('scroll',262,173,222,49,'Viewport exposure','Scroll requests visible-region repaint')
g.link('updater','layout');g.text(250,237,'Both paths finish by painting the visible editor region.',7.5,MUTED)
drawings['06-repaint.svg']=g.save('06-repaint.svg')

sequence('07-removal.svg',['Browser dialog','Package API','Governance'],[
 (0,1,'GET contents using package baseline'),(1,0,'Included changes + current revision'),
 (0,1,'POST selected root IDs + expected revision'),(1,2,'Lock; authorize; compare revision'),
 (1,2,'Remove selections; create new candidate'),(2,1,'Retain new revision and schedule checks'),
 (1,0,'New package state; refresh comparison'),
],notes='Cancel: no write. Stale revision: reject. Saved workspace: unchanged.')

g=Diagram(247)
g.box('b',15,13,216,52,'Interactive browser test','Resident draft; transient debug results')
g.box('p',269,13,216,52,'Fixed package candidate','Saved versions + toolchain pins')
g.box('w',269,100,216,55,'Isolated native MPS worker','Validate models; run Testspraak cases')
g.box('r',269,189,216,44,'Retained evidence','Report + source integrity + package')
g.box('e',15,100,216,55,'Editor feedback','Not retained release approval evidence')
g.link('b','e');g.link('p','w');g.link('w','r')
g.text(123,203,'Same language family;',8);g.text(123,217,'different execution and evidence scopes.',8)
drawings['08-checks.svg']=g.save('08-checks.svg')

g=Diagram(217)
g.box('request',12,14,152,45,'Authorized selection','Baseline + exact versions')
g.box('cache',190,14,145,45,'Durable graph cache','Decoder-aware keys')
g.box('display',363,14,124,45,'Comparison','Derived display only')
g.link('request','cache','r','l');g.link('cache','display','r','l')
g.box('native',190,105,145,46,'Decode missing history','One contents worker')
g.box('saved',12,105,152,46,'Accepted workspace graph','Retained after activation/save')
g.link('cache','native',label='Miss',labelpos=(278,86));g.link('saved','cache',label='Reuse',labelpos=(146,89))
g.text(250,187,'Retain decoded inputs, then compose and cache the selection.',8)
g.text(250,202,'Neither a cache hit nor a display graph counts as validation.',8,MUTED)
drawings['09-cache.svg']=g.save('09-cache.svg')

sequence('10-deployment.svg',['Browser','Backend / jobs','Target simulator'],[
 (0,1,'Deploy approved digest with expected target state'),(1,2,'Submit durable operation after dispatch gate'),
 (2,1,'Accepted, refused, or uncertain response'),(1,2,'Lookup / observe original operation'),
 (2,1,'Observed installed digest and outcome'),(1,0,'Display progress and installed package'),
],notes='Promotion: same digest must already be installed in the previous environment.')

styles={
 'body':ParagraphStyle('body',fontName='Body',fontSize=9.65,leading=14.2,textColor=INK,spaceAfter=8,splitLongWords=True),
 'h1':ParagraphStyle('h1',fontName='Title-Bold',fontSize=19.5,leading=24,textColor=INK,spaceBefore=17,spaceAfter=15,keepWithNext=True),
 'h2':ParagraphStyle('h2',fontName='Body-Bold',fontSize=11.8,leading=15.4,textColor=BLUE,spaceBefore=10,spaceAfter=7,keepWithNext=True),
 'table':ParagraphStyle('table',fontName='Body',fontSize=8.05,leading=11.25,textColor=INK,splitLongWords=True),
 'th':ParagraphStyle('th',fontName='Body-Bold',fontSize=8.1,leading=11.25,textColor=INK),
 'code':ParagraphStyle('code',fontName='Mono',fontSize=8.1,leading=11,textColor=INK,backColor=PALE,borderPadding=10,spaceBefore=3,spaceAfter=10),
 'caption':ParagraphStyle('caption',fontName='Body',fontSize=8.3,leading=11.7,textColor=MUTED,spaceBefore=4,spaceAfter=10),
 'bullet':ParagraphStyle('bullet',fontName='Body',fontSize=9.65,leading=14.2,textColor=INK,leftIndent=13,firstLineIndent=-9,spaceAfter=6),
}

def inline(text):
    text=html.escape(text)
    text=re.sub(r'`([^`]+)`',lambda m:'<font name="Mono" size="8.65">'+m[1]+'</font>',text)
    text=re.sub(r'\*\*([^*]+)\*\*',r'<b>\1</b>',text)
    return text

class PaperDoc(BaseDocTemplate):
    def __init__(self,*args,**kwargs):
        super().__init__(*args,**kwargs);self.section='ALEF in the browser';self.headings=[]
    def afterFlowable(self,flowable):
        if isinstance(flowable,Paragraph) and flowable.style.name=='h1':
            title=flowable.getPlainText();key='section-'+str(len(self.headings));self.section=title
            self.canv.bookmarkPage(key);self.canv.addOutlineEntry(title,key,level=0)
            self.headings.append({'title':title,'page':self.page})

def page_decor(canvas,doc):
    canvas.saveState()
    canvas.setFillColor(ORANGE);canvas.rect(48,805,25,3,fill=1,stroke=0)
    canvas.setFont('Body-Bold',7.2);canvas.setFillColor(MUTED)
    canvas.drawString(80,805,'ALEF  /  ARCHITECTURE, IMPLEMENTATION AND RISK')
    canvas.setStrokeColor(BORDER);canvas.setLineWidth(.5);canvas.line(48,40,547.28,40)
    canvas.setFont('Body',7);canvas.drawString(48,27,'Implementation snapshot: 29 September 2026  |  v1.0')
    canvas.drawRightString(547.28,27,str(doc.page));canvas.restoreState()

story=[];lines=(HERE/'ALEF-technical-paper.md').read_text(encoding='utf-8').splitlines();i=0
while i<len(lines):
    line=lines[i].strip()
    if not line:i+=1;continue
    if line=='<!-- pagebreak -->':story.append(PageBreak());i+=1;continue
    if line.startswith('# '):
        story.append(Spacer(1,28));story.append(Paragraph(inline(line[2:]),ParagraphStyle('cover',fontName='Title-Bold',fontSize=35,leading=42,textColor=INK,spaceAfter=10)))
        i+=1;continue
    if line.startswith('## '):story.append(Paragraph(inline(line[3:]),styles['h1']));i+=1;continue
    if line.startswith('### '):story.append(Paragraph(inline(line[4:]),styles['h2']));i+=1;continue
    if line.startswith('!['):
        match=re.fullmatch(r'!\[(.*?)\]\(diagrams/(.*?)\)',line)
        if not match:raise ValueError(line)
        d=drawings[match[2]];d.hAlign='CENTER';j=i+1
        while j<len(lines) and not lines[j].strip():j+=1
        caption=[]
        if j<len(lines) and lines[j].startswith('**Figure '):
            while j<len(lines) and lines[j].strip():caption.append(lines[j].strip());j+=1
        lead=[]
        if story and isinstance(story[-1],Paragraph) and story[-1].style.name=='h1':lead.append(story.pop())
        story.append(KeepTogether(lead+[d,Spacer(1,6),Paragraph(inline(' '.join(caption)),styles['caption'])]));i=j;continue
    if line.startswith('```'):
        code=[];i+=1
        while i<len(lines) and not lines[i].startswith('```'):code.append(lines[i]);i+=1
        story.append(KeepTogether([Preformatted('\n'.join(code),styles['code'],maxLineLength=90)]));i+=1;continue
    if line.startswith('|'):
        rows=[]
        while i<len(lines) and lines[i].strip().startswith('|'):
            values=[v.strip() for v in lines[i].strip().strip('|').split('|')]
            if not all(re.fullmatch(r':?-+:?',v) for v in values):rows.append(values)
            i+=1
        n=len(rows[0]);widths=([93,W-93] if n==2 else [106,153,W-259] if n==3 else [100,92,115,W-307])
        if rows[0][0]=='ID':widths=[38,W-38]
        table=Table([[Paragraph(inline(cell),styles['th' if r==0 else 'table']) for cell in row] for r,row in enumerate(rows)],colWidths=widths,repeatRows=1,hAlign='LEFT')
        table.setStyle(TableStyle([
            ('BACKGROUND',(0,0),(-1,0),PALE),('ROWBACKGROUNDS',(0,1),(-1,-1),[colors.white,colors.HexColor('#f8fafb')]),
            ('VALIGN',(0,0),(-1,-1),'TOP'),('LEFTPADDING',(0,0),(-1,-1),7),('RIGHTPADDING',(0,0),(-1,-1),7),
            ('TOPPADDING',(0,0),(-1,-1),6),('BOTTOMPADDING',(0,0),(-1,-1),6),
            ('LINEBELOW',(0,0),(-1,0),.65,BORDER),('LINEBELOW',(0,1),(-1,-1),.3,BORDER),
        ]));lead=[]
        if story and isinstance(story[-1],Paragraph) and story[-1].style.name=='h2':lead.append(story.pop())
        story.extend([KeepTogether(lead+[table]),Spacer(1,10)]);continue
    if re.match(r'^(?:- |\d+\. )',line):
        text=line;story.append(Paragraph(inline(text),styles['bullet']));i+=1;continue
    paragraph=[line];i+=1
    while i<len(lines) and lines[i].strip() and not re.match(r'^(#|\||!\[|```|<!--|- |\d+\. )',lines[i]):paragraph.append(lines[i].strip());i+=1
    text=' '.join(paragraph);style=styles['caption'] if text.startswith('**Figure ') else styles['body']
    story.append(Paragraph(inline(text),style))

doc=PaperDoc(str(PDF),pagesize=A4,leftMargin=48,rightMargin=48,topMargin=58,bottomMargin=53,
            title='ALEF in the browser: Architecture, implementation and risk',author='Technical implementation analysis',subject='Source-grounded analysis of the integrated ALEF application')
doc.addPageTemplates(PageTemplate(id='paper',frames=[Frame(48,53,W,731,leftPadding=0,rightPadding=0,topPadding=0,bottomPadding=0)],onPage=page_decor))
doc.build(story)

sources={
'S01':[ROOT/'example/interface.js'], 'S02':[ROOT/'example/backend.js'], 'S03':[APP/'WorkspaceService.java'],
'S04':[ROOT/'example/BackendModelBridge.java'], 'S05':[APP/'PackageService.java'], 'S06':[ROOT/'example/saved-workspace.js'],
'S07':[APP/'PublicationCoordinator.java'], 'S08':[ROOT/'example'/p for p in ['BrowserComparisonSession.java','model-diff.js','mps-diff.js']],
'S09':[ROOT/'example'/p for p in ['BrowserComparisonEditor.java','ComparisonTextDiff.java']],
'S10':[APP/p for p in ['SecurityConfiguration.java','SessionFreshness.java','DurableAuthorizedClients.java','SessionCipher.java']],
'S11':[APP/'WorkerSupervisor.java',APP/'WorkspaceConfiguration.java'],
'S12':[BACKEND/'alef-workspace-store/src/main/java/nl/alef/backend/workspace/RevisionStore.java',APP/'ModelStoreConfiguration.java'],
'S13':[APP/'PackageComposer.java',APP/'ReleaseBuilder.java'],
'S14':[ROOT/'docs/package-test-report.md',ROOT/'example/package-checks.js'],
'S15':[ROOT/'docs/package-deletion.md',APP/'PackageService.java'],
'S16':[BACKEND/'alef-runtime-mps/src/main/java/nl/alef/backend/runtime/mps/RuntimeWorker.java',APP/'ReleaseBuilder.java',APP/'ReleaseToolchain.java'],
'S17':[APP/p for p in ['DeploymentService.java','ExecutionPlatformAdapter.java','DurableStubAdapter.java']],
'S18':[APP/'PackageContentsStore.java',APP/'PackageContents.java',ROOT/'docs/package-contents-cache.md'],
'S19':[ROOT/'source-lock.json',ROOT/'BUILD.bazel',ROOT/'README.md',BACKEND/'alef-application/build.gradle'],
'S20':[BACKEND/'docs/running-the-application.md'],
'S21':[ROOT/p for p in ['tools/prepare.py','tools/adapt_translators.py','mps-access.json']],
'S22':[ROOT/'out/backend-browser-artifacts.json',ROOT/'tools/package_backend_browser.py'],
'S23':[ROOT/p for p in ['docs/editor-refresh-investigation.md','docs/design/IMPLEMENTATION-23.md','tools/adapt_editor_updates.py','tools/adapt_tables.py','tools/adapt_backend_guard.py']],
'S24':[ROOT/'tools'/p for p in ['test_interface.mjs','test_package_change_removal.mjs','verify_package_change_removal_live.mjs']],
'S25':[BACKEND/'alef-application/build/test-results/test'/('TEST-nl.alef.backend.application.'+p+'.xml') for p in ['PackageWorkflowTest','DeploymentRecoveryTest']]+[ROOT/'tools'/p for p in ['test_debug_results.mjs','test_editor_updates.mjs','test_mps_text_layout.mjs','test_mps_diff_live.mjs','test_save_without_reload.mjs','test_editor_locks.mjs']],
'S26':[ROOT/'docs/java-mps-runtime-plan.md']}
index={'inspection_date':'2026-09-29','scope':'Integrated ALEF application','local_configuration':'Storage selector inspected; secrets and configuration bytes intentionally excluded.','sources':{}}
for key,paths in sources.items():
    index['sources'][key]=[]
    for p in paths:
        raw=p.read_bytes();index['sources'][key].append({'path':str(p),'sha256':hashlib.sha256(raw).hexdigest(),'lines':len(raw.splitlines())})
(HERE/'source-index.json').write_text(json.dumps(index,indent=2)+'\n',encoding='utf-8')
(HERE/'build-report.json').write_text(json.dumps({'pdf':str(PDF),'sha256':hashlib.sha256(PDF.read_bytes()).hexdigest(),'sections':doc.headings,'figures':len(drawings)},indent=2)+'\n',encoding='utf-8')
print(json.dumps({'pdf':str(PDF),'pages':doc.page,'figures':len(drawings),'sections':doc.headings},indent=2))
