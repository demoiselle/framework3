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
import org.apache.commons.beanutils.Converter;
import org.demoiselle.annotation.Name;
import org.demoiselle.annotation.literal.NameQualifier;
import org.demoiselle.exception.DemoiselleException;
import org.demoiselle.jsf.util.Parameter;
import org.demoiselle.util.Reflections;
import org.demoiselle.util.ResourceBundle;

import javax.enterprise.context.NormalScope;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.annotation.Annotation;

public class ParameterImpl<T extends Serializable> implements Parameter<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private T value;

    private final String key;

    private boolean sessionScoped = false;

    private boolean requestScoped = false;

    private boolean scoped = false;

    private Annotated annotatedInjectionPoint;

    private Class<T> type;

    private final ConvertUtilsBean converter = new ConvertUtilsBean();

    private HttpServletRequest getRequest() {
        return CDI.current().select(HttpServletRequest.class).get();
    }

    public ParameterImpl(InjectionPoint ip) {
        if (ip.getAnnotated().isAnnotationPresent(Name.class)) {
            String key = ip.getAnnotated().getAnnotation(Name.class).value();

            if (!"".equals(key)) {
                this.key = key;
            } else {
                this.key = ip.getMember().getName();
            }
        } else {
            this.key = ip.getMember().getName();
        }

        this.type = Reflections.getGenericTypeArgument(ip.getMember(), 0);
        this.sessionScoped = ip.getAnnotated().isAnnotationPresent(SessionScoped.class);
        this.scoped = ip.getAnnotated().isAnnotationPresent(NormalScope.class);

        // O padrão de parâmetros é ser RequestScoped, então a ausência de escopos na injeção
        // indica que o escopo é Request
        this.requestScoped = ip.getAnnotated().isAnnotationPresent(RequestScoped.class) || !scoped;

        this.annotatedInjectionPoint = ip.getAnnotated();
    }

    public String getKey() {
        return key;
    }

    private boolean isSessionScoped() {
        return sessionScoped;
    }

    private boolean isRequestScoped() {
        return requestScoped;
    }

    private boolean isScoped() {
        return scoped;
    }

    @SuppressWarnings("unchecked")
    public T getValue() {
        final String parameterValue = getRequest().getParameter(key);

        if (isSessionScoped()) {
            if (parameterValue != null) {
                getRequest().getSession().setAttribute(key, convert(parameterValue, type));
            }

            value = (T) getRequest().getSession().getAttribute(key);

        } else if (isScoped() && !isRequestScoped()) {
            value = null;
            for (Annotation annotation : annotatedInjectionPoint.getAnnotations()) {
                if (annotation.annotationType().isAnnotationPresent(NormalScope.class)) {
                    ScopedParameterValueHolder holder = CDI.current().select(ScopedParameterValueHolder.class, annotation).get();

                    if (parameterValue != null) {
                        holder.setValue(convert(parameterValue, type));
                    }

                    value = (T) holder.getValue();

                    break;
                }
            }
        } else {
            value = convert(parameterValue, type);
        }

        return value;
    }

    @Override
    public void setValue(T value) {
        if (isSessionScoped()) {
            getRequest().getSession().setAttribute(key, value);
        } else if (isRequestScoped()) {
            throw new DemoiselleException(getBundle().getString("parameter-invalid-request-operation"));
        }
        //TODO Terminar implementação

        //else if

        /*else if (isViewScoped()) {
			Map<String, Object> viewMap = getViewMap();
			viewMap.put(key, value);

		} else {
			this.value = value;
		}*/

        // Se o escopo desse bean for View ou Dependent, armazenamos o valor no próprio bean
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    private T convert(final String parameterValue, Class<T> targetClass) {
        Converter typeConverter = converter.lookup(String.class, targetClass);
        if (typeConverter != null) {
            return (T) converter.convert(parameterValue, targetClass);
        } else {
            throw new DemoiselleException(getBundle().getString("parameter-converter-not-found", targetClass.getCanonicalName()));
        }
    }

	/*private Converter getConverter(Class<?> targetClass) {
		Converter result;

		try {
			Application application = FacesContext.getCurrentInstance().getApplication();
			result = application.createConverter(targetClass);

		} catch (Exception e) {
			result = null;
		}

		return result;
	}

	private Object convert(final String value, final Converter converter) {
		Object result = null;

		if (!Strings.isEmpty(value)) {
			if (converter != null) {
				result = converter
						.getAsObject(FacesContext.getCurrentInstance(), FacesContext.getCurrentInstance().getViewRoot(),
								value);
			} else {
				result = value;
			}
		}

		return result;
	}*/

	/*private static Map<String, Object> getViewMap() {
		UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
		return viewRoot.getViewMap(true);
	}*/

    private ResourceBundle getBundle() {
        return CDI.current().select(ResourceBundle.class, new NameQualifier("demoiselle-servlet-bundle.properties")).get();
    }

    @SuppressWarnings("WeakerAccess")
    class ScopedParameterValueHolder implements Serializable {

        private static final long serialVersionUID = 1L;

        private Object value;

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}
