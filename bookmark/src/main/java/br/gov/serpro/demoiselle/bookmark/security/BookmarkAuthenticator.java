package br.gov.serpro.demoiselle.bookmark.security;

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
public class BookmarkAuthenticator implements Authenticator {

    private Principal principal;

    @Override
    public void authenticate() throws Exception {

        Credentials credentials = CDI.current().select(Credentials.class).get();

        if(credentials.getPassword().equals("secret")){
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
