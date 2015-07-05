/**
 * 
 */
package yoan.shopping.user.repository;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import yoan.shopping.infra.util.error.ErrorMessage;

/**
 *
 * @author yoan
 */
public enum UserRepositoryErrorMessage implements ErrorMessage {
	/** Error while creating user : %s */
	PROBLEM_CREATION_USER("Error while creating user : %s"),
	/** Error while updating user : %s */
	PROBLEM_UPDATE_USER("Error while updating user : %s"),
	/** Password does not comply to the minimum security level */
	PROBLEM_PASSWORD_VALIDITY("Password does not comply to the minimum security level")
	/** Unable to convert unsecure user : %s */,
	UNABLE_TO_CONVERT_UNSECURE_USER("Unable to convert unsecure user : %s");
	
	private String message;
	
	private UserRepositoryErrorMessage(String message) {
		checkArgument(isNotBlank(message), "An error message should not be empty");
		this.message = message;
	}
	
	@Override
	public String getDevReadableMessage() {
		return message;
	}

	@Override
	public String getDevReadableMessage(Object... params) {
		return String.format(message, params);
	}

}