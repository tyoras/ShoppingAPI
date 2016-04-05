package yoan.shopping.user.resource;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.rest.error.Level.INFO;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.user.resource.UserResourceErrorMessage.ALREADY_EXISTING_USER;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import yoan.shopping.infra.rest.Link;
import yoan.shopping.infra.rest.RestRepresentation;
import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.infra.util.error.RepositoryErrorCode;
import yoan.shopping.test.TestHelper;
import yoan.shopping.user.User;
import yoan.shopping.user.repository.SecuredUserRepository;
import yoan.shopping.user.repository.UserRepository;
import yoan.shopping.user.representation.SecuredUserWriteRepresentation;
import yoan.shopping.user.representation.UserRepresentation;
import yoan.shopping.user.representation.UserWriteRepresentation;

@RunWith(MockitoJUnitRunner.class)
public class UserResourceTest {

	@Mock
	UserRepository mockedUserRepo;
	
	@Mock
	SecuredUserRepository mockedSecuredUserRepo;
	
	private UserResource getUserResource(User connectedUser) {
		UserResource testedResource = new UserResource(connectedUser, mockedUserRepo, mockedSecuredUserRepo);
		return spy(testedResource);
	}
	
	@Test
	public void getRootLinks_should_contains_self_link() {
		//given
		String expectedURL = "http://test";
		UriInfo mockedUriInfo = TestHelper.mockUriInfo(expectedURL);
		UserResource testedResource = getUserResource(TestHelper.generateRandomUser());
		when(testedResource.getUriInfo()).thenReturn(mockedUriInfo);
		
		//when
		List<Link> links = testedResource.getRootLinks();
		
		//then
		assertThat(links).isNotNull();
		assertThat(links).isNotEmpty();
		assertThat(links).contains(Link.self(expectedURL));
	}
	
	@Test
	public void root_should_work() {
		//given
		String expectedURL = "http://test";
		UriInfo mockedUriInfo = TestHelper.mockUriInfo(expectedURL);
		User connectedUser = TestHelper.generateRandomUser();
		UserResource testedResource = getUserResource(connectedUser);
		when(testedResource.getUriInfo()).thenReturn(mockedUriInfo);
		
		//when
		Response response = testedResource.root();
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
		RestRepresentation representation = (RestRepresentation) response.getEntity();
		assertThat(representation).isNotNull();
		assertThat(representation.getLinks()).contains(Link.self(expectedURL));
	}
	
	@Test
	public void create_should_work_with_valid_input_representation() {
		//given
		UUID expectedID = UUID.randomUUID();
		String expectedName = "name";
		String expectedMail = "mail";
		@SuppressWarnings("deprecation")
		SecuredUserWriteRepresentation representation = new SecuredUserWriteRepresentation(expectedID, expectedName, expectedMail, "password");
		UserResource testedResource = getUserResource(TestHelper.generateRandomUser());
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		when(testedResource.getUriInfo()).thenReturn(mockedUriInfo);
		
		//when
		Response response = testedResource.create(representation);
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(CREATED.getStatusCode());
		UserRepresentation userRepresentation = (UserRepresentation) response.getEntity();
		assertThat(userRepresentation).isNotNull();
		assertThat(userRepresentation.getId()).isEqualTo(expectedID);
		assertThat(userRepresentation.getName()).isEqualTo(expectedName);
		assertThat(userRepresentation.getEmail()).isEqualTo(expectedMail);
	}
	
	@Test
	public void create_should_work_with_input_representation_without_id() {
		//given
		String expectedName = "name";
		String expectedMail = "mail";
		@SuppressWarnings("deprecation")
		SecuredUserWriteRepresentation representationwithoutId = new SecuredUserWriteRepresentation(null, expectedName, expectedMail, "password");
		UserResource testedResource = getUserResource(TestHelper.generateRandomUser());
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		when(testedResource.getUriInfo()).thenReturn(mockedUriInfo);
		
		//when
		Response response = testedResource.create(representationwithoutId);
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(CREATED.getStatusCode());
		UserRepresentation userRepresentation = (UserRepresentation) response.getEntity();
		assertThat(userRepresentation).isNotNull();
		assertThat(userRepresentation.getId()).isNotEqualTo(User.DEFAULT_ID);
		assertThat(userRepresentation.getName()).isEqualTo(expectedName);
		assertThat(userRepresentation.getEmail()).isEqualTo(expectedMail);
	}
	
	@Test(expected = WebApiException.class)
	public void create_should_return_409_with_already_existing_user() {
		//given
		UUID alreadyExistingUserId = UUID.randomUUID();
		UserResource testedResource = getUserResource(TestHelper.generateRandomUser());
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		when(testedResource.getUriInfo()).thenReturn(mockedUriInfo);
		@SuppressWarnings("deprecation")
		SecuredUserWriteRepresentation representation = new SecuredUserWriteRepresentation(alreadyExistingUserId, "name", "mail", "password");
		when(mockedUserRepo.checkUserExistsByIdOrEmail(eq(alreadyExistingUserId), any())).thenReturn(true);
		String expectedMessage = ALREADY_EXISTING_USER.getDevReadableMessage(alreadyExistingUserId);
		
		//when
		try {
			testedResource.create(representation);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, CONFLICT, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getById_should_return_400_with_invalid_Id() {
		//given
		String invalidId = "invalid ID";
		UserResource testedResource = getUserResource(TestHelper.generateRandomUser());
		String expectedMessage = "Invalid Param named userId : invalid ID";
		
		//when
		try {
			testedResource.getById(invalidId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getById_should_return_404_with_unknown_user_Id() {
		//given
		String unknownId = UUID.randomUUID().toString();
		UserResource testedResource = getUserResource(TestHelper.generateRandomUser());
		String expectedMessage = "User not found";
		
		//when
		try {
			testedResource.getById(unknownId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, NOT_FOUND, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test
	public void getById_should_work_with_existing_user_Id() {
		//given
		UUID existingId = UUID.randomUUID();
		UserResource testedResource = getUserResource(TestHelper.generateRandomUser());
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		when(testedResource.getUriInfo()).thenReturn(mockedUriInfo);
		User existingUser = User.Builder.createDefault().withId(existingId).build();
		when(mockedUserRepo.getById(existingId)).thenReturn(existingUser);
		
		//when
		Response response = testedResource.getById(existingId.toString());
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
		UserRepresentation userRepresentation = (UserRepresentation) response.getEntity();
		assertThat(userRepresentation).isNotNull();
		assertThat(userRepresentation.getId()).isNotEqualTo(User.DEFAULT_ID);
		assertThat(userRepresentation.getName()).isEqualTo(existingUser.getName());
		assertThat(userRepresentation.getEmail()).isEqualTo(existingUser.getEmail());
	}
	
	@Test
	public void update_should_work_with_existing_user() {
		//given
		UUID expectedID = UUID.randomUUID();
		String expectedName = "name";
		String expectedMail = "mail";
		@SuppressWarnings("deprecation")
		UserWriteRepresentation representation = new UserWriteRepresentation(expectedID, expectedName, expectedMail);
		UserResource testedResource = getUserResource(TestHelper.generateRandomUser());
		User existingUser = User.Builder.createDefault().withId(expectedID).build();
		when(mockedUserRepo.getById(expectedID)).thenReturn(existingUser);
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		when(testedResource.getUriInfo()).thenReturn(mockedUriInfo);
		
		//when
		Response response = testedResource.update(representation);
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(NO_CONTENT.getStatusCode());
	}
	
	@Test(expected = WebApiException.class)
	public void update_should_return_400_with_input_representation_without_id() {
		//given
		@SuppressWarnings("deprecation")
		UserWriteRepresentation representationWithoutId = new UserWriteRepresentation(null, "name", "mail");
		UserResource testedResource = getUserResource(TestHelper.generateRandomUser());
		String expectedMessage = UserResourceErrorMessage.MISSING_USER_ID_FOR_UPDATE.getDevReadableMessage();
		
		//when
		try {
			testedResource.update(representationWithoutId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = ApplicationException.class)
	public void update_should_return_404_with_unknown_user() {
		//given
		@SuppressWarnings("deprecation")
		UserWriteRepresentation representation = new UserWriteRepresentation(UUID.randomUUID(), "name", "mail");
		UserResource testedResource = getUserResource(TestHelper.generateRandomUser());
		String expectedMessage = "User not found";
		
		//when
		try {
			testedResource.update(representation);
		} catch(ApplicationException ae) {
		//then
			TestHelper.assertApplicationException(ae, INFO, RepositoryErrorCode.NOT_FOUND, expectedMessage);
			throw ae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void changePassword_should_return_400_with_invalid_Id() {
		//given
		String invalidId = "invalid ID";
		UserResource testedResource = getUserResource(TestHelper.generateRandomUser());
		String expectedMessage = "Invalid Param named userId : invalid ID";
		
		//when
		try {
			testedResource.changePassword(invalidId, "new password");
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = ApplicationException.class)
	public void changePassword_should_return_404_with_unknown_user() {
		//given
		User user = TestHelper.generateRandomUser();
		UserResource testedResource = getUserResource(TestHelper.generateRandomUser());
		String expectedMessage = "User not found";
		
		//when
		try {
			testedResource.changePassword(user.getId().toString(), "new password");
		} catch(ApplicationException ae) {
		//then
			TestHelper.assertApplicationException(ae, INFO, RepositoryErrorCode.NOT_FOUND, expectedMessage);
			throw ae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void deleteById_should_return_400_with_invalid_Id() {
		//given
		String invalidId = "invalid ID";
		UserResource testedResource = getUserResource(TestHelper.generateRandomUser());
		String expectedMessage = "Invalid Param named userId : invalid ID";
		
		//when
		try {
			testedResource.deleteById(invalidId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void deleteById_should_return_404_with_unknown_user_Id() {
		//given
		String unknownId = UUID.randomUUID().toString();
		UserResource testedResource = getUserResource(TestHelper.generateRandomUser());
		String expectedMessage = "User not found";
		
		//when
		try {
			testedResource.deleteById(unknownId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, NOT_FOUND, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test
	public void deleteById_should_work_with_existing_user_Id() {
		//given
		UUID existingId = UUID.randomUUID();
		UserResource testedResource = getUserResource(TestHelper.generateRandomUser());
		User existingUser = User.Builder.createDefault().withId(existingId).build();
		when(mockedUserRepo.getById(existingId)).thenReturn(existingUser);
		
		//when
		Response response = testedResource.deleteById(existingId.toString());
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
	}
	
	@Test(expected = WebApiException.class)
	public void getByEmail_should_return_400_with_invalid_email() {
		//given
		String invalidEmail = "invalid email";
		UserResource testedResource = getUserResource(TestHelper.generateRandomUser());
		String expectedMessage = "Invalid Param named userEmail is not a valid email adress : invalid email";
		
		//when
		try {
			testedResource.getByEmail(invalidEmail);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getByEmail_should_return_404_with_unknown_user_email() {
		//given
		String unknownEmail = "unknown@unknown.com";
		UserResource testedResource = getUserResource(TestHelper.generateRandomUser());
		String expectedMessage = "User not found";
		
		//when
		try {
			testedResource.getByEmail(unknownEmail);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, NOT_FOUND, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test
	public void getByEmail_should_work_with_existing_user_email() {
		//given
		String existingEmail = "existing@existing.com";
		UserResource testedResource = getUserResource(TestHelper.generateRandomUser());
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		when(testedResource.getUriInfo()).thenReturn(mockedUriInfo);
		User existingUser = User.Builder.createDefault().withRandomId().withEmail(existingEmail).build();
		when(mockedUserRepo.getByEmail(existingEmail)).thenReturn(existingUser);
		
		//when
		Response response = testedResource.getByEmail(existingEmail);
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
		UserRepresentation userRepresentation = (UserRepresentation) response.getEntity();
		assertThat(userRepresentation).isNotNull();
		assertThat(userRepresentation.getId()).isNotEqualTo(User.DEFAULT_ID);
		assertThat(userRepresentation.getName()).isEqualTo(existingUser.getName());
		assertThat(userRepresentation.getEmail()).isEqualTo(existingUser.getEmail());
	}
}
