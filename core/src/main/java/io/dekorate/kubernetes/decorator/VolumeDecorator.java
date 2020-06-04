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

import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.api.model.Volume;
import io.sundr.transform.annotations.VelocityTransformation;

@VelocityTransformation("/templates/fluent-visitor.vm")
public class VolumeDecorator implements FluentDecorator<Volume, PodSpecBuilder> {

  public static VolumeFluentVisitor createNew() {
    return new VolumeFluentVisitor(v -> new Decorator<PodSpecBuilder>() {
        @Override
        public void visit(PodSpecBuilder b) {
          b.addToVolumes(v);
        }
      });
  }
}
