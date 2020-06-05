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
package io.dekorate.jaeger.generator;

import io.dekorate.Generator;
import io.dekorate.Session;
import io.dekorate.WithSession;
import io.dekorate.config.ConfigurationSupplier;
import io.dekorate.config.AnnotationConfiguration;
import io.dekorate.config.PropertyConfiguration;
import io.dekorate.jaeger.adapter.JaegerAgentConfigAdapter;
import io.dekorate.jaeger.annotation.EnableJaegerAgent;
import io.dekorate.jaeger.config.AddDefaultPortsToAgentConfigurator;
import io.dekorate.jaeger.config.JaegerAgentConfig;
import io.dekorate.jaeger.config.JaegerAgentConfigBuilder;
import io.dekorate.jaeger.handler.JaegerAgentHandler;
import io.dekorate.kubernetes.config.Configuration;

import javax.lang.model.element.Element;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface JaegerAgentGenerator extends Generator, WithSession {

  default String getKey() {
    return "jaeger";
  }

  default Class<? extends Configuration> getConfigType() {
    return JaegerAgentConfig.class;
  }

  @Override
  default void addAnnotationConfiguration(Map map) {
    on(new AnnotationConfiguration<>(JaegerAgentConfigAdapter.newBuilder(propertiesMap(map, JaegerAgentConfig.class))));
  }

  @Override
  default void addPropertyConfiguration(Map map) {
    on(new PropertyConfiguration<>(JaegerAgentConfigAdapter.newBuilder(propertiesMap(map, JaegerAgentConfig.class))));
  }

  default void on(ConfigurationSupplier<JaegerAgentConfig> config) {
    Session session = getSession();
    session.configurators().add(config);
    session.configurators().add(new AddDefaultPortsToAgentConfigurator());
    session.handlers().add(new JaegerAgentHandler(session.resources()));
  }
}
