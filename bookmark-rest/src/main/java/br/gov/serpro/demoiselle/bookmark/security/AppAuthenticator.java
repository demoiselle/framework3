package br.gov.serpro.demoiselle.bookmark.security;

import org.demoiselle.annotation.Priority;
import org.demoiselle.rest.security.TokenAuthenticator;
import org.demoiselle.security.InvalidCredentialsException;
import org.demoiselle.servlet.security.Credentials;

import javax.enterprise.inject.spi.CDI;
import java.security.Principal;

/**
 * <p>Implements the Authenticator method.</p>
 *
 * @author SERPRO
 */
@Priority(Priority.L4_PRIORITY)
public class AppAuthenticator extends TokenAuthenticator {

    private static final long serialVersionUID = 1L;

    @Override
    protected Principal customAuthentication() throws Exception {
        Principal user = null;
        final Credentials credentials = CDI.current().select(Credentials.class).get(); //Beans.getReference(Credentials.class);
        final String username = credentials.getUsername();

        if (credentials.getPassword().equals("secret")) {
            user = () -> username;
        } else {
            throw new InvalidCredentialsException();
        }

        return user;
    }
}
