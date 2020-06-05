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

import io.fabric8.tekton.pipeline.v1beta1.TaskSpecFluent;

public class AddResourceInputToTaskDecorator extends NamedTaskDecorator {

  private final String type;
  private final String name;
  private final String targetPath;

  public AddResourceInputToTaskDecorator(String taskName, String type, String name) {
    this(taskName, type, name, null);
  }

  public AddResourceInputToTaskDecorator(String taskName, String type, String name, String targetPath) {
    super(taskName);
    this.type = type;
    this.name = name;
    this.targetPath = targetPath;
  }

  @Override
  public void andThenVisit(TaskSpecFluent<?> taskSpec) {
    if (!taskSpec.hasResources()) {
      taskSpec.withNewResources()
        .addNewInput()
        .withName(name)
        .withType(type)
        .withTargetPath(targetPath)
        .endInput()
        .endResources();
    } else {
      taskSpec.editResources().removeMatchingFromInputs(r -> r.getName().equals(name)).endResources();
      taskSpec.editResources()
        .addNewInput()
        .withName(name)
        .withType(type)
        .withTargetPath(targetPath)
        .endInput()
        .endResources();
    }
  }
}
