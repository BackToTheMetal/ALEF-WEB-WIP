/*
 * Copyright 2003-2019 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.mps.typechecking;

import jetbrains.mps.errors.item.NodeReportItem;
import jetbrains.mps.lang.pattern.INodeMatchingPattern;
import jetbrains.mps.typechecking.backend.TypecheckingSessionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.language.SConcept;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.project.Project;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The façade to launching typechecking operations. This is the entry point for executing typechecking queries, requesting new sessions,
 * and running special computations either in "isolation" or reusing a shared session.
 * <p/>
 * This class is a mixin of {@link TypecheckingSessionHandler} and {@link TypecheckingQueries} and provides the implementation for the latter, which
 * uses the backend API for selecting the correct provider and then forwards the query to it.
 * <p/>
 * One should not try to cache instances of this class, rather the method {@link TypecheckingFacade#getFromContext()} should be used
 * exclusively, and the instance management is up to the implementation.
 *
 * @author Fedor Isakov
 */
public abstract class TypecheckingFacade
    extends TypecheckingSessionHandler
    implements TypecheckingQueries, TypecheckingComputations
{

  /**
   * Provides access to an instance of {@code TypecheckingFacade} that is available from the environment.
   * The implementation is responsible for selecting the correct facade instance depending on the context, such as
   * whether the current thread is a UI thread etc.
   *
   * @throws IllegalStateException if no instance is available in the current context.
   */
  @NotNull
  public static TypecheckingFacade getFromContext() {
    return getContextInstance();
  }

  abstract public <C> C getData(Class<? extends C> dataClass);

  @Override
  public SNode getTypeOf(SNode expression) {
    if (expression == null) return null;
    return activeController().getTypeOf(expression);
  }

  @Override
  public SNode getInferredType(SNode expression) {
    if (expression == null) return null;
    return activeController().getInferredType(expression);
  }

  @Override
  public boolean convertsTo(@NotNull SNode typeA, @NotNull SNode typeB) {
    return activeController().convertsTo(typeA, typeB);
  }

  @Override
  public boolean isSubtype(SNode typeA, SNode typeB) {
    if (typeA == null || typeB == null) return false;
    return activeController().isSubtype(typeA, typeB);
  }

  @Override
  public boolean isStrongSubtype(SNode typeA, SNode typeB) {
    if (typeA == null || typeB == null) return false;
    return activeController().isStrongSubtype(typeA, typeB);
  }

  @NotNull
  @Override
  public Collection<SNode> getImmediateSupertypes(@NotNull SNode type) {
    return activeController().getImmediateSupertypes(type);
  }

  @Override
  public SNode coerceType(SNode type, @NotNull SConcept typeConcept) {
    if (type == null) return null;
    return activeController().coerceType(type, typeConcept);
  }

  @Nullable
  @Override
  public SNode coerceType(SNode type, @NotNull INodeMatchingPattern targetPattern) {
    if (type == null) return null;
    return activeController().coerceType(type, targetPattern);
  }

  @Override
  public SNode strongCoerceType(SNode type, @NotNull SConcept typeConcept) {
    if (type == null) return null;
    return activeController().strongCoerceType(type, typeConcept);
  }

  @Nullable
  @Override
  public SNode strongCoerceType(SNode type, @NotNull INodeMatchingPattern targetPattern) {
    if (type == null) return null;
    return activeController().strongCoerceType(type, targetPattern);
  }

  @Override
  public void checkRecursively(SNode root, Consumer<? super NodeReportItem> errorsConsumer) {
    activeController().checkRecursively(root, errorsConsumer);
  }

  @Override
  public void clearCache(SNode root) {
    activeController().clearCache(root);
  }

  @Override
  public boolean isCacheUpToDate(SNode root) {
    return activeController().isCacheUpToDate(root);
  }

  @Override
  public CacheState getCacheState(SNode root) {
    return activeController().getCacheState(root);
  }

  public Map<String, Object> configure(Project project) {
    return activeController().configure(project);
  }

  protected static TypecheckingFacade getContextInstance() {
    return CONTEXT_INSTANCE.get();
  }

  /** Browser component shutdown/startup owns the event-thread facade explicitly. */
  protected static void clearContextInstance() { CONTEXT_INSTANCE.remove(); }

  // initialized by TypecheckingFacadeComponent
  protected static Supplier<TypecheckingFacade> DEFAULT_INSTANCE_FACTORY;

  private static ThreadLocal<TypecheckingFacade> CONTEXT_INSTANCE = ThreadLocal.withInitial(
      () -> {
        if (DEFAULT_INSTANCE_FACTORY != null) {
          return DEFAULT_INSTANCE_FACTORY.get();

        } else {
          throw new IllegalStateException("TypecheckingFacade factory not initialized");
        }
      }
  );


}
