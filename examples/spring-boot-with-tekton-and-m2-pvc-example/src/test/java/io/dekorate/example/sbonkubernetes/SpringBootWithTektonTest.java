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
package io.dekorate.example.sbonkubernetes;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesList;
import io.fabric8.tekton.pipeline.v1beta1.Pipeline;
import io.fabric8.tekton.pipeline.v1beta1.PipelineRun;
import io.fabric8.tekton.pipeline.v1beta1.PipelineTask;
import io.fabric8.tekton.pipeline.v1beta1.Task;
import io.fabric8.tekton.pipeline.v1beta1.TaskRun;
import io.fabric8.tekton.pipeline.v1beta1.WorkspaceBinding;
import io.dekorate.utils.Serialization;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SpringBootWithTektonTest {

  @Test
  public void shouldContainPipelineWithM2Workspace() {
    KubernetesList list = Serialization.unmarshalAsList(getClass().getClassLoader().getResourceAsStream("META-INF/dekorate/tekton-pipeline.yml"));
    assertNotNull(list);
    Pipeline p = findFirst(list, Pipeline.class).orElseThrow(() -> new IllegalStateException());
    assertNotNull(p);
    assertTrue(p.getSpec().getWorkspaces().stream().filter(w -> w.getName().equals("pipeline-m2-ws")).findAny().isPresent(), "Pipeline should contain workspace named 'pipeline-m2-ws'");
    Optional<PipelineTask> buildTask = findTask("build", p);
    assertTrue(buildTask.isPresent());

    assertTrue(buildTask.get().getWorkspaces().stream().filter(w -> w.getName().equals("m2") && w.getWorkspace().equals("pipeline-m2-ws")).findAny().isPresent(), "Build task should contain workspace 'm2 -> pipeline-m2-ws'");
  }

  @Test
  public void shouldContainPipelineRun() {
    KubernetesList list = Serialization.unmarshalAsList(getClass().getClassLoader().getResourceAsStream("META-INF/dekorate/tekton-pipeline-run.yml"));
    assertNotNull(list);
    PipelineRun p = findFirst(list, PipelineRun.class).orElseThrow(() -> new IllegalStateException());
    assertNotNull(p);
    Optional<WorkspaceBinding> binding  = p.getSpec().getWorkspaces().stream().filter(w -> w.getName().equals("pipeline-m2-ws")).findAny();
    assertTrue(binding.isPresent(), "PipelineRun should contain workspace binding named 'pipeline-m2-ws'");
    assertEquals(binding.get().getPersistentVolumeClaim().getClaimName(), "m2-pvc");
  }

  @Test
  public void shouldContainTaskWithM2Workspace() {
    KubernetesList list = Serialization.unmarshalAsList(getClass().getClassLoader().getResourceAsStream("META-INF/dekorate/tekton-task.yml"));
    assertNotNull(list);
    Task t = findFirst(list, Task.class).orElseThrow(() -> new IllegalStateException());
    assertNotNull(t);
    assertTrue(t.getSpec().getWorkspaces().stream().filter(w -> w.getName().equals("m2")).findAny().isPresent(), "Pipeline should contain workspace named 'pipeline-m2-ws'");
  }

  @Test
  public void shouldContainTaskRunWithM2PvcBinding() {
    KubernetesList list = Serialization.unmarshalAsList(getClass().getClassLoader().getResourceAsStream("META-INF/dekorate/tekton-task-run.yml"));
    assertNotNull(list);
    TaskRun t = findFirst(list, TaskRun.class).orElseThrow(() -> new IllegalStateException());
    assertNotNull(t);

    Optional<WorkspaceBinding> binding  = t.getSpec().getWorkspaces().stream().filter(w -> w.getName().equals("m2")).findAny();
    assertTrue(binding.isPresent(), "PipelineRun should contain workspace binding named 'pipeline-m2-ws'");
    assertEquals(binding.get().getPersistentVolumeClaim().getClaimName(), "m2-pvc");
  }


  Optional<PipelineTask> findTask(String name, Pipeline p) {
    return p.getSpec().getTasks().stream().filter(t -> name.equals(t.getName())).findFirst();
  }

  <T extends HasMetadata> Optional<T> findFirst(KubernetesList list, Class<T> t) {
    return (Optional<T>) list.getItems().stream()
      .filter(i -> t.isInstance(i))
      .findFirst();
  }
}
