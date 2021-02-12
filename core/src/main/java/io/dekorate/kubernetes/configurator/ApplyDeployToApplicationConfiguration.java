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
package io.dekorate.kubernetes.configurator;

import io.dekorate.kubernetes.config.BaseConfigFluent;
import io.dekorate.kubernetes.config.Configurator;

/**
 * Apply deployment related info to application configuration.
 */
public class ApplyDeployToApplicationConfiguration extends Configurator<BaseConfigFluent> {

  public static final String DEKORATE_DEPLOY = "dekorate.deploy";

  @Override
  public void visit(BaseConfigFluent config) {
    config.withAutoDeployEnabled(
        Boolean.parseBoolean(System.getProperty(DEKORATE_DEPLOY, String.valueOf(config.isAutoDeployEnabled()))));
  }
}
