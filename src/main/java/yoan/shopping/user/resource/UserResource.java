/**
 * 
 */
package yoan.shopping.user.resource;

import java.net.URI;
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

import yoan.shopping.user.User;
import yoan.shopping.user.repository.UserRepository;
import yoan.shopping.user.representation.UserRepresentation;

import com.google.inject.Inject;
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
public class UserResource {
	@Context
	private UriInfo uriInfo;
	private final UserRepository userRepo;
	
	@Inject
	public UserResource(UserRepository userRepo) {
		this.userRepo = Objects.requireNonNull(userRepo);
	}
	
	@POST
	@ApiOperation(value = "Create user", notes = "This will can only be done by the logged in user.", position = 1)
	@ApiResponses(value = {
		@ApiResponse(code = 201, message = "User created") })
	public Response createUser(@ApiParam(value = "User to create", required = true) UserRepresentation userToCreate) {
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
	@ApiOperation(value = "Get user by Id", notes = "This will can only be done by the logged in user.", position = 2)
	public Response createUser(@PathParam("userId") @ApiParam(value = "User identifier", required = true) String userIdStr) {
		UUID userId = UUID.fromString(userIdStr);
		User foundUser = userRepo.getById(userId);
		
		if (foundUser == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		UserRepresentation createdUserRepresentation = UserRepresentation.fromUser(foundUser);
		return Response.ok().entity(createdUserRepresentation).build();
	}
	
	//TODO ajouter des opérations de lectures, update et suppression
}
