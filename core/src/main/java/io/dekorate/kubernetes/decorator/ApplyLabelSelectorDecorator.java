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
 */
package io.dekorate.kubernetes.decorator;

import io.fabric8.kubernetes.api.model.LabelSelector;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpecFluent;

public class ApplyLabelSelectorDecorator extends Decorator<DeploymentSpecFluent>  {

  private final LabelSelector labelSelector;

  public ApplyLabelSelectorDecorator(LabelSelector labelSelector) {
    this.labelSelector = labelSelector;
  }

  @Override
  public void visit(DeploymentSpecFluent deploymentSpec) {
   deploymentSpec.withSelector(labelSelector);
  }
}
