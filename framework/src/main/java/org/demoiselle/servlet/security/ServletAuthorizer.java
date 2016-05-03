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
import org.demoiselle.exception.DemoiselleException;
import org.demoiselle.security.Authorizer;
import org.demoiselle.security.RequiredPermission;
import org.demoiselle.util.ResourceBundle;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.http.HttpServletRequest;

import static org.demoiselle.annotation.Priority.L2_PRIORITY;

/**
 * <p>
 * Implements the {@link Authorizer} interface, offering a way to implement the authorizer's functionalities.
 * </p>
 *
 * @author SERPRO
 */

@Priority(L2_PRIORITY)
public class ServletAuthorizer implements Authorizer {

	private static final long serialVersionUID = 1L;

	private transient ResourceBundle bundle;

	@Override
	public boolean hasRole(String role) throws Exception {
		return getRequest().isUserInRole(role);
	}

	@Override
	public boolean hasPermission(String resource, String operation) throws Exception {
		throw new DemoiselleException(getBundle().getString("has-permission-not-supported",
				RequiredPermission.class.getSimpleName()));
	}

	private HttpServletRequest getRequest() {
		return CDI.current().select(HttpServletRequest.class).get();
//		return Beans.getReference(HttpServletRequest.class);
	}

	private ResourceBundle getBundle() {
		if (bundle == null) {
			bundle = CDI.current().select(ResourceBundle.class, new NameQualifier("demoiselle-servlet-bundle")).get();
//			bundle = Beans.getReference(ResourceBundle.class, new NameQualifier("demoiselle-servlet-bundle"));
		}

		return bundle;
	}
}
