/**
 * Copyright 2018 The original authors.
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
 **/

package io.dekorate.tekton.decorator;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.tekton.pipeline.v1beta1.PipelineRunSpecFluent;
import io.dekorate.kubernetes.decorator.NamedResourceDecorator;

public class AddPvcToPipelineRunDecorator extends NamedResourceDecorator<PipelineRunSpecFluent<?>> {

  private final String workspace;
  private final String claim;
  private final boolean readOnly;

  public AddPvcToPipelineRunDecorator(String name, String workspace, String claim, boolean readOnly) {
    super(name);
    this.workspace = workspace;
    this.claim = claim;
    this.readOnly = readOnly;
  }

  @Override
  public void andThenVisit(PipelineRunSpecFluent<?> spec, ObjectMeta meta) {
    spec.addNewWorkspace()
      .withName(workspace)
      .withNewPersistentVolumeClaim(claim, readOnly)
      .endWorkspace();
  }
}
