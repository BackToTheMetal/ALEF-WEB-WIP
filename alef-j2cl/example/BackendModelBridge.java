package alefj2cl.example;

import elemental2.core.Global;
import elemental2.core.JsArray;
import java.util.*;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import jetbrains.mps.project.ModuleId;
import jetbrains.mps.project.Solution;
import jetbrains.mps.project.structure.modules.SolutionDescriptor;
import jetbrains.mps.smodel.*;
import jetbrains.mps.smodel.adapter.ids.SConceptId;
import jetbrains.mps.smodel.adapter.ids.MetaIdHelper;
import jetbrains.mps.smodel.adapter.structure.MetaAdapterFactory;
import jetbrains.mps.smodel.loading.ModelLoadingState;
import mpsj2cl.editor.BrowserEditorHost;
import org.jetbrains.mps.openapi.language.*;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.model.SModel;
import org.jetbrains.mps.openapi.model.ResolveInfo;
import org.jetbrains.mps.openapi.persistence.*;

/** Authorized graph codec and lease policy for the existing generated editor. */
final class BackendModelBridge implements jetbrains.mps.smodel.SNode.BrowserChangeGuard, com.intellij.openapi.command.CommandProcessor.BrowserTransactionGuard {
  private final BrowserEditorHost host;
  private final String comparisonModule;
  private final List<Solution> ownedModules=new ArrayList<>();
  private final Map<String,MemoryModel> models=new LinkedHashMap<>();
  private final Map<String,org.jetbrains.mps.openapi.model.SModelReference> absentComparisonModels=new HashMap<>();
  private final Map<String,JsPropertyMap<Object>> metadata=new LinkedHashMap<>();
  private final Map<String,String> rootIds=new HashMap<>();
  private final Map<String,Double> leaseDeadlines=new HashMap<>();
  private final Map<String,Double> modelDeadlines=new HashMap<>();
  private final Map<String,String> pendingReservations=new HashMap<>();
  private final Map<String,JsPropertyMap<Object>> restricted=new HashMap<>();
  private final Map<String,JsPropertyMap<Object>> dynamicOrigins=new HashMap<>();
  private final Set<String> dirty=new HashSet<>();
  private boolean installing=true;
  private final Map<String,JsPropertyMap<Object>> checkpoints=new HashMap<>();
  private final Map<String,String> checkpointModels=new HashMap<>();
  private Set<String> priorDirty=new HashSet<>();
  private boolean commandActive,commandRejected;
  private record Pending(JsPropertyMap<Object> input,String model,SNode parent,SContainmentLink role){}
  private final ArrayDeque<Pending> pending=new ArrayDeque<>();
  private int loadedNodes,totalNodes;private boolean closed;
  BackendModelBridge(BrowserEditorHost host){this(host,null);}
  BackendModelBridge(BrowserEditorHost host,String comparisonModule){this.host=host;this.comparisonModule=comparisonModule;}
  void loadAsync(String json,Runnable completed,java.util.function.Consumer<Throwable> failure){
    try{loadWrite(()->prepareLoad(json));javax.swing.SwingUtilities.invokeLater(()->loadBatch(completed,failure));}catch(Throwable problem){failure.accept(problem);}
  }
  private void loadWrite(Runnable action){
    // Snapshot loading is not an editing command and must not enter workspace
    // history or discard the user's redo stack.
    if(comparisonModule==null)host.getProject().getModelAccess().executeCommand(action);
    else host.getProject().getModelAccess().runWriteAction(action);
  }
  String loadProgress(){return "{\"completed\":"+loadedNodes+",\"total\":"+totalNodes+"}";}
  private void prepareLoad(String json){
    var graph=object(Global.JSON.parse(json));Map<String,Solution> modules=new LinkedHashMap<>();
    for(Object item:array(graph,"models")){
      var input=object(item);String moduleId=string(input,"moduleId"),modelRef=string(input,"reference");
      // Accessory language models are hosted by a read-only solution in the
      // browser, preserving their global model IDs and original references.
      boolean readOnly=comparisonModule!=null||Boolean.TRUE.equals(input.get("readOnly"));
      String ownerId=comparisonModule!=null?comparisonModule:readOnly?"8270cb09-651a-458a-9f4e-86e3e4423c92":moduleId;
      Solution solution=modules.get(ownerId);
      if(solution==null){
        var descriptor=new SolutionDescriptor();descriptor.setId(ModuleId.fromString(ownerId));descriptor.setNamespace(comparisonModule!=null?"ALEF comparison "+comparisonModule:readOnly?"ALEF standard models":string(input,"moduleName"));
        solution=new Solution(descriptor,null){@Override public boolean isPackaged(){return false;}};
        host.getProject().addModule(solution);ownedModules.add(solution);modules.put(ownerId,solution);
      }
      var original=PersistenceFacade.getInstance().createModelReference(modelRef);
      var reference=comparisonModule==null?original:new SModelReference(solution.getModuleReference(),SModelId.generate(),original.getName());
      var model=new MemoryModel(reference,readOnly);
      solution.registerModel(model);models.put(modelRef,model);metadata.put(modelRef,input);
      for(Object rootValue:array(input,"roots")){
        var root=object(rootValue);pending.add(new Pending(root,modelRef,null,null));totalNodes+=countNodes(root);
        if(!readOnly){rootIds.put(new SNodePointer(model.getReference(),PersistenceFacade.getInstance().createNodeId(string(root,"id"))).toString(),string(root,"rootId"));}
      }
    }
    if(graph.get("deletedDraftRoots")!=null)for(Object value:array(graph,"deletedDraftRoots")){
      var removed=object(value);var pointer=new SNodePointer(PersistenceFacade.getInstance().createModelReference(string(removed,"model")),PersistenceFacade.getInstance().createNodeId(string(removed,"nodeId")));
      rootIds.put(pointer.toString(),string(removed,"rootId"));
    }
    if(graph.get("dirtyRootIds")!=null)for(Object id:array(graph,"dirtyRootIds"))dirty.add(Js.asString(id));
  }
  private static int countNodes(JsPropertyMap<Object> input){int total=1;for(Object child:array(input,"children"))total+=countNodes(object(object(child).get("node")));return total;}
  private void loadBatch(Runnable completed,java.util.function.Consumer<Throwable> failure){
    if(closed)return;
    try{
      loadWrite(()->{
        long start=System.currentTimeMillis();int batch=0;
        while(!pending.isEmpty()&&batch++<1000&&System.currentTimeMillis()-start<12){
          var next=pending.removeFirst();SNode node=decode(next.input(),next.model());
          if(next.parent()==null)models.get(next.model()).addRootNode(node);else next.parent().addChild(next.role(),node);
          for(Object value:array(next.input(),"children")){
            var child=object(value);SContainmentLink role=null;for(var candidate:node.getConcept().getContainmentLinks())if(MetaIdHelper.getAggregation(candidate).serialize().equals(string(child,"role")))role=candidate;
            if(role==null)throw new IllegalArgumentException("Unknown containment role in server graph");pending.addLast(new Pending(object(child.get("node")),next.model(),node,role));
          }
          loadedNodes++;
        }
      });
      if(!pending.isEmpty()){javax.swing.SwingUtilities.invokeLater(()->loadBatch(completed,failure));return;}
      loadWrite(()->{
        // Scopes use the entire authorized graph. Only the server persists imports.
        for(var model:models.values())for(var imported:models.values())if(model!=imported)model.addModelImport(imported.getReference());
      });
      installing=false;
      if(comparisonModule==null){jetbrains.mps.smodel.SNode.setBrowserChangeGuard(this);com.intellij.openapi.command.CommandProcessor.getInstance().setBrowserTransactionGuard(this);}
      else for(var model:models.values())model.comparisonSealed=true;
      completed.run();
    }catch(Throwable problem){failure.accept(problem);}
  }
  private SNode decode(JsPropertyMap<Object> json,String model){
    SConcept concept=MetaAdapterFactory.getConcept(SConceptId.deserialize(string(json,"concept")),string(json,"conceptName"));
    SNode node=new jetbrains.mps.smodel.SNode(concept,PersistenceFacade.getInstance().createNodeId(string(json,"id")));
    MemoryModel owner=models.get(model);owner.addLanguage(concept.getLanguage());
    var properties=object(json.get("properties"));properties.forEach(key->{
      for(SProperty property:concept.getProperties())if(MetaIdHelper.getProperty(property).serialize().equals(key)){node.setProperty(property,Js.asString(properties.get(key)));return;}
      throw new IllegalArgumentException("Unknown property in server graph");
    });
    for(Object refValue:array(json,"references")){
      var ref=object(refValue);SReferenceLink role=null;
      for(var candidate:concept.getReferenceLinks())if(MetaIdHelper.getAssociation(candidate).serialize().equals(string(ref,"role")))role=candidate;
      if(role==null)throw new IllegalArgumentException("Unknown reference role in server graph");
      if("RESTRICTED".equals(string(ref,"kind"))){
        restricted.put(edgeKey(model,node.getNodeId().toString(),string(ref,"role")),ref);
        // No hidden identity/resolveInfo is reconstructed. Retain an unresolved,
        // neutral association so editor diagnostics cannot reveal target data.
        node.setReference(role,ResolveInfo.of("Restricted reference"));
      }else{
        if("DYNAMIC".equals(string(ref,"kind")))dynamicOrigins.put(edgeKey(model,node.getNodeId().toString(),string(ref,"role")),ref);
        String targetReference=ref.get("model")==null?null:string(ref,"model");
        var target=targetReference==null?null:referenceTarget(targetReference);
        var id=PersistenceFacade.getInstance().createNodeId(string(ref,"nodeId"));
        String resolve=ref.get("resolveInfo")==null?null:string(ref,"resolveInfo");
        node.setReference(role,target==null?ResolveInfo.of(id,resolve):ResolveInfo.of(new SNodePointer(target,id),resolve));
      }
    }
    return node;
  }
  private org.jetbrains.mps.openapi.model.SModelReference referenceTarget(String reference){
    if(comparisonModule==null)return PersistenceFacade.getInstance().createModelReference(reference);
    if(models.containsKey(reference))return models.get(reference).getReference();
    // A model absent from this version stays unresolved. Never fall through to
    // a same-ID model in the editable workspace or the other comparison side.
    return absentComparisonModels.computeIfAbsent(reference,key->new SModelReference(null,SModelId.generate(),PersistenceFacade.getInstance().createModelReference(key).getName()));
  }
  String tree(){
    var result=JsPropertyMap.<Object>of();result.set("name","Shared workspace");var list=new JsArray<Object>();result.set("models",list);
    for(var entry:models.entrySet()){
      var model=entry.getValue();if(model.readOnly)continue;
      var item=JsPropertyMap.<Object>of();item.set("id",entry.getKey());item.set("name",model.getName().getValue());var roots=new JsArray<Object>();item.set("roots",roots);list.push(item);
      for(var root:model.getRootNodes()){
        var r=JsPropertyMap.<Object>of();r.set("id",root.getReference().toString());r.set("rootId",rootIds.get(root.getReference().toString()));r.set("name",root.getPresentation());r.set("concept",root.getConcept().getName());
        String virtualPackage=root.getProperty(SNodeUtil.property_BaseConcept_virtualPackage);r.set("virtualPackage",virtualPackage==null?"":virtualPackage);roots.push(r);
      }
    }return Global.JSON.stringify(result);
  }
  SModel writableModel(String reference){var model=models.get(reference);if(model==null||model.readOnly)throw new IllegalArgumentException("Choose a workspace model");return model;}
  boolean workspaceModel(SModel model){return model!=null&&models.containsValue(model)&&!((MemoryModel)model).readOnly;}
  void lease(String rootId,double until){leaseDeadlines.put(rootId,until);}
  void modelLease(String model,double until){modelDeadlines.put(model,until);}
  void reserve(String model,String rootId,String nodeId,double until){
    writableModel(model);String pointer=new SNodePointer(PersistenceFacade.getInstance().createModelReference(model),PersistenceFacade.getInstance().createNodeId(nodeId)).toString();
    rootIds.put(pointer,rootId);pendingReservations.put(model,nodeId);modelLease(model,until);lease(rootId,until);
  }
  void assignReservation(SModel model,SNode root){
    String id=pendingReservations.remove(model.getReference().toString());if(id==null)throw new IllegalStateException("Reserve a root before creating it");
    ((jetbrains.mps.smodel.SNode)root).setId(PersistenceFacade.getInstance().createNodeId(id));
  }
  void freeze(){leaseDeadlines.clear();modelDeadlines.clear();}
  String dirtyRoots(){return Global.JSON.stringify(dirty.toArray(new String[0]));}
  void acknowledgeSave(String json){
    // A save receipt only clears dirty markers. Keep native nodes, editors and
    // command history intact, and reject stale receipts before changing anything.
    var snapshots=object(Global.JSON.parse(json));
    snapshots.forEach(id->{
      if(!rootSnapshot(id).equals(Js.asString(snapshots.get(id))))
        throw new IllegalStateException("The local draft changed while Save was completing");
    });
    snapshots.forEach(dirty::remove);
  }
  String rootSnapshot(String id){
    for(var model:models.values())for(var root:model.getRootNodes())if(id.equals(rootIds.get(root.getReference().toString())))return Global.JSON.stringify(encode(root));
    if(rootIds.containsValue(id))return "null";
    throw new IllegalArgumentException("Root is unavailable");
  }
  String containmentTargets(String sourceReference){
    SNode source=PersistenceFacade.getInstance().createNodeReference(sourceReference).resolve(host.getProject().getRepository());
    if(source==null||source.getParent()==null||!workspaceModel(source.getModel()))throw new IllegalArgumentException("Choose contained model content");
    var targets=new JsArray<Object>();
    for(SNode root:source.getModel().getRootNodes())for(SNode node:descendants(root)){
      boolean descendant=false;for(SNode parent=node;parent!=null;parent=parent.getParent())if(parent==source)descendant=true;if(descendant)continue;
      for(var role:node.getConcept().getContainmentLinks())if(source.getConcept().isSubConceptOf(role.getTargetConcept())&&(role.isMultiple()||!node.getChildren(role).iterator().hasNext())){
        var item=JsPropertyMap.<Object>of();item.set("reference",node.getReference().toString());item.set("rootId",rootIds.get(root.getReference().toString()));item.set("role",MetaIdHelper.getAggregation(role).serialize());item.set("label",root.getPresentation()+" / "+node.getPresentation()+" / "+role.getName());targets.push(item);
      }
    }
    return Global.JSON.stringify(targets);
  }
  void moveContent(String sourceReference,String targetReference,String roleId,String beforeReference){
    var repository=host.getProject().getRepository();SNode source=PersistenceFacade.getInstance().createNodeReference(sourceReference).resolve(repository),target=PersistenceFacade.getInstance().createNodeReference(targetReference).resolve(repository);
    if(source==null||target==null||source.getParent()==null||source.getModel()!=target.getModel()||!workspaceModel(source.getModel()))throw new IllegalArgumentException("Move contained content within its model; use Move root for model relocation");
    for(SNode ancestor=target;ancestor!=null;ancestor=ancestor.getParent())if(ancestor==source)throw new IllegalArgumentException("Cannot move into a descendant");
    SContainmentLink selected=null;for(var role:target.getConcept().getContainmentLinks())if(MetaIdHelper.getAggregation(role).serialize().equals(roleId))selected=role;
    if(selected==null||!source.getConcept().isSubConceptOf(selected.getTargetConcept())||!selected.isMultiple()&&target.getChildren(selected).iterator().hasNext())throw new IllegalArgumentException("Choose an available compatible containment role");
    SNode anchor=beforeReference==null||beforeReference.isEmpty()?null:PersistenceFacade.getInstance().createNodeReference(beforeReference).resolve(repository);
    if(anchor!=null&&(anchor.getParent()!=target||anchor==source))throw new IllegalArgumentException("Insertion anchor must be another child of the target");
    if(beforeReference!=null&&!beforeReference.isEmpty()&&anchor==null)throw new IllegalArgumentException("Insertion anchor is unavailable");
    requireLease(source);requireLease(target);source.getParent().removeChild(source);target.insertChildBefore(selected,source,anchor);
  }
  private static List<SNode> descendants(SNode root){var result=new ArrayList<SNode>();result.add(root);for(var child:root.getChildren())result.addAll(descendants(child));return result;}
  JsPropertyMap<Object> encode(SNode node){
    var json=JsPropertyMap.<Object>of();json.set("id",node.getNodeId().toString());json.set("concept",MetaIdHelper.getConcept(node.getConcept()).serialize());json.set("conceptName",node.getConcept().getQualifiedName());
    var properties=JsPropertyMap.<Object>of();for(var p:node.getProperties())properties.set(MetaIdHelper.getProperty(p).serialize(),node.getProperty(p));json.set("properties",properties);
    var children=new JsArray<Object>();for(var child:node.getChildren()){var value=JsPropertyMap.<Object>of();value.set("role",MetaIdHelper.getAggregation(child.getContainmentLink()).serialize());value.set("node",encode(child));children.push(value);}json.set("children",children);
    var refs=new JsArray<Object>();Set<String> included=new HashSet<>();
    for(var reference:node.getReferences()){
      String role=MetaIdHelper.getAssociation(reference.getLink()).serialize();included.add(role);
      var preserved=restricted.get(edgeKey(node.getModel().getReference().toString(),node.getNodeId().toString(),role));
      if(preserved!=null){refs.push(preserved);continue;}
      // Use the native-resolved authorized target for local operations. Preserve
      // the original dynamic edge while that association remains unchanged.
      var dynamic=dynamicOrigins.get(edgeKey(node.getModel().getReference().toString(),node.getNodeId().toString(),role));
      if(dynamic!=null&&reference.getTargetSModelReference()!=null&&reference.getTargetNodeId()!=null&&reference.getTargetSModelReference().toString().equals(string(dynamic,"model"))&&reference.getTargetNodeId().toString().equals(string(dynamic,"nodeId"))){refs.push(dynamic);continue;}
      var value=JsPropertyMap.<Object>of();value.set("role",role);value.set("kind","STATIC");value.set("model",reference.getTargetSModelReference()==null?null:reference.getTargetSModelReference().toString());value.set("nodeId",reference.getTargetNodeId()==null?null:reference.getTargetNodeId().toString());
      value.set("resolveInfo",reference instanceof jetbrains.mps.smodel.SReference?((jetbrains.mps.smodel.SReference)reference).getResolveInfo():null);refs.push(value);
    }
    // Generated code can clear an unresolved association; the serialized proposal
    // must still carry every original protected edge on a retained source node.
    for(var role:node.getConcept().getReferenceLinks()){
      String id=MetaIdHelper.getAssociation(role).serialize();var edge=restricted.get(edgeKey(node.getModel().getReference().toString(),node.getNodeId().toString(),id));
      if(edge!=null&&!included.contains(id))refs.push(edge);
    }
    json.set("references",refs);return json;
  }
  @Override public void beforeNodeChange(SNode node){
    requireMutableSnapshot(node.getModel());
    if(installing||node.getModel()==null||!models.containsValue(node.getModel()))return;
    requireLease(node);
    var root=node.getContainingRoot();String id=rootIds.get(root.getReference().toString());
    checkpoint(id,node.getModel(),root);
    dirty.add(id);
  }
  private void requireLease(SNode node){
    if(!canChangeNode(node)){commandRejected=true;throw new IllegalStateException("Acquire an editing lock for this root first");}
  }
  @Override public boolean canChangeNode(SNode node){
    if(node==null||node.getModel()==null)return false;
    var root=node.getContainingRoot();String id=rootIds.get(root.getReference().toString());
    Double deadline=leaseDeadlines.get(id),parent=modelDeadlines.get(node.getModel().getReference().toString());
    if(parent!=null&&(deadline==null||parent>deadline))deadline=parent;
    return id!=null&&deadline!=null&&System.currentTimeMillis()<deadline;
  }
  @Override public void beforeRootChange(SModel model,SNode root,boolean adding){
    requireMutableSnapshot(model);
    if(installing||model==null||!models.containsValue(model))return;
    String reference=model.getReference().toString();Double until=modelDeadlines.get(reference);
    String pointer=new SNodePointer(model.getReference(),root.getNodeId()).toString();String id=rootIds.get(pointer);
    if(until==null||System.currentTimeMillis()>=until||id==null){commandRejected=true;throw new IllegalStateException("Root structural changes require a current model lease and root reservation");}
    checkpoint(id,model,root.getModel()==model?root:null);
    dirty.add(id);
  }
  private void checkpoint(String id,SModel model,SNode root){if(commandActive&&!checkpoints.containsKey(id)){checkpoints.put(id,root==null?null:encode(root));checkpointModels.put(id,model.getReference().toString());}}
  @Override public void begin(){if(installing)return;commandActive=true;commandRejected=false;checkpoints.clear();checkpointModels.clear();priorDirty=new HashSet<>(dirty);}
  @Override public void finish(boolean success){
    if(!commandActive)return;commandActive=false;
    if((!success||commandRejected)&&!checkpoints.isEmpty()){
      freeze();String snapshot=host.getProject().getModelAccess().computeReadAction(this::rollbackSnapshot);
      AlefEditorApp.rollbackRejectedCommand(snapshot);
    }
    checkpoints.clear();checkpointModels.clear();
  }
  private String rollbackSnapshot(){
    var graph=JsPropertyMap.<Object>of();var documents=new JsArray<Object>();graph.set("models",documents);graph.set("dirtyRootIds",priorDirty.toArray(new String[0]));var present=new HashSet<String>();
    for(var entry:models.entrySet()){
      String reference=entry.getKey();var document=object(Global.JSON.parse(Global.JSON.stringify(metadata.get(reference))));var roots=new JsArray<Object>();document.set("roots",roots);documents.push(document);
      for(SNode root:entry.getValue().getRootNodes()){
        String id=rootIds.get(root.getReference().toString());JsPropertyMap<Object> encoded=checkpoints.containsKey(id)?checkpoints.get(id):encode(root);if(encoded==null)continue;
        if(id!=null){encoded.set("rootId",id);present.add(id);}roots.push(encoded);
      }
      for(String id:checkpoints.keySet())if(!present.contains(id)&&reference.equals(checkpointModels.get(id))&&checkpoints.get(id)!=null){var encoded=checkpoints.get(id);encoded.set("rootId",id);roots.push(encoded);present.add(id);}
    }
    var deleted=new JsArray<Object>();graph.set("deletedDraftRoots",deleted);
    for(var entry:rootIds.entrySet())if(!present.contains(entry.getValue())&&priorDirty.contains(entry.getValue())){
      var pointer=PersistenceFacade.getInstance().createNodeReference(entry.getKey());var item=JsPropertyMap.<Object>of();item.set("rootId",entry.getValue());item.set("model",pointer.getModelReference().toString());item.set("nodeId",pointer.getNodeId().toString());deleted.push(item);
    }
    return Global.JSON.stringify(graph);
  }
  SNode comparisonRoot(String reference){var pointer=PersistenceFacade.getInstance().createNodeReference(reference);var model=models.get(pointer.getModelReference().toString());return model==null?null:model.getNode(pointer.getNodeId());}
  private static void requireMutableSnapshot(SModel model){if(model instanceof MemoryModel&&((MemoryModel)model).comparisonSealed)throw new IllegalStateException("Comparison models are read-only");}
  void close(){closed=true;pending.clear();if(comparisonModule==null){jetbrains.mps.smodel.SNode.setBrowserChangeGuard(null);com.intellij.openapi.command.CommandProcessor.getInstance().setBrowserTransactionGuard(null);}freeze();}
  void closeRepository(){close();for(var model:models.values())model.comparisonSealed=false;for(var module:ownedModules)host.getProject().removeModule(module);ownedModules.clear();models.clear();absentComparisonModels.clear();restricted.clear();dynamicOrigins.clear();}
  private static String edgeKey(String model,String node,String role){return model+"\n"+node+"\n"+role;}
  private static JsPropertyMap<Object> object(Object value){return Js.cast(value);}
  private static List<Object> array(JsPropertyMap<Object> object,String key){
    JsArray<Object> values=Js.cast(object.get(key));List<Object> result=new ArrayList<>();
    for(int i=0;i<values.length;i++)result.add(values.getAt(i));return result;
  }
  private static String string(JsPropertyMap<Object> object,String key){return Js.asString(object.get(key));}
  private static final class MemoryModel extends EditableModelDescriptor {
    final boolean readOnly;
    boolean comparisonSealed;
    MemoryModel(org.jetbrains.mps.openapi.model.SModelReference reference,boolean readOnly){super(reference,new NullDataSource());this.readOnly=readOnly;}
    @Override protected ModelLoadResult<jetbrains.mps.smodel.SModel> createModel(){return new ModelLoadResult<>(new jetbrains.mps.smodel.SModel(getReference()),ModelLoadingState.FULLY_LOADED);}
    @Override public boolean isReadOnly(){return readOnly;}
    @Override public void save(){throw new UnsupportedOperationException("Use governed Save");}
    @Override public void reloadFromSource(){throw new UnsupportedOperationException("Use snapshot synchronization");}
  }
}
