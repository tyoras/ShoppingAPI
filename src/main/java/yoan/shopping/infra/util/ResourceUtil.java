/**
 * 
 */
package yoan.shopping.infra.util;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static yoan.shopping.infra.rest.error.Level.INFO;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.infra.util.error.CommonErrorMessage.INVALID;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import yoan.shopping.infra.rest.error.WebApiException;

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
		} catch(IllegalArgumentException | NullPointerException e) {
			String message = INVALID.getHumanReadableMessage(getParamNameMessage(paramName) + " : " + param);
			throw new WebApiException(BAD_REQUEST, INFO, API_RESPONSE, message, e);
		}
		return id;
	}
	
	private static String getParamNameMessage(String paramName) {
		if (StringUtils.isBlank(paramName)) {
			return "Unknown param";
		} else {
			return "Param named " + paramName;
		}
	}
}
