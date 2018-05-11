package io.tyoras.shopping.user.repository;

import io.tyoras.shopping.infra.util.error.ErrorCode;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Error codes specific to the user repositories
 *
 * @author yoan
 */
public enum UserRepositoryErrorCode implements ErrorCode {
    UNSECURE_PASSWORD("UNSECURE_PASSWORD"),
    TOO_MUCH_RESULT("TOO_MUCH_RESULT");

    private String code;

    private UserRepositoryErrorCode(String code) {
        checkArgument(isNotBlank(code), "an error code should not be empty");
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}