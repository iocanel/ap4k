/**
 * Copyright 2019 The original authors.
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
package io.dekorate.thorntail.it;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesList;
import io.fabric8.kubernetes.api.model.extensions.Ingress;
import io.fabric8.openshift.api.model.Route;
import io.dekorate.utils.Serialization;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThorntailAnnotationlessTest {

  @Test
  void shouldContainKubernetesIngress() {
    KubernetesList list = Serialization.unmarshalAsList(ThorntailAnnotationlessTest.class.getClassLoader().getResourceAsStream("META-INF/dekorate/kubernetes.yml"));
    assertNotNull(list);

    Optional<Ingress> ingress = findFirst(list, Ingress.class);
    assertTrue(ingress.isPresent());
    assertEquals(9090, ingress.get().getSpec().getRules().get(0).getHttp().getPaths().get(0).getBackend().getServicePort().getIntVal().intValue());
  }

  @Test
  void shouldContainOpenShiftRoute() {
    KubernetesList list = Serialization.unmarshalAsList(ThorntailAnnotationlessTest.class.getClassLoader().getResourceAsStream("META-INF/dekorate/openshift.yml"));
    assertNotNull(list);

    Optional<Route> route = findFirst(list, Route.class);
    assertTrue(route.isPresent());
    assertEquals(9090, route.get().getSpec().getPort().getTargetPort().getIntVal().intValue());
  }

  <T extends HasMetadata> Optional<T> findFirst(KubernetesList list, Class<T> type) {
    return list.getItems()
      .stream()
      .filter(type::isInstance)
      .map(type::cast)
      .findFirst();
  }
}
