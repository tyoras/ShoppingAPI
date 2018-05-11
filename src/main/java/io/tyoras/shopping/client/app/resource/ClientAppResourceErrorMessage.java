package io.tyoras.shopping.client.app.resource;

import io.tyoras.shopping.infra.util.error.ErrorMessage;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Error messages specific to Client app API
 *
 * @author yoan
 */
public enum ClientAppResourceErrorMessage implements ErrorMessage {
    /**
     * Client application not found
     */
    CLIENT_APP_NOT_FOUND("Client application not found"),
    /**
     * Client application not found
     */
    CLIENT_APPS_NOT_FOUND("Client applications not found for owner id : %s");

    private String message;

    private ClientAppResourceErrorMessage(String message) {
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