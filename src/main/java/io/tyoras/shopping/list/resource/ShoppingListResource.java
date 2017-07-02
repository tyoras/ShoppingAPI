package io.tyoras.shopping.list.resource;

import static io.tyoras.shopping.infra.config.guice.SwaggerModule.SECURITY_DEFINITION_OAUTH2;
import static io.tyoras.shopping.infra.rest.error.Level.INFO;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static io.tyoras.shopping.list.resource.ShoppingListResourceErrorMessage.LISTS_NOT_FOUND;
import static io.tyoras.shopping.list.resource.ShoppingListResourceErrorMessage.LIST_NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.tyoras.shopping.infra.rest.Link;
import io.tyoras.shopping.infra.rest.RestAPI;
import io.tyoras.shopping.infra.rest.error.WebApiException;
import io.tyoras.shopping.infra.util.ResourceUtil;
import io.tyoras.shopping.list.ShoppingList;
import io.tyoras.shopping.list.repository.ShoppingListRepository;
import io.tyoras.shopping.list.representation.ShoppingListRepresentation;
import io.tyoras.shopping.list.representation.ShoppingListWriteRepresentation;
import io.tyoras.shopping.user.User;

/**
 * Shopping list API
 * @ApiParam(hidden = true) @Author yoan
 */
@Path("/api/list")
@Api(value = "Shopping List", authorizations = { @Authorization(value = SECURITY_DEFINITION_OAUTH2, scopes = {})})
@Produces({ "application/json", "application/xml" })
public class ShoppingListResource extends RestAPI {
	private final ShoppingListRepository listRepo;
	
	@Inject
	public ShoppingListResource(ShoppingListRepository listRepo) {
		super();
		this.listRepo = Objects.requireNonNull(listRepo);
	}
	
	@Override
	public List<Link> getRootLinks() {
		List<Link> links = Lists.newArrayList(Link.self(getUriInfo()));
		
		URI createURI = getUriInfo().getAbsolutePath();
		links.add(new Link("create", createURI));
		URI getByIdURI = getUriInfo().getAbsolutePathBuilder().path(ShoppingListResource.class, "getById").build("{listId}");
		links.add(new Link("getById", getByIdURI));
		URI getByOwnerIdURI = getUriInfo().getAbsolutePathBuilder().path(ShoppingListResource.class, "getByOwnerId").build("{ownerId}");
		links.add(new Link("getByOwnerId", getByOwnerIdURI));
		URI updateURI = getUriInfo().getAbsolutePathBuilder().path(ShoppingListResource.class, "update").build("{listId}");
		links.add(new Link("update", updateURI));
		URI deleteByIdURI = getUriInfo().getAbsolutePathBuilder().path(ShoppingListResource.class, "deleteById").build("{listId}");
		links.add(new Link("deleteById", deleteByIdURI));
		
		return links;
	}
	
	@POST
	@Timed
	@ApiOperation(value = "Create shopping list", notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 201, message = "List created"),
		@ApiResponse(code = 400, message = "Invalid list")})
	public Response create(@ApiParam(hidden = true) @Auth User connectedUser, @ApiParam(value = "List to create", required = true) ShoppingListWriteRepresentation listToCreate) {
		UUID newListId = UUID.randomUUID();
		ShoppingList createdList = ShoppingListWriteRepresentation.toShoppingList(listToCreate, newListId);
		
		listRepo.create(createdList);
		ShoppingListRepresentation createdShoppingListRepresentation = new ShoppingListRepresentation(createdList, getUriInfo());
		UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(createdList.getId().toString()).build();
		return Response.created(location).entity(createdShoppingListRepresentation).build();
	}
	
	@GET
	@Path("/{listId}")
	@Timed
	@ApiOperation(value = "Get shopping list by Id", notes = "This can only be done by the logged in user.", response = ShoppingListRepresentation.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Found list"),
		@ApiResponse(code = 400, message = "Invalid list Id"),
		@ApiResponse(code = 404, message = "List not found") })
	public Response getById(@ApiParam(hidden = true) @Auth User connectedUser, @PathParam("listId") @ApiParam(value = "Shopping list identifier", required = true) String listIdStr) {
		ShoppingList foundList = findShoppingListById(listIdStr);
		ShoppingListRepresentation foundShoppingListRepresentation = new ShoppingListRepresentation(foundList, getUriInfo());
		return Response.ok().entity(foundShoppingListRepresentation).build();
	}
	
	@GET
	@Path("/user/{ownerId}")
	@Timed
	@ApiOperation(value = "Get shopping list by owner Id", notes = "This can only be done by the logged in user.", response = ShoppingListRepresentation.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Found lists"),
		@ApiResponse(code = 400, message = "Invalid owner Id"),
		@ApiResponse(code = 404, message = "Owner not found") })
	public Response getByOwnerId(@ApiParam(hidden = true) @Auth User connectedUser, @PathParam("ownerId") @ApiParam(value = "Owner identifier", required = true) String ownerIdStr) {
		ImmutableList<ShoppingList> foundLists = findShoppingListByOwnerId(ownerIdStr);
		List<ShoppingListRepresentation> listsRepresentation = new ArrayList<>();
		foundLists.forEach(list -> listsRepresentation.add(new ShoppingListRepresentation(list, getUriInfo())));
		return Response.ok().entity(listsRepresentation).build();
	}
	
	@PUT
	@Path("/{listId}")
	@Timed
	@ApiOperation(value = "Update", notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 204, message = "Shopping list updated"),
		@ApiResponse(code = 400, message = "Invalid list Id"),
		@ApiResponse(code = 404, message = "List not found") })
	public Response update(@ApiParam(hidden = true) @Auth User connectedUser, @PathParam("listId") @ApiParam(value = "Shopping list identifier", required = true) String listIdStr, 
						   @ApiParam(value = "List to update", required = true) ShoppingListWriteRepresentation listToUpdate) {
		UUID listId = ResourceUtil.getIdfromParam("listId", listIdStr);
		ShoppingList updatedList = ShoppingListWriteRepresentation.toShoppingList(listToUpdate, listId);
		listRepo.update(updatedList);

		UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(updatedList.getId().toString()).build();
		return Response.noContent().location(location).build();
	}
	
	@DELETE
	@Path("/{listId}")
	@Timed
	@ApiOperation(value = "Delete list by Id", notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "List deleted"),
		@ApiResponse(code = 400, message = "Invalid list Id"),
		@ApiResponse(code = 404, message = "List not found") })
	public Response deleteById(@ApiParam(hidden = true) @Auth User connectedUser, @PathParam("listId") @ApiParam(value = "Shopping list identifier", required = true) String listIdStr) {
		ShoppingList foundList = findShoppingListById(listIdStr);
		listRepo.deleteById(foundList.getId());
		return Response.noContent().build();
	}
	
	private ShoppingList findShoppingListById(String listIdStr) {
		UUID listId = ResourceUtil.getIdfromParam("listId", listIdStr);
		ShoppingList foundShoppingList = listRepo.getById(listId);
		
		if (foundShoppingList == null) {
			throw new WebApiException(NOT_FOUND, INFO, API_RESPONSE, LIST_NOT_FOUND);
		}
		
		return foundShoppingList;
	}
	
	private ImmutableList<ShoppingList> findShoppingListByOwnerId(String ownerIdStr) {
		UUID ownerId = ResourceUtil.getIdfromParam("ownerId", ownerIdStr);
		ImmutableList<ShoppingList> foundLists = listRepo.getByOwner(ownerId);
		
		if (foundLists.isEmpty()) {
			throw new WebApiException(NOT_FOUND, INFO, API_RESPONSE, LISTS_NOT_FOUND.getDevReadableMessage(ownerIdStr));
		}
		
		return foundLists;
	}
}