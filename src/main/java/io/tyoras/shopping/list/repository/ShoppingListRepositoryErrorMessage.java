/**
 *
 */
package io.tyoras.shopping.list.repository;

import io.tyoras.shopping.infra.util.error.ErrorMessage;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author yoan
 */
public enum ShoppingListRepositoryErrorMessage implements ErrorMessage {
    /**
     * Error while reading list : %s
     */
    PROBLEM_READ_LIST("Error while reading list : %s"),
    /**
     * Error while reading this user lists : %s
     */
    PROBLEM_READ_USER_LISTS("Error while reading this user lists : %s"),
    /**
     * Error while creating list : %s
     */
    PROBLEM_CREATION_LIST("Error while creating list : %s"),
    /**
     * Error while updating list : %s
     */
    PROBLEM_UPDATE_LIST("Error while updating list : %s"),
    /**
     * Error while deleting list : %s
     */
    PROBLEM_DELETE_LIST("Error while deleting list : %s");


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
