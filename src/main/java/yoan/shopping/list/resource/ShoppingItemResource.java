package yoan.shopping.list.resource;

import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static yoan.shopping.infra.config.guice.ShoppingWebModule.CONNECTED_USER;
import static yoan.shopping.infra.config.guice.SwaggerModule.SECURITY_DEFINITION_OAUTH2;
import static yoan.shopping.infra.rest.error.Level.INFO;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.list.resource.ShoppingItemResourceErrorMessage.ITEM_NOT_FOUND;

import java.net.URI;
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
import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.infra.util.ResourceUtil;
import yoan.shopping.list.ShoppingItem;
import yoan.shopping.list.repository.ShoppingItemRepository;
import yoan.shopping.list.representation.ShoppingItemRepresentation;
import yoan.shopping.list.representation.ShoppingItemWriteRepresentation;
import yoan.shopping.user.User;

/**
 * Shopping item API
 * @author yoan
 */
@Path("/api/list/{listId}/item")
@Api(value = "Shopping Item", authorizations = { @Authorization(value = SECURITY_DEFINITION_OAUTH2, scopes = {})})
@Produces({ "application/json", "application/xml" })
public class ShoppingItemResource extends RestAPI {
	/** Currently connected user */
	@SuppressWarnings("unused")
	private final User connectedUser;
	private final ShoppingItemRepository itemRepo;
	
	@Inject
	public ShoppingItemResource(@Named(CONNECTED_USER) User connectedUser, ShoppingItemRepository itemRepo) {
		super();
		this.connectedUser = requireNonNull(connectedUser);
		this.itemRepo = Objects.requireNonNull(itemRepo);
	}
	
	@Override
	public List<Link> getRootLinks() {
		List<Link> links = Lists.newArrayList(Link.self(getUriInfo()));
		
		URI createURI = getUriInfo().getAbsolutePath();
		links.add(new Link("create", createURI));
		URI getByIdURI = getUriInfo().getAbsolutePathBuilder().path(ShoppingItemResource.class, "getById").build("{itemId}");
		links.add(new Link("getById", getByIdURI));
		URI updateURI = getUriInfo().getAbsolutePath();
		links.add(new Link("update", updateURI));
		URI deleteByIdURI = getUriInfo().getAbsolutePathBuilder().path(ShoppingItemResource.class, "deleteById").build("{itemId}");
		links.add(new Link("deleteById", deleteByIdURI));
		
		return links;
	}
	
	@POST
	@ApiOperation(value = "Create shopping item", notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 201, message = "Item created"),
		@ApiResponse(code = 400, message = "Invalid item"),
		@ApiResponse(code = 404, message = "List not found")})
	public Response create(@PathParam("listId") @ApiParam(value = "Shopping list identifier", required = true) String listIdStr,
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
	@Path("/{itemId}")
	@ApiOperation(value = "Get shopping item by Id", notes = "This can only be done by the logged in user.", response = ShoppingItemRepresentation.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Found item"),
		@ApiResponse(code = 400, message = "Invalid item Id"),
		@ApiResponse(code = 404, message = "Item not found") })
	public Response getById(@PathParam("listId") @ApiParam(value = "Shopping list identifier", required = true) String listIdStr,
							@PathParam("itemId") @ApiParam(value = "Shopping item identifier", required = true) String itemIdStr) {
		UUID listId = extractListId(listIdStr);
		ShoppingItem foundItem = findShoppingItemById(listId, itemIdStr);
		ShoppingItemRepresentation foundShoppingItemRepresentation = new ShoppingItemRepresentation(foundItem);
		return Response.ok().entity(foundShoppingItemRepresentation).build();
	}
	
	@PUT
	@Path("/{itemId}")
	@ApiOperation(value = "Update", notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 204, message = "Shopping item updated"),
		@ApiResponse(code = 400, message = "Invalid list Id"),
		@ApiResponse(code = 404, message = "Item not found") })
	public Response update(@PathParam("listId") @ApiParam(value = "Shopping list identifier", required = true) String listIdStr,
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
	@Path("/{itemId}")
	@ApiOperation(value = "Delete item by Id", notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Item deleted"),
		@ApiResponse(code = 400, message = "Invalid item Id"),
		@ApiResponse(code = 404, message = "Item not found") })
	public Response deleteById(@PathParam("listId") @ApiParam(value = "Shopping list identifier", required = true) String listIdStr,
							   @PathParam("itemId") @ApiParam(value = "Shopping item identifier", required = true) String itemIdStr) {
		UUID listId = extractListId(listIdStr);
		ShoppingItem foundItem = findShoppingItemById(listId, itemIdStr);
		itemRepo.deleteById(listId, foundItem.getId());
		return Response.ok().build();
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