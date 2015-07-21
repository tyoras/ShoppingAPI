/**
 * 
 */
package yoan.shopping.list.repository;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import yoan.shopping.infra.util.error.ErrorMessage;

/**
 *
 * @author yoan
 */
public enum ShoppingListRepositoryErrorMessage implements ErrorMessage {
	/** Error while creating list : %s */
	PROBLEM_CREATION_LIST("Error while creating list : %s"),
	/** Error while updating list : %s */
	PROBLEM_UPDATE_LIST("Error while updating list : %s");
	
	
	private String message;
	
	private ShoppingListRepositoryErrorMessage(String message) {
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
