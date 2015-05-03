/**
 * 
 */
package yoan.shopping.infra.util.error;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Enumeration of all non specific error messages
 * @author yoan
 */
public enum CommonErrorMessage implements ErrorMessage {
	/** Problem with %s URL */
	PROBLEM_WITH_URL("Problem with %s URL");

	private String message;
	
	private CommonErrorMessage(String message) {
		checkArgument(isNotBlank(message), "An error message should not be empty");
		this.message = message;
	}
	
	@Override
	public String getHumanReadableMessage() {
		return message;
	}

	@Override
	public String getHumanReadableMessage(Object... params) {
		return String.format(message, params);
	}

}
