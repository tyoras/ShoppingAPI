package yoan.shopping.user.representation;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.fest.assertions.api.Assertions.assertThat;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.infra.util.error.CommonErrorMessage.INVALID;

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
		UserWriteRepresentation nullRepresentation = null;
		
		//when
		try {
			UserWriteRepresentation.toUser(nullRepresentation);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Unable to create User from null UserWriteRepresentation");
			throw npe;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void toUser_should_fail_with_invalid_Representation() {
		//given
		@SuppressWarnings("deprecation")
		UserWriteRepresentation invalidUserWriteRepresentation = new UserWriteRepresentation(UUID.randomUUID(), " ", " ");
		String expectedMessage = INVALID.getDevReadableMessage("user") + " : Invalid user name";
		
		//when
		try {
			UserWriteRepresentation.toUser(invalidUserWriteRepresentation);
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
		UserWriteRepresentation validUserWriteRepresentation = new UserWriteRepresentation(expectedUser.getId(), expectedUser.getName(), expectedUser.getEmail());
		
		//when
		User result = UserWriteRepresentation.toUser(validUserWriteRepresentation);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedUser);
	}
}
