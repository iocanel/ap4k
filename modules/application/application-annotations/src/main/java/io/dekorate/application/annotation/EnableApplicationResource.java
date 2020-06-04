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
package io.dekorate.application.annotation;


import io.dekorate.kubernetes.config.Configuration;
import io.sundr.builder.annotations.Adapter;
import io.sundr.builder.annotations.Buildable;
import io.sundr.builder.annotations.Pojo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Buildable(builderPackage = "io.fabric8.kubernetes.api.builder")
@Pojo(name = "ApplicationConfig", relativePath = "../config",
  mutable = true,
  superClass = Configuration.class,
  withStaticBuilderMethod = false,
  withStaticAdapterMethod = false,
  adapter = @Adapter(name = "ApplicationConfigAdapter", relativePath = "../adapter", withMapAdapterMethod = true))
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface EnableApplicationResource {
  /**
   * The group of the application.
   * This value will be use as:
   * - docker image repo
   * - labeling resources
   * @return The specified group name.
   */
  String group() default "";

  /**
   * The name of the application.
   * This value will be used for naming Kubernetes resources like:
   * - Deployment
   * - Service
   * and so on ...
   * If no value is specified it will attempt to determine the name using the following rules:
   * If its a maven/gradle project use the artifact id.
   * Else if its a bazel project use the name.
   * Else if the system property app.name is present it will be used.
   * Else find the project root folder and use its name (root folder detection is done by moving to the parent folder until .git is found).
   * @return The specified application name.
   */
  String name() default "";

  /**
   * The version of the application.
   * This value be used for things like:
   * - The docker image tag.
   * If no value specified it will attempt to determine the name using the following rules:
   * @return The version.
   */
  String version() default "";

  Contact[] owners() default {};
  Contact[] maintainers() default {};
  Info[] info() default {};
  Icon[] icons() default {};
  Link[] links() default {};
  String notes() default "";
  String[] keywords() default {};
}
