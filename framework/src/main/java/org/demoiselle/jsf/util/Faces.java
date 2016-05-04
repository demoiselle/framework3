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
package org.demoiselle.jsf.util;

import org.demoiselle.annotation.ApplicationException;
import org.demoiselle.jsf.message.ValidationFailedMessage;
import org.demoiselle.message.Message;
import org.demoiselle.message.SeverityType;
import org.demoiselle.util.Strings;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.*;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.util.List;
import java.util.Map;

import static javax.faces.application.FacesMessage.*;

/**
 * Utility class to insert messages in the FacesContext.
 * 
 * @author SERPRO
 */
@SuppressWarnings("unused")
public class Faces {

	private Faces() {
	}

	@SuppressWarnings("unused")
	public static void addMessages(final List<Message> messages) {
		if (messages != null) {
			for (Message m : messages) {
				addMessage(m);
			}
		}
	}

	@SuppressWarnings("WeakerAccess")
	public static void addMessage(final Message message) {
		getFacesContext().addMessage(null, parse(message));
		if (message instanceof ValidationFailedMessage) {
			getFacesContext().validationFailed();
		}
	}

	public static void addMessage(final String clientId, final Message message) {
		getFacesContext().addMessage(clientId, parse(message));
	}

	@SuppressWarnings("WeakerAccess")
	public static void addMessage(final String clientId, final Throwable throwable) {
		getFacesContext().addMessage(clientId, parse(throwable));
	}

	@SuppressWarnings("unused")
	public static void addMessage(final Throwable throwable) {
		addMessage(null, throwable);
	}

	private static FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	@SuppressWarnings("WeakerAccess")
	public static Severity parse(final SeverityType severityType) {
		Severity result = null;

		switch (severityType) {
			case INFO:
				result = SEVERITY_INFO;
				break;
			case WARN:
				result = SEVERITY_WARN;
				break;
			case ERROR:
				result = SEVERITY_ERROR;
				break;
			case FATAL:
				result = SEVERITY_FATAL;
		}

		return result;
	}

	@SuppressWarnings("WeakerAccess")
	public static FacesMessage parse(final Throwable throwable) {
		FacesMessage facesMessage = new FacesMessage();
		ApplicationException annotation = throwable.getClass().getAnnotation(ApplicationException.class);

		if (annotation != null) {
			facesMessage.setSeverity(parse(annotation.severity()));
		} else {
			facesMessage.setSeverity(SEVERITY_ERROR);
		}

		if (throwable.getMessage() != null) {
			facesMessage.setSummary(throwable.getMessage());
		} else {
			facesMessage.setSummary(throwable.toString());
		}

		return facesMessage;
	}

	@SuppressWarnings("WeakerAccess")
	public static FacesMessage parse(final Message message) {
		FacesMessage facesMessage = new FacesMessage();
		facesMessage.setSeverity(parse(message.getSeverity()));
		facesMessage.setSummary(message.getText());
		return facesMessage;
	}

	@SuppressWarnings("unused")
	public static Object convert(final String value, final Converter converter) {
		Object result = null;

		if (!Strings.isEmpty(value)) {
			if (converter != null) {
				result = converter.getAsObject(getFacesContext(), getFacesContext().getViewRoot(), value);
			} else {
				result = value;
			}
		}

		return result;
	}

	@SuppressWarnings("unused")
	public static Converter getConverter(Class<?> targetClass) {
		Converter result;

		try {
			Application application = getFacesContext().getApplication();
			result = application.createConverter(targetClass);

		} catch (Exception e) {
			result = null;
		}

		return result;
	}

	public static Map<String, Object> getViewMap() {
		UIViewRoot viewRoot = getFacesContext().getViewRoot();
		return viewRoot.getViewMap(true);
	}

	@SuppressWarnings("unused")
	public static String getCurrentViewId(){
		return getFacesContext().getViewRoot().getViewId();
	}
	
}
