package yoan.shopping.list.resource;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import yoan.shopping.infra.util.error.ErrorMessage;

/**
* @author yoan
*/
public enum ShoppingItemResourceErrorMessage implements ErrorMessage {
	/** Item not found */
	ITEM_NOT_FOUND("Item not found"),
	/** Item Id is a mandatory field to update a item */
	MISSING_ITEM_ID_FOR_UPDATE("Item Id is a mandatory field to update an item"),
	/** Item with Id : %s aready exists */
	ALREADY_EXISTING_ITEM("Item with Id : %s already exists");

	private String message;
	
	private ShoppingItemResourceErrorMessage(String message) {
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