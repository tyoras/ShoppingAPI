package yoan.shopping.client.app.repository;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import yoan.shopping.infra.util.error.ErrorMessage;

/**
 * Error codes specific to the client app repository
 * @author yoan
 */
public enum ClientAppRepositoryErrorMessage implements ErrorMessage {
	/** Secret does not comply to the minimum security level */
	PROBLEM_SECRET_VALIDITY("Secret does not comply to the minimum security level");
	
	private String message;
	
	private ClientAppRepositoryErrorMessage(String message) {
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
