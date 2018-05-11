/**
 *
 */
package io.tyoras.shopping.infra.rest.error;

import io.tyoras.shopping.infra.util.error.ApplicationException;
import io.tyoras.shopping.infra.util.error.ErrorCode;
import io.tyoras.shopping.infra.util.error.ErrorMessage;

import javax.ws.rs.core.Response.Status;

import static java.util.Objects.requireNonNull;

/**
 * Exception at the web API level
 * Handled as HTTP response
 *
 * @author yoan
 */
public class WebApiException extends ApplicationException {
    private static final long serialVersionUID = -7873617301665383085L;

    /**
     * Status wanted in the response
     */
    private final Status status;

    public WebApiException(Status status, Level level, ErrorCode errorCode, String message) {
        super(level, errorCode, message);
        this.status = requireNonNull(status);
    }

    public WebApiException(Status status, Level level, ErrorCode errorCode, String message, Throwable t) {
        super(level, errorCode, message, t);
        this.status = requireNonNull(status);
    }

    public WebApiException(Status status, Level level, ErrorCode errorCode, ErrorMessage message) {
        super(level, errorCode, message);
        this.status = requireNonNull(status);
    }

    public WebApiException(Status status, Level level, ErrorCode errorCode, ErrorMessage message, Throwable t) {
        super(level, errorCode, message, t);
        this.status = requireNonNull(status);
    }

    public Status getStatus() {
        return status;
    }

}
