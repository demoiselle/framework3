/*
 * Demoiselle Framework
 * Copyright (C) 2010 SERPRO
 * ----------------------------------------------------------------------------
 * This file is part of Demoiselle Framework.
 * 
 * Demoiselle Framework is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License version 3
 * along with this program; if not,  see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301, USA.
 * ----------------------------------------------------------------------------
 * Este arquivo é parte do Framework Demoiselle.
 * 
 * O Framework Demoiselle é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da GNU LGPL versão 3 como publicada pela Fundação
 * do Software Livre (FSF).
 * 
 * Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA
 * GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou
 * APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU/LGPL em português
 * para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da GNU LGPL versão 3, sob o título
 * "LICENCA.txt", junto com esse programa. Se não, acesse <http://www.gnu.org/licenses/>
 * ou escreva para a Fundação do Software Livre (FSF) Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02111-1301, USA.
 */
package org.demoiselle.internal.bootstrap;

import javassist.*;
import org.demoiselle.configuration.Configuration;
import org.demoiselle.configuration.ConfigurationValueExtractor;
import org.demoiselle.internal.implementation.ConfigurationProxyTemplateImpl;
import org.demoiselle.internal.producer.LoggerProducer;
import org.demoiselle.internal.producer.ResourceBundleProducer;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import java.util.*;
import java.util.logging.Logger;

public class ConfigurationBootstrap extends AbstractStrategyBootstrap<ConfigurationValueExtractor> {

	private Logger logger;

	private ResourceBundle bundle;

	private static final Map<ClassLoader, Map<String, Class<Object>>> cacheClassLoader = Collections
			.synchronizedMap(new HashMap<>());

	public void processAnnotatedType(@Observes @WithAnnotations(Configuration.class) final ProcessAnnotatedType<Object> event, BeanManager beanManager)
			throws Exception {
		final AnnotatedType<Object> annotatedType = event.getAnnotatedType();

		Class<Object> proxyClass = createProxy(annotatedType.getJavaClass());
		AnnotatedType<Object> proxyAnnotatedType = beanManager.createAnnotatedType(proxyClass);
		event.setAnnotatedType(proxyAnnotatedType);
	}

	private static List<CtMethod> getMethods(CtClass type) throws NotFoundException {
		List<CtMethod> fields = new ArrayList<CtMethod>();

		if (!type.getName().equals(Object.class.getName())) {
			fields.addAll(Arrays.asList(type.getDeclaredMethods()));
			fields.addAll(getMethods(type.getSuperclass()));
		}

		return fields;
	}

	@SuppressWarnings("unchecked")
	private Class<Object> createProxy(Class<Object> type) throws Exception {
		String superClassName = type.getCanonicalName();
		String childClassName = superClassName + "_$$_DemoiselleProxy";

		Map<String, Class<Object>> cacheProxy = Collections.synchronizedMap(new HashMap<>());

		Class<Object> clazzProxy = null;

		ClassLoader classLoader = type.getClassLoader();
		if (cacheClassLoader.containsKey(classLoader)) {
			cacheProxy = cacheClassLoader.get(classLoader);
			if (cacheProxy.containsKey(childClassName)) {
				clazzProxy = cacheProxy.get(childClassName);
			}
		}

		if (clazzProxy == null) {

			ClassPool pool = new ClassPool();
			CtClass ctChildClass;

			pool.appendClassPath(new LoaderClassPath(classLoader));
			CtClass ctSuperClass = pool.get(superClassName);

			ctChildClass = pool.getAndRename(ConfigurationProxyTemplateImpl.class.getCanonicalName(), childClassName);
			ctChildClass.setSuperclass(ctSuperClass);

			CtMethod ctChildMethod;
			for (CtMethod ctSuperMethod : getMethods(ctSuperClass)) {
				ctChildMethod = CtNewMethod.delegator(ctSuperMethod, ctChildClass);
				ctChildMethod.insertBefore("load(this);");

				ctChildClass.addMethod(ctChildMethod);
			}

			clazzProxy = ctChildClass.toClass(classLoader, type.getProtectionDomain());

			cacheProxy.put(childClassName, clazzProxy);
			cacheClassLoader.put(classLoader, cacheProxy);
		}

		return clazzProxy;
	}

	@Override
	protected Logger getLogger() {
		if (logger == null) {
			logger = LoggerProducer.create("br.gov.frameworkdemoiselle.configuration");
		}

		return logger;
	}

	protected ResourceBundle getBundle() {
		if (bundle == null) {
			bundle = ResourceBundleProducer.create("demoiselle-core-bundle");
		}

		return bundle;
	}


}
