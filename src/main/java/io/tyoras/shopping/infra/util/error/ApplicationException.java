/**
 *
 */
package io.tyoras.shopping.infra.util.error;

import io.tyoras.shopping.infra.rest.error.Level;

import static java.util.Objects.requireNonNull;

/**
 * Exception to throw when known by the application
 *
 * @author yoan
 */
public class ApplicationException extends RuntimeException {
    private static final long serialVersionUID = -5128489610524038153L;

    /**
     * Error identifier
     */
    private final ErrorCode errorCode;
    /**
     * Error criticity level
     */
    private final Level level;

    public ApplicationException(Level level, ErrorCode errorCode, String message) {
        super(message);
        this.level = requireNonNull(level);
        this.errorCode = requireNonNull(errorCode);
    }

    public ApplicationException(Level level, ErrorCode errorCode, String message, Throwable t) {
        super(message, t);
        this.level = requireNonNull(level);
        this.errorCode = requireNonNull(errorCode);
    }

    public ApplicationException(Level level, ErrorCode errorCode, ErrorMessage message) {
        super(message.getDevReadableMessage());
        this.level = requireNonNull(level);
        this.errorCode = requireNonNull(errorCode);
    }

    public ApplicationException(Level level, ErrorCode errorCode, ErrorMessage message, Throwable t) {
        super(message.getDevReadableMessage(), t);
        this.level = requireNonNull(level);
        this.errorCode = requireNonNull(errorCode);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Level getLevel() {
        return level;
    }
}
