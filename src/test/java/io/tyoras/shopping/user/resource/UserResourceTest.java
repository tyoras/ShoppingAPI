package io.tyoras.shopping.user.resource;

import static io.tyoras.shopping.infra.rest.error.Level.ERROR;
import static io.tyoras.shopping.infra.rest.error.Level.INFO;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static io.tyoras.shopping.user.ProfileVisibility.PUBLIC;
import static io.tyoras.shopping.user.repository.UserRepositoryErrorCode.TOO_MUCH_RESULT;
import static io.tyoras.shopping.user.repository.UserRepositoryErrorCode.UNSECURE_PASSWORD;
import static io.tyoras.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_PASSWORD_VALIDITY;
import static io.tyoras.shopping.user.repository.UserRepositoryErrorMessage.TOO_MUCH_RESULT_FOR_SEARCH;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;

import io.tyoras.shopping.infra.rest.Link;
import io.tyoras.shopping.infra.rest.RestRepresentation;
import io.tyoras.shopping.infra.rest.error.WebApiException;
import io.tyoras.shopping.infra.util.error.ApplicationException;
import io.tyoras.shopping.infra.util.error.RepositoryErrorCode;
import io.tyoras.shopping.test.TestHelper;
import io.tyoras.shopping.user.User;
import io.tyoras.shopping.user.repository.SecuredUserRepository;
import io.tyoras.shopping.user.repository.UserRepository;
import io.tyoras.shopping.user.representation.SecuredUserWriteRepresentation;
import io.tyoras.shopping.user.representation.UserRepresentation;
import io.tyoras.shopping.user.representation.UserWriteRepresentation;
import io.tyoras.shopping.user.resource.UserResource;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UserResourceTest {

	@Mock
	UserRepository mockedUserRepo;
	
	@Mock
	SecuredUserRepository mockedSecuredUserRepo;
	
	private UserResource getUserResource() {
		UserResource testedResource = new UserResource(mockedUserRepo, mockedSecuredUserRepo);
		return spy(testedResource);
	}
	
	@Test
	public void getRootLinks_should_contains_self_link() {
		//given
		String expectedURL = "http://test";
		UriInfo mockedUriInfo = TestHelper.mockUriInfo(expectedURL);
		UserResource testedResource = getUserResource();
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		
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
		UserResource testedResource = getUserResource();
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		
		//when
		Response response = testedResource.root(connectedUser);
		
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
		String expectedName = "name";
		String expectedMail = "mail";
		String expectedProfileVisibility = PUBLIC.name();
		@SuppressWarnings("deprecation")
		SecuredUserWriteRepresentation representation = new SecuredUserWriteRepresentation(expectedName, expectedMail, expectedProfileVisibility, "password");
		UserResource testedResource = getUserResource();
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		
		//when
		Response response = testedResource.create(TestHelper.generateRandomUser(), representation);
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(CREATED.getStatusCode());
		UserRepresentation userRepresentation = (UserRepresentation) response.getEntity();
		assertThat(userRepresentation).isNotNull();
		assertThat(userRepresentation.getId()).isNotEqualTo(User.DEFAULT_ID);
		assertThat(userRepresentation.getName()).isEqualTo(expectedName);
		assertThat(userRepresentation.getEmail()).isEqualTo(expectedMail);
		assertThat(userRepresentation.getProfileVisibility()).isEqualTo(expectedProfileVisibility);
	}
	
	@Test(expected = WebApiException.class)
	public void create_should_fail_with_409_if_user_exist() {
		//given
		String alreadyExistingEmail = "already@exist.com";
		@SuppressWarnings("deprecation")
		SecuredUserWriteRepresentation representation = new SecuredUserWriteRepresentation("name", alreadyExistingEmail, PUBLIC.name(), "password");
		when(mockedUserRepo.checkUserExistsByIdOrEmail(any(), eq(alreadyExistingEmail))).thenReturn(true);
		String expectedMessage = "User with email : " + alreadyExistingEmail + " already exists";
		UserResource testedResource = getUserResource();
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		
		//when
		try {
			testedResource.create(TestHelper.generateRandomUser(), representation);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, CONFLICT, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void create_should_fail_with_401_if_password_is_invalid() {
		//given
		String invalidPassword = "invalidPass";
		@SuppressWarnings("deprecation")
		SecuredUserWriteRepresentation representation = new SecuredUserWriteRepresentation("name", "test@mail.com", PUBLIC.name(), invalidPassword);
		String expectedMessage = PROBLEM_PASSWORD_VALIDITY.getDevReadableMessage();
		doThrow(new ApplicationException(ERROR, UNSECURE_PASSWORD, expectedMessage)).when(mockedSecuredUserRepo).create(any(), eq(invalidPassword));
		UserResource testedResource = getUserResource();
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		
		//when
		try {
			testedResource.create(TestHelper.generateRandomUser(), representation);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getById_should_return_400_with_invalid_Id() {
		//given
		String invalidId = "invalid ID";
		UserResource testedResource = getUserResource();
		String expectedMessage = "Invalid Param named userId : invalid ID";
		
		//when
		try {
			testedResource.getById(TestHelper.generateRandomUser(), invalidId);
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
		UserResource testedResource = getUserResource();
		String expectedMessage = "User not found";
		
		//when
		try {
			testedResource.getById(TestHelper.generateRandomUser(), unknownId);
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
		UserResource testedResource = getUserResource();
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		User existingUser = User.Builder.createDefault().withId(existingId).build();
		when(mockedUserRepo.getById(existingId)).thenReturn(existingUser);
		
		//when
		Response response = testedResource.getById(TestHelper.generateRandomUser(), existingId.toString());
		
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
		String expectedProfileVisibility = PUBLIC.name();
		@SuppressWarnings("deprecation")
		UserWriteRepresentation representation = new UserWriteRepresentation(expectedName, expectedMail, expectedProfileVisibility);
		UserResource testedResource = getUserResource();
		User existingUser = User.Builder.createDefault().withId(expectedID).build();
		when(mockedUserRepo.getById(expectedID)).thenReturn(existingUser);
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		
		//when
		Response response = testedResource.update(TestHelper.generateRandomUser(), expectedID.toString(), representation);
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(NO_CONTENT.getStatusCode());
	}
	
	@Test(expected = WebApiException.class)
	public void update_should_return_400_with_input_representation_without_id() {
		//given
		@SuppressWarnings("deprecation")
		UserWriteRepresentation representationWithoutId = new UserWriteRepresentation("name", "mail", PUBLIC.name());
		UserResource testedResource = getUserResource();
		String expectedMessage = "Invalid Param named userId : null";
		
		//when
		try {
			testedResource.update(TestHelper.generateRandomUser(), null, representationWithoutId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void update_should_return_400_with_input_representation_with_invalid_id() {
		//given
		String invalidId = "invalid";
		@SuppressWarnings("deprecation")
		UserWriteRepresentation representationWithoutId = new UserWriteRepresentation("name", "mail", PUBLIC.name());
		UserResource testedResource = getUserResource();
		String expectedMessage = "Invalid Param named userId : invalid";
		
		//when
		try {
			testedResource.update(TestHelper.generateRandomUser(), invalidId, representationWithoutId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = ApplicationException.class)
	public void update_should_return_404_with_unknown_user() {
		//given
		String unknownUserId = UUID.randomUUID().toString();
		@SuppressWarnings("deprecation")
		UserWriteRepresentation representation = new UserWriteRepresentation("name", "mail", PUBLIC.name());
		UserResource testedResource = getUserResource();
		String expectedMessage = "User not found";
		
		//when
		try {
			testedResource.update(TestHelper.generateRandomUser(), unknownUserId, representation);
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
		UserResource testedResource = getUserResource();
		String expectedMessage = "Invalid Param named userId : invalid ID";
		
		//when
		try {
			testedResource.changePassword(TestHelper.generateRandomUser(), invalidId, "new password");
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
		UserResource testedResource = getUserResource();
		String expectedMessage = "User not found";
		
		//when
		try {
			testedResource.changePassword(TestHelper.generateRandomUser(), user.getId().toString(), "new password");
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
		UserResource testedResource = getUserResource();
		String expectedMessage = "Invalid Param named userId : invalid ID";
		
		//when
		try {
			testedResource.deleteById(TestHelper.generateRandomUser(), invalidId);
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
		UserResource testedResource = getUserResource();
		String expectedMessage = "User not found";
		
		//when
		try {
			testedResource.deleteById(TestHelper.generateRandomUser(), unknownId);
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
		UserResource testedResource = getUserResource();
		User existingUser = User.Builder.createDefault().withId(existingId).build();
		when(mockedUserRepo.getById(existingId)).thenReturn(existingUser);
		
		//when
		Response response = testedResource.deleteById(TestHelper.generateRandomUser(), existingId.toString());
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(NO_CONTENT.getStatusCode());
	}
	
	@Test(expected = WebApiException.class)
	public void getByEmail_should_return_400_with_invalid_email() {
		//given
		String invalidEmail = "invalid email";
		UserResource testedResource = getUserResource();
		String expectedMessage = "Invalid Param named userEmail is not a valid email adress : invalid email";
		
		//when
		try {
			testedResource.getByEmail(TestHelper.generateRandomUser(), invalidEmail);
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
		UserResource testedResource = getUserResource();
		String expectedMessage = "User not found";
		
		//when
		try {
			testedResource.getByEmail(TestHelper.generateRandomUser(), unknownEmail);
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
		UserResource testedResource = getUserResource();
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		User existingUser = User.Builder.createDefault().withRandomId().withEmail(existingEmail).build();
		when(mockedUserRepo.getByEmail(existingEmail)).thenReturn(existingUser);
		
		//when
		Response response = testedResource.getByEmail(TestHelper.generateRandomUser(), existingEmail);
		
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
	public void searchByName_should_work_with_existing_users() {
		//given
		UserResource testedResource = getUserResource();
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		User existingUser1 = TestHelper.generateRandomUser();
		User existingUser2 = TestHelper.generateRandomUser();
		when(mockedUserRepo.searchByName("search")).thenReturn(ImmutableList.of(existingUser1,existingUser2));
		
		//when
		Response response = testedResource.searchByName(TestHelper.generateRandomUser(), "search");
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
		List<?> userRepresentations = (List<?>) response.getEntity();
		assertThat(userRepresentations).isNotNull();
		assertThat(userRepresentations).hasSize(2);
		
		UserRepresentation userRepresentation = (UserRepresentation) userRepresentations.get(0);
		assertThat(userRepresentation).isNotNull();
		assertThat(userRepresentation.getId()).isEqualTo(existingUser1.getId());
		assertThat(userRepresentation.getName()).isEqualTo(existingUser1.getName());
		assertThat(userRepresentation.getEmail()).isEqualTo(existingUser1.getEmail());
		
		UserRepresentation userRepresentation2 = (UserRepresentation) userRepresentations.get(1);
		assertThat(userRepresentation2).isNotNull();
		assertThat(userRepresentation2.getId()).isEqualTo(existingUser2.getId());
		assertThat(userRepresentation2.getName()).isEqualTo(existingUser2.getName());
		assertThat(userRepresentation2.getEmail()).isEqualTo(existingUser2.getEmail());
	}
	
	@Test(expected = WebApiException.class)
	public void searchByName_should_return_400_with_invalid_search() {
		//given
		String invalidSearch = "ab";
		UserResource testedResource = getUserResource();
		String expectedMessage = "Invalid search : \"ab\"";
		
		//when
		try {
			testedResource.searchByName(TestHelper.generateRandomUser(), invalidSearch);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void searchByName_should_return_400_with_too_much_result_search() {
		//given
		String tooMuchSearch = "too_much!";
		UserResource testedResource = getUserResource();
		String expectedMessage = TOO_MUCH_RESULT_FOR_SEARCH.getDevReadableMessage(15, tooMuchSearch);
		when(mockedUserRepo.searchByName(tooMuchSearch)).thenThrow(new ApplicationException(INFO, TOO_MUCH_RESULT, expectedMessage));
		
		//when
		try {
			testedResource.searchByName(TestHelper.generateRandomUser(), tooMuchSearch);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, TOO_MUCH_RESULT, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void searchByName_should_return_404_with_no_result_found_search() {
		//given
		String searchWithNoResult = "no_result";
		UserResource testedResource = getUserResource();
		String expectedMessage = "Users not found for search : \"no_result\"";
		when(mockedUserRepo.searchByName(searchWithNoResult)).thenReturn(ImmutableList.of());
		
		//when
		try {
			testedResource.searchByName(TestHelper.generateRandomUser(), searchWithNoResult);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, NOT_FOUND, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
}
