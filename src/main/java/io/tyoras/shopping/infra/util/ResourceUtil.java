/**
 *
 */
package io.tyoras.shopping.infra.util;

import io.tyoras.shopping.infra.rest.error.WebApiException;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;
import java.util.regex.Pattern;

import static io.tyoras.shopping.infra.rest.error.Level.INFO;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static io.tyoras.shopping.infra.util.error.CommonErrorMessage.INVALID;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

/**
 * @author yoan
 */
public class ResourceUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");

    private ResourceUtil() {
    }

    public static UUID getIdfromParam(String paramName, String param) {
        UUID id;
        try {
            id = UUID.fromString(param);
        } catch (IllegalArgumentException | NullPointerException e) {
            String message = INVALID.getDevReadableMessage(getParamNameMessage(paramName) + " : " + param);
            throw new WebApiException(BAD_REQUEST, INFO, API_RESPONSE, message, e);
        }
        return id;
    }

    public static String getEmailfromParam(String paramName, String param) {
        if (StringUtils.isBlank(param) || !EMAIL_PATTERN.matcher(param.toLowerCase()).matches()) {
            String message = INVALID.getDevReadableMessage(getParamNameMessage(paramName) + " is not a valid email adress : " + param);
            throw new WebApiException(BAD_REQUEST, INFO, API_RESPONSE, message);
        }
        return param.toLowerCase();
    }

    private static String getParamNameMessage(String paramName) {
        if (StringUtils.isBlank(paramName)) {
            return "Unknown param";
        } else {
            return "Param named " + paramName;
        }
    }
}
