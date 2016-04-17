package yoan.shopping.user.resource;

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import yoan.shopping.infra.rest.Link;
import yoan.shopping.infra.rest.RestAPI;
import yoan.shopping.infra.rest.error.ErrorRepresentation;
import yoan.shopping.user.User;
import yoan.shopping.user.UserCreationHelper;
import yoan.shopping.user.repository.SecuredUserRepository;
import yoan.shopping.user.repository.UserRepository;
import yoan.shopping.user.representation.SecuredUserWriteRepresentation;
import yoan.shopping.user.representation.UserRepresentation;
import yoan.shopping.user.representation.UserWriteRepresentation;

/**
 * User registration API
 * @author yoan
 */
@Path("/public/user")
@Api(value = "User registration")
@Produces({ "application/json", "application/xml" })
public class RegisterUserResource extends RestAPI {
	
	private final UserRepository userRepo;
	private final SecuredUserRepository securedUserRepo;
	
	@Inject
	public RegisterUserResource(UserRepository userRepo, SecuredUserRepository securedUserRepo) {
		super();
		this.userRepo = requireNonNull(userRepo);
		this.securedUserRepo = requireNonNull(securedUserRepo);
	}
	
	@Override
	public List<Link> getRootLinks() {
		List<Link> links = Lists.newArrayList(Link.self(getUriInfo()));
		
		URI registerURI = getUriInfo().getAbsolutePath();
		links.add(new Link("register", registerURI));
		
		return links;
	}
	
	@POST
	@ApiOperation(value = "Register new user", notes = "This can be done without authenticated user.", code = 201)
	@ApiResponses(value = {
		@ApiResponse(code = 201, message = "User created", response = UserRepresentation.class),
		@ApiResponse(code = 400, message = "Invalid User", response = ErrorRepresentation.class),
		@ApiResponse(code = 409, message = "User with email adress already exists", response = ErrorRepresentation.class)})
	public Response register(@ApiParam(value = "User to create", required = true) SecuredUserWriteRepresentation userToCreate) {
		String password = userToCreate.getPassword();
		UUID newUserId = UUID.randomUUID();
		User userCreated = UserWriteRepresentation.toUser(userToCreate, newUserId);
		
		UserCreationHelper.ensureUserNotExists(userRepo, userCreated.getId(), userCreated.getEmail());
		UserCreationHelper.createUser(securedUserRepo, userCreated, password);
		UserRepresentation createdUserRepresentation = new UserRepresentation(userCreated, getUriInfo());
		UriBuilder ub = getUriInfo().getBaseUriBuilder();
        URI location = ub.path(UserResource.class)
        				 .path(userCreated.getId().toString())
        				 .build();
		return Response.created(location).entity(createdUserRepresentation).build();
	}
}
