/*
 * Copyright 2003-2024 JetBrains s.r.o.
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
package jetbrains.mps.persistence;

import jetbrains.mps.logging.Logger;
import jetbrains.mps.smodel.adapter.ids.SConceptFeatureId;
import jetbrains.mps.smodel.adapter.ids.SConceptId;
import jetbrains.mps.smodel.adapter.ids.SContainmentLinkId;
import jetbrains.mps.smodel.adapter.ids.SLanguageId;
import jetbrains.mps.smodel.adapter.ids.SPropertyId;
import jetbrains.mps.smodel.adapter.ids.SReferenceLinkId;
import jetbrains.mps.smodel.language.ConceptRegistryUtil;
import jetbrains.mps.smodel.language.LanguageRegistry;
import jetbrains.mps.smodel.language.LanguageRuntime;
import jetbrains.mps.smodel.runtime.ConceptDescriptor;
import jetbrains.mps.smodel.runtime.ConceptKind;
import jetbrains.mps.smodel.runtime.LinkDescriptor;
import jetbrains.mps.smodel.runtime.PropertyDescriptor;
import jetbrains.mps.smodel.runtime.ReferenceDescriptor;
import jetbrains.mps.smodel.runtime.StaticScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.mps.openapi.persistence.ModelLoadingOption;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * PROVISIONAL API, DO NOT USE
 * <p/>
 * This class hides mechanism to access aspects of meta-model we need for persistence purposes.
 * Generally, this information could be retrieved from SConcept/ConceptDescriptor and friends, however,
 * there are two use-cases when we can't rely on general MPS mechanism:
 * 1) Model merge, primarily (or only) merge driver (git-invoked conflict resolution tool).
 * 2) Convert models to binary during build.
 * <p/>
 * In either case, we read model first, and then save, and need save to know (and keep) information we've read.
 * <p/>
 * The reasons not to use general MPS  mechanism are:
 * (a) We don't want to pay MPS start-up costs
 * (b) Languages used in the models might not be available in classpath (Doesn't apply to (2) - we do know them during build)
 * <p/>
 * The idea is to fill this provider with information read, and use it from model write. This provider shall not survive
 * single read/write pair for a given model. Although perhaps in the future we might utilize it to keep model-specific
 * "Debug Registry", which used to be global, see {@link RegularMetaModelInfo#RegularMetaModelInfo(MetaModelInfoProvider)}.
 * <p/>
 * To certain extent, this class serves to overcome limitations of SConcept API, as it doesn't expose e.g. scope or kind. Once (and if)
 * we decide to expose these from SConcept, there would be no need in this mediator. Perhaps, it's the right way to go? XXX revisit
 * <p/>
 * Note, this class replaces {@code jetbrains.mps.persistence.ModelEnvironmentInfo} which was likely intended for the similar purpose,
 * but is ugly and doesn't suite modern (v9-bis) persistence well.
 * <p/>
 * There's implementation for most use-cases, {@link jetbrains.mps.persistence.MetaModelInfoProvider.RegularMetaModelInfo}, which merely delegates
 * to appropriate general MPS facilities, and doesn't support change (attempt to change is no-op).
 * <p/>
 * IMPLEMENTATION NOTE: use {@link jetbrains.mps.persistence.MetaModelInfoProvider.BaseMetaModelInfo} as base class, do not implement this interface directly.
 * <p/>
 * Instances are not thread-safe unless noted otherwise.
 * <p/>
 * All methods are allowed to return <code>null</code> to indicate provider knows nothing about attribute questioned.
 * Setters arguments, except for id (i.e. name, concept kind, etc), may be <code>null</code> as well.
 *
 * @author Artem Tikhomirov
 * @see jetbrains.mps.persistence.MetaModelInfoProvider.BaseMetaModelInfo
 */
public interface MetaModelInfoProvider {
  /**
   * Indicates whether we intend to use model read without access to regular (MPS instance) meta info.
   * The models read with this option set to true, could be serialized without access to concept registry of MPS (of course,
   * if respective ModelFactory supports this. Our default(aka xml) and binary do).
   */
  enum MetaInfoLoadingOption implements ModelLoadingOption {
    KEEP_READ
  }

  String getLanguageName(SLanguageId lang);

  void setLanguageName(SLanguageId lang, String name);

  /**
   * @return FIXME qualified concept name at the moment, short name once we switch to short names in persistence
   */
  String getConceptName(SConceptId concept);

  void setConceptName(SConceptId concept, String name);

  String getPropertyName(SPropertyId property);

  void setPropertyName(SPropertyId property, String name);

  String getAssociationName(SReferenceLinkId link);

  void setAssociationName(SReferenceLinkId link, String name);

  String getAggregationName(SContainmentLinkId link);

  void setAggregationName(SContainmentLinkId link, String name);

  ConceptKind getKind(SConceptId concept);

  void setKind(SConceptId concept, ConceptKind kind);

  StaticScope getScope(SConceptId concept);

  void setScope(SConceptId concept, StaticScope scope);

  // since 2023.2
  Boolean isInterfaceConcept(SConceptId concept);

  void setInterfaceConcept(SConceptId concept);

  Boolean isUnordered(SContainmentLinkId link);

  void setUnordered(SContainmentLinkId link, boolean unordered);

  /**
   * no need for corresponding setter as the moment we read a feature (property/link) meta-info, we can be sure it's not transient
   * FIXME what if feature id is INVALID, how about an idea to report it as transient?
   */
  boolean isTransient(SConceptFeatureId feature);

  /**
   * This method makes sense only for concepts with
   * {@link #getKind(jetbrains.mps.smodel.adapter.ids.SConceptId) kind} == {@link jetbrains.mps.smodel.runtime.ConceptKind#IMPLEMENTATION_WITH_STUB}
   */
  SConceptId getStubConcept(SConceptId origin);

  void setStubConcept(SConceptId origin, SConceptId stub);

  /**
   * Base implementation, clients shall extend this class rather than implement {@link jetbrains.mps.persistence.MetaModelInfoProvider} directly.
   * Always answers <code>null</code>, and a no-op for updates.
   */
  class BaseMetaModelInfo implements MetaModelInfoProvider {

    @Override
    public String getLanguageName(SLanguageId lang) {
      return null;
    }

    @Override
    public void setLanguageName(SLanguageId lang, String name) {
      // intentionally no-op
    }

    @Override
    public String getConceptName(SConceptId concept) {
      return null;
    }

    @Override
    public void setConceptName(SConceptId concept, String name) {
      // intentionally no-op
    }

    @Override
    public String getPropertyName(SPropertyId property) {
      return null;
    }

    @Override
    public void setPropertyName(SPropertyId property, String name) {
      // intentionally no-op
    }

    @Override
    public String getAssociationName(SReferenceLinkId link) {
      return null;
    }

    @Override
    public void setAssociationName(SReferenceLinkId link, String name) {
      // intentionally no-op
    }

    @Override
    public String getAggregationName(SContainmentLinkId link) {
      return null;
    }

    @Override
    public void setAggregationName(SContainmentLinkId link, String name) {
      // intentionally no-op
    }

    @Override
    public ConceptKind getKind(SConceptId concept) {
      return null;
    }

    @Override
    public void setKind(SConceptId concept, ConceptKind kind) {
      // intentionally no-op
    }

    @Override
    public StaticScope getScope(SConceptId concept) {
      return null;
    }

    @Override
    public void setScope(SConceptId concept, StaticScope scope) {
      // intentionally no-op
    }

    @Override
    public Boolean isInterfaceConcept(SConceptId concept) {
      return null;
    }

    @Override
    public void setInterfaceConcept(SConceptId concept) {
      // intentionally no-op
    }

    @Override
    public Boolean isUnordered(SContainmentLinkId link) {
      return null;
    }

    @Override
    public void setUnordered(SContainmentLinkId link, boolean unordered) {
      // intentionally no-op
    }

    @Override
    public boolean isTransient(SConceptFeatureId feature) {
      return false;
    }

    @Override
    public SConceptId getStubConcept(SConceptId origin) {
      return null;
    }

    @Override
    public void setStubConcept(SConceptId origin, SConceptId stub) {
      // intentionally left no-op
    }
  }

  /**
   * Default implementation to use in general MPS scenarios.
   * Sort of {@link jetbrains.mps.smodel.runtime.ConceptDescriptor}, limited to methods essential for persistence.
   * Ensures non-<code>null</code> values (empty strings for names to satisfy persistence) and reasonable defaults otherwise.
   * <p/>
   */
  class RegularMetaModelInfo extends BaseMetaModelInfo {
    private static final Logger LOG = Logger.getLogger(MetaModelInfoProvider.class);
    private final MetaModelInfoProvider myDebugRegistry;

    public RegularMetaModelInfo() {
      // just ensure non-null value for debugRegistry, which doesn't keep nor answer anything
      this(new BaseMetaModelInfo());
    }

    /**
     * Replacement for {@code DebugRegistry} singleton, that allowed to obtain 'last known' value for model write or to update the value on model read.
     * If you've got a place to keep MetaModelInfoProvider instance (likely, {@code StuffedMetaModelInfo} and can share it between different
     * {@code RegularMetaModelInfo} instances to accomplish the same as what {@code DebugRegistry} intended to provide.
     *
     * I suppose {@code new StuffedMetaModelInfo(new BaseMetaModelInfo())} would be ok to initialize that 'debug registry' instance.
     */
    public RegularMetaModelInfo(@NotNull MetaModelInfoProvider debugRegistry) {
      myDebugRegistry = debugRegistry;
    }

    @Override
    public String getLanguageName(SLanguageId lang) {
      final LanguageRuntime langRT = LanguageRegistry.getInstance().getLanguage(lang);
      if (langRT != null) {
        return langRT.getNamespace();
      }
      String name = myDebugRegistry.getLanguageName(lang);
      return name == null ? "" : name;
    }

    @Override
    public String getConceptName(SConceptId concept) {
      ConceptDescriptor descriptor = ConceptRegistryUtil.getConceptDescriptor(concept);
      if (descriptor != null) {
        return descriptor.getConceptFqName();
      }
      String name = myDebugRegistry.getConceptName(concept);
      return name == null ? "" : name;
    }

    @Override
    public String getPropertyName(SPropertyId property) {
      ConceptDescriptor descriptor = ConceptRegistryUtil.getConceptDescriptor(property.getConceptId());
      if (descriptor != null) {
        final PropertyDescriptor pd = descriptor.getPropertyDescriptor(property);
        if (pd != null) {
          return pd.getName();
        }
      }
      String name = myDebugRegistry.getPropertyName(property);
      return name == null ? "" : name;
    }

    @Override
    public String getAssociationName(SReferenceLinkId link) {
      ConceptDescriptor descriptor = ConceptRegistryUtil.getConceptDescriptor(link.getConceptId());
      if (descriptor != null) {
        final ReferenceDescriptor ld = descriptor.getRefDescriptor(link);
        if (ld != null) {
          return ld.getName();
        }
      }
      String name = myDebugRegistry.getAssociationName(link);
      return name == null ? "" : name;
    }

    @Override
    public String getAggregationName(SContainmentLinkId link) {
      ConceptDescriptor descriptor = ConceptRegistryUtil.getConceptDescriptor(link.getConceptId());
      if (descriptor != null) {
        final LinkDescriptor ld = descriptor.getLinkDescriptor(link);
        if (ld != null) {
          return ld.getName();
        }
      }
      String name = myDebugRegistry.getAggregationName(link);
      return name == null ? "" : name;
    }

    @Override
    public ConceptKind getKind(SConceptId concept) {
      ConceptDescriptor descriptor = ConceptRegistryUtil.getConceptDescriptor(concept);
      if (descriptor != null) {
        return descriptor.getConceptKind();
      }
      return ConceptKind.NORMAL;
    }

    @Override
    public StaticScope getScope(SConceptId concept) {
      ConceptDescriptor descriptor = ConceptRegistryUtil.getConceptDescriptor(concept);
      if (descriptor != null) {
        return descriptor.getStaticScope();
      }
      return StaticScope.GLOBAL;
    }

    @Override
    public Boolean isInterfaceConcept(SConceptId concept) {
      ConceptDescriptor descriptor = ConceptRegistryUtil.getConceptDescriptor(concept);
      if (descriptor != null) {
        return descriptor.isInterfaceConcept();
      }
      // in persistence, we used to treat everything with SConceptId as a Concept
      return Boolean.FALSE;
    }

    @Override
    public Boolean isUnordered(SContainmentLinkId link) {
      ConceptDescriptor descriptor = ConceptRegistryUtil.getConceptDescriptor(link.getConceptId());
      if (descriptor != null) {
        final LinkDescriptor ld = descriptor.getLinkDescriptor(link);
        if (ld != null) {
          return ld.isUnordered();
        }
      }
      return Boolean.FALSE;
    }

    @Override
    public boolean isTransient(SConceptFeatureId feature) {
      ConceptDescriptor descriptor = ConceptRegistryUtil.getConceptDescriptor(feature.getConceptId());
      if (descriptor != null) {
        if (feature instanceof SContainmentLinkId) {
          final LinkDescriptor ld = descriptor.getLinkDescriptor((SContainmentLinkId) feature);
          return ld != null && ld.isTransient();
        }
        if (feature instanceof SPropertyId) {
          final PropertyDescriptor pd = descriptor.getPropertyDescriptor((SPropertyId) feature);
          return pd != null && pd.isTransient();
        }
        if (feature instanceof SReferenceLinkId) {
          final ReferenceDescriptor rd = descriptor.getRefDescriptor((SReferenceLinkId) feature);
          return rd != null && rd.isTransient();
        }
        assert false : String.format("Feature class: %s, id: %s. toString: %s", feature.getClass(), feature.getIdValue(), feature);
        // fall-through, false is ok for defaults
      }
      return false;
    }

    @Override
    public SConceptId getStubConcept(SConceptId origin) {
      ConceptDescriptor cd = ConceptRegistryUtil.getConceptDescriptor(origin);
      if (cd == null) {
        LOG.info(String.format("No descriptor for concept %s", origin));
        return null;
      }
      if (cd.getVersion() < 2) {
        LOG.error(String.format("Outdated descriptor for concept %s (%s), please re-generate respective language", cd.getConceptFqName(), origin));
        return null;
      }
      return cd.getStubConceptId();
    }

    @Override
    public void setLanguageName(SLanguageId lang, String name) {
      myDebugRegistry.setLanguageName(lang, name);
    }

    @Override
    public void setConceptName(SConceptId concept, String name) {
      myDebugRegistry.setConceptName(concept, name);
    }

    @Override
    public void setPropertyName(SPropertyId property, String name) {
      myDebugRegistry.setPropertyName(property, name);
    }

    @Override
    public void setAssociationName(SReferenceLinkId link, String name) {
      myDebugRegistry.setAssociationName(link, name);
    }

    @Override
    public void setAggregationName(SContainmentLinkId link, String name) {
      myDebugRegistry.setAggregationName(link, name);
    }
  }

  /**
   * Provider which is filled with desired information.
   * Queries, if could not be resolved with the information filled in, are directed to delegate.
   * Depending on actual delegate used, may answer <code>null</code>.
   */
  class StuffedMetaModelInfo extends BaseMetaModelInfo {
    private final Map<SLanguageId, String> myLanguageNames = new HashMap<>();
    private final Map<SConceptId, String> myConceptNames = new HashMap<>();
    private final Map<SPropertyId, String> myPropertyNames = new HashMap<>();
    private final Map<SReferenceLinkId, String> myAssociationNames = new HashMap<>();
    private final Map<SContainmentLinkId, String> myAggregationNames = new HashMap<>();
    private final Map<SContainmentLinkId, Boolean> myUnordered = new HashMap<>();
    private final Map<SConceptId, StaticScope> myScope = new HashMap<>();
    private final Map<SConceptId, ConceptKind> myKind = new HashMap<>();
    private final Map<SConceptId, SConceptId> myStubs = new HashMap<>();
    private final Set<SConceptId> myInterfaceConcepts = new HashSet<>();
    private final MetaModelInfoProvider myDelegate;

    public StuffedMetaModelInfo(@NotNull MetaModelInfoProvider delegate) {
      myDelegate = delegate;
    }

    /**
     * Fill another StuffedMetaModelInfo instance with information known to this one.
     * It might be necessary if we don't want to use this provider as is due to delegate.
     * <p/>
     * There would be no need in this method if we could pass proper StuffedMetaModelInfo
     * instance right into modelFactory.load(). However, given Map(String,String) for options,
     * we have to assume there might be inappropriate delegate which we can't rely on (i.e. merge has to
     * combine few stuffed sources, asks them one by one, first one to answer wins)
     */
    public void populate(@NotNull StuffedMetaModelInfo other) {
      other.myLanguageNames.putAll(myLanguageNames);
      other.myConceptNames.putAll(myConceptNames);
      other.myPropertyNames.putAll(myPropertyNames);
      other.myAssociationNames.putAll(myAssociationNames);
      other.myAggregationNames.putAll(myAggregationNames);
      other.myUnordered.putAll(myUnordered);
      other.myScope.putAll(myScope);
      other.myKind.putAll(myKind);
      other.myStubs.putAll(myStubs);
      other.myInterfaceConcepts.addAll(myInterfaceConcepts);
    }

    @Override
    public void setLanguageName(SLanguageId lang, String name) {
      if (isEmpty(name)) {
        myLanguageNames.remove(lang);
      } else {
        myLanguageNames.put(lang, name);
      }
      myDelegate.setLanguageName(lang, name);
    }

    @Override
    public void setConceptName(SConceptId concept, String name) {
      if (isEmpty(name)) {
        myConceptNames.remove(concept);
      } else {
        myConceptNames.put(concept, name);
      }
      myDelegate.setConceptName(concept, name);
    }

    @Override
    public void setPropertyName(SPropertyId property, String name) {
      if (isEmpty(name)) {
        myPropertyNames.remove(property);
      } else {
        myPropertyNames.put(property, name);
      }
      myDelegate.setPropertyName(property, name);
    }

    @Override
    public void setAssociationName(SReferenceLinkId link, String name) {
      if (isEmpty(name)) {
        myAssociationNames.remove(link);
      } else {
        myAssociationNames.put(link, name);
      }
      myDelegate.setAssociationName(link, name);
    }

    @Override
    public void setAggregationName(SContainmentLinkId link, String name) {
      if (isEmpty(name)) {
        myAggregationNames.remove(link);
      } else {
        myAggregationNames.put(link, name);
      }
      myDelegate.setAggregationName(link, name);
    }

    @Override
    public void setKind(SConceptId concept, ConceptKind kind) {
      if (kind == null) {
        myKind.remove(concept);
      } else {
        myKind.put(concept, kind);
      }
      myDelegate.setKind(concept, kind);
    }

    @Override
    public void setScope(SConceptId concept, StaticScope scope) {
      if (scope == null) {
        myScope.remove(concept);
      } else {
        myScope.put(concept, scope);
      }
      myDelegate.setScope(concept, scope);
    }

    @Override
    public void setInterfaceConcept(SConceptId concept) {
      myInterfaceConcepts.add(concept);
      myDelegate.setInterfaceConcept(concept);
    }

    @Override
    public void setUnordered(SContainmentLinkId link, boolean unordered) {
      myUnordered.put(link, unordered);
      myDelegate.setUnordered(link, unordered);
    }

    @Override
    public void setStubConcept(SConceptId origin, SConceptId stub) {
      if (stub == null) {
        myStubs.remove(origin);
      } else {
        myStubs.put(origin, stub);
      }
      myDelegate.setStubConcept(origin, stub);
    }

    private static boolean isEmpty(String name) {
      return name == null || name.isEmpty();
    }

    @Override
    public String getLanguageName(SLanguageId lang) {
      String name = myLanguageNames.get(lang);
      if (name != null) {
        return name;
      }
      return myDelegate.getLanguageName(lang);
    }

    @Override
    public String getConceptName(SConceptId concept) {
      String name = myConceptNames.get(concept);
      if (name != null) {
        return name;
      }
      return myDelegate.getConceptName(concept);
    }

    @Override
    public String getPropertyName(SPropertyId property) {
      String name = myPropertyNames.get(property);
      if (name != null) {
        return name;
      }
      return myDelegate.getPropertyName(property);
    }

    @Override
    public String getAssociationName(SReferenceLinkId link) {
      String name = myAssociationNames.get(link);
      if (name != null) {
        return name;
      }
      return myDelegate.getAssociationName(link);
    }

    @Override
    public String getAggregationName(SContainmentLinkId link) {
      String name = myAggregationNames.get(link);
      if (name != null) {
        return name;
      }
      return myDelegate.getAggregationName(link);
    }

    @Override
    public ConceptKind getKind(SConceptId concept) {
      ConceptKind kind = myKind.get(concept);
      if (kind != null) {
        return kind;
      }
      return myDelegate.getKind(concept);
    }

    @Override
    public StaticScope getScope(SConceptId concept) {
      StaticScope scope = myScope.get(concept);
      if (scope != null) {
        return scope;
      }
      return myDelegate.getScope(concept);
    }

    @Override
    public Boolean isInterfaceConcept(SConceptId concept) {
      return myInterfaceConcepts.contains(concept) ? Boolean.TRUE : myDelegate.isInterfaceConcept(concept);
    }

    @Override
    public Boolean isUnordered(SContainmentLinkId link) {
      Boolean unordered = myUnordered.get(link);
      if (unordered != null) {
        return unordered;
      }
      return myDelegate.isUnordered(link);
    }

    @Override
    public boolean isTransient(SConceptFeatureId feature) {
      // just to use base implementation explicitly, to show no delegation (myDelegate) is deliberate.
      // I believe for scenarios with StuffedMetaModelInfo, when we read model first, we shall never encounter a case
      // with a transient feature (here, I don't want to even think about features that used to be non-transient and then
      // became transient, it's ok to read and write them back then, I suppose)
      return super.isTransient(feature);
    }

    @Override
    public SConceptId getStubConcept(SConceptId origin) {
      SConceptId stub = myStubs.get(origin);
      if (stub != null) {
        return stub;
      }
      return myDelegate.getStubConcept(origin);
    }
  }
}
