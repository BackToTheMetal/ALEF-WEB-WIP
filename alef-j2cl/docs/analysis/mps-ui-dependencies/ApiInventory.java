import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.*;
import javax.tools.*;
import com.sun.source.util.JavacTask;

/** Source-level declared API inventory from javac's --release platform view. */
public class ApiInventory {
    static Elements elements;
    static Types types;
    static List<String> rows;
    static String effectiveVisibility(Element e) {
        for (Element p=e; p!=null && !(p instanceof PackageElement); p=p.getEnclosingElement()) {
            if (p.getModifiers().contains(Modifier.PROTECTED)) return "protected";
        }
        return "public";
    }
    static String join(Collection<?> values) { return values.stream().map(Object::toString).collect(Collectors.joining(",")); }
    static String mods(Element e) {
        return e.getModifiers().stream().filter(m -> m != Modifier.NATIVE && m != Modifier.SYNCHRONIZED && m != Modifier.STRICTFP)
            .map(Object::toString).sorted().collect(Collectors.joining(" "));
    }
    static String params(List<? extends TypeParameterElement> ps) {
        return ps.stream().map(p -> p.getSimpleName()+" extends "+join(p.getBounds())).collect(Collectors.joining(","));
    }
    static boolean visible(Element e) { return e.getModifiers().contains(Modifier.PUBLIC) || e.getModifiers().contains(Modifier.PROTECTED); }
    static void add(String pkg, String owner, Element e, String kind, String key, String signature) {
        String deprecated = e.getAnnotationMirrors().stream().filter(a -> a.getAnnotationType().toString().equals("java.lang.Deprecated"))
            .map(a -> elements.getElementValuesWithDefaults(a).entrySet().stream().map(x -> x.getKey().getSimpleName()+"="+x.getValue()).sorted().collect(Collectors.joining(",")))
            .collect(Collectors.joining(";"));
        rows.add(String.join("\t", pkg, owner, e.getModifiers().contains(Modifier.PUBLIC)?"public":"protected", kind, key, signature.replace("\t"," ").replace("\n"," "), deprecated, effectiveVisibility(e)));
    }
    static void visit(TypeElement t) {
        if (!visible(t)) return;
        String pkg = elements.getPackageOf(t).getQualifiedName().toString(), owner = t.getQualifiedName().toString();
        add(pkg,owner,t,"type",owner,mods(t)+" "+t.getKind()+" "+owner+"<"+params(t.getTypeParameters())+"> extends "+t.getSuperclass()+" implements "+join(t.getInterfaces())+" permits "+join(t.getPermittedSubclasses()));
        for (Element e : t.getEnclosedElements()) {
            if (!visible(e)) continue;
            if (e instanceof TypeElement nested) { visit(nested); continue; }
            if (e instanceof ExecutableElement m) {
                String kind = e.getKind()==ElementKind.CONSTRUCTOR?"constructor":"method";
                String key = owner+"#"+m.getSimpleName()+"("+m.getParameters().stream().map(p->types.erasure(p.asType()).toString()).collect(Collectors.joining(","))+")";
                String sig = mods(m)+" <"+params(m.getTypeParameters())+"> "+m.getReturnType()+" "+m.getSimpleName()+"("+m.getParameters().stream().map(p->p.asType().toString()).collect(Collectors.joining(","))+") throws "+join(m.getThrownTypes())+" varargs="+m.isVarArgs()+" default="+m.getDefaultValue();
                add(pkg,owner,m,kind,key,sig);
            } else if (e instanceof VariableElement v) {
                add(pkg,owner,v,"field",owner+"#"+v.getSimpleName(),mods(v)+" "+v.asType()+" "+v.getSimpleName()+" constant="+String.valueOf(v.getConstantValue()).replace("\n","\\n").replace("\r","\\r").replace("\t","\\t"));
            }
        }
    }
    public static void main(String[] args) throws Exception {
        Files.createDirectories(Path.of("api-results"));
        for (String release : args) {
            rows = new ArrayList<>();
            JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();
            DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<>();
            try (StandardJavaFileManager fm=compiler.getStandardFileManager(diagnostics,null,null)) {
                var source=new SimpleJavaFileObject(URI.create("string:///Probe.java"),JavaFileObject.Kind.SOURCE) {
                    public CharSequence getCharContent(boolean ignore) { return "class Probe {}"; }
                };
                JavacTask task=(JavacTask)compiler.getTask(null,fm,diagnostics,List.of("--release",release,"--add-modules","java.desktop","-proc:none"),null,List.of(source));
                task.parse(); task.analyze(); elements=task.getElements(); types=task.getTypes();
                SortedSet<String> packages=new TreeSet<>();
                for(String moduleName:List.of("java.desktop","java.datatransfer")) {
                    ModuleElement module=elements.getModuleElement(moduleName);
                    for(var directive:module.getDirectives()) {
                        if(directive instanceof ModuleElement.ExportsDirective export && (export.getTargetModules()==null || export.getTargetModules().isEmpty())) {
                            PackageElement p=export.getPackage(); String name=p.getQualifiedName().toString();
                            if(name.equals("java.awt") || name.startsWith("java.awt.") || name.equals("javax.swing") || name.startsWith("javax.swing.")) {
                                packages.add(name);
                                for(Element e:p.getEnclosedElements()) if(e instanceof TypeElement t) visit(t);
                            }
                        }
                    }
                }
                if(diagnostics.getDiagnostics().stream().anyMatch(d->d.getKind()==Diagnostic.Kind.ERROR)) throw new IllegalStateException(diagnostics.getDiagnostics().toString());
                Collections.sort(rows);
                Files.write(Path.of("api-results", "jdk-"+release+".tsv"),Stream.concat(Stream.of("package\towner\tvisibility\tkind\tkey\tsignature\tdeprecated\teffectiveVisibility"),rows.stream()).toList());
                Files.write(Path.of("api-results","packages-"+release+".txt"),packages);
                Path queries=Path.of("api-results","method-queries.txt");
                if(Files.exists(queries)) {
                    List<String> resolutions=new ArrayList<>();
                    for(String query:Files.readAllLines(queries)) {
                        String owner=query.substring(0,query.indexOf('#')), method=query.substring(query.indexOf('#')+1);
                        TypeElement t=elements.getTypeElement(owner);
                        if(t==null) { resolutions.add(query+"\tMISSING_TYPE"); continue; }
                        List<String> matches=new ArrayList<>();
                        for(Element e:elements.getAllMembers(t)) if(e instanceof ExecutableElement m && e.getKind()==ElementKind.METHOD && visible(e)) {
                            String key=m.getSimpleName()+"("+m.getParameters().stream().map(p->types.erasure(p.asType()).toString()).collect(Collectors.joining(","))+")";
                            if(key.equals(method)) matches.add(((TypeElement)m.getEnclosingElement()).getQualifiedName()+"#"+key+" ["+mods(m)+"]");
                        }
                        resolutions.add(query+"\t"+(matches.isEmpty()?"MISSING":join(matches)));
                    }
                    Files.write(Path.of("api-results","resolution-"+release+".tsv"),resolutions);
                }
                System.out.println("JDK "+release+": "+packages.size()+" packages, "+rows.size()+" API declarations");
            }
        }
    }
}
