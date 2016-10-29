package yoan.shopping.list;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static yoan.shopping.infra.util.error.CommonErrorMessage.UNABLE_TO_FIND;

import org.slf4j.LoggerFactory;

/**
 * Describe the state of a shopping item
 * @author yoan
 */
public enum ItemState {
	TO_BUY("Item not already bought"),
	BOUGHT("Item already bought"),
	CANCELLED("Item not required anymore");
	
	/** State decription */
	private final String description;
	
	private ItemState(String description) {
		checkArgument(isNotBlank(description), "A state description should not be empty");
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public static ItemState of(String codeStr) {
		try {
			return ItemState.valueOf(codeStr);
		} catch(IllegalArgumentException iae) {
			LoggerFactory.getLogger(ItemState.class).info(UNABLE_TO_FIND.getDevReadableMessage("ItemState", codeStr));
			return null;
		}
	}
}