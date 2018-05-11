package io.tyoras.shopping.user.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.*;
import io.tyoras.shopping.infra.rest.Link;
import io.tyoras.shopping.infra.rest.RestAPI;
import io.tyoras.shopping.infra.rest.error.ErrorRepresentation;
import io.tyoras.shopping.infra.rest.error.WebApiException;
import io.tyoras.shopping.infra.util.ResourceUtil;
import io.tyoras.shopping.infra.util.error.ApplicationException;
import io.tyoras.shopping.user.User;
import io.tyoras.shopping.user.UserCreationHelper;
import io.tyoras.shopping.user.repository.SecuredUserRepository;
import io.tyoras.shopping.user.repository.UserRepository;
import io.tyoras.shopping.user.representation.SecuredUserWriteRepresentation;
import io.tyoras.shopping.user.representation.UserRepresentation;
import io.tyoras.shopping.user.representation.UserWriteRepresentation;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.tyoras.shopping.infra.config.guice.SwaggerModule.SECURITY_DEFINITION_OAUTH2;
import static io.tyoras.shopping.infra.rest.error.Level.INFO;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static io.tyoras.shopping.user.repository.UserRepositoryErrorCode.TOO_MUCH_RESULT;
import static io.tyoras.shopping.user.resource.UserResourceErrorMessage.*;
import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * User API
 *
 * @author yoan
 */
@Path("/api/user")
@Api(value = "User", authorizations = {@Authorization(value = SECURITY_DEFINITION_OAUTH2, scopes = {})})
@Produces({"application/json", "application/xml"})
public class UserResource extends RestAPI {

    private final UserRepository userRepo;
    private final SecuredUserRepository securedUserRepo;

    @Inject
    public UserResource(UserRepository userRepo, SecuredUserRepository securedUserRepo) {
        super();
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
        URI searchByNameURI = getUriInfo().getAbsolutePathBuilder().path(UserResource.class, "searchByName").build("{search}");
        links.add(new Link("searchByName", searchByNameURI));
        URI updateURI = getUriInfo().getAbsolutePathBuilder().path(UserResource.class, "update").build("{userId}");
        links.add(new Link("update", updateURI));
        URI changePasswordURI = getUriInfo().getAbsolutePathBuilder().path(UserResource.class, "changePassword").build("{userId}", "{newPassword}");
        links.add(new Link("changePassword", changePasswordURI));
        URI deleteByIdURI = getUriInfo().getAbsolutePathBuilder().path(UserResource.class, "deleteById").build("{userId}");
        links.add(new Link("deleteById", deleteByIdURI));

        return links;
    }

    @POST
    @Timed
    @ApiOperation(value = "Create user", notes = "This can only be done by the logged in user.", code = 201, response = UserRepresentation.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User created", response = UserRepresentation.class),
            @ApiResponse(code = 400, message = "Invalid User", response = ErrorRepresentation.class),
            @ApiResponse(code = 409, message = "User with email adress already exists", response = ErrorRepresentation.class)})
    public Response create(@ApiParam(hidden = true) @Auth User connectedUser, @ApiParam(value = "User to create", required = true) SecuredUserWriteRepresentation userToCreate) {
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
    @Timed
    @ApiOperation(value = "Get user by Id", notes = "This can only be done by the logged in user.", response = UserRepresentation.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Found user", response = UserRepresentation.class),
            @ApiResponse(code = 400, message = "Invalid user Id", response = ErrorRepresentation.class),
            @ApiResponse(code = 404, message = "User not found", response = ErrorRepresentation.class)})
    public Response getById(@ApiParam(hidden = true) @Auth User connectedUser, @PathParam("userId") @ApiParam(value = "User identifier", required = true, example = "a7b58ac5-ecc0-43b0-b07e-8396b2065439") String userIdStr) {
        User foundUser = findUserById(userIdStr);
        UserRepresentation foundUserRepresentation = new UserRepresentation(foundUser, getUriInfo());
        return Response.ok().entity(foundUserRepresentation).build();
    }

    @GET
    @Path("/email/{userEmail}")
    @Timed
    @ApiOperation(value = "Get user by Email adress", notes = "This can only be done by the logged in user.", response = UserRepresentation.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Found user"),
            @ApiResponse(code = 400, message = "Invalid user email"),
            @ApiResponse(code = 404, message = "User not found")})
    public Response getByEmail(@ApiParam(hidden = true) @Auth User connectedUser, @PathParam("userEmail") @ApiParam(value = "User email adress", required = true) String userEmail) {
        User foundUser = findUserByEmail(userEmail);
        UserRepresentation foundUserRepresentation = new UserRepresentation(foundUser, getUriInfo());
        return Response.ok().entity(foundUserRepresentation).build();
    }

    @GET
    @Path("/name/{search}")
    @Timed
    @ApiOperation(value = "Search users by name", notes = "This can only be done by the logged in user.", response = UserRepresentation.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Found users"),
            @ApiResponse(code = 400, message = "Invalid search or too much users found"),
            @ApiResponse(code = 404, message = "Users not found")})
    public Response searchByName(@ApiParam(hidden = true) @Auth User connectedUser, @PathParam("search") @ApiParam(value = "User name search", required = true) String search) {
        ImmutableList<User> foundusers = searchUsersByName(search);
        List<UserRepresentation> usersRepresentation = new ArrayList<>();
        foundusers.forEach(user -> usersRepresentation.add(new UserRepresentation(user, getUriInfo())));
        return Response.ok().entity(usersRepresentation).build();
    }

    @PUT
    @Path("/{userId}")
    @Timed
    @ApiOperation(value = "Update", notes = "This can only be done by the logged in user.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "User updated"),
            @ApiResponse(code = 400, message = "Invalid user Id"),
            @ApiResponse(code = 404, message = "User not found")})
    public Response update(@ApiParam(hidden = true) @Auth User connectedUser,
                           @PathParam("userId") @ApiParam(value = "User identifier", required = true, example = "a7b58ac5-ecc0-43b0-b07e-8396b2065439") String userIdStr,
                           @ApiParam(value = "User to update", required = true) UserWriteRepresentation userToUpdate) {
        UUID userId = ResourceUtil.getIdfromParam("userId", userIdStr);
        User updatedUser = UserWriteRepresentation.toUser(userToUpdate, userId);
        userRepo.update(updatedUser);

        UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(updatedUser.getId().toString()).build();
        return Response.noContent().location(location).build();
    }

    @PUT
    @Path("/{userId}/password")
    @ApiOperation(value = "Change password", notes = "This can only be done by the logged in user.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Password changed"),
            @ApiResponse(code = 400, message = "Invalid user Id"),
            @ApiResponse(code = 404, message = "User not found")})
    public Response changePassword(@ApiParam(hidden = true) @Auth User connectedUser,
                                   @PathParam("userId") @ApiParam(value = "Id of the user to update", required = true) String userIdStr,
                                   @ApiParam(value = "new password", required = true) String newPassword) {
        UUID userId = ResourceUtil.getIdfromParam("userId", userIdStr);

        securedUserRepo.changePassword(userId, newPassword);

        UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(userId.toString()).build();
        return Response.noContent().location(location).build();
    }

    @DELETE
    @Path("/{userId}")
    @Timed
    @ApiOperation(value = "Delete user by Id", notes = "This can only be done by the logged in user.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User deleted"),
            @ApiResponse(code = 400, message = "Invalid user Id"),
            @ApiResponse(code = 404, message = "User not found")})
    public Response deleteById(@ApiParam(hidden = true) @Auth User connectedUser, @PathParam("userId") @ApiParam(value = "User identifier", required = true) String userIdStr) {
        User foundUser = findUserById(userIdStr);
        userRepo.deleteById(foundUser.getId());
        return Response.noContent().build();
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

    private ImmutableList<User> searchUsersByName(String search) {
        ensureSearchByName(search);

        ImmutableList<User> foundUsers = null;
        try {
            foundUsers = userRepo.searchByName(search);
        } catch (ApplicationException ae) {
            handleTooMuchResult(ae);
        }

        ensureFoundusers(search, foundUsers);

        return foundUsers;
    }

    private void ensureFoundusers(String search, ImmutableList<User> foundUsers) {
        if (foundUsers.isEmpty()) {
            throw new WebApiException(NOT_FOUND, INFO, API_RESPONSE, USERS_NOT_FOUND.getDevReadableMessage(search));
        }
    }

    private void handleTooMuchResult(ApplicationException ae) {
        if (ae.getErrorCode() == TOO_MUCH_RESULT) {
            throw new WebApiException(BAD_REQUEST, INFO, ae.getErrorCode(), ae.getMessage());
        }
        throw ae;
    }

    private void ensureSearchByName(String search) {
        if (StringUtils.isBlank(search) || search.length() < UserRepository.NAME_SEARCH_MIN_LENGTH) {
            throw new WebApiException(BAD_REQUEST, INFO, API_RESPONSE, INVALID_SEARCH.getDevReadableMessage(search));
        }
    }
}
