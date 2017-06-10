/**
 * 
 */
package io.tyoras.shopping.root.resource;

import static io.tyoras.shopping.root.RootKey.*;
import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.tyoras.shopping.infra.rest.Link;
import io.tyoras.shopping.infra.rest.RestAPI;
import io.tyoras.shopping.root.BuildInfo;
import io.tyoras.shopping.root.repository.BuildInfoRepository;
import io.tyoras.shopping.root.representation.RootRepresentation;
import io.tyoras.shopping.user.User;


/**
 * Root Resource
 * @author yoan
 */
@Path("/api")
@Produces(APPLICATION_JSON)
public class RootResource extends RestAPI {
	
	/** Repository to get the build informations */
	private final BuildInfoRepository buildInfoRepository;
	
	@Inject
	public RootResource(BuildInfoRepository buildInfoRepo) {
		super();
		this.buildInfoRepository = requireNonNull(buildInfoRepo);
	}
	
	
	@OPTIONS
	@Timed
	@Override
	@ApiOperation(value = "Get API root", notes = "This can only be done by the logged in user.", response = RootRepresentation.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Root", response = RootRepresentation.class) })
	public Response root(@ApiParam(hidden = true) @Auth User connectedUser) {
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
