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
 * 
**/

package io.dekorate.examples.pojo2crd;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.dekorate.utils.Serialization;
import io.dekorate.deps.kubernetes.api.model.HasMetadata;
import io.dekorate.deps.kubernetes.api.model.KubernetesList;
import io.dekorate.deps.kubernetes.api.model.apiextensions.v1beta1.CustomResourceDefinition;
import io.dekorate.deps.kubernetes.api.model.apiextensions.v1beta1.CustomResourceDefinitionVersion;

class CustomResourceTest {

  @Test
  public void testCrd() {
    KubernetesList list = Serialization.unmarshalAsList(getClass().getClassLoader().getResourceAsStream("META-INF/dekorate/kubernetes.yml"));
    assertNotNull(list);
    CustomResourceDefinition d = findFirst(list, CustomResourceDefinition.class).orElseThrow(() -> new IllegalStateException());
    assertNotNull(d);
    assertEquals("Karaf", d.getSpec().getNames().getKind());
    assertEquals("karafs", d.getSpec().getNames().getPlural());
    assertEquals("Namespaced", d.getSpec().getScope());
    Optional<CustomResourceDefinitionVersion> v1 = d.getSpec().getVersions().stream().filter(v -> v.getName().equals("v1")).findFirst();
    v1.ifPresent(v -> {
        assertNull(v.getSubresources());
      });

    assertNotNull(d.getSpec().getSubresources());
    assertNotNull(d.getSpec().getSubresources().getStatus());
  }

  <T extends HasMetadata> Optional<T> findFirst(KubernetesList list, Class<T> t) {
    return (Optional<T>) list.getItems().stream()
        .filter(i -> t.isInstance(i))
        .findFirst();
  }
}

