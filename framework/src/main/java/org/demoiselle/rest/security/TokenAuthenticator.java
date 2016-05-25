package org.demoiselle.rest.security;

import org.demoiselle.annotation.Priority;
import org.demoiselle.security.Authenticator;
import org.demoiselle.security.InvalidCredentialsException;
import org.demoiselle.servlet.security.ServletAuthenticator;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.CDI;
import java.security.Principal;

/**
 * Created by 01748913506 on 24/05/16.
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

