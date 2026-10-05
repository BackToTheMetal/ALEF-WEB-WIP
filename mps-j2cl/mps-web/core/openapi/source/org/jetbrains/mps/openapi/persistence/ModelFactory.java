/*
 * Copyright 2003-2026 JetBrains s.r.o.
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
package org.jetbrains.mps.openapi.persistence;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.model.SModel;
import org.jetbrains.mps.openapi.model.SModelName;
import org.jetbrains.mps.openapi.persistence.datasource.DataSourceType;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.jetbrains.mps.openapi.persistence.MFProblem.NO_PROBLEM;

/**
 * Represents a data source loading/saving/upgrading strategy.
 *
 * The location resides rather on the {@link DataSource}
 * side than here while the storage parameters belong here.
 * NB: The <code>ModelFactory</code> knows about <code>DataSource</code> but
 * not vice versa.
 *
 * creates/saves/loads models (instances of SModel) from data sources.
 *
 * @author apyshkin, others
 */
public interface ModelFactory {
  /**
   * Determines whether the provided data source is maintained by this model factory instance.
   * Consider calling it before load/create/upgrade methods in order to avoid {@link UnsupportedDataSourceException}.
   *
   * @return true iff the given data source can be managed by this factory
   */
  boolean supports(@NotNull DataSource dataSource);

  /**
   * Some issues can arise when one tries to create a model onto some data source.
   * For instance, data source might be read-only or such model name already exists and data source does not support two different
   * models with the same names.
   *
   * Call it before #create invocation.
   *
   * @param dataSource target of you model write
   * @param modelName the new model's name
   * @param options the additional options you will pass to {@link #create}
   * @return the problem from which you can extract some text presentation
   * or {@link MFProblem#NO_PROBLEM} if there is no problem
   */
  @NotNull
  default MFProblem canCreate(@NotNull DataSource dataSource,
                              @NotNull SModelName modelName,
                              @NotNull ModelLoadingOption... options) {
    if (!supports(dataSource)) {
      return new DataSourceNotSupportedProblem(dataSource);
    }
    return NO_PROBLEM;
  }


  /**
   * Creates a new model with the supplied <code>modelName</code> on the given <code>DataSource</code>.
   * Might consider additional options provided in the <code>options</code> varargs.
   * [General rule: options.contain(SomeOption) => SomeOption is on]
   *
   * @param dataSource -- location to create the model in
   * @param modelName -- a new model name (note that it might be constructed easily from full-qualified <code>String</code>)
   * @param options -- custom options
   * @see ModelLoadingOption
   * @return newly created model
   *
   * @throws UnsupportedDataSourceException iff {@link #supports(DataSource)} returns false
   * @throws ModelCreationException iff there was an irrecoverable problem during creation (for instance model file already exists)
   *                                or {@link #canCreate} returns false
   */
  @NotNull
  SModel create(@NotNull DataSource dataSource,
                @NotNull SModelName modelName,
                @NotNull ModelLoadingOption... options) throws UnsupportedDataSourceException,
                                                               ModelCreationException;

  /**
   * Loads an existing model from the given <code>DataSource</code>.
   * Might consider additional options provided in the <code>options</code> varargs.
   * [General rule: options.contain(SomeOption) => SomeOption is on]
   *
   * @param dataSource -- location to load model from
   * @param options -- custom options
   *                   @see ModelLoadingOption
   * @return loaded model
   *
   * @throws UnsupportedDataSourceException iff {@link #supports(DataSource)} returns false
   * @throws ModelLoadException iff there was an irrecoverable load problem (for instance format was broken or unrecognized)
   */
  @NotNull
  SModel load(@NotNull DataSource dataSource, @NotNull ModelLoadingOption... options) throws UnsupportedDataSourceException,
                                                                                             ModelLoadException;

  /**
   * Saves the model to the provided data source in the factory-specific format (including conversion when needed).
   * Note, use of this method is discouraged as it exposes {@code IOException},
   * use {@link #save(SModel, DataSource, ModelSaveOption...)} instead.
   * Not deprecated yet as there are quite some uses in MPS itself, although since 2020 there's better alternative
   */
  default void save(@NotNull SModel model, @NotNull DataSource dataSource) throws ModelSaveException, IOException {
    save(model, dataSource, new ModelSaveOption[0]);
  }

  /**
   * Serialize the model to the provided data source in the factory-specific format with respect to options, if any
   * @throws ModelSaveException in case serialization fails
   */
  default void save(@NotNull SModel model, @NotNull DataSource dataSource, @Nullable ModelSaveOption ... options) throws ModelSaveException {
    final ModelSaveOption mandatoryOption = options != null ? Arrays.stream(options).filter(ModelSaveOption::mandatory).findFirst().orElse(null) : null;
    if (mandatoryOption != null) {
      final String m = "Could not handle mandatory save option " + mandatoryOption;
      throw new ModelSaveException(m, Collections.emptySet());
    }
    try {
      save(model, dataSource);
    } catch (IOException ex) {
      throw new ModelSaveException(ex.getMessage(), Collections.emptySet(), ex);
    }
  }

  /**
   * Returns an id which is used to get model factory by id in the
   * {@code ModelFactoryService}.
   *
   * @return model factory unique identification entity
   */
  @NotNull ModelFactoryType getType();

  /**
   * As for 193 we have multiple versions for the model persistence.
   * Essentially the different versions of the same persistence could be represented by {@link ModelFactory}.
   * This method returns true when the version of the particular persistence is not the latest and could be updated.
   *
   * In that case the resaving the model will probably fix the problem.
   */
  default boolean needsUpgrade(@NotNull DataSource dataSource) throws IOException {
    return false;
  }

  /**
   * Declares a list of preferred data source formats,
   * sorted from the most preferred to the less preferred data source type.
   *
   * fixme [AP] I will move it from here since it does not relate to the API of the model factory itself,
   *            it is just a plugin for DefaultModelRoot to enable automatic 'data source' <-> 'model factory'
   *            relation.
   *            It was a mistake to put this method here in the first place.
   *
   * @return a list of data source kinds which might be considered when MPS
   *         meets a data source location and needs to choose a model factory
   *         which is likely to support the content of the data source.
   *         For instance if we have data source types associated with file names (e.g. prefix or suffix)
   *         we are able to establish a file name pattern association with a specific model factory.
   *         For example each model file which ends with '.mps_binary' suffix would be associated with the
   *         corresponding data source type which in turn would be associated with 'MyBinaryModelFactory'.
   */
  @Deprecated(since = "193", forRemoval = true)
  @NotNull List<DataSourceType> getPreferredDataSourceTypes();
}
