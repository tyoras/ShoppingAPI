/**
 * 
 */
package yoan.shopping.user.resource;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import yoan.shopping.infra.util.error.ErrorMessage;

/**
 *
 * @author yoan
 */
public enum UserResourceErrorMessage  implements ErrorMessage {
	/** USER_NOT_FOUND */
	USER_NOT_FOUND("User not found");

	private String message;
	
	private UserResourceErrorMessage(String message) {
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