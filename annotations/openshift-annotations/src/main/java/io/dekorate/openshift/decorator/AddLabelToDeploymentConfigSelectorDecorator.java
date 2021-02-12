
package io.dekorate.openshift.decorator;

import io.dekorate.kubernetes.decorator.Decorator;
import io.dekorate.kubernetes.decorator.NamedResourceDecorator;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.openshift.api.model.DeploymentConfigSpecFluent;

/**
 * Add the specifed key/value pair as a label to the specifed DeploymnetConfig.
 */
public class AddLabelToDeploymentConfigSelectorDecorator extends NamedResourceDecorator<DeploymentConfigSpecFluent<?>> {

  private String key;
  private String value;

  public AddLabelToDeploymentConfigSelectorDecorator(String name, String key, String value) {
    super(name);
    this.key = key;
    this.value = value;
  }

  public AddLabelToDeploymentConfigSelectorDecorator(String kind, String name, String key, String value) {
    super(kind, name);
    this.key = key;
    this.value = value;
  }

  @Override
  public void andThenVisit(DeploymentConfigSpecFluent<?> spec, ObjectMeta resourceMeta) {
    spec.addToSelector(key, value);
  }

  @Override
  public Class<? extends Decorator>[] before() {
    return new Class[] { RemoveLabelFromDeploymentConfigSelectorDecorator.class };
  }
}
