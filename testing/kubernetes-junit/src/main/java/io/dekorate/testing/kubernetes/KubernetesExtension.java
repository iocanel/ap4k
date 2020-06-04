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
package io.dekorate.testing.kubernetes;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import io.dekorate.BuildService;
import io.dekorate.BuildServiceFactories;
import io.dekorate.BuildServiceFactory;
import io.dekorate.DekorateException;
import io.dekorate.Logger;
import io.dekorate.LoggerFactory;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.VersionInfo;
import io.fabric8.kubernetes.client.internal.readiness.Readiness;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.dekorate.kubernetes.config.ImageConfiguration;
import io.dekorate.kubernetes.config.ImageConfigurationBuilder;
import io.dekorate.kubernetes.config.KubernetesConfig;
import io.dekorate.testing.Diagnostics;
import io.dekorate.testing.Pods;
import io.dekorate.testing.WithEvents;
import io.dekorate.testing.WithKubernetesClient;
import io.dekorate.testing.WithPod;
import io.dekorate.testing.WithProject;
import io.dekorate.testing.WithImageConfig;
import io.dekorate.testing.config.KubernetesIntegrationTestConfig;

public class KubernetesExtension implements  ExecutionCondition, BeforeAllCallback, AfterAllCallback,
                                             WithKubernetesIntegrationTestConfig, WithPod, WithKubernetesClient, WithKubernetesResources, WithEvents, WithProject, WithKubernetesConfig, WithImageConfig {

  private final Logger LOGGER = LoggerFactory.getLogger();
   
  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    try {
      VersionInfo version = getKubernetesClient(context).getVersion();
      String message = "Found version:" + version.getMajor() + "." + version.getMinor();
      LOGGER.info(message);
      return ConditionEvaluationResult.enabled(message);
    } catch (Throwable t) {
      String reason = "Could not communicate with KubernetesExtension API server.";
      LOGGER.error(reason);
      return ConditionEvaluationResult.disabled(reason);
    }
  }

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    KubernetesIntegrationTestConfig config = getKubernetesIntegrationTestConfig(context);
    KubernetesClient client = getKubernetesClient(context);
    KubernetesList list = getKubernetesResources(context);

    if (hasKubernetesConfig() && hasImageConfig()) {
      KubernetesConfig kubernetesConfig = getKubernetesConfig();
      ImageConfiguration imageConfig = getImageConfig().get();
      BuildService buildService = null;
      try {
        BuildServiceFactory buildServiceFactory = BuildServiceFactories.find(getProject(), imageConfig)
          .orElseThrow(() -> new IllegalStateException("No applicable BuildServiceFactory found."));
        buildService = buildServiceFactory.create(getProject(), imageConfig, list.getItems());
      } catch (Exception e) {
        throw DekorateException.launderThrowable("Failed to lookup BuildService.", e);
      }

      //
      // We use the isAutoPushEnabled flag of the @KubernetesApplication annotation and not @KubernetesIntegrationTest.
      // The reason is that the @KubernetesApplication.isAutoPushEnabled() affects the generated manifests (adds the registry).
      // and thus the tests MUST follow.
      if (imageConfig.isAutoPushEnabled()) {
        buildService.prepare();
        buildService.build();
        buildService.push();

      } else if (imageConfig.isAutoBuildEnabled()) {
        buildService.prepare();
        buildService.build();
      } else if (config.isBuildEnabled()) {
        buildService.prepare();
        buildService.build();
      }
    }
    
    if (config.isDeployEnabled()) {
      list.getItems().stream()
        .forEach(i -> {
          client.resourceList(i).createOrReplace();
          LOGGER.info("Created: " + i.getKind() + " name:" + i.getMetadata().getName() + ".");
        });

            List<HasMetadata> waitables = list.getItems().stream().filter(i->
                                                                    i instanceof Deployment ||
                                                                    i instanceof DeploymentConfig ||
                                                                    i instanceof Pod ||
                                                                    i instanceof ReplicaSet ||
                                                                    i instanceof ReplicationController).collect(Collectors.toList());
      long started = System.currentTimeMillis();
      LOGGER.info("Waiting until ready ("+config.getReadinessTimeout()+ " ms)...");
      waitUntilCondition(context, waitables, i -> Readiness.isReady(i), config.getReadinessTimeout(), TimeUnit.MILLISECONDS);
      long ended = System.currentTimeMillis();
      LOGGER.info("Waited: " +  (ended-started)+ " ms.");
      //Display the item status
      waitables.stream().map(r->client.resource(r).fromServer().get())
        .forEach(i -> {
          if (!Readiness.isReady(i)) {
            LOGGER.warning(i.getKind() + ":" + i.getMetadata().getName() + " not ready!");
          }
        });
    }
  }

  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
    Arrays.stream( testInstance.getClass().getDeclaredFields())
      .forEach(f -> {
        injectKubernetesClient(context, testInstance, f);
        injectKubernetesResources(context, testInstance, f);
        injectPod(context, testInstance, f);
      });
  }

  @Override
  public void afterAll(ExtensionContext context) {
    try {
      KubernetesClient client = getKubernetesClient(context);
      final Diagnostics diagnostics = new Diagnostics(client);
      boolean failed = context.getExecutionException().isPresent();
      if (failed) {
        displayDiagnostics(context);
      }
      getKubernetesResources(context).getItems().stream().forEach(r -> {
        LOGGER.info("Deleting: " + r.getKind() + " name:" + r.getMetadata().getName() + ". Deleted:"
            + getKubernetesClient(context).resource(r).cascading(true).delete());
      });
    } finally {
      closeKubernetesClient(context);
    }
  }

  public void displayDiagnostics(ExtensionContext context) {
    KubernetesClient client = getKubernetesClient(context);
    Pods pods = new Pods(client);
    PodList podList = pods.list(pods);

    final Diagnostics diagnostics = new Diagnostics(client, pods);
    if (podList == null || podList.getItems().isEmpty()) {
      diagnostics.displayAll();
    } else {
      getKubernetesResources(context).getItems().stream().forEach(r -> diagnostics.display(r));
    }
  }

  
 /**
   * Returns the configured name.
   * @return  The name.
   */
  public String getName() {
    return getKubernetesConfig().getName();
  } 
}
