package yoan.shopping.authentication.repository;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import yoan.shopping.infra.util.error.ErrorMessage;

/**
 * Error codes specific to the OAuth2 authorization code repository
 * @author yoan
 */
public enum OAuth2AuthorizationCodeRepositoryErrorMessage implements ErrorMessage {
	/** Invalid authorization code : %s while %s */
	PROBLEM_INVALID_AUTH_CODE("Invalid authorization code : %s while %s"),
	/** Unable to insert auth code : %s because user ID is null */
	PROBLEM_INSERT_USER_ID_NULL("Unable to insert auth code : %s because user ID is null");
	
	private String message;
	
	private OAuth2AuthorizationCodeRepositoryErrorMessage(String message) {
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
