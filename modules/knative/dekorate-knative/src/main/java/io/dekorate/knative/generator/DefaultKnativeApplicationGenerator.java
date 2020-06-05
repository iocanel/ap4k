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
package io.dekorate.knative.generator;

import io.dekorate.config.DefaultConfiguration;
import io.dekorate.knative.config.KnativeConfig;
import io.dekorate.knative.config.KnativeConfigBuilder;
import io.dekorate.kubernetes.configurator.ApplyDeployToApplicationConfiguration;
import io.dekorate.kubernetes.configurator.ApplyImagePullSecretConfiguration;
import io.dekorate.project.ApplyProjectInfo;

public class DefaultKnativeApplicationGenerator implements KnativeApplicationGenerator {

    public DefaultKnativeApplicationGenerator () {
      on(new DefaultConfiguration<KnativeConfig>(new KnativeConfigBuilder()
                                                   .accept(new ApplyImagePullSecretConfiguration())
                                                   .accept(new ApplyProjectInfo(getProject()))
                                                   .accept(new ApplyDeployToApplicationConfiguration())));
    }
}
