/**
 * 
 */
package yoan.shopping.infra.rest.error;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

/**
 * Map all uncaucght exceptions from the application to an HTTP response
 * @author yoan
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionMapper.class);

	@Override
	public Response toResponse(Throwable exception) {
		//TODO implémenter le global exception mapper
		LOGGER.error("Error", exception);
        return Response.status(INTERNAL_SERVER_ERROR).header("Content-Type", TEXT_PLAIN).build();
	}
	
	//TODO implémenter un mapper pour les ApplicationExceptions

}
