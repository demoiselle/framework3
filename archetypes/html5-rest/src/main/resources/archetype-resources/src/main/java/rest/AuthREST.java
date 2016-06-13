#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.rest;

import org.demoiselle.security.LoggedIn;
import org.demoiselle.security.SecurityContext;
import org.demoiselle.servlet.security.Credentials;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.security.Principal;

/**
 * <p>Respnsible for realize login and logout of application.</p>
 *
 * @author SERPRO
 */
@Path("auth")
public class AuthREST {

    @Inject
    private SecurityContext securityContext;

    @POST
    @Path("login")
//    @ValidatePayload
    @Consumes("application/json")
    @Produces("application/json")
    public Principal login(CredentialsBody body) {
        Credentials credentials = CDI.current().select(Credentials.class).get(); //Beans.getReference(Credentials.class);
        credentials.setUsername(body.username);
        credentials.setPassword(body.password);

        securityContext.login();
        return securityContext.getUser();
    }

    @POST
    @LoggedIn
    @Path("logout")
//    @ValidatePayload
    public void logout() {
        securityContext.logout();
    }

    public static class CredentialsBody {

        @NotNull(message = "{required.field}")
        @Size(min = 1, message = "{required.field}")
        public String username;

        @NotNull(message = "{required.field}")
        @Size(min = 1, message = "{required.field}")
        public String password;
    }
}
