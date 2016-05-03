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
package org.demoiselle.internal.implementation;

import org.demoiselle.annotation.ApplicationException;
import org.demoiselle.annotation.Priority;
import org.demoiselle.annotation.literal.NameQualifier;
import org.demoiselle.internal.producer.LoggerProducer;
import org.demoiselle.internal.producer.ResourceBundleProducer;
import org.demoiselle.message.SeverityType;
import org.demoiselle.util.ResourceBundle;

import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.CDI;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;

/**
 * Represents an annotated method to be processed;
 * 
 * @param <T>
 *            declaring class owner of the method
 */
public class AnnotatedMethodProcessor<T> implements Comparable<AnnotatedMethodProcessor<T>> {

	private AnnotatedMethod<T> annotatedMethod;

	private transient ResourceBundle bundle;

	private transient Logger logger;

	public AnnotatedMethodProcessor(final AnnotatedMethod<T> annotatedMethod) {
		this.annotatedMethod = annotatedMethod;
	}

	public AnnotatedMethod<T> getAnnotatedMethod() {
		return this.annotatedMethod;
	}

	@SuppressWarnings("unchecked")
	protected T getReferencedBean() {
		Class<T> classType = (Class<T>) getAnnotatedMethod().getJavaMember().getDeclaringClass();
		return CDI.current().select(classType).get();
	}

	public int compareTo(final AnnotatedMethodProcessor<T> other) {
		Integer orderThis = getPriority(getAnnotatedMethod());
		Integer orderOther = getPriority(other.getAnnotatedMethod());

		return orderThis.compareTo(orderOther);
	}

	public boolean process(Object... args) throws Exception {
		getLogger().info(getBundle().getString("processing", getAnnotatedMethod().getJavaMember().toGenericString()));

		try {
			getAnnotatedMethod().getJavaMember().invoke(getReferencedBean(), args);

		} catch (InvocationTargetException cause) {
			handleException(cause.getCause());
		}

		return true;
	}

	private void handleException(Throwable cause) throws Exception {
		ApplicationException ann = cause.getClass().getAnnotation(ApplicationException.class);

		if (ann == null || SeverityType.FATAL == ann.severity()) {
			throw (cause instanceof Exception ? (Exception) cause : new Exception(cause));

		} else {
			switch (ann.severity()) {
				case INFO:
					getLogger().info(cause.getMessage());
					break;

				case WARN:
					getLogger().warning(cause.getMessage());
					break;

				default:
					getLogger().log(SEVERE, getBundle().getString("processing-fail"), cause);
					break;
			}
		}
	}

	private static <T> Integer getPriority(AnnotatedMethod<T> annotatedMethod) {
		Integer priority = Priority.MIN_PRIORITY;

		Priority annotation = annotatedMethod.getAnnotation(Priority.class);
		if (annotation != null) {
			priority = annotation.value();
		}

		return priority;
	}

	protected ResourceBundle getBundle() {
		if (bundle == null) {
			bundle = CDI.current().select(ResourceBundle.class, new NameQualifier("demoiselle-core-bundle")).get();
		}

		return bundle;
	}

	protected Logger getLogger() {
		if (logger == null) {
			logger = CDI.current().select(Logger.class, new NameQualifier("br.gov.frameworkdemoiselle.lifecycle")).get();
		}

		return logger;
	}
}
