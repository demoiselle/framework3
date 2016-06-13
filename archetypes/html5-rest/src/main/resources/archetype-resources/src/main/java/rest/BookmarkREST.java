#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.rest;

import ${package}.business.BookmarkBC;
import ${package}.domain.Bookmark;
import org.demoiselle.security.LoggedIn;
import org.demoiselle.util.Strings;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

/**
 * <p>Offers services to manipulate Bookmarks</p>
 *
 * @author SERPRO
 */
@Path("bookmark")
public class BookmarkREST {

    @Inject
    private BookmarkBC bc;

    @GET
    @Produces("application/json")
    public List<Bookmark> find(@QueryParam("q") String query) throws Exception {
        List<Bookmark> result;

        if (Strings.isEmpty(query)) {
            result = bc.listAll();//findAll();
        } else {
            result = bc.find(query);
        }

        return result;
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public Bookmark load(@PathParam("id") Long id) throws Exception {
        Bookmark result = bc.load(id);

        if (result == null) {
            throw new NotFoundException();
        }

        return result;
    }

    @POST
    @LoggedIn
    @Transactional
//    @ValidatePayload
    @Produces("application/json")
    @Consumes("application/json")
    public Response insert(@Valid Bookmark body, @Context UriInfo uriInfo) throws Exception {
        checkId(body);

        String id = bc.merge(body).getId().toString(); //insert(body).getId().toString();
        URI location = uriInfo.getRequestUriBuilder().path(id).build();

        return Response.created(location).entity(id).build();
    }

    @PUT
    @LoggedIn
    @Path("{id}")
    @Transactional
//    @ValidatePayload
    @Produces("application/json")
    @Consumes("application/json")
    public void update(@PathParam("id") Long id, @Valid Bookmark body) throws Exception {
        checkId(body);
        load(id);

        body.setId(id);
        bc.merge(body); // update(body);
    }

    @DELETE
    @LoggedIn
    @Path("{id}")
    @Transactional
    public void delete(@PathParam("id") Long id) throws Exception {
        load(id);
        bc.remove(id); //delete(id);
    }

    @DELETE
    @LoggedIn
    @Transactional
    public void delete(List<Long> ids) throws Exception {
        bc.remove(ids); //delete(ids);
    }

    private void checkId(Bookmark entity) throws Exception {
        if (entity.getId() != null) {
            throw new BadRequestException();
        }
    }
}
