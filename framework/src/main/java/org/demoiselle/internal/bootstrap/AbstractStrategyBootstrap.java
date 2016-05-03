package org.demoiselle.internal.bootstrap;

import org.demoiselle.util.Reflections;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Logger;

public abstract class AbstractStrategyBootstrap<I> implements Extension {

	private Class<? extends I> strategyClass;

	private Collection<Class<? extends I>> cache;

	protected abstract Logger getLogger();

	@SuppressWarnings("WeakerAccess")
	protected Class<? extends I> getStrategyClass() {
		if (this.strategyClass == null) {
			this.strategyClass = Reflections.getGenericTypeArgument(this.getClass(), 0);
		}

		return this.strategyClass;
	}

	public Collection<Class<? extends I>> getCache() {
		if (this.cache == null) {
			this.cache = Collections.synchronizedSet(new HashSet<Class<? extends I>>());
		}

		return this.cache;
	}

	@SuppressWarnings("unchecked")
	public <T> void processAnnotatedType(@Observes final ProcessAnnotatedType<T> event) {
		final AnnotatedType<T> annotatedType = event.getAnnotatedType();

		if (Reflections.isOfType(annotatedType.getJavaClass(), this.getStrategyClass())) {
			this.getCache().add((Class<I>) annotatedType.getJavaClass());
		}
	}
}
