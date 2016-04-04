package yoan.shopping.user;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.user.repository.UserRepositoryErrorCode.UNSECURE_PASSWORD;
import static yoan.shopping.user.resource.UserResourceErrorMessage.ALREADY_EXISTING_USER;

import java.util.UUID;

import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.user.repository.SecuredUserRepository;
import yoan.shopping.user.repository.UserRepository;

/**
 * Helper for user creation
 * @author yoan
 */
public final class UserCreationHelper {

	public static void ensureUserNotExists(UserRepository userRepo, UUID userId) {
		User foundUser = userRepo.getById(userId);
		
		if (foundUser != null) {
			throw new WebApiException(CONFLICT, ERROR, API_RESPONSE, ALREADY_EXISTING_USER.getDevReadableMessage(userId));
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
