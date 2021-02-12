
package io.dekorate.tekton.configurator;

import io.dekorate.kubernetes.config.Configurator;
import io.dekorate.tekton.config.TektonConfigFluent;

/**
 * Enable the use of `.docker/config.json`.
 */
public class UseLocalDockerConfigJsonConfigurator extends Configurator<TektonConfigFluent<?>> {

  private final boolean enabled;
  
	public UseLocalDockerConfigJsonConfigurator(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void visit(TektonConfigFluent<?> config) {
    config.withUseLocalDockerConfigJson(enabled);
	}
}
