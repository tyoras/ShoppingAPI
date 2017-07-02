package io.tyoras.shopping.user.resource;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import io.tyoras.shopping.infra.util.error.ErrorMessage;

/**
 * Error messages specific to User API
 * @author yoan
 */
public enum UserResourceErrorMessage  implements ErrorMessage {
	/** User not found */
	USER_NOT_FOUND("User not found"),
	/** User with email : %s aready exists */
	ALREADY_EXISTING_USER_WITH_EMAIL("User with email : %s already exists"),
	/** Users not found for search : \"%s\" */
	USERS_NOT_FOUND("Users not found for search : \"%s\""),
	/** Invalid search : \"%s\" */
	INVALID_SEARCH("Invalid search : \"%s\"");

	private String message;
	
	private UserResourceErrorMessage(String message) {
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