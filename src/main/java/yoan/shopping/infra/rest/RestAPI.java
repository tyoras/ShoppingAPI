/**
 * 
 */
package yoan.shopping.infra.rest;

import java.util.List;

import javax.ws.rs.core.Response;

/**
 * Restful API
 * @author yoan
 */
public interface RestAPI {
	/**
	 * API operation to provide navigation links in the resource
	 * @return HTTP response
	 */
	public Response root();
	
	/**
	 * Provide one link per operation in the API
	 * @return
	 */
	public List<Link> getRootLinks();
}
