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
package io.dekorate.kubernetes.adapter;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.dekorate.kubernetes.config.Env;
import io.dekorate.kubernetes.config.Mount;
import io.dekorate.kubernetes.config.Port;
import io.dekorate.kubernetes.decorator.AddEnvVarDecorator;
import io.dekorate.kubernetes.decorator.AddLivenessProbeDecorator;
import io.dekorate.kubernetes.decorator.AddMountDecorator;
import io.dekorate.kubernetes.decorator.AddPortDecorator;
import io.dekorate.kubernetes.decorator.AddReadinessProbeDecorator;
import io.dekorate.kubernetes.decorator.ApplyImagePullPolicyDecorator;
import io.dekorate.utils.Images;
import io.dekorate.utils.Strings;

public class ContainerAdapter {

  public static Container adapt(io.dekorate.kubernetes.config.Container container)  {
     String name = container.getName();
    if (Strings.isNullOrEmpty(name)) {
      name = Images.getName(container.getImage());
    }

    ContainerBuilder builder = new ContainerBuilder()
      .withName(name)
      .withImage(container.getImage())
      .withCommand(container.getCommand())
      .withArgs(container.getArguments());

     for (Env env : container.getEnvVars()) {
      builder.accept(new AddEnvVarDecorator(env));
    }
    for (Port port : container.getPorts()) {
      builder.accept(new AddPortDecorator(port));
    }
    for (Mount mount: container.getMounts()) {
      builder.accept(new AddMountDecorator(mount));
    }

    builder.accept(new ApplyImagePullPolicyDecorator(container.getImagePullPolicy()));

    builder.accept(new AddLivenessProbeDecorator(name, container.getLivenessProbe()));
    builder.accept(new AddReadinessProbeDecorator(name, container.getReadinessProbe()));
    return builder.build();
  }
}
