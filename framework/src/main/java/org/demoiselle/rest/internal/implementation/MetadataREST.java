package org.demoiselle.rest.internal.implementation;

import static javax.ws.rs.core.MediaType.TEXT_HTML;

import org.demoiselle.rest.exception.NotFoundException;
import org.demoiselle.rest.exception.ServiceUnavailableException;
import org.demoiselle.util.Metadata;
import org.demoiselle.util.ResourceBundle;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * Created by 01748913506 on 24/05/16.
 */
@Path("metadata")
public class MetadataREST {

    @Inject
    private ResourceBundle bundle;

    @GET
    @Path("demoiselle/version")
    @Produces("text/plain")
    public String getDemoiselleVersion() {
        return Metadata.getVersion();
    }

    @GET
    @Path("version")
    @Produces("text/plain")
    public String getAppVersion() throws Exception {
        String key = "application.version";

        if (!bundle.containsKey(key)) {
            // logger.debug();

            throw new ServiceUnavailableException();
        }

        return bundle.getString(key);
    }

    @GET
    @Path("message/{key}")
    @Produces(TEXT_HTML)
    public String getMessage(@PathParam("key") String key) throws Exception {
        if (!bundle.containsKey(key)) {
            throw new NotFoundException();
        }

        return bundle.getString(key);
    }
}

