package yoan.shopping.authentication.repository;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import yoan.shopping.infra.util.error.ErrorMessage;

/**
 * Error messages specific to the OAuth2 access token repository
 * @author yoan
 */
public enum OAuth2AccessTokenRepositoryErrorMessage implements ErrorMessage {
	/** Invalid access token : %s while %s */
	PROBLEM_INVALID_ACCESS_TOKEN("Invalid access token : %s while %s"),
	/** Unable to insert access token : %s because user ID is null */
	PROBLEM_INSERT_USER_ID_NULL("Unable to insert access token : %s because user ID is null");
	
	private String message;
	
	private OAuth2AccessTokenRepositoryErrorMessage(String message) {
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