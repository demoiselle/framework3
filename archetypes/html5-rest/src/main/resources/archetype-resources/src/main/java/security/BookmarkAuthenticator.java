#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.security;

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
@Priority(Priority.L3_PRIORITY)
public class BookmarkAuthenticator implements Authenticator {

	private static final long serialVersionUID = -118515447020255993L;

	private Principal principal;

    /**
     * Authenticates any user since that password be "secret".
     * @throws Exception
     */
    @Override
    public void authenticate() throws Exception {

        Credentials credentials = CDI.current().select(Credentials.class).get();

        if(credentials.getPassword().equals("secret")){
            this.principal = credentials::getUsername;
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
