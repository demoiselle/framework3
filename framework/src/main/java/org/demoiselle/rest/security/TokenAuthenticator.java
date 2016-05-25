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
package org.demoiselle.rest.security;

import org.demoiselle.annotation.Priority;
import org.demoiselle.security.Authenticator;
import org.demoiselle.security.InvalidCredentialsException;
import org.demoiselle.servlet.security.ServletAuthenticator;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.CDI;
import java.security.Principal;

/**
 * @author SERPRO
 */
@RequestScoped
@Priority(Priority.L2_PRIORITY)
public class TokenAuthenticator implements Authenticator {

    private static final long serialVersionUID = 1L;

    private Principal user;

    @Override
    public void authenticate() throws Exception {
        Token token = CDI.current().select(Token.class).get(); //Beans.getReference(Token.class);
        TokenManager tokenManager = CDI.current().select(TokenManager.class).get(); //Beans.getReference(TokenManager.class, new StrategyQualifier());

        if (token.isEmpty()) {
            this.user = customAuthentication();

            String newToken = tokenManager.persist(this.user);
            token.setValue(newToken);

        } else {
            this.user = tokenAuthentication(token, tokenManager);
        }
    }

    protected Principal customAuthentication() throws Exception {
        ServletAuthenticator authenticator = CDI.current().select(ServletAuthenticator.class).get(); //Beans.getReference(ServletAuthenticator.class);
        authenticator.authenticate();

        return authenticator.getUser();
    }

    private Principal tokenAuthentication(Token token, TokenManager tokenManager) throws Exception {
        Principal principal = tokenManager.load(token.getValue());

        if (principal == null) {
            throw new InvalidCredentialsException("token inválido");
        }

        return principal;
    }

    @Override
    // TODO Apagar o token
    public void unauthenticate() {
        this.user = null;
    }

    @Override
    public Principal getUser() {
        return this.user;
    }
}

