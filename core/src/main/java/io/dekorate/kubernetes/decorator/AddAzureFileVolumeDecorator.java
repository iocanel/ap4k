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

import io.dekorate.kubernetes.config.AzureFileVolume;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.dekorate.doc.Description;

@Description("Add an Azure File volume to the Pod spec.")
public class AddAzureFileVolumeDecorator extends Decorator<PodSpecBuilder> {

  private final AzureFileVolume volume;

  public AddAzureFileVolumeDecorator(AzureFileVolume volume) {
    this.volume = volume;
  }

  @Override
  public void visit(PodSpecBuilder podSpec) {
    podSpec.addNewVolume()
      .withName(volume.getVolumeName())
      .withNewAzureFile()
      .withSecretName(volume.getSecretName())
      .withShareName(volume.getShareName())
      .withReadOnly(volume.isReadOnly())
      .endAzureFile()
      .endVolume();

  }
}
