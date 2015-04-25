/**
 * 
 */
package yoan.shopping.infra.util;

/**
 * Exception to throw when known by the application
 * @author yoan
 */
public class ApplicationException extends RuntimeException {
	private static final long serialVersionUID = -5128489610524038153L;

	public ApplicationException(String message) {
        super(message);
    }

     public ApplicationException(String message, Throwable t) {
        super(message,t);
    }

    public ApplicationException(Exception innerException) {
        super(innerException);
    }
}
