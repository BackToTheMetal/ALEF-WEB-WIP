/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.j2cl.java.util.currency.generated;

public final class CurrencyProvider {

  public final static String ANNOTATION_PROCESSOR_LOCALES_FILTER = preventInlining(",en,en-US,en-GB,nl,nl-NL");

  public final static String SELECTED_LOCALES = preventInlining("en,en-GB,en-US,nl,nl-NL");



  public final static String DATA = preventInlining("4,EUR,2,978,€,1,nl-NL,0,GBP,2,826,£,1,en-GB,0,USD,2,840,US$,1,en-US,1,$,2,en,en-US,XXX,-1,999,XXX,0,1,¤,3,en,en-GB,en-US");

  private static String preventInlining(final String string){
    return string;
  }
}
