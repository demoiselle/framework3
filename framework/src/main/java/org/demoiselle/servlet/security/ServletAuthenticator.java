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
package org.demoiselle.servlet.security;

import org.demoiselle.annotation.Priority;
import org.demoiselle.annotation.literal.NameQualifier;
import org.demoiselle.security.AuthenticationException;
import org.demoiselle.security.Authenticator;
import org.demoiselle.security.InvalidCredentialsException;
import org.demoiselle.util.ResourceBundle;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

import static org.demoiselle.annotation.Priority.L2_PRIORITY;

/**
 * Implements the {@link Authenticator} interface, offering a way to implement offering a manner to use the
 * authenticator's functionalities.
 * 
 * @author SERPRO
 */

@Priority(L2_PRIORITY)
public class ServletAuthenticator implements Authenticator {

	private static final long serialVersionUID = 1L;

	private static ResourceBundle bundle;

	@Override
	public void authenticate() throws Exception {
		try {
			getRequest().login(getCredentials().getUsername(), getCredentials().getPassword());

		} catch (ServletException cause) {
			if (cause.getMessage().toLowerCase().contains("invalid")
					|| cause.getMessage().toLowerCase().contains("incorrect")
					|| cause.getMessage().toLowerCase().contains("failed")) {
				throw new InvalidCredentialsException();
			} else {
				throw new AuthenticationException(getBundle().getString("authentication-failed"), cause);
			}
		}
	}

	@Override
	public void unauthenticate() throws Exception {
		getCredentials().clear();
		try {
			getRequest().logout();
		} catch (ServletException e) {
			// Logout já havia sido efetuado
		}
		getRequest().getSession().invalidate();
	}

	@Override
	public Principal getUser() {
		return getRequest().getUserPrincipal();
	}

	protected Credentials getCredentials() {
		return CDI.current().select(Credentials.class).get();
//		return Beans.getReference(Credentials.class);
	}

	private HttpServletRequest getRequest() {
		return CDI.current().select(HttpServletRequest.class).get();
//		return Beans.getReference(HttpServletRequest.class);
	}

	private static ResourceBundle getBundle() {
		if (bundle == null) {
			bundle = CDI.current().select(ResourceBundle.class, new NameQualifier("demoiselle-servlet-bundle")).get();
//			bundle = Beans.getReference(ResourceBundle.class, new NameQualifier("demoiselle-servlet-bundle"));
		}

		return bundle;
	}
}
