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
 */
package io.dekorate.halkyon.model;

import io.dekorate.crd.annotation.CustomResource;
import io.dekorate.deps.jackson.annotation.JsonInclude;
import io.dekorate.deps.jackson.annotation.JsonPropertyOrder;
import io.fabric8.kubernetes.api.model.Doneable;
import io.sundr.builder.annotations.Buildable;
import io.sundr.builder.annotations.Inline;
import io.sundr.transform.annotations.VelocityTransformation;
import io.sundr.transform.annotations.VelocityTransformations;

/**
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "deploymentMode",
  "runtime",
  "version",
  "exposeService",
  "storage",
  "envs",
  "capabilities",
  "buildConfig"
})
@Buildable(editableEnabled = false, builderPackage = "io.fabric8.kubernetes.api.builder", inline = @Inline(type = Doneable.class, prefix = "Doneable", value = "done"))
@VelocityTransformations({
  @VelocityTransformation(value = "/halkyon-resource.vm"),
  @VelocityTransformation(value = "/halkyon-resource-list.vm"),
  @VelocityTransformation(value = "/halkyon-status.vm"),
})
@CustomResource(group = "halkyon.io", version = "v1beta1")
public class ComponentSpec {
  
  private DeploymentMode deploymentMode;
  private String runtime;
  private String version;
  private boolean exposeService;
  private Integer port;
  private Storage storage;
  private Env[] envs;
  private BuildConfig buildConfig;
  private Capabilities capabilities;
  
  public ComponentSpec() {
  }
  
  public ComponentSpec(DeploymentMode deploymentMode, String runtime, String version, boolean exposeService, Integer port, Storage storage, Env[] envs, BuildConfig buildConfig, Capabilities capabilities) {
    this.deploymentMode = deploymentMode;
    this.runtime = runtime;
    this.version = version;
    this.exposeService = exposeService;
    this.port = port;
    this.storage = storage;
    this.envs = envs;
    this.capabilities = capabilities;
    this.buildConfig = buildConfig;
  }
  
  public DeploymentMode getDeploymentMode() {
    return deploymentMode;
  }
  
  public void setDeploymentMode(DeploymentMode deploymentMode) {
    this.deploymentMode = deploymentMode;
  }
  
  public String getRuntime() {
    return runtime;
  }
  
  public void setRuntime(String runtime) {
    this.runtime = runtime;
  }
  
  public String getVersion() {
    return version;
  }
  
  public void setVersion(String version) {
    this.version = version;
  }
  
  public boolean isExposeService() {
    return exposeService;
  }
  
  public void setExposeService(boolean exposeService) {
    this.exposeService = exposeService;
  }
  
  public Integer getPort() {
    return port;
  }
  
  public void setPort(Integer port) {
    this.port = port;
  }
  
  public Storage getStorage() {
    return storage;
  }
  
  public void setStorage(Storage storage) {
    this.storage = storage;
  }
  
  public Env[] getEnvs() {
    return envs;
  }
  
  public void setEnvs(Env[] envs) {
    this.envs = envs;
  }
  
  public BuildConfig getBuildConfig() {
    return buildConfig;
  }
  
  public void setBuildConfig(BuildConfig buildConfig) {
    this.buildConfig = buildConfig;
  }

  public Capabilities getCapabilities() {
    return capabilities;
  }

  public void setCapabilities(Capabilities capabilities) {
    this.capabilities = capabilities;
  }
}
