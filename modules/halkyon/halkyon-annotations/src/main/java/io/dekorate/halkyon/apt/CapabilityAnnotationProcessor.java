/**
 * Copyright 2018 The original authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package io.dekorate.halkyon.apt;

import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import io.dekorate.Session;
import io.dekorate.config.AnnotationConfiguration;
import io.dekorate.doc.Description;
import io.dekorate.halkyon.adapter.CapabilityConfigAdapter;
import io.dekorate.halkyon.annotation.HalkyonCapability;
import io.dekorate.halkyon.config.CapabilityConfig;
import io.dekorate.halkyon.generator.CapabilityConfigGenerator;
import io.dekorate.processor.AbstractAnnotationProcessor;

@Description("Generate halkyon capability custom resources.")
@SupportedAnnotationTypes("io.dekorate.halkyon.annotation.HalkyonCapability")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CapabilityAnnotationProcessor extends AbstractAnnotationProcessor implements CapabilityConfigGenerator {
  
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Session session = getSession();
    if (roundEnv.processingOver()) {
      session.close();
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
    HalkyonCapability capability = element.getAnnotation(HalkyonCapability.class);
    add(capability != null
      ? new AnnotationConfiguration<>(CapabilityConfigAdapter.newBuilder(capability))
      : new AnnotationConfiguration<>(CapabilityConfig.newCapabilityConfigBuilder()));
  }
}
