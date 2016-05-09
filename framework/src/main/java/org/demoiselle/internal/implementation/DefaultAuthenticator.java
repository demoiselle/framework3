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

import org.demoiselle.exception.DemoiselleException;
import org.demoiselle.annotation.Priority;
import org.demoiselle.security.AuthenticationException;
import org.demoiselle.security.Authenticator;
import org.demoiselle.security.SecurityContext;
import org.demoiselle.annotation.literal.NameQualifier;
import org.demoiselle.util.ResourceBundle;

import javax.enterprise.inject.spi.CDI;
import java.security.Principal;

import static org.demoiselle.annotation.Priority.L1_PRIORITY;

/**
 * <p>
 * Authenticator that actually does nothing but raise exceptions.
 * </p>
 *
 * @author SERPRO
 * @see Authenticator
 */
@Priority(L1_PRIORITY)
public class DefaultAuthenticator implements Authenticator {

	private static final long serialVersionUID = 1L;

	private transient ResourceBundle bundle;

	/**
	 * @see org.demoiselle.security.Authenticator#authenticate()
	 */
	@Override
	public void authenticate() throws AuthenticationException {
		throw getException();
	}

	/**
	 * @see org.demoiselle.security.Authenticator#unauthenticate()
	 */
	@Override
	public void unauthenticate() {
		throw getException();
	}

	/**
	 * @see org.demoiselle.security.Authenticator#getUser()
	 */
	@Override
	public Principal getUser() {
		throw getException();
	}

	private AuthenticationException getException() {
		return new AuthenticationException(getBundle().getString("authenticator-not-defined",
				SecurityContext.class.getSimpleName()), new ClassNotFoundException());
	}

	private ResourceBundle getBundle() {
		if (bundle == null) {
			bundle = CDI.current().select(ResourceBundle.class, new NameQualifier("demoiselle-core-bundle")).get();
//			bundle = Beans.getReference(ResourceBundle.class, new NameQualifier("demoiselle-core-bundle"));
		}

		return bundle;
	}
}
