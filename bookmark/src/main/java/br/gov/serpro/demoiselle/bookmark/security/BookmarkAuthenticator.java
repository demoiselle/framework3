package br.gov.serpro.demoiselle.bookmark.security;

import org.demoiselle.annotation.Priority;
import org.demoiselle.security.Authenticator;
import org.demoiselle.security.InvalidCredentialsException;
import org.demoiselle.servlet.security.Credentials;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.CDI;
import java.security.Principal;

/**
 * @author SERPRO
 */
@SessionScoped
@Priority(Priority.L2_PRIORITY)
public class BookmarkAuthenticator implements Authenticator {

    private Principal principal;

    /**
     * Authenticates any user since that password be "secret".
     *
     * @throws Exception
     */
    @Override
    public void authenticate() throws Exception {

        Credentials credentials = CDI.current().select(Credentials.class).get();

        if (credentials.getPassword().equals("secret")) {
            this.principal = new Principal() {
                @Override
                public String getName() {
                    return credentials.getUsername();
                }
            };
        } else {
            throw new InvalidCredentialsException();
        }

    }

    @Override
    public void unauthenticate() throws Exception {
        this.principal = null;
    }

    @Override
    public Principal getUser() {
        return this.principal;
    }
}