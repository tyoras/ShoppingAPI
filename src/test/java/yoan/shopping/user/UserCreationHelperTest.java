package yoan.shopping.user;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.user.repository.UserRepositoryErrorCode.UNSECURE_PASSWORD;
import static yoan.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_PASSWORD_VALIDITY;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.test.TestHelper;
import yoan.shopping.user.repository.SecuredUserRepository;
import yoan.shopping.user.repository.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserCreationHelperTest {
	@Mock
	UserRepository mockedUserRepo;
	
	@Mock
	SecuredUserRepository mockedSecuredUserRepo;
	
	@Test(expected = WebApiException.class)
	public void ensureUserNotExists_should_fail_with_409_if_user_exist() {
		//given
		UUID userId = UUID.randomUUID();
		String alreadyExistingEmail = "already@exist.com";
		when(mockedUserRepo.checkUserExistsByIdOrEmail(any(), eq(alreadyExistingEmail))).thenReturn(true);
		String expectedMessage = "User with email : " + alreadyExistingEmail + " already exists";
		
		//when
		try {
			UserCreationHelper.ensureUserNotExists(mockedUserRepo, userId, alreadyExistingEmail);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, CONFLICT, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test
	public void ensureUserNotExists_should_not_fail_if_user_does_not_exist() {
		//given
		UUID userId = UUID.randomUUID();
		String email = "does_not@exist.com";
		when(mockedUserRepo.checkUserExistsByIdOrEmail(any(), eq(email))).thenReturn(false);
		
		//when
		UserCreationHelper.ensureUserNotExists(mockedUserRepo, userId, email);

		//then
		//should not fail
		verify(mockedUserRepo).checkUserExistsByIdOrEmail(userId, email);
	}
	
	@Test(expected = WebApiException.class)
	public void createUser_should_fail_with_401_if_password_is_invalid() {
		//given
		User user = TestHelper.generateRandomUser();
		String invalidPassword = "invalidPass";
		String expectedMessage = PROBLEM_PASSWORD_VALIDITY.getDevReadableMessage();
		doThrow(new ApplicationException(ERROR, UNSECURE_PASSWORD, expectedMessage)).when(mockedSecuredUserRepo).create(any(), eq(invalidPassword));
		
		//when
		try {
			UserCreationHelper.createUser(mockedSecuredUserRepo, user, invalidPassword);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
}
