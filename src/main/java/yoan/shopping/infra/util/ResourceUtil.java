/**
 * 
 */
package yoan.shopping.infra.util;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorMessage.INVALID;

import java.util.UUID;

import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.infra.util.error.CommonErrorCode;

/**
 *
 * @author yoan
 */
public class ResourceUtil {
	private ResourceUtil() { }
	
	public static UUID getIdfromParam(String paramName, String param) {
		UUID id;
		try {
			id = UUID.fromString(param);
		} catch(IllegalArgumentException e) {
			String message = INVALID.getHumanReadableMessage("param named " + paramName + " : " + param);
			throw new WebApiException(BAD_REQUEST, ERROR, CommonErrorCode.API_RESPONSE, message, e);
		}
		return id;
	}
}
