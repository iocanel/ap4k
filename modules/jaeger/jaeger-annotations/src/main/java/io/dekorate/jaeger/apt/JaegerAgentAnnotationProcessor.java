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
package io.dekorate.jaeger.apt;


import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import io.dekorate.config.AnnotationConfiguration;
import io.dekorate.jaeger.adapter.JaegerAgentConfigAdapter;
import io.dekorate.jaeger.annotation.EnableJaegerAgent;
import io.dekorate.jaeger.config.JaegerAgentConfig;
import io.dekorate.jaeger.config.JaegerAgentConfigBuilder;
import io.dekorate.jaeger.generator.JaegerAgentGenerator;
import io.dekorate.processor.AbstractAnnotationProcessor;

@SupportedAnnotationTypes({"io.dekorate.jaeger.annotation.EnableJaegerAgent"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class JaegerAgentAnnotationProcessor extends AbstractAnnotationProcessor implements JaegerAgentGenerator {

  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (roundEnv.processingOver()) {
      getSession().close();
      return true;
    }
    for (TypeElement typeElement : annotations) {
      for (Element mainClass : roundEnv.getElementsAnnotatedWith(typeElement)) {
        add(mainClass);
      }
    }
    return false;
  }

  @Override
  public void add(Element element) {
    EnableJaegerAgent serviceMonitor = element.getAnnotation(EnableJaegerAgent.class);
    on(serviceMonitor != null
      ? new AnnotationConfiguration<JaegerAgentConfig>(JaegerAgentConfigAdapter.newBuilder(serviceMonitor))
      : new AnnotationConfiguration<JaegerAgentConfig>(new JaegerAgentConfigBuilder()));
  }
}
