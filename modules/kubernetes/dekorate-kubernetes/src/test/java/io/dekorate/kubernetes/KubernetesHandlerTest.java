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

package io.dekorate.kubernetes;

import io.dekorate.kubernetes.annotation.ImagePullPolicy;
import io.dekorate.kubernetes.annotation.ServiceType;
import io.dekorate.kubernetes.config.Annotation;
import io.dekorate.kubernetes.config.AwsElasticBlockStoreVolume;
import io.dekorate.kubernetes.config.AzureDiskVolume;
import io.dekorate.kubernetes.config.AzureFileVolume;
import io.dekorate.kubernetes.config.ConfigKey;
import io.dekorate.kubernetes.config.ConfigMapVolume;
import io.dekorate.kubernetes.config.Container;
import io.dekorate.kubernetes.config.EditableKubernetesConfig;
import io.dekorate.kubernetes.config.Env;
import io.dekorate.kubernetes.config.GitRepoVolume;
import io.dekorate.kubernetes.config.KubernetesConfig;
import io.dekorate.kubernetes.config.Label;
import io.dekorate.kubernetes.config.Mount;
import io.dekorate.kubernetes.config.PersistentVolumeClaimVolume;
import io.dekorate.kubernetes.config.Port;
import io.dekorate.kubernetes.config.Probe;
import io.dekorate.kubernetes.config.SecretVolume;
import io.dekorate.kubernetes.handler.KubernetesHandler;
import io.dekorate.project.Project;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class KubernetesHandlerTest {

  @Test
  public void shouldAcceptKubernetesConfig()  {
    KubernetesHandler generator = new KubernetesHandler();
    assertTrue(generator.canHandle(KubernetesConfig.class));
  }

  @Test
  public void shouldAcceptEditableKubernetesConfig()  {
    KubernetesHandler generator = new KubernetesHandler();
    assertTrue(generator.canHandle(EditableKubernetesConfig.class));
  }

  @Test
  public void shouldNotAcceptKubernetesConfigSubclasses()  {
    KubernetesHandler generator = new KubernetesHandler();
    assertFalse(generator.canHandle(KubernetesConfigSubclass.class));
  }

  private abstract class KubernetesConfigSubclass extends KubernetesConfig {
  }
}
