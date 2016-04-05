/**
 * 
 */
package yoan.shopping.root.resource;

import static java.util.Objects.requireNonNull;
import static yoan.shopping.infra.config.guice.ShoppingWebModule.CONNECTED_USER;
import static yoan.shopping.infra.config.guice.SwaggerModule.SECURITY_DEFINITION_OAUTH2;
import static yoan.shopping.root.RootKey.CLIENT_APP;
import static yoan.shopping.root.RootKey.ITEM;
import static yoan.shopping.root.RootKey.LIST;
import static yoan.shopping.root.RootKey.USER;

import java.util.List;

import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import yoan.shopping.infra.rest.Link;
import yoan.shopping.infra.rest.RestAPI;
import yoan.shopping.root.BuildInfo;
import yoan.shopping.root.repository.BuildInfoRepository;
import yoan.shopping.root.representation.RootRepresentation;
import yoan.shopping.user.User;


/**
 * Root Resource
 * @author yoan
 */
@Path("/api")
@Api(value = "Root", authorizations = { @Authorization(value = SECURITY_DEFINITION_OAUTH2, scopes = {})})
@Produces({ "application/json", "application/xml" })
public class RootResource extends RestAPI {
	/** Currently connected user */
	private final User connectedUser;
	/** Repository to get the build informations */
	private final BuildInfoRepository buildInfoRepository;
	
	@Inject
	public RootResource(@Named(CONNECTED_USER) User connectedUser, BuildInfoRepository buildInfoRepo) {
		super();
		this.buildInfoRepository = requireNonNull(buildInfoRepo);
		this.connectedUser = requireNonNull(connectedUser);
	}
	
	@OPTIONS
	@Override
	@ApiOperation(value = "Get API root", notes = "This can only be done by the logged in user.", response = RootRepresentation.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Root", response = RootRepresentation.class) })
	public Response root() {
		List<Link> links = getRootLinks();
		BuildInfo buildInfo = buildInfoRepository.getCurrentBuildInfos();
		
		RootRepresentation root = new RootRepresentation(buildInfo, connectedUser.getId(), links);
		
		return Response.ok(root).build();
	}
	
	@Override
	public List<Link> getRootLinks() {
		List<Link> links = Lists.newArrayList(Link.self(getUriInfo()));
		
		links.add(USER.getlink(getUriInfo()));
		links.add(LIST.getlink(getUriInfo()));
		links.add(ITEM.getlink(getUriInfo(), "{listId}"));
		links.add(CLIENT_APP.getlink(getUriInfo()));
		
		return links;
	}
}
