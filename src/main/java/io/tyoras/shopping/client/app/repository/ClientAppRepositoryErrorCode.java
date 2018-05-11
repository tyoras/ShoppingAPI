package io.tyoras.shopping.client.app.repository;

import io.tyoras.shopping.infra.util.error.ErrorCode;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Error codes specific to the client app repository
 *
 * @author yoan
 */
public enum ClientAppRepositoryErrorCode implements ErrorCode {
    UNSECURE_SECRET("UNSECURE_SECRET");

    private String code;

    private ClientAppRepositoryErrorCode(String code) {
        checkArgument(isNotBlank(code), "an error code should not be empty");
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
