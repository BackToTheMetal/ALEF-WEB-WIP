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

package walkingkooka.j2cl.java.text.generated;

public final class DecimalFormatSymbolsProvider {

  public final static String ANNOTATION_PROCESSOR_LOCALES_FILTER = preventInlining(",en,en-US,en-GB,nl,nl-NL");

  public final static String SELECTED_LOCALES = preventInlining("en,en-GB,en-US,nl,nl-NL");



  public final static String DATA = preventInlining("5,1,en,.#E,,∞,XXX,-.NaN,;%‰01,en-GB,.#E,,∞,GBP,-.NaN,;%‰01,en-US,.#E,,∞,USD,-.NaN,;%‰01,nl,,#E,.∞,XXX,-,NaN,;%‰01,nl-NL,,#E,.∞,EUR,-,NaN,;%‰0");

  private static String preventInlining(final String string){
    return string;
  }
}
