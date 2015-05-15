/**
 * 
 */
package yoan.shopping.user.resource;

import static java.util.Objects.requireNonNull;
import static yoan.shopping.infra.config.guice.ShoppingWebModule.CONNECTED_USER;
import static yoan.shopping.infra.rest.error.Level.INFO;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.user.resource.UserResourceErrorMessage.USER_NOT_FOUND;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import yoan.shopping.infra.rest.Link;
import yoan.shopping.infra.rest.RestAPI;
import yoan.shopping.infra.rest.RestRepresentation;
import yoan.shopping.infra.rest.error.WebApiException;
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
@Path("/user")
@Api(value = "/user", description = "Operations on Users")
@Produces({ "application/json", "application/xml" })
public class UserResource implements RestAPI {
	@Context
	private UriInfo uriInfo;
	/** Currently connected user */
	private final User connectedUser;
	private final UserRepository userRepo;
	
	@Inject
	public UserResource(@Named(CONNECTED_USER) User connectedUser, UserRepository userRepo) {
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
		List<Link> links = Lists.newArrayList(Link.self(uriInfo));
		
		URI createURI = uriInfo.getAbsolutePath();
		links.add(new Link("create", createURI));
		URI getByIdURI = uriInfo.getAbsolutePathBuilder().path(UserResource.class, "getById").build(connectedUser.getId().toString());
		links.add(new Link("getById", getByIdURI));
		
		return links;
	}
	
	@POST
	@ApiOperation(value = "Create user", notes = "This will can only be done by the logged in user.", position = 1)
	@ApiResponses(value = {
		@ApiResponse(code = 201, message = "User created") })
	public Response create(@ApiParam(value = "User to create", required = true) UserRepresentation userToCreate) {
		//its id field is already generated
		User userCreated = UserRepresentation.toUser(userToCreate);
		userRepo.create(userCreated);
		UserRepresentation createdUserRepresentation = UserRepresentation.fromUser(userCreated);
		UriBuilder ub = uriInfo.getAbsolutePathBuilder();
        URI location = ub.path(userCreated.getId().toString()).build();
		return Response.created(location).entity(createdUserRepresentation).build();
	}
	
	@GET
	@Path("/{userId}")
	@ApiOperation(value = "Get user by Id", notes = "This will can only be done by the logged in user.", response = UserRepresentation.class, position = 2)
	@ApiResponses(value = {
		@ApiResponse(code = 404, message = "User not found") })
	public Response getById(@PathParam("userId") @ApiParam(value = "User identifier", required = true) String userIdStr) {
		UUID userId = UUID.fromString(userIdStr);
		User foundUser = userRepo.getById(userId);
		
		if (foundUser == null) {
			throw new WebApiException(Status.NOT_FOUND, INFO, API_RESPONSE, USER_NOT_FOUND);
		}
		UserRepresentation createdUserRepresentation = UserRepresentation.fromUser(foundUser);
		return Response.ok().entity(createdUserRepresentation).build();
	}
	
	//TODO implémenter update
	
	//TODO implementer delete
	
	//TODO implémenter root
}
