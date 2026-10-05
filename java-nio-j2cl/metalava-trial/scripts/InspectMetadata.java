import com.sun.source.tree.*;
import com.sun.source.util.*;
import java.nio.file.*;
import java.util.*;
import javax.lang.model.element.Modifier;
import javax.tools.*;

/** Read sealed declarations and enum order from syntax trees without running the library. */
public final class InspectMetadata {
  public static void main(String[] args) throws Exception {
    try (var paths = Files.walk(Path.of(args[0]));
         var manager = ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null)) {
      var files = paths.filter(p -> p.toString().endsWith(".java")).sorted().toList();
      JavacTask task = (JavacTask) ToolProvider.getSystemJavaCompiler().getTask(null, manager, null,
          List.of("-proc:none", "--source", "21"), null, manager.getJavaFileObjectsFromPaths(files));
      List<String> result = new ArrayList<>();
      for (CompilationUnitTree unit : task.parse()) {
        new TreeScanner<Void, String>() {
          @Override public Void visitClass(ClassTree tree, String owner) {
            if (tree.getSimpleName().length() == 0) return null;
            String name = owner + "." + tree.getSimpleName();
            if (tree.getModifiers().getFlags().contains(Modifier.SEALED)) result.add("sealed\t" + name);
            if (tree.getKind() == Tree.Kind.ENUM) {
              List<String> constants = new ArrayList<>();
              for (Tree member : tree.getMembers()) {
                if (member instanceof VariableTree field && field.getInitializer() instanceof NewClassTree)
                  constants.add(field.getName().toString());
              }
              result.add("enum\t" + name + "\t" + String.join(",", constants));
            }
            return super.visitClass(tree, name);
          }
        }.scan(unit, unit.getPackageName().toString());
      }
      result.stream().sorted().forEach(System.out::println);
    }
  }
}
