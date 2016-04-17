package yoan.shopping.user.resource;

import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static yoan.shopping.infra.config.guice.ShoppingWebModule.CONNECTED_USER;
import static yoan.shopping.infra.config.guice.SwaggerModule.SECURITY_DEFINITION_OAUTH2;
import static yoan.shopping.infra.rest.error.Level.INFO;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.user.resource.UserResourceErrorMessage.USER_NOT_FOUND;

import java.net.URI;
import java.util.List;
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
import yoan.shopping.infra.rest.error.ErrorRepresentation;
import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.infra.util.ResourceUtil;
import yoan.shopping.user.User;
import yoan.shopping.user.UserCreationHelper;
import yoan.shopping.user.repository.SecuredUserRepository;
import yoan.shopping.user.repository.UserRepository;
import yoan.shopping.user.representation.SecuredUserWriteRepresentation;
import yoan.shopping.user.representation.UserRepresentation;
import yoan.shopping.user.representation.UserWriteRepresentation;

/**
 * User API
 * @author yoan
 */
@Path("/api/user")
@Api(value = "User", authorizations = { @Authorization(value = SECURITY_DEFINITION_OAUTH2, scopes = {})})
@Produces({ "application/json", "application/xml" })
public class UserResource extends RestAPI {
	/** Currently connected user */
	//private final User connectedUser;
	private final UserRepository userRepo;
	private final SecuredUserRepository securedUserRepo;
	
	@Inject
	public UserResource(@Named(CONNECTED_USER) User connectedUser, UserRepository userRepo, SecuredUserRepository securedUserRepo) {
		super();
		//this.connectedUser = requireNonNull(connectedUser);
		this.userRepo = requireNonNull(userRepo);
		this.securedUserRepo = requireNonNull(securedUserRepo);
	}
	
	@Override
	public List<Link> getRootLinks() {
		List<Link> links = Lists.newArrayList(Link.self(getUriInfo()));
		
		URI createURI = getUriInfo().getAbsolutePath();
		links.add(new Link("create", createURI));
		URI getByIdURI = getUriInfo().getAbsolutePathBuilder().path(UserResource.class, "getById").build("{userId}");
		links.add(new Link("getById", getByIdURI));
		URI getByEmailURI = getUriInfo().getAbsolutePathBuilder().path(UserResource.class, "getByEmail").build("{userEmail}");
		links.add(new Link("getByEmail", getByEmailURI));
		URI updateURI =  getUriInfo().getAbsolutePathBuilder().path(UserResource.class, "update").build("{userId}");
		links.add(new Link("update", updateURI));
		URI changePasswordURI = getUriInfo().getAbsolutePathBuilder().path(UserResource.class, "changePassword").build("{userId}", "{newPassword}");
		links.add(new Link("changePassword", changePasswordURI));
		URI deleteByIdURI = getUriInfo().getAbsolutePathBuilder().path(UserResource.class, "deleteById").build("{userId}");
		links.add(new Link("deleteById", deleteByIdURI));
		
		return links;
	}
	
	@POST
	@ApiOperation(value = "Create user", notes = "This can only be done by the logged in user.", code = 201, response = UserRepresentation.class)
	@ApiResponses(value = {
		@ApiResponse(code = 201, message = "User created", response = UserRepresentation.class),
		@ApiResponse(code = 400, message = "Invalid User", response = ErrorRepresentation.class),
		@ApiResponse(code = 409, message = "User with email adress already exists", response = ErrorRepresentation.class)})
	public Response create(@ApiParam(value = "User to create", required = true) SecuredUserWriteRepresentation userToCreate) {
		String password = userToCreate.getPassword();
		UUID newUserId = UUID.randomUUID();
		User userCreated = UserWriteRepresentation.toUser(userToCreate, newUserId);
		
		UserCreationHelper.ensureUserNotExists(userRepo, userCreated.getId(), userCreated.getEmail());
		UserCreationHelper.createUser(securedUserRepo, userCreated, password);
		UserRepresentation createdUserRepresentation = new UserRepresentation(userCreated, getUriInfo());
		UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(userCreated.getId().toString()).build();
		return Response.created(location).entity(createdUserRepresentation).build();
	}
	
	@GET
	@Path("/{userId}")
	@ApiOperation(value = "Get user by Id", notes = "This can only be done by the logged in user.", response = UserRepresentation.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Found user", response = UserRepresentation.class),
		@ApiResponse(code = 400, message = "Invalid user Id", response = ErrorRepresentation.class),
		@ApiResponse(code = 404, message = "User not found", response = ErrorRepresentation.class) })
	public Response getById(@PathParam("userId") @ApiParam(value = "User identifier", required = true, example = "a7b58ac5-ecc0-43b0-b07e-8396b2065439") String userIdStr) {
		User foundUser = findUserById(userIdStr);
		UserRepresentation foundUserRepresentation = new UserRepresentation(foundUser, getUriInfo());
		return Response.ok().entity(foundUserRepresentation).build();
	}
	
	@GET
	@Path("/email/{userEmail}")
	@ApiOperation(value = "Get user by Email adress", notes = "This can only be done by the logged in user.", response = UserRepresentation.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Found user"),
		@ApiResponse(code = 400, message = "Invalid user email"),
		@ApiResponse(code = 404, message = "User not found") })
	public Response getByEmail(@PathParam("userEmail") @ApiParam(value = "User email adress", required = true) String userEmail) {
		User foundUser = findUserByEmail(userEmail);
		UserRepresentation foundUserRepresentation = new UserRepresentation(foundUser, getUriInfo());
		return Response.ok().entity(foundUserRepresentation).build();
	}
	
	@PUT
	@Path("/{userId}")
	@ApiOperation(value = "Update", notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 204, message = "User updated"),
		@ApiResponse(code = 400, message = "Invalid user Id"),
		@ApiResponse(code = 404, message = "User not found") })
	public Response update(@PathParam("userId") @ApiParam(value = "User identifier", required = true, example = "a7b58ac5-ecc0-43b0-b07e-8396b2065439") String userIdStr, 
						   @ApiParam(value = "User to update", required = true) UserWriteRepresentation userToUpdate) {
		UUID userId = ResourceUtil.getIdfromParam("userId", userIdStr);
		User updatedUser = UserWriteRepresentation.toUser(userToUpdate, userId);
		userRepo.update(updatedUser);

		UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(updatedUser.getId().toString()).build();
		return Response.noContent().location(location).build();
	}
	
	@PUT
	@Path("/{userId}/password/{newPassword}")
	@ApiOperation(value = "Change password", notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 204, message = "Password changed"),
		@ApiResponse(code = 400, message = "Invalid user Id"),
		@ApiResponse(code = 404, message = "User not found") })
	public Response changePassword(@PathParam("userId") @ApiParam(value = "Id of the user to update", required = true) String userIdStr, 
								   @PathParam("newPassword") @ApiParam(value = "new password", required = true) String newPassword) {
		UUID userId = ResourceUtil.getIdfromParam("userId", userIdStr);
		
		securedUserRepo.changePassword(userId, newPassword);

		UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(userId.toString()).build();
		return Response.noContent().location(location).build();
	}
	
	@DELETE
	@Path("/{userId}")
	@ApiOperation(value = "Delete user by Id", notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "User deleted"),
		@ApiResponse(code = 400, message = "Invalid user Id"),
		@ApiResponse(code = 404, message = "User not found") })
	public Response deleteById(@PathParam("userId") @ApiParam(value = "User identifier", required = true) String userIdStr) {
		User foundUser = findUserById(userIdStr);
		userRepo.deleteById(foundUser.getId());
		return Response.ok().build();
	}
	
	private User findUserById(String userIdStr) {
		UUID userId = ResourceUtil.getIdfromParam("userId", userIdStr);
		User foundUser = userRepo.getById(userId);
		
		ensureFoundUser(foundUser);
		
		return foundUser;
	}
	
	private User findUserByEmail(String userEmailStr) {
		String userEmail = ResourceUtil.getEmailfromParam("userEmail", userEmailStr);
		User foundUser = userRepo.getByEmail(userEmail);
		ensureFoundUser(foundUser);
		return foundUser;
	}
	
	private void ensureFoundUser(User foundUser) {
		if (foundUser == null) {
			throw new WebApiException(NOT_FOUND, INFO, API_RESPONSE, USER_NOT_FOUND);
		}
	}
}
