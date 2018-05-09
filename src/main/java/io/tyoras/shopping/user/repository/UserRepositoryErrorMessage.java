/**
 *
 */
package io.tyoras.shopping.user.repository;

import io.tyoras.shopping.infra.util.error.ErrorMessage;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author yoan
 */
public enum UserRepositoryErrorMessage implements ErrorMessage {
    /**
     * Error while reading user : %s
     */
    PROBLEM_READ_USER("Error while reading user : %s"),
    /**
     * Error while searching users : %s
     */
    PROBLEM_SEARCH_USER("Error while searching users : %s"),
    /**
     * Error while creating user : %s
     */
    PROBLEM_CREATION_USER("Error while creating user : %s"),
    /**
     * Error while updating user : %s
     */
    PROBLEM_UPDATE_USER("Error while updating user : %s"),
    /**
     * Error while deleting user : %s
     */
    PROBLEM_DELETE_USER("Error while deleting user : %s"),
    /**
     * Password does not comply to the minimum security level
     */
    PROBLEM_PASSWORD_VALIDITY("Password does not comply to the minimum security level")
    /** Unable to convert unsecure user : %s */
    ,
    UNABLE_TO_CONVERT_UNSECURE_USER("Unable to convert unsecure user : %s"),
    /**
     * Error while updating user password : %s
     */
    PROBLEM_UPDATE_USER_PASSWORD("Error while updating user password : %s"),
    /**
     * Too much users (%s) for search : %s
     */
    TOO_MUCH_RESULT_FOR_SEARCH("Too much users (%s) for search : %s");

    private String message;

    private UserRepositoryErrorMessage(String message) {
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