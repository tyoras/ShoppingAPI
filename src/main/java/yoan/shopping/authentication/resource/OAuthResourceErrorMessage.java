package yoan.shopping.authentication.resource;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import yoan.shopping.infra.util.error.ErrorMessage;

/**
 * Enumeration of all Oauth resources related error messages 
 * @author yoan
 */
public enum OAuthResourceErrorMessage implements ErrorMessage {
	/** Invalid OAuth callback URL provided by client : %s */
	INVALID_REDIRECT_URI("Invalid OAuth callback URL provided by client : %s"),
	/** OAuth callback url needs to be provided by client! */
	MISSING_REDIRECT_URI("OAuth callback URL needs to be provided by client!");
	
	private String message;
	
	private OAuthResourceErrorMessage(String message) {
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
