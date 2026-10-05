import com.sun.source.tree.*;
import com.sun.source.util.*;
import java.nio.file.*;
import java.util.*;
import javax.tools.*;

/** Syntax-aware removal of explicitly excluded members; all other Metalava text is preserved. */
public final class AdaptStubs {
  record Edit(int start, int end) {}

  public static void main(String[] args) throws Exception {
    Path input = Path.of(args[0]);
    Path output = Path.of(args[1]);
    Set<String> exclusions = new TreeSet<>(Arrays.asList(args).subList(2, args.length));
    Set<String> found = new TreeSet<>();
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    try (var files = Files.walk(input);
         var manager = compiler.getStandardFileManager(null, null, null)) {
      for (Path file : files.filter(p -> p.toString().endsWith(".java")).sorted().toList()) {
        String text = Files.readString(file);
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavacTask task = (JavacTask) compiler.getTask(null, manager, diagnostics,
            List.of("-proc:none", "--source", "21"), null,
            manager.getJavaFileObjects(file));
        CompilationUnitTree unit = task.parse().iterator().next();
        if (diagnostics.getDiagnostics().stream().anyMatch(d -> d.getKind() == Diagnostic.Kind.ERROR)) {
          throw new IllegalStateException("Cannot parse " + file + ": " + diagnostics.getDiagnostics());
        }
        SourcePositions positions = Trees.instance(task).getSourcePositions();
        List<Edit> edits = new ArrayList<>();
        new TreeScanner<Void, String>() {
          @Override public Void visitClass(ClassTree tree, String owner) {
            String name = owner + "." + tree.getSimpleName();
            return super.visitClass(tree, name);
          }
          @Override public Void visitMethod(MethodTree tree, String owner) {
            String params = String.join(",", tree.getParameters().stream()
                .map(p -> p.getType().toString().replace(" ", "")).toList());
            String id = owner + "#" + tree.getName() + "(" + params + ")";
            if (exclusions.contains(id)) {
              if (!found.add(id)) throw new IllegalStateException("Duplicate exclusion match: " + id);
              edits.add(new Edit((int) positions.getStartPosition(unit, tree),
                  (int) positions.getEndPosition(unit, tree)));
              return null;
            }
            return super.visitMethod(tree, owner);
          }
        }.scan(unit, unit.getPackageName().toString());
        StringBuilder updated = new StringBuilder(text);
        edits.stream().sorted(Comparator.comparingInt(Edit::start).reversed())
            .forEach(e -> updated.delete(e.start(), e.end()));
        Path destination = output.resolve(input.relativize(file));
        Files.createDirectories(destination.getParent());
        Files.writeString(destination, updated.toString());
      }
    }
    if (!found.equals(exclusions)) {
      exclusions.removeAll(found);
      throw new IllegalStateException("Policy exclusions did not match: " + exclusions);
    }
    for (String id : found) System.out.println("Excluded: " + id);
  }
}
