/*
 * Copyright 2003-2016 JetBrains s.r.o.
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

import jetbrains.mps.newTypesystem.rules.DoubleTermRules;
import jetbrains.mps.languageScope.LanguageScope;
import jetbrains.mps.typesystem.inference.NamespaceRank;
import jetbrains.mps.util.Pair;
import org.jetbrains.mps.openapi.language.SAbstractConcept;
import org.jetbrains.mps.openapi.model.SNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

/*
 *   Synchronized.
 */
public class DoubleRuleSet<T extends IApplicableTo2Concepts> {
  private static final String TYPESYSTEM_SUFFIX = ".typesystem";
  ConcurrentMap<Pair<SAbstractConcept, SAbstractConcept>, List<T>> myRules = new ConcurrentHashMap<>();

  private DoubleTermRules<T> myDoubleTermRules = new DoubleTermRules<T>() {

    @Override
    protected Iterable<T> allForConceptPair(SAbstractConcept leftConcept, SAbstractConcept rightConcept, LanguageScope langScope) {
      return getAllApplicableTo(leftConcept, rightConcept, langScope);
    }

    @Override
    protected boolean isOverriding(T rule) {
      return rule instanceof InequationReplacementRule_Runtime && ((InequationReplacementRule_Runtime) rule).overrides();
    }
  };

  public void addRuleSetItem(Set<T> rules, NamespaceRank namespaceRank) {
    for (T rule : rules) {
      SAbstractConcept concept1 = rule.getApplicableConcept1();
      SAbstractConcept concept2 = rule.getApplicableConcept2();
      Pair<SAbstractConcept, SAbstractConcept> pair = new Pair<>(concept1, concept2);
      myRules.compute(pair, updateRules(rule, namespaceRank));
    }
    myDoubleTermRules.purgeCache();
  }

  private BiFunction<Pair<SAbstractConcept, SAbstractConcept>, List<T>, List<T>> updateRules(T newRule, NamespaceRank namespaceRank) {
    return (pair, rules) -> {
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
   * Left node's concept comes first in the compound key built for comparison.
   */
  public Set<T> getRules(SNode leftTerm, SNode righTerm) {
    return myDoubleTermRules.lookupRules(leftTerm, righTerm);
  }

  private Iterable<T> getAllApplicableTo(SAbstractConcept leftConcept, SAbstractConcept rightConcept, LanguageScope scope) {
    Pair<SAbstractConcept, SAbstractConcept> conceptPair = new Pair<>(leftConcept, rightConcept);
    if (!myRules.containsKey(conceptPair)) return Collections.emptyList();

    List<T> result = new ArrayList<>(4);
    List<T> rules = myRules.get(conceptPair);
    synchronized (rules) {
      for (T rule : rules) {
        if (scope.containsNamespace(getNamespace(rule))) {
          result.add(rule);
        }
      }
    }
    return Collections.unmodifiableList(result);
  }

  private String getNamespace(T rule) {
    return RuleSet.namespaceOf(rule);
  }

  public void clear() {
    myRules.clear();
    myDoubleTermRules.purgeCache();
  }
}
