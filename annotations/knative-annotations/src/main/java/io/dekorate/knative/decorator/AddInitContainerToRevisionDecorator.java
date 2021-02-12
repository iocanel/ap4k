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
package io.dekorate.knative.decorator;

import io.dekorate.kubernetes.adapter.ContainerAdapter;
import io.dekorate.kubernetes.config.Container;
import io.dekorate.kubernetes.decorator.NamedResourceDecorator;
import io.fabric8.knative.serving.v1.RevisionSpecFluent;
import io.fabric8.kubernetes.api.model.ObjectMeta;

/**
 * A decorator that adds an init container to a pod template.
 */
/**
 * Add an init container to a revision.
 */
public class AddInitContainerToRevisionDecorator extends NamedResourceDecorator<RevisionSpecFluent<?>> {

  private final Container container;

  public AddInitContainerToRevisionDecorator(Container container) {
    this(ANY, container);
  } 

  public AddInitContainerToRevisionDecorator(String deployment, Container container) {
    super(deployment);
    this.container = container;
  }

  @Override
  public void andThenVisit(RevisionSpecFluent<?> revisionSpec, ObjectMeta resourceMeta) {
    revisionSpec.addToInitContainers(ContainerAdapter.adapt(container));
  }
}
