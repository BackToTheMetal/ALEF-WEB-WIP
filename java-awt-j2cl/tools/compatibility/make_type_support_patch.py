#!/usr/bin/env python3
"""Generate the coordinated J2CL 20260402 Class/compiler patch without editing upstream."""
from __future__ import annotations

import argparse
import difflib
from pathlib import Path


def replace_once(text: str, old: str, new: str) -> str:
    if text.count(old) != 1:
        raise ValueError(f"Expected one upstream anchor, found {text.count(old)}: {old[:100]!r}")
    return text.replace(old, new, 1)


def generate(upstream: Path) -> str:
    files: dict[str, tuple[str, str]] = {}

    def read(name: str) -> str:
        return (upstream / name).read_text(encoding="utf-8")

    def save(name: str, edited: str) -> None:
        files[name] = (read(name), edited)

    name = "jre/java/java/lang/Class.java"
    src = read(name)
    methods = '''  /** Internal bridge for java.lang.reflect.Array using this Class's exact array metadata. */
  public Object $newArray(int[] dimensions) {
    if (dimensions == null) {
      throw new NullPointerException();
    }
    if (dimensions.length == 0 || dimensions.length > 255 - dimensionCount) {
      throw new IllegalArgumentException("Invalid array component type or dimension count");
    }
    for (int length : dimensions) {
      if (length < 0) {
        throw new NegativeArraySizeException();
      }
    }
    if (this == void.class) {
      throw new IllegalArgumentException("void cannot be an array component type");
    }
    if (!ctor.isPrimitiveType() && !ctor.hasTypeRelations()) {
      throw new UnsupportedOperationException("Class array type metadata is unavailable");
    }
    return $createReflectively(dimensions, ctor, dimensionCount);
  }

  @JsMethod(namespace = "vmbootstrap.Arrays")
  private static native Object $createReflectively(int[] dimensions, Constructor ctor, int existingDimensions);

  /** Tests the runtime Java type without constructing or initializing the target class. */
  public boolean isInstance(Object object) {
    return object != null && isAssignableFrom(object.getClass());
  }

  @SuppressWarnings("unchecked")
  public T cast(Object object) {
    if (object != null && !isInstance(object)) {
      throw new ClassCastException("Cannot cast to " + getName());
    }
    return (T) object;
  }

  @SuppressWarnings("unchecked")
  public <U> Class<? extends U> asSubclass(Class<U> superClass) {
    if (superClass == null) {
      throw new NullPointerException();
    }
    if (!superClass.isAssignableFrom(this)) {
      throw new ClassCastException("Class is not assignable to the requested superclass");
    }
    return (Class<? extends U>) this;
  }

  /** Tests identity or widening reference conversion, with no boxing or numeric widening. */
  public boolean isAssignableFrom(Class<?> source) {
    if (source == null) {
      throw new NullPointerException();
    }
    boolean targetPrimitive = !isArray() && ctor.isPrimitiveType();
    boolean sourcePrimitive = !source.isArray() && source.ctor.isPrimitiveType();
    if (targetPrimitive || sourcePrimitive) {
      return this == source;
    }
    if (this == Object.class) {
      return true;
    }
    if (source.isArray()) {
      if (isArray()) {
        return getComponentType().isAssignableFrom(source.getComponentType());
      }
      return this == Cloneable.class || this == Serializable.class;
    }
    if (isArray()) {
      return false;
    }
    // Native JsType and JsFunction class literals are erased to shared bootstrap proxies.
    // They cannot provide a distinct nominal Java relation, so never guess from prototypes.
    if (!ctor.hasTypeRelations() || !source.ctor.hasTypeRelations()) {
      throw new UnsupportedOperationException("Class type relation metadata is unavailable");
    }
    return ctor.isAssignableFrom(source.ctor);
  }

'''
    save(name, replace_once(src, "  public T[] getEnumConstants() {", methods + "  public T[] getEnumConstants() {"))

    name = "jre/java/java/lang/reflect/Array.java"
    src = read(name)
    start = src.index("  // There is a good reason to not implement the generic newInstance methods:")
    end = src.index("  private Array() {}", start)
    src = src[:start] + '''  public static Object newInstance(Class<?> componentType, int length) {
    if (componentType == null) {
      throw new NullPointerException();
    }
    return componentType.$newArray(new int[] {length});
  }

  public static Object newInstance(Class<?> componentType, int... dimensions) {
    if (componentType == null) {
      throw new NullPointerException();
    }
    return componentType.$newArray(dimensions);
  }

''' + src[end:]
    save(name, src)

    name = "jre/java/javaemul/internal/vmbootstrap/Arrays.impl.java.js"
    src = read(name)
    helpers = '''  /**
   * Allocates using Class's retained leaf constructor and exact dimension count.
   * Null trailing lengths represent component dimensions that remain unallocated.
   * @param {!Array<number>} dimensions
   * @param {!Constructor} leafType
   * @param {number} existingDimensions
   * @return {!Array<*>}
   * @public
   */
  static $createReflectively(dimensions, leafType, existingDimensions) {
    const lengths = dimensions.slice();
    for (let i = 0; i < existingDimensions; i++) {
      lengths.push(null);
    }
    return Arrays.$createInternal_(lengths, leafType, Util.$getClassIsInstance(leafType));
  }

'''
    save(name, replace_once(src, "class Arrays {\n", "class Arrays {\n" + helpers))

    name = "jre/java/javaemul/internal/Constructor.java"
    src = read(name)
    methods = '''  @JsOverlay
  public final boolean hasTypeRelations() {
    return Util.$hasClassTypeRelations(this);
  }

  @JsOverlay
  public final boolean isAssignableFrom(Constructor source) {
    return Util.$isClassAssignableFrom(this, source);
  }

'''
    src = replace_once(src, '  @JsType(isNative = true, namespace = "nativebootstrap")', methods + '  @JsType(isNative = true, namespace = "nativebootstrap")')
    src = replace_once(src, '    public static native boolean $isPrimitiveType(Constructor ctor);', '''    public static native boolean $isPrimitiveType(Constructor ctor);

    public static native boolean $hasClassTypeRelations(Constructor ctor);

    public static native boolean $isClassAssignableFrom(Constructor target, Constructor source);''')
    save(name, src)

    name = "jre/java/javaemul/internal/nativebootstrap/Util.impl.java.js"
    src = read(name)
    helpers = '''  /**
   * Records nominal type relations separately from optional display-name metadata.
   * The owning constructor at index zero detects inherited or missing metadata.
   * @param {!Constructor} ctor
   * @param {!Array<!Constructor>} types
   * @param {!Function} isInstance
   * @public
   */
  static $setClassTypeRelations(ctor, types, isInstance) {
    ctor.prototype.$$typeSupertypes = types;
    // The compiler passes a direct method reference, so Closure retains the actual
    // predicate even when it moves static constructor properties to free functions.
    ctor.prototype.$$typeIsInstance = isInstance;
  }

  /**
   * @param {!Constructor} ctor
   * @return {!Function}
   * @public
   */
  static $getClassIsInstance(ctor) {
    if (Util.$isPrimitiveType(ctor)) {
      // The existing primitive array predicates accept their unboxed value types.
      // Type-directed Java stores already perform primitive conversions/checks.
      return () => true;
    }
    const predicate = ctor.prototype.$$typeIsInstance;
    if (!predicate) {
      throw new Error('Missing compiler-emitted J2CL array predicate');
    }
    return predicate;
  }

  /**
   * @param {!Constructor} ctor
   * @return {boolean}
   * @public
   */
  static $hasClassTypeRelations(ctor) {
    const types = ctor.prototype.$$typeSupertypes;
    return !!types && types[0] === ctor;
  }

  /**
   * Traverses compiler-emitted constructor identities without instantiation or clinit.
   * @param {!Constructor} target
   * @param {!Constructor} source
   * @return {boolean}
   * @public
   */
  static $isClassAssignableFrom(target, source) {
    const pending = [source];
    const visited = new Set();
    while (pending.length) {
      const current = pending.pop();
      if (current === target) {
        return true;
      }
      if (visited.has(current)) {
        continue;
      }
      visited.add(current);
      const types = current.prototype.$$typeSupertypes;
      if (!types || types[0] !== current) {
        throw new Error('Missing compiler-emitted J2CL Class type relations');
      }
      for (let i = 1; i < types.length; i++) {
        pending.push(types[i]);
      }
    }
    return false;
  }

'''
    save(name, replace_once(src, "class Util {\n", "class Util {\n" + helpers))

    name = "transpiler/java/com/google/j2cl/transpiler/ast/RuntimeMethods.java"
    src = read(name)
    descriptor = '''                              .put(
                                  "$setClassTypeRelations",
                                  MethodInfo.newBuilder()
                                      .setReturnType(PrimitiveTypes.VOID)
                                      .setParameters(
                                          TypeDescriptors.get().javaemulInternalConstructor,
                                          TypeDescriptors.get().javaLangObjectArray,
                                          TypeDescriptors.get().nativeFunction)
                                      .build())
'''
    anchor = '                              .put(\n                                  "$setClassMetadata",'
    save(name, replace_once(src, anchor, descriptor + anchor))

    name = "transpiler/java/com/google/j2cl/transpiler/passes/ImplementClassMetadataViaConstructors.java"
    src = read(name)
    src = replace_once(src, "import com.google.j2cl.transpiler.ast.Expression;", "import com.google.j2cl.transpiler.ast.ArrayLiteral;\nimport com.google.j2cl.transpiler.ast.DeclaredTypeDescriptor;\nimport com.google.j2cl.transpiler.ast.Expression;\nimport com.google.j2cl.transpiler.ast.FieldAccess;\nimport com.google.j2cl.transpiler.ast.FieldDescriptor;\nimport com.google.j2cl.transpiler.ast.JsInfo;")
    src = replace_once(src, "    String name =\n", '''    // Nominal Java type relations are independent of display-name metadata. Native
    // overlays and JsFunction implementations have erased Class identities.
    if (!targetTypeDeclaration.isNative() && !targetTypeDeclaration.isJsFunctionImplementation()) {
      // One literal assignment lets Closure discard unused relation metadata.
      // Incremental Array.push calls retain the entire constructor graph.
      ArrayLiteral.Builder relations = ArrayLiteral.newBuilder()
          .setTypeDescriptor(TypeDescriptors.get().javaLangObjectArray)
          .addValueExpressions(new JsConstructorReference(type.getDeclaration()));
      DeclaredTypeDescriptor superType = targetTypeDeclaration.getSuperTypeDescriptor();
      if (superType != null) {
        relations.addValueExpressions(superType.getMetadataConstructorReference());
      }
      for (DeclaredTypeDescriptor superInterface : targetTypeDeclaration.getInterfaceTypeDescriptors()) {
        if (!superInterface.isNative() && !superInterface.isJsFunctionInterface()) {
          relations.addValueExpressions(superInterface.getMetadataConstructorReference());
        }
      }
      type.addLoadTimeStatement(
          RuntimeMethods.createUtilMethodCall(
                  "$setClassTypeRelations",
                  new JsConstructorReference(type.getDeclaration()),
                  relations.build(),
                  FieldAccess.Builder.from(
                          FieldDescriptor.newBuilder()
                              .setEnclosingTypeDescriptor(
                                  type.getTypeDescriptor().getIsInstanceMethodDescriptor().getEnclosingTypeDescriptor())
                              .setTypeDescriptor(TypeDescriptors.get().nativeFunction)
                              .setName(type.getTypeDescriptor().getIsInstanceMethodDescriptor().getName())
                              .setOriginalJsInfo(JsInfo.RAW)
                              .setStatic(true)
                              .build())
                      .build())
              .makeStatement(SourcePosition.NONE));
    }

    String name =
''')
    save(name, src)

    patch = []
    for path, (before, after) in sorted(files.items()):
        patch.append(f"diff --git a/{path} b/{path}\n")
        patch.extend(difflib.unified_diff(before.splitlines(keepends=True), after.splitlines(keepends=True), fromfile=f"a/{path}", tofile=f"b/{path}"))
    return "".join(patch)


def main() -> None:
    parser = argparse.ArgumentParser(__doc__)
    parser.add_argument("--upstream", required=True, type=Path)
    parser.add_argument("--output", type=Path, default=Path("third_party/patches/j2cl-type-support.patch"))
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    patch = generate(args.upstream)
    if args.check:
        if args.output.read_text(encoding="utf-8") != patch:
            raise SystemExit("Type-support patch does not match generator output")
        print("Type-support patch is reproducible")
    else:
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(patch, encoding="utf-8", newline="\n")
        print(f"Wrote {args.output}")


if __name__ == "__main__":
    main()
