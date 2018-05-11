package io.tyoras.shopping.user;

import io.tyoras.shopping.infra.rest.error.WebApiException;
import io.tyoras.shopping.infra.util.error.ApplicationException;
import io.tyoras.shopping.user.repository.SecuredUserRepository;
import io.tyoras.shopping.user.repository.UserRepository;

import java.util.UUID;

import static io.tyoras.shopping.infra.rest.error.Level.ERROR;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static io.tyoras.shopping.user.repository.UserRepositoryErrorCode.UNSECURE_PASSWORD;
import static io.tyoras.shopping.user.resource.UserResourceErrorMessage.ALREADY_EXISTING_USER_WITH_EMAIL;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;

/**
 * Helper for user creation
 *
 * @author yoan
 */
public final class UserCreationHelper {

    public static void ensureUserNotExists(UserRepository userRepo, UUID userId, String email) {
        boolean userExists = userRepo.checkUserExistsByIdOrEmail(userId, email);

        if (userExists) {
            throw new WebApiException(CONFLICT, ERROR, API_RESPONSE, ALREADY_EXISTING_USER_WITH_EMAIL.getDevReadableMessage(email));
        }
    }

    public static void createUser(SecuredUserRepository securedUserRepo, User userCreated, String password) {
        try {
            securedUserRepo.create(userCreated, password);
        } catch (ApplicationException ae) {
            if (ae.getErrorCode() == UNSECURE_PASSWORD) {
                throw new WebApiException(BAD_REQUEST, ERROR, API_RESPONSE, ae.getMessage());
            }
            throw ae;
        }
    }
}
