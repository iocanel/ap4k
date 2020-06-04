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

package io.dekorate.s2i.generator;

import java.util.Map;

import io.dekorate.Generator;
import io.dekorate.Logger;
import io.dekorate.LoggerFactory;
import io.dekorate.Session;
import io.dekorate.WithProject;
import io.dekorate.config.ConfigurationSupplier;
import io.dekorate.config.PropertyConfiguration;
import io.dekorate.kubernetes.config.Configuration;
import io.dekorate.kubernetes.configurator.ApplyBuildToImageConfiguration;
import io.dekorate.project.ApplyProjectInfo;
import io.dekorate.s2i.adapter.S2iBuildConfigAdapter;
import io.dekorate.s2i.config.S2iBuildConfig;
import io.dekorate.s2i.handler.S2iHanlder;

public interface S2iBuildGenerator extends Generator, WithProject {

  String S2I = "s2i";

  default String getKey() {
    return S2I;
  }

  default Class<? extends Configuration> getConfigType() {
    return S2iBuildConfig.class;
  }

  @Override
  default void add(Map map) {
    on(new PropertyConfiguration<S2iBuildConfig>(S2iBuildConfigAdapter.newBuilder(propertiesMap(map, S2iBuildConfig.class))
                                                 .accept(new ApplyBuildToImageConfiguration())
                                                 .accept(new ApplyProjectInfo(getProject()))));
  }

  default void on(ConfigurationSupplier<S2iBuildConfig> config) {
    Logger log = LoggerFactory.getLogger();

    Session session = getSession();
    session.configurators().add(config);
    session.handlers().add(new S2iHanlder(session.resources()));
  }
}
