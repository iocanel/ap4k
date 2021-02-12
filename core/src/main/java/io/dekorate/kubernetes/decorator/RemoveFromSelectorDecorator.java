/**
 * Copyright (C) 2020 Original Authors
 *     
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
**/

package io.dekorate.kubernetes.decorator;

import java.util.Optional;

import io.dekorate.SelectorDecoratorFactories;
import io.dekorate.SelectorDecoratorFactory;
import io.fabric8.kubernetes.api.builder.VisitableBuilder;
import io.fabric8.kubernetes.api.builder.Visitor;
import io.fabric8.kubernetes.api.model.ObjectMeta;

/**
 * Remove the specified key from the specified resource`s selector.
 */
public class RemoveFromSelectorDecorator extends NamedResourceDecorator<VisitableBuilder> {

  private final String key;

  public RemoveFromSelectorDecorator(String name, String key) {
    super(name);
    this.key = key;
  }

  public RemoveFromSelectorDecorator(String kind, String name, String key) {
    super(kind, name);
    this.key = key;
  }

	@Override
	public void andThenVisit(VisitableBuilder builder ,String kind, ObjectMeta resourceMeta) {
    Optional<SelectorDecoratorFactory> factory = SelectorDecoratorFactories.find(kind);
    factory.map(f -> f.createRemoveFromSelectorDecorator(resourceMeta.getName(), key)).ifPresent(m -> builder.accept((Visitor) m));
  }

  @Override
  public void andThenVisit(VisitableBuilder item, ObjectMeta resourceMeta) {
    //Not needed
	}

	@Override
  public Class<? extends Decorator>[] after() {
    return new Class[] {ResourceProvidingDecorator.class, AddToSelectorDecorator.class};
  }
}
