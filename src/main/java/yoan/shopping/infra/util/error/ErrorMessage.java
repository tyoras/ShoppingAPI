/**
 * 
 */
package yoan.shopping.infra.util.error;

/**
 * Common interface for all the error codes
 * @author yoan
 */
public interface ErrorMessage {
	/**
	 * Get the error message as a String
	 * @return error message
	 */
	public String getHumanReadableMessage();
	
	/**
	 * Get the message formated with the provided params
	 * @param params
	 * @return formated error message
	 */
	public String getHumanReadableMessage(Object... params);
}
