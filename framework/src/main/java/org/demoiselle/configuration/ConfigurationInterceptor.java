package org.demoiselle.configuration;

import org.demoiselle.internal.implementation.ConfigurationLoader;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * <p>
 * Interceptor class that loads the values of configuration files
 * into it's mapped class.
 * </p>
 */
@Configuration
@Interceptor
@Dependent
public class ConfigurationInterceptor {

	@Inject
	@Named("demoiselle-configuration-loader")
	private ConfigurationLoader configurationLoader;

	@AroundInvoke
	public Object manage(final InvocationContext ic) throws Exception {
		configurationLoader.load(ic.getTarget());
		return ic.proceed();
	}

}
