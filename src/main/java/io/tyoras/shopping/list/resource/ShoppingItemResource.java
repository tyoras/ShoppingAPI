package io.tyoras.shopping.list.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.*;
import io.tyoras.shopping.infra.rest.Link;
import io.tyoras.shopping.infra.rest.RestAPI;
import io.tyoras.shopping.infra.rest.error.WebApiException;
import io.tyoras.shopping.infra.util.ResourceUtil;
import io.tyoras.shopping.list.ShoppingItem;
import io.tyoras.shopping.list.repository.ShoppingItemRepository;
import io.tyoras.shopping.list.representation.ShoppingItemRepresentation;
import io.tyoras.shopping.list.representation.ShoppingItemWriteRepresentation;
import io.tyoras.shopping.user.User;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static io.tyoras.shopping.infra.config.guice.SwaggerModule.SECURITY_DEFINITION_OAUTH2;
import static io.tyoras.shopping.infra.rest.error.Level.INFO;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static io.tyoras.shopping.list.resource.ShoppingItemResourceErrorMessage.ITEM_NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * Shopping item API
 *
 * @ApiParam(hidden = true) @Author yoan
 */
@Path("/api/list/{listId}/item")
@Api(value = "Shopping Item", authorizations = {@Authorization(value = SECURITY_DEFINITION_OAUTH2, scopes = {})})
@Produces({"application/json", "application/xml"})
public class ShoppingItemResource extends RestAPI {
    private final ShoppingItemRepository itemRepo;

    @Inject
    public ShoppingItemResource(ShoppingItemRepository itemRepo) {
        super();
        this.itemRepo = Objects.requireNonNull(itemRepo);
    }

    @Override
    public List<Link> getRootLinks() {
        List<Link> links = Lists.newArrayList(Link.self(getUriInfo()));

        URI createURI = getUriInfo().getAbsolutePath();
        links.add(new Link("create", createURI));
        URI getByIdURI = getUriInfo().getAbsolutePathBuilder().path(ShoppingItemResource.class, "getById").build("{itemId}");
        links.add(new Link("getById", getByIdURI));
        URI updateURI = getUriInfo().getAbsolutePathBuilder().path(ShoppingItemResource.class, "update").build("{itemId}");
        links.add(new Link("update", updateURI));
        URI deleteByIdURI = getUriInfo().getAbsolutePathBuilder().path(ShoppingItemResource.class, "deleteById").build("{itemId}");
        links.add(new Link("deleteById", deleteByIdURI));

        return links;
    }

    @POST
    @Timed
    @ApiOperation(value = "Create shopping item", notes = "This can only be done by the logged in user.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Item created"),
            @ApiResponse(code = 400, message = "Invalid item"),
            @ApiResponse(code = 404, message = "List not found")})
    public Response create(@ApiParam(hidden = true) @Auth User connectedUser, @PathParam("listId") @ApiParam(value = "Shopping list identifier", required = true) String listIdStr,
                           @ApiParam(value = "Item to create", required = true) ShoppingItemWriteRepresentation itemToCreate) {
        UUID listId = extractListId(listIdStr);
        UUID newItemId = UUID.randomUUID();
        ShoppingItem createdItem = ShoppingItemWriteRepresentation.toShoppingItem(itemToCreate, newItemId);

        itemRepo.create(listId, createdItem);
        ShoppingItemRepresentation createdShoppingItemRepresentation = new ShoppingItemRepresentation(createdItem);
        UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(createdItem.getId().toString()).build();
        return Response.created(location).entity(createdShoppingItemRepresentation).build();
    }

    @GET
    @Timed
    @Path("/{itemId}")
    @ApiOperation(value = "Get shopping item by Id", notes = "This can only be done by the logged in user.", response = ShoppingItemRepresentation.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Found item"),
            @ApiResponse(code = 400, message = "Invalid item Id"),
            @ApiResponse(code = 404, message = "Item not found")})
    public Response getById(@ApiParam(hidden = true) @Auth User connectedUser, @PathParam("listId") @ApiParam(value = "Shopping list identifier", required = true) String listIdStr,
                            @PathParam("itemId") @ApiParam(value = "Shopping item identifier", required = true) String itemIdStr) {
        UUID listId = extractListId(listIdStr);
        ShoppingItem foundItem = findShoppingItemById(listId, itemIdStr);
        ShoppingItemRepresentation foundShoppingItemRepresentation = new ShoppingItemRepresentation(foundItem);
        return Response.ok().entity(foundShoppingItemRepresentation).build();
    }

    @PUT
    @Timed
    @Path("/{itemId}")
    @ApiOperation(value = "Update", notes = "This can only be done by the logged in user.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Shopping item updated"),
            @ApiResponse(code = 400, message = "Invalid list Id"),
            @ApiResponse(code = 404, message = "Item not found")})
    public Response update(@ApiParam(hidden = true) @Auth User connectedUser, @PathParam("listId") @ApiParam(value = "Shopping list identifier", required = true) String listIdStr,
                           @PathParam("itemId") @ApiParam(value = "Shopping item identifier", required = true) String itemIdStr,
                           @ApiParam(value = "Item to update", required = true) ShoppingItemWriteRepresentation itemToUpdate) {
        UUID listId = extractListId(listIdStr);
        UUID itemId = ResourceUtil.getIdfromParam("itemId", itemIdStr);
        ShoppingItem updatedItem = ShoppingItemWriteRepresentation.toShoppingItem(itemToUpdate, itemId);
        itemRepo.update(listId, updatedItem);

        UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(updatedItem.getId().toString()).build();
        return Response.noContent().location(location).build();
    }

    @DELETE
    @Timed
    @Path("/{itemId}")
    @ApiOperation(value = "Delete item by Id", notes = "This can only be done by the logged in user.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Item deleted"),
            @ApiResponse(code = 400, message = "Invalid item Id"),
            @ApiResponse(code = 404, message = "Item not found")})
    public Response deleteById(@ApiParam(hidden = true) @Auth User connectedUser, @PathParam("listId") @ApiParam(value = "Shopping list identifier", required = true) String listIdStr,
                               @PathParam("itemId") @ApiParam(value = "Shopping item identifier", required = true) String itemIdStr) {
        UUID listId = extractListId(listIdStr);
        ShoppingItem foundItem = findShoppingItemById(listId, itemIdStr);
        itemRepo.deleteById(listId, foundItem.getId());
        return Response.noContent().build();
    }

    private ShoppingItem findShoppingItemById(UUID listId, String itemIdStr) {
        UUID itemId = ResourceUtil.getIdfromParam("itemId", itemIdStr);
        ShoppingItem foundShoppingItem = itemRepo.getById(listId, itemId);

        if (foundShoppingItem == null) {
            throw new WebApiException(NOT_FOUND, INFO, API_RESPONSE, ITEM_NOT_FOUND);
        }

        return foundShoppingItem;
    }

    private UUID extractListId(String listIdStr) {
        return ResourceUtil.getIdfromParam("listId", listIdStr);
    }
}