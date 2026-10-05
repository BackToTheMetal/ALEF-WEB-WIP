/*
 * Copyright 2003-2022 JetBrains s.r.o.
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
package jetbrains.mps.lang.typesystem.runtime;

import jetbrains.mps.lang.smodel.generator.smodelAdapter.SConceptOperations;
import jetbrains.mps.languageScope.LanguageScope;
import jetbrains.mps.logging.Logger;
import jetbrains.mps.newTypesystem.rules.SingleTermRules;
import jetbrains.mps.typesystem.inference.NamespaceRank;
import org.jetbrains.mps.openapi.language.SAbstractConcept;
import org.jetbrains.mps.openapi.language.SLanguage;
import org.jetbrains.mps.openapi.model.SNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

/*
 *  Synchronized.
 */
public class RuleSet<T extends IApplicableToConcept> {
  private static final java.util.Map<Class<?>, String> RULE_NAMESPACES = new java.util.HashMap<>();

  /** Registers stable language ownership without relying on stripped Java class names. */
  public static void registerRuleNamespace(Class<?> ruleClass, String languageNamespace) {
    java.util.Objects.requireNonNull(ruleClass);
    java.util.Objects.requireNonNull(languageNamespace);
    String prior = RULE_NAMESPACES.putIfAbsent(ruleClass, languageNamespace);
    if (prior != null && !prior.equals(languageNamespace)) {
      throw new IllegalStateException("Conflicting rule language ownership");
    }
  }

  /** Deployment must unregister its rule classes after disposing sessions and rule caches. */
  public static void unregisterRuleNamespace(Class<?> ruleClass) { RULE_NAMESPACES.remove(ruleClass); }

  static String namespaceOf(Object rule) {
    String namespace = RULE_NAMESPACES.get(rule.getClass());
    if (namespace == null) throw new IllegalStateException("Typesystem rule class has no registered language namespace");
    return namespace;
  }

  private final Logger LOG = Logger.getLogger(RuleSet.class);

  private static final String TYPESYSTEM_SUFFIX = ".typesystem";
  private final ConcurrentMap<SAbstractConcept, List<T>> myRules = new ConcurrentHashMap<>();

  private final SingleTermRules<T> mySingleTermRules = new SingleTermRules<T>() {

    @Override
    protected List<SAbstractConcept> getParents(SAbstractConcept nextConcept) {
      return SConceptOperations.getDirectSuperConcepts(nextConcept, false);
    }

    @Override
    protected Iterable<T> allForConcept(SAbstractConcept concept, LanguageScope langScope) {
      return getAllApplicableTo(concept, langScope);
    }

    @Override
    protected boolean isOverriding(T rule) {
      return rule instanceof ICheckingRule_Runtime && ((ICheckingRule_Runtime) rule).overrides();
    }
  };


  public void addRuleSetItem(Set<T> rules, NamespaceRank namespaceRank) {
    for (T rule : rules) {
      try {
        addRule_internal(rule, namespaceRank);
      } catch (Throwable ex) {
        LOG.error("Error initializing rule '"+String.valueOf(rule)+"'", ex);
      }
    }
    mySingleTermRules.purgeCache();
  }

  public void addRuleSetItem(Set<T> rules) {
    addRuleSetItem(rules, NamespaceRank.ZERO);
  }

  @Deprecated
  public void addRule(T rule) {
    addRule_internal(rule, NamespaceRank.ZERO);
    mySingleTermRules.purgeCache();
  }

  private void addRule_internal(T rule, NamespaceRank namespaceRank) {
    SAbstractConcept concept = rule.getApplicableConcept();
    myRules.compute(concept, updateRules(rule, namespaceRank));
  }

  private BiFunction<SAbstractConcept, List<T>, List<T>> updateRules(T newRule, NamespaceRank namespaceRank) {
    return (concept, rules) -> {
      if (rules == null || !rules.contains(newRule)) {
        // copy on write pattern
        ArrayList<T> rulesCopy = rules != null ? new ArrayList<>(rules) : new ArrayList<>(2);
        rulesCopy.add(newRule);
        if (rulesCopy.size() > 1 && namespaceRank != NamespaceRank.ZERO) {
          rulesCopy.sort(Comparator.comparing(this::getNamespace, Comparator.comparingInt(namespaceRank::getRank)));
        }
        rules = rulesCopy;
      }
      return rules;
    };
  }

  /**
   * Returns a set of rules with predictable iteration order: on the node concept, from most specific to most generic.
   * @param term
   * @return
   */
  public Set<T> getRules(SNode term) {
    return mySingleTermRules.lookupRules(term);
  }

  private Iterable<T> getAllApplicableTo(SAbstractConcept concept, LanguageScope scope) {
    List<T> rules = myRules.getOrDefault(concept, Collections.emptyList());
    if (!rules.isEmpty()) {
      // operate on a copy
      ArrayList<T> rulesCopy = new ArrayList<>(rules);
      rulesCopy.removeIf(r -> !scope.containsNamespace(getNamespace(r)));
      rules = Collections.unmodifiableList(rulesCopy);
    }
    return rules;
  }

  private String getNamespace(T rule) {
    return namespaceOf(rule);
  }

  public void clear() {
    myRules.clear();
    mySingleTermRules.purgeCache();
  }
}
