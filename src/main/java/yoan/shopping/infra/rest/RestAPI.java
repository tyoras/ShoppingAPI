/**
 * 
 */
package yoan.shopping.infra.rest;

import java.util.List;

import javax.ws.rs.OPTIONS;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Restful API
 * @author yoan
 */
public abstract class RestAPI {
	@Context
	private UriInfo uriInfo;
	
	/**
	 * API operation to provide navigation links in the resource
	 * @return HTTP response
	 */
	@OPTIONS
	@ApiOperation(value = "Get API root", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in user.", response = RestRepresentation.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Root"), @ApiResponse(code = 401, message = "Not authenticated") })
	public Response root() {
		RestRepresentation rootRepresentation = new RestRepresentation(getRootLinks());
		return Response.ok().entity(rootRepresentation).build();
	}
	
	/**
	 * Provide one link per operation in the API
	 * @return
	 */
	public abstract List<Link> getRootLinks();
	
	/**
	 * Expose current HTTP request URI info
	 * @return
	 */
	//TODO : find a better way to mock UriInfo in tests
	public UriInfo getUriInfo() {
		return uriInfo;
	}
	
}
