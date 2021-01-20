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

import io.dekorate.doc.Description;
import io.dekorate.kubernetes.config.SecretVolume;
import io.dekorate.kubernetes.decorator.NamedResourceDecorator;
import io.dekorate.deps.knative.serving.v1.RevisionSpecFluent;
import io.dekorate.deps.kubernetes.api.model.ObjectMeta;
import io.dekorate.deps.kubernetes.api.model.VolumeBuilder;


@Description("Add a secret volume to all pod specs.")
public class AddSecretVolumeToRevisionDecorator extends NamedResourceDecorator<RevisionSpecFluent<?>> {

  private final SecretVolume volume;

  public AddSecretVolumeToRevisionDecorator(SecretVolume volume) {
    this(ANY, volume);
  }

  public AddSecretVolumeToRevisionDecorator(String name, SecretVolume volume) {
    super(name);
    this.volume = volume;
  }

  @Override
  public void andThenVisit(RevisionSpecFluent<?> revisionSpec, ObjectMeta resourceMeta) {
    revisionSpec.addToVolumes(new VolumeBuilder()
        .withName(volume.getVolumeName())
        .withNewSecret()
        .withSecretName(volume.getSecretName())
        .withDefaultMode(volume.getDefaultMode())
        .withOptional(volume.isOptional())
        .endSecret()
        .build());
  }
}
