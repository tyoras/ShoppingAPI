package io.tyoras.shopping.client.app.resource;

import static io.tyoras.shopping.client.app.repository.ClientAppRepositoryErrorCode.UNSECURE_SECRET;
import static io.tyoras.shopping.client.app.resource.ClientAppResourceErrorMessage.CLIENT_APPS_NOT_FOUND;
import static io.tyoras.shopping.client.app.resource.ClientAppResourceErrorMessage.CLIENT_APP_NOT_FOUND;
import static io.tyoras.shopping.infra.config.guice.SwaggerModule.SECURITY_DEFINITION_OAUTH2;
import static io.tyoras.shopping.infra.rest.error.Level.ERROR;
import static io.tyoras.shopping.infra.rest.error.Level.INFO;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.security.PermitAll;
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
import io.tyoras.shopping.client.app.ClientApp;
import io.tyoras.shopping.client.app.repository.ClientAppRepository;
import io.tyoras.shopping.client.app.representation.ClientAppRepresentation;
import io.tyoras.shopping.client.app.representation.ClientAppWriteRepresentation;
import io.tyoras.shopping.infra.rest.Link;
import io.tyoras.shopping.infra.rest.RestAPI;
import io.tyoras.shopping.infra.rest.error.WebApiException;
import io.tyoras.shopping.infra.util.ResourceUtil;
import io.tyoras.shopping.infra.util.error.ApplicationException;
import io.tyoras.shopping.user.User;

/**
 * Client application API
 * @author yoan
 */
@Path("/api/client/app")
@PermitAll
@Timed
@Api(value = "Client App", authorizations = { @Authorization(value = SECURITY_DEFINITION_OAUTH2, scopes = {})})
@Produces({ "application/json", "application/xml" })
public class ClientAppResource extends RestAPI {
	private final ClientAppRepository clientAppRepo;
	
	@Inject
	public ClientAppResource(ClientAppRepository clientAppRepo) {
		super();
		this.clientAppRepo = Objects.requireNonNull(clientAppRepo);
	}
	
	@Override
	public List<Link> getRootLinks() {
		List<Link> links = Lists.newArrayList(Link.self(getUriInfo()));
		
		URI createURI = getUriInfo().getAbsolutePath();
		links.add(new Link("create", createURI));
		URI getByIdURI = getUriInfo().getBaseUriBuilder().path(ClientAppResource.class, "getById").build("{appId}");
		links.add(new Link("getById", getByIdURI));
		URI getByOwnerIdURI = getUriInfo().getBaseUriBuilder().path(ClientAppResource.class, "getByOwnerId").build("{ownerId}");
		links.add(new Link("getByOwnerId", getByOwnerIdURI));
		URI updateURI = getUriInfo().getBaseUriBuilder().path(ClientAppResource.class, "update").build("{appId}");
		links.add(new Link("update", updateURI));
		URI changeSecretKeyURI = getUriInfo().getBaseUriBuilder().path(ClientAppResource.class, "changeSecretKey").build("{appId}");
		links.add(new Link("changeSecretKey", changeSecretKeyURI));
		URI deleteByIdURI = getUriInfo().getBaseUriBuilder().path(ClientAppResource.class, "deleteById").build("{appId}");
		links.add(new Link("deleteById", deleteByIdURI));
		
		return links;
	}
	
	@POST
	@ApiOperation(value = "Create client app", notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 201, message = "User created"),
		@ApiResponse(code = 400, message = "Invalid User")})
	public Response create(@ApiParam(hidden = true) @Auth User connectedUser, @ApiParam(value = "Client application to create", required = true) ClientAppWriteRepresentation clientAppToCreate) {
		UUID newAppId = UUID.randomUUID();
		ClientApp clientAppCreated = ClientAppWriteRepresentation.toClientApp(clientAppToCreate, newAppId);
		
		String secretkey = generateNewSecretKey();
		createClientApp(clientAppCreated, secretkey);
		ClientAppRepresentation createdAppRepresentation = new ClientAppRepresentation(clientAppCreated, getUriInfo());
		createdAppRepresentation.setSecretKey(secretkey);
		UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(clientAppCreated.getId().toString()).build();
		return Response.created(location).entity(createdAppRepresentation).build();
	}
	
	@GET
	@Path("/{appId}")
	@ApiOperation(value = "Get client app by Id", notes = "This can only be done by the logged in user.", response = ClientAppRepresentation.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Found client application"),
		@ApiResponse(code = 400, message = "Invalid client application Id"),
		@ApiResponse(code = 404, message = "Client application not found") })
	public Response getById(@ApiParam(hidden = true) @Auth User connectedUser, @PathParam("appId") @ApiParam(value = "Client application identifier", required = true) String appIdStr) {
		ClientApp foundApp = findClientAppById(appIdStr);
		ClientAppRepresentation foundAppRepresentation = new ClientAppRepresentation(foundApp, getUriInfo());
		return Response.ok().entity(foundAppRepresentation).build();
	}
	
	@GET
	@Path("/user/{ownerId}")
	@ApiOperation(value = "Get client apps by Id", notes = "This can only be done by the logged in user.", response = ClientAppRepresentation.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Found client applications"),
		@ApiResponse(code = 400, message = "Invalid owner Id"),
		@ApiResponse(code = 404, message = "Owner not found") })
	public Response getByOwnerId(@ApiParam(hidden = true) @Auth User connectedUser, @PathParam("ownerId") @ApiParam(value = "Owner identifier", required = true) String ownerIdStr) {
		ImmutableList<ClientApp> foundApps = findClientAppsByOwnerId(ownerIdStr);
		List<ClientAppRepresentation> appsRepresentation = new ArrayList<>();
		foundApps.forEach(app -> appsRepresentation.add(new ClientAppRepresentation(app, getUriInfo())));
		return Response.ok().entity(appsRepresentation).build();
	}
	
	@PUT
	@Path("/{appId}")
	@ApiOperation(value = "Update", notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 204, message = "Client application updated"),
		@ApiResponse(code = 400, message = "Invalid client application Id"),
		@ApiResponse(code = 404, message = "Client application not found") })
	public Response update(@ApiParam(hidden = true) @Auth User connectedUser, @PathParam("appId") @ApiParam(value = "Client application identifier", required = true) String appIdStr, 
						   @ApiParam(value = "Client application to update", required = true) ClientAppWriteRepresentation appToUpdate) {
		UUID appId = ResourceUtil.getIdfromParam("appId", appIdStr);
		ClientApp updatedClientApp = ClientAppWriteRepresentation.toClientApp(appToUpdate, appId);
		clientAppRepo.update(updatedClientApp);

		UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(updatedClientApp.getId().toString()).build();
		return Response.noContent().location(location).build();
	}

	private ClientApp findClientAppById(String appIdStr) {
		UUID appId = ResourceUtil.getIdfromParam("appId", appIdStr);
		ClientApp foundApp = clientAppRepo.getById(appId);
		
		ensureFoundApp(foundApp);
		
		return foundApp;
	}
	
	@POST
	@Path("/{appId}/secret")
	@ApiOperation(value = "Change secret key", notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Secret key changed"),
		@ApiResponse(code = 400, message = "Invalid client application Id"),
		@ApiResponse(code = 404, message = "Client application not found") })
	public Response changeSecretKey(@ApiParam(hidden = true) @Auth User connectedUser, @PathParam("appId") @ApiParam(value = "Id of the client app to update", required = true) String appIdStr) {
		UUID appId = ResourceUtil.getIdfromParam("appId", appIdStr);
		String newSecretKey = generateNewSecretKey();
		clientAppRepo.changeSecret(appId, newSecretKey);

		ClientApp foundApp = findClientAppById(appIdStr);
		ClientAppRepresentation foundAppRepresentation = new ClientAppRepresentation(foundApp, getUriInfo());
		foundAppRepresentation.setSecretKey(newSecretKey);
		
        return Response.ok().entity(foundAppRepresentation).build();
	}
	
	@DELETE
	@Path("/{appId}")
	@ApiOperation(value = "Delete client application by Id", notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Client application deleted"),
		@ApiResponse(code = 400, message = "Invalid client application Id"),
		@ApiResponse(code = 404, message = "Client application not found") })
	public Response deleteById(@ApiParam(hidden = true) @Auth User connectedUser, @PathParam("appId") @ApiParam(value = "Id of the client app to delete", required = true) String appIdStr) {
		ClientApp foundApp = findClientAppById(appIdStr);
		clientAppRepo.deleteById(foundApp.getId());
		return Response.ok().build();
	}
	
	private ImmutableList<ClientApp> findClientAppsByOwnerId(String ownerIdStr) {
		UUID ownerId = ResourceUtil.getIdfromParam("ownerId", ownerIdStr);
		ImmutableList<ClientApp> foundApps = clientAppRepo.getByOwner(ownerId);
		
		if (foundApps.isEmpty()) {
			throw new WebApiException(NOT_FOUND, INFO, API_RESPONSE, CLIENT_APPS_NOT_FOUND.getDevReadableMessage(ownerIdStr));
		}
		
		return foundApps;
	}
	
	private void ensureFoundApp(ClientApp foundApp) {
		if (foundApp == null) {
			throw new WebApiException(NOT_FOUND, INFO, API_RESPONSE, CLIENT_APP_NOT_FOUND);
		}
	}
	
	private void createClientApp(ClientApp clientAppCreated, String secret) {
		try {
			clientAppRepo.create(clientAppCreated, secret);
		} catch (ApplicationException ae) {
			if (ae.getErrorCode() == UNSECURE_SECRET) {
				throw new WebApiException(BAD_REQUEST, ERROR, API_RESPONSE, ae.getMessage());
			}
			throw ae;
		}
	}
	
	private String generateNewSecretKey() {
		return UUID.randomUUID().toString();
	}
}
