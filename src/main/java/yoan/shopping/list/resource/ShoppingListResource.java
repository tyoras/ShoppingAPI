package yoan.shopping.list.resource;

import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static yoan.shopping.infra.config.guice.ShoppingWebModule.CONNECTED_USER;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.rest.error.Level.INFO;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.list.resource.ShoppingListResourceErrorMessage.ALREADY_EXISTING_LIST;
import static yoan.shopping.list.resource.ShoppingListResourceErrorMessage.LISTS_NOT_FOUND;
import static yoan.shopping.list.resource.ShoppingListResourceErrorMessage.LIST_NOT_FOUND;
import static yoan.shopping.list.resource.ShoppingListResourceErrorMessage.MISSING_LIST_ID_FOR_UPDATE;

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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import yoan.shopping.infra.rest.Link;
import yoan.shopping.infra.rest.RestAPI;
import yoan.shopping.infra.rest.RestRepresentation;
import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.infra.util.ResourceUtil;
import yoan.shopping.list.ShoppingList;
import yoan.shopping.list.repository.ShoppingListRepository;
import yoan.shopping.list.representation.ShoppingListRepresentation;
import yoan.shopping.user.User;

/**
 * Shopping list API
 * @author yoan
 */
@Path("/api/list")
@Api(value = "/list")
@Produces({ "application/json", "application/xml" })
public class ShoppingListResource extends RestAPI {
	/** Currently connected user */
	private final User connectedUser;
	private final ShoppingListRepository listRepo;
	
	@Inject
	public ShoppingListResource(@Named(CONNECTED_USER) User connectedUser, ShoppingListRepository listRepo) {
		super();
		this.connectedUser = requireNonNull(connectedUser);
		this.listRepo = Objects.requireNonNull(listRepo);
	}
	
	@GET
	@ApiOperation(value = "Get shopping list API root", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This will can only be done by the logged in user.", response = ShoppingListRepresentation.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Root"), @ApiResponse(code = 401, message = "Not authenticated") })
	@Override
	public Response root() {
		RestRepresentation rootRepresentation = new RestRepresentation(getRootLinks());
		return Response.ok().entity(rootRepresentation).build();
	}
	
	@Override
	public List<Link> getRootLinks() {
		List<Link> links = Lists.newArrayList(Link.self(getUriInfo()));
		
		URI createURI = getUriInfo().getAbsolutePath();
		links.add(new Link("create", createURI));
		URI getByIdURI = getUriInfo().getAbsolutePathBuilder().path(ShoppingListResource.class, "getById").build("{listId}");
		links.add(new Link("getById", getByIdURI));
		URI getByOwnerIdURI = getUriInfo().getAbsolutePathBuilder().path(ShoppingListResource.class, "getByOwnerId").build(connectedUser.getId().toString());
		links.add(new Link("getByOwnerId", getByOwnerIdURI));
		URI updateURI = getUriInfo().getAbsolutePath();
		links.add(new Link("update", updateURI));
		URI deleteByIdURI = getUriInfo().getAbsolutePathBuilder().path(ShoppingListResource.class, "deleteById").build("{listId}");
		links.add(new Link("deleteById", deleteByIdURI));
		
		return links;
	}
	
	@POST
	@ApiOperation(value = "Create shopping list", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This will can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 201, message = "List created"),
		@ApiResponse(code = 400, message = "Invalid list"),
		@ApiResponse(code = 409, message = "Already existing list")})
	public Response create(@ApiParam(value = "List to create", required = true) ShoppingListRepresentation listToCreate) {
		ShoppingList createdList = ShoppingListRepresentation.toShoppingList(listToCreate);
		//if the Id was not provided we generate one
		if (createdList.getId().equals(ShoppingList.DEFAULT_ID)) {
			createdList = ShoppingList.Builder.createFrom(createdList).withRandomId().build();
		}
		
		ensureShoppingListNotExists(createdList.getId());
		listRepo.create(createdList);
		ShoppingListRepresentation createdShoppingListRepresentation = new ShoppingListRepresentation(createdList, getUriInfo());
		UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(createdList.getId().toString()).build();
		return Response.created(location).entity(createdShoppingListRepresentation).build();
	}
	
	@GET
	@Path("/{listId}")
	@ApiOperation(value = "Get shopping list by Id", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This will can only be done by the logged in user.", response = ShoppingListRepresentation.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Found list"),
		@ApiResponse(code = 400, message = "Invalid list Id"),
		@ApiResponse(code = 404, message = "List not found") })
	public Response getById(@PathParam("listId") @ApiParam(value = "Shopping list identifier", required = true) String listIdStr) {
		ShoppingList foundList = findShoppingListById(listIdStr);
		ShoppingListRepresentation foundShoppingListRepresentation = new ShoppingListRepresentation(foundList, getUriInfo());
		return Response.ok().entity(foundShoppingListRepresentation).build();
	}
	
	@GET
	@Path("/user/{ownerId}")
	@ApiOperation(value = "Get shopping list by Id", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This will can only be done by the logged in user.", response = ShoppingListRepresentation.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Found lists"),
		@ApiResponse(code = 400, message = "Invalid owner Id"),
		@ApiResponse(code = 404, message = "Owner not found") })
	public Response getByOwnerId(@PathParam("ownerId") @ApiParam(value = "Owner identifier", required = true) String ownerIdStr) {
		ImmutableList<ShoppingList> foundLists = findShoppingListByOwnerId(ownerIdStr);
		List<ShoppingListRepresentation> listsRepresentation = new ArrayList<>();
		foundLists.forEach(list -> listsRepresentation.add(new ShoppingListRepresentation(list, getUriInfo())));
		return Response.ok().entity(listsRepresentation).build();
	}
	
	@PUT
	@ApiOperation(value = "Update", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This will can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 204, message = "Shopping list updated"),
		@ApiResponse(code = 400, message = "Invalid list Id"),
		@ApiResponse(code = 404, message = "List not found") })
	public Response update(@ApiParam(value = "List to update", required = true) ShoppingListRepresentation listToUpdate) {
		ShoppingList updatedList = ShoppingListRepresentation.toShoppingList(listToUpdate);
		ensureListIdProvidedForUpdate(updatedList.getId());
		listRepo.update(updatedList);

		UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(updatedList.getId().toString()).build();
		return Response.noContent().location(location).build();
	}
	
	@DELETE
	@Path("/{listId}")
	@ApiOperation(value = "Delete list by Id", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This will can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "List deleted"),
		@ApiResponse(code = 400, message = "Invalid list Id"),
		@ApiResponse(code = 404, message = "List not found") })
	public Response deleteById(@PathParam("listId") @ApiParam(value = "Shopping list identifier", required = true) String listIdStr) {
		ShoppingList foundList = findShoppingListById(listIdStr);
		listRepo.deleteById(foundList.getId());
		return Response.ok().build();
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
	
	private void ensureShoppingListNotExists(UUID listId) {
		ShoppingList foundShoppingList = listRepo.getById(listId);
		
		if (foundShoppingList != null) {
			throw new WebApiException(CONFLICT, ERROR, API_RESPONSE, ALREADY_EXISTING_LIST.getDevReadableMessage(listId));
		}
	}
	
	private void ensureListIdProvidedForUpdate(UUID listId) {
		if (listId.equals(ShoppingList.DEFAULT_ID)) {
			throw new WebApiException(BAD_REQUEST, ERROR, API_RESPONSE, MISSING_LIST_ID_FOR_UPDATE);
		}
	}
}