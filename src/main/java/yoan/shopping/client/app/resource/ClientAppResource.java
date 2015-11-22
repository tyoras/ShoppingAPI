package yoan.shopping.client.app.resource;

import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static yoan.shopping.infra.config.guice.ShoppingWebModule.CONNECTED_USER;
import static yoan.shopping.infra.rest.error.Level.INFO;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.user.resource.UserResourceErrorMessage.USER_NOT_FOUND;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import yoan.shopping.client.app.ClientApp;
import yoan.shopping.client.app.repository.ClientAppRepository;
import yoan.shopping.client.app.representation.ClientAppRepresentation;
import yoan.shopping.infra.rest.Link;
import yoan.shopping.infra.rest.RestAPI;
import yoan.shopping.infra.rest.RestRepresentation;
import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.infra.util.ResourceUtil;
import yoan.shopping.user.User;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Client application API
 * @author yoan
 */
@Path("/api/client/app")
@Api(value = "/client/app")
@Produces({ "application/json", "application/xml" })
public class ClientAppResource extends RestAPI {
	/** Currently connected user */
	private final User connectedUser;
	private final ClientAppRepository clientAppRepo;
	
	@Inject
	public ClientAppResource(@Named(CONNECTED_USER) User connectedUser, ClientAppRepository clientAppRepo) {
		super();
		this.connectedUser = requireNonNull(connectedUser);
		this.clientAppRepo = Objects.requireNonNull(clientAppRepo);
	}
	
	@GET
	@ApiOperation(value = "Get client application API root", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in user.", response = RestRepresentation.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Root"), @ApiResponse(code = 401, message = "Not authenticated") })
	@Override
	public Response root() {
		RestRepresentation rootRepresentation = new RestRepresentation(getRootLinks());
		return Response.ok().entity(rootRepresentation).build();
	}
	
	@Override
	public List<Link> getRootLinks() {
		List<Link> links = Lists.newArrayList(Link.self(getUriInfo()));
		
//		URI createURI = getUriInfo().getAbsolutePath();
//		links.add(new Link("create", createURI));
		URI getByIdURI = getUriInfo().getAbsolutePathBuilder().path(ClientAppResource.class, "getById").build("{appId}");
		links.add(new Link("getById", getByIdURI));
//		URI updateURI = getUriInfo().getAbsolutePath();
//		links.add(new Link("update", updateURI));
//		URI changePasswordURI = getUriInfo().getAbsolutePathBuilder().path(UserResource.class, "changePassword").build(connectedUser.getId().toString(), "{newPassword}");
//		links.add(new Link("changePassword", changePasswordURI));
//		URI deleteByIdURI = getUriInfo().getAbsolutePathBuilder().path(UserResource.class, "deleteById").build("{appId}");
//		links.add(new Link("deleteById", deleteByIdURI));
		
		return links;
	}
	
	
	@GET
	@Path("/{appId}")
	@ApiOperation(value = "Get client app by Id", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in user.", response = ClientAppRepresentation.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Found user"),
		@ApiResponse(code = 400, message = "Invalid user Id"),
		@ApiResponse(code = 404, message = "User not found") })
	public Response getById(@PathParam("appId") @ApiParam(value = "Client application identifier", required = true) String appIdStr) {
		ClientApp foundApp = findClientAppById(appIdStr);
		ClientAppRepresentation foundAppRepresentation = new ClientAppRepresentation(foundApp, getUriInfo());
		return Response.ok().entity(foundAppRepresentation).build();
	}

	private ClientApp findClientAppById(String appIdStr) {
		UUID appId = ResourceUtil.getIdfromParam("appId", appIdStr);
		ClientApp foundApp = clientAppRepo.getById(appId);
		
		ensureFoundApp(foundApp);
		
		return foundApp;
	}
	
	private void ensureFoundApp(ClientApp foundApp) {
		if (foundApp == null) {
			throw new WebApiException(NOT_FOUND, INFO, API_RESPONSE, CLIENT_APP_NOT_FOUND);
		}
	}
	
}
