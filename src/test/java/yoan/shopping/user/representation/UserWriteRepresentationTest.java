package yoan.shopping.user.representation;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.infra.util.error.CommonErrorMessage.INVALID;
import static yoan.shopping.user.ProfileVisibility.PUBLIC;

import java.util.UUID;

import org.junit.Test;

import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.test.TestHelper;
import yoan.shopping.user.User;

public class UserWriteRepresentationTest {
	
	@Test(expected = NullPointerException.class)
	public void userWriteRepresentation_should_fail_without_user() {
		//given
		User nullUser = null;
		
		//when
		new UserWriteRepresentation(nullUser);
	}
	
	@Test(expected = NullPointerException.class)
	public void toUser_should_fail_without_representation() {
		//given
		UUID userId = UUID.randomUUID();
		UserWriteRepresentation nullRepresentation = null;
		
		//when
		try {
			UserWriteRepresentation.toUser(nullRepresentation, userId);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Unable to create User from null UserWriteRepresentation");
			throw npe;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void toUser_should_fail_without_user_id() {
		//given
		UUID nullUserId = null;
		User user = TestHelper.generateRandomUser();
		@SuppressWarnings("deprecation")
		UserWriteRepresentation validUserWriteRepresentation = new UserWriteRepresentation(user.getName(), user.getEmail(), user.getProfileVisibility().name());
		String expectedMessage = INVALID.getDevReadableMessage("user") + " : User Id is mandatory";
		
		//when
		try {
			UserWriteRepresentation.toUser(validUserWriteRepresentation, nullUserId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void toUser_should_fail_with_invalid_Representation() {
		//given
		UUID userId = UUID.randomUUID();
		@SuppressWarnings("deprecation")
		UserWriteRepresentation invalidUserWriteRepresentation = new UserWriteRepresentation(" ", " ", PUBLIC.name());
		String expectedMessage = INVALID.getDevReadableMessage("user") + " : Invalid user name";
		
		//when
		try {
			UserWriteRepresentation.toUser(invalidUserWriteRepresentation, userId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test
	public void toUser_should_work() {
		//given
		User expectedUser = TestHelper.generateRandomUser();
		@SuppressWarnings("deprecation")
		UserWriteRepresentation validUserWriteRepresentation = new UserWriteRepresentation(expectedUser.getName(), expectedUser.getEmail(), expectedUser.getProfileVisibility().name());
		
		//when
		User result = UserWriteRepresentation.toUser(validUserWriteRepresentation, expectedUser.getId());
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedUser);
	}
}
