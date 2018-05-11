package io.tyoras.shopping.list.repository;

import io.tyoras.shopping.infra.util.error.ErrorMessage;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author yoan
 */
public enum ShoppingItemRepositoryErrorMessage implements ErrorMessage {
    /**
     * Error while reading item : %s
     */
    PROBLEM_READ_ITEM("Error while reading item : %s"),
    /**
     * Error while creating item : %s
     */
    PROBLEM_CREATION_ITEM("Error while creating item : %s"),
    /**
     * Error while creating item : %s already exists
     */
    PROBLEM_CREATION_ITEM_ALREADY_EXISTS("Error while creating item : %s already exists"),
    /**
     * Error while updating item : %s
     */
    PROBLEM_UPDATE_ITEM("Error while updating item : %s"),
    /**
     * Error while deleting item : %s
     */
    PROBLEM_DELETE_ITEM("Error while deleting item : %s");


    private String message;

    private ShoppingItemRepositoryErrorMessage(String message) {
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
