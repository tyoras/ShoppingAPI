/**
 * 
 */
package io.tyoras.shopping.infra.rest;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.codahale.metrics.annotation.Timed;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import io.tyoras.shopping.user.User;

/**
 * Restful API
 * @author yoan
 */
public abstract class RestAPI {
	@Inject
	private Provider<UriInfo> uriInfo;
	
	/**
	 * API operation to provide navigation links in the resource
	 * @return HTTP response
	 */
	@OPTIONS
	@Timed //@Metered @ExceptionMetered
	@ApiOperation(value = "Get API root", notes = "This can only be done by the logged in user.", response = RestRepresentation.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Root", response = RestRepresentation.class), 
							@ApiResponse(code = 401, message = "Not authenticated" , responseHeaders = { @ResponseHeader(name = "WWW-Authenticate", response = String.class) }) })
	public Response root(@ApiParam(hidden = true) @Auth User connectedUser) {
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
		return uriInfo.get();
	}
	
}
