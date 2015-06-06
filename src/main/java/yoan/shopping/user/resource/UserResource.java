/**
 * 
 */
package yoan.shopping.user.resource;

import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static yoan.shopping.infra.config.guice.ShoppingWebModule.CONNECTED_USER;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.rest.error.Level.INFO;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.user.resource.UserResourceErrorMessage.ALREADY_EXISTING_USER;
import static yoan.shopping.user.resource.UserResourceErrorMessage.MISSING_USER_ID_FOR_UPDATE;
import static yoan.shopping.user.resource.UserResourceErrorMessage.USER_NOT_FOUND;

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

import yoan.shopping.infra.rest.Link;
import yoan.shopping.infra.rest.RestAPI;
import yoan.shopping.infra.rest.RestRepresentation;
import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.infra.util.ResourceUtil;
import yoan.shopping.user.User;
import yoan.shopping.user.repository.UserRepository;
import yoan.shopping.user.representation.UserRepresentation;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * User Resource
 * @author yoan
 */
@Path("/api/user")
@Api(value = "/user", description = "Operations on Users")
@Produces({ "application/json", "application/xml" })
public class UserResource extends RestAPI {
	/** Currently connected user */
	private final User connectedUser;
	private final UserRepository userRepo;
	
	@Inject
	public UserResource(@Named(CONNECTED_USER) User connectedUser, UserRepository userRepo) {
		super();
		this.connectedUser = requireNonNull(connectedUser);
		this.userRepo = Objects.requireNonNull(userRepo);
	}
	
	@GET
	@ApiOperation(value = "Get user API root", notes = "This will can only be done by the logged in user.", response = UserRepresentation.class, position = 1)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Root") })
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
		URI getByIdURI = getUriInfo().getAbsolutePathBuilder().path(UserResource.class, "getById").build(connectedUser.getId().toString());
		links.add(new Link("getById", getByIdURI));
		
		return links;
	}
	
	@POST
	@ApiOperation(value = "Create user", notes = "This will can only be done by the logged in user.", position = 2)
	@ApiResponses(value = {
		@ApiResponse(code = 201, message = "User created"),
		@ApiResponse(code = 400, message = "Invalid User"),
		@ApiResponse(code = 409, message = "Already existing user")})
	public Response create(@ApiParam(value = "User to create", required = true) UserRepresentation userToCreate) {
		//its id field is already generated
		User userCreated = UserRepresentation.toUser(userToCreate);
		//if the Id was not provided we generate one
		if (userCreated.getId().equals(User.DEFAULT_ID)) {
			userCreated = User.Builder.createFrom(userCreated).withRandomId().build();
		}
		
		ensureUserNotExists(userCreated.getId());
		userRepo.create(userCreated);
		UserRepresentation createdUserRepresentation = new UserRepresentation(userCreated, getUriInfo());
		UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(userCreated.getId().toString()).build();
		return Response.created(location).entity(createdUserRepresentation).build();
	}
	
	@GET
	@Path("/{userId}")
	@ApiOperation(value = "Get user by Id", notes = "This will can only be done by the logged in user.", response = UserRepresentation.class, position = 3)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Found user"),
		@ApiResponse(code = 400, message = "Invalid user Id"),
		@ApiResponse(code = 404, message = "User not found") })
	public Response getById(@PathParam("userId") @ApiParam(value = "User identifier", required = true) String userIdStr) {
		User foundUser = findUser(userIdStr);
		UserRepresentation foundUserRepresentation = new UserRepresentation(foundUser, getUriInfo());
		return Response.ok().entity(foundUserRepresentation).build();
	}
	
	@PUT
	@ApiOperation(value = "Update", notes = "This will can only be done by the logged in user.", response = UserRepresentation.class, position = 4)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Found user"),
		@ApiResponse(code = 400, message = "Invalid user Id"),
		@ApiResponse(code = 404, message = "User not found") })
	public Response update(@ApiParam(value = "User to update", required = true) UserRepresentation userToUpdate) {
		User updatedUser = UserRepresentation.toUser(userToUpdate);
		//if the Id was not provided we generate one
		if (updatedUser.getId().equals(User.DEFAULT_ID)) {
			throw new WebApiException(BAD_REQUEST, ERROR, API_RESPONSE, MISSING_USER_ID_FOR_UPDATE);
		}
		//ensuring user exist before updating it
		findUser(userToUpdate.getId().toString());
		userRepo.upsert(updatedUser);

		UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(updatedUser.getId().toString()).build();
		return Response.noContent().location(location).build();
	}
	
	@DELETE
	@Path("/{userId}")
	@ApiOperation(value = "Delete user by Id", notes = "This will can only be done by the logged in user.", position = 5)
	@ApiResponses(value = {
		@ApiResponse(code = 400, message = "Invalid user Id"),
		@ApiResponse(code = 404, message = "User not found") })
	public Response deleteById(@PathParam("userId") @ApiParam(value = "User identifier", required = true) String userIdStr) {
		User foundUser = findUser(userIdStr);
		userRepo.deleteById(foundUser.getId());
		return Response.ok().build();
	}
	
	private User findUser(String userIdStr) {
		UUID userId = ResourceUtil.getIdfromParam("userId", userIdStr);
		User foundUser = userRepo.getById(userId);
		
		if (foundUser == null) {
			throw new WebApiException(NOT_FOUND, INFO, API_RESPONSE, USER_NOT_FOUND);
		}
		
		return foundUser;
	}
	
	private void ensureUserNotExists(UUID userId) {
		User foundUser = userRepo.getById(userId);
		
		if (foundUser != null) {
			throw new WebApiException(CONFLICT, ERROR, API_RESPONSE, ALREADY_EXISTING_USER.getHumanReadableMessage(userId));
		}
	}
}
