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
package org.demoiselle.jsf.internal.implementation;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean2;
import org.apache.commons.beanutils.Converter;
import org.demoiselle.annotation.Name;
import org.demoiselle.annotation.literal.NameQualifier;
import org.demoiselle.exception.DemoiselleException;
import org.demoiselle.jsf.util.Parameter;
import org.demoiselle.util.Reflections;
import org.demoiselle.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.*;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

public class ParameterImpl<T extends Serializable> implements Parameter<T>, Serializable {

	private static final long serialVersionUID = -5175793135089243818L;

	private String key;

	private Class<? extends Annotation> scope = null;

	private Class<T> type;

	private transient ConvertUtilsBean converter;

	private HttpServletRequest getRequest() {
		return CDI.current().select(HttpServletRequest.class).get();
	}

	public ParameterImpl(InjectionPoint ip) {
		boolean nameAnnotationPresent = false;
		for (Annotation qualifier : ip.getQualifiers()) {
			if (Name.class.isAssignableFrom(qualifier.annotationType())) {
				nameAnnotationPresent = true;
				String key = ((Name)qualifier).value();

				if (!"".equals(key)) {
					this.key = key;
				} else {
					this.key = ip.getMember().getName();
				}

				break;
			}
		}

		if (!nameAnnotationPresent) {
			this.key = ip.getMember().getName();
		}

		this.type = Reflections.getGenericTypeArgument(ip.getType(), 0);
		checkScoped(ip);
	}

	public String getKey() {
		return key;
	}

	private boolean isSessionScoped() {
		return scope != null && SessionScoped.class.isAssignableFrom(scope);
	}

	private boolean isRequestScoped() {
		return scope == null || RequestScoped.class.isAssignableFrom(scope);
	}

	private boolean isScoped() {
		return scope != null;
	}

	private void checkScoped(final InjectionPoint ip) {
		if (ip.getAnnotated() != null) {
			for (Annotation annotation : ip.getAnnotated().getAnnotations()) {
				if (CDI.current().getBeanManager().isNormalScope(annotation.annotationType())) {
					this.scope = annotation.annotationType();
					break;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public T getValue() {
		final String parameterValue = getRequest().getParameter(key);

		if (isSessionScoped()) {
			if (parameterValue != null) {
				getRequest().getSession().setAttribute(key, convert(parameterValue, type));
			}

			return (T) getRequest().getSession().getAttribute(key);
		} else if (isScoped() && !isRequestScoped()) {
			final ParameterValueImpl scopedValueHolder = getScopedHolder(scope);

			if (parameterValue != null) {
				scopedValueHolder.setValue(convert(parameterValue, type));
			}

			return (T) scopedValueHolder.getValue();
		} else {
			return parameterValue != null ? convert(parameterValue, type) : null;
		}
	}

	@Override
	public void setValue(T value) {
		if (isSessionScoped()) {
			getRequest().getSession().setAttribute(key, value);
		} else if (isScoped() && !isRequestScoped()) {
			final ParameterValueImpl scopedValueHolder = getScopedHolder(scope);
			scopedValueHolder.setValue(value);
		} else if (isRequestScoped()) {
			throw new DemoiselleException(getBundle().getString("parameter-invalid-request-operation"));
		}
	}

	@SuppressWarnings("unchecked")
	private T convert(final String parameterValue, Class<T> targetClass) {
		if (converter == null) {
			converter = new ConvertUtilsBean2();
		}

		Converter typeConverter = converter.lookup(String.class, targetClass);
		if (typeConverter != null) {
			return (T) converter.convert(parameterValue, targetClass);
		} else {
			throw new DemoiselleException(
					getBundle().getString("parameter-converter-not-found", targetClass.getCanonicalName()));
		}
	}

	private ResourceBundle getBundle() {
		return CDI.current().select(ResourceBundle.class, new NameQualifier("demoiselle-servlet-bundle.properties"))
				.get();
	}

	private ParameterValueImpl getScopedHolder(final Class<? extends Annotation> scope) {
		final BeanManager manager = CDI.current().getBeanManager();
		final AnnotatedType<ParameterValueImpl> annotatedType = manager.createAnnotatedType(ParameterValueImpl.class);
		final BeanAttributes<ParameterValueImpl> attrWrapper = new BeanAttributes<ParameterValueImpl>() {

			final BeanAttributes<ParameterValueImpl> attributes = manager.createBeanAttributes(annotatedType);

			@Override
			public Set<Type> getTypes() {
				return attributes.getTypes();
			}

			@Override
			public Set<Annotation> getQualifiers() {
				return attributes.getQualifiers();
			}

			@Override
			public Class<? extends Annotation> getScope() {
				return scope;
			}

			@Override
			public String getName() {
				return attributes.getName();
			}

			@Override
			public Set<Class<? extends Annotation>> getStereotypes() {
				return attributes.getStereotypes();
			}

			@Override
			public boolean isAlternative() {
				return attributes.isAlternative();
			}
		};

		InjectionTargetFactory<ParameterValueImpl> ijFactory = manager.getInjectionTargetFactory(annotatedType);
		Bean<ParameterValueImpl> bean = manager.createBean(attrWrapper, ParameterValueImpl.class, ijFactory);
		CreationalContext<ParameterValueImpl> creationalContext = manager.createCreationalContext(bean);

		//noinspection unchecked
		return (ParameterValueImpl) manager.getReference(bean, ParameterValueImpl.class, creationalContext);
	}

}
