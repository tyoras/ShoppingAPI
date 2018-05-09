package io.tyoras.shopping.list.resource;

import io.tyoras.shopping.infra.util.error.ErrorMessage;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author yoan
 */
public enum ShoppingListResourceErrorMessage implements ErrorMessage {
    /**
     * List not found
     */
    LIST_NOT_FOUND("List not found"),
    /**
     * Lists not found for owner id : %s
     */
    LISTS_NOT_FOUND("Lists not found for owner id : %s");

    private String message;

    private ShoppingListResourceErrorMessage(String message) {
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