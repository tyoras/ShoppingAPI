package yoan.shopping.user.resource;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.user.ProfileVisibility.PUBLIC;
import static yoan.shopping.user.repository.UserRepositoryErrorCode.UNSECURE_PASSWORD;
import static yoan.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_PASSWORD_VALIDITY;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import yoan.shopping.infra.rest.Link;
import yoan.shopping.infra.rest.RestRepresentation;
import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.test.TestHelper;
import yoan.shopping.user.User;
import yoan.shopping.user.repository.SecuredUserRepository;
import yoan.shopping.user.repository.UserRepository;
import yoan.shopping.user.representation.SecuredUserWriteRepresentation;
import yoan.shopping.user.representation.UserRepresentation;

@RunWith(MockitoJUnitRunner.class)
public class RegisterUserResourceTest {

	@Mock
	UserRepository mockedUserRepo;
	
	@Mock
	SecuredUserRepository mockedSecuredUserRepo;
	
	@Spy
	@InjectMocks
	RegisterUserResource testedResource;
	
	@Test
	public void getRootLinks_should_contains_self_link() {
		//given
		String expectedURL = "http://test";
		UriInfo mockedUriInfo = TestHelper.mockUriInfo(expectedURL);
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
	public void register_should_work_with_valid_input_representation() {
		//given
		String expectedName = "name";
		String expectedMail = "mail";
		String expectedProfileVisibility = PUBLIC.name();
		@SuppressWarnings("deprecation")
		SecuredUserWriteRepresentation representation = new SecuredUserWriteRepresentation(expectedName, expectedMail, expectedProfileVisibility, "password");
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		when(testedResource.getUriInfo()).thenReturn(mockedUriInfo);
		
		//when
		Response response = testedResource.register(representation);
		
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
	public void register_should_fail_with_409_if_user_exist() {
		//given
		String alreadyExistingEmail = "already@exist.com";
		@SuppressWarnings("deprecation")
		SecuredUserWriteRepresentation representation = new SecuredUserWriteRepresentation("name", alreadyExistingEmail, PUBLIC.name(), "password");
		when(mockedUserRepo.checkUserExistsByIdOrEmail(any(), eq(alreadyExistingEmail))).thenReturn(true);
		String expectedMessage = "User with email : " + alreadyExistingEmail + " already exists";
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		when(testedResource.getUriInfo()).thenReturn(mockedUriInfo);
		
		//when
		try {
			testedResource.register(representation);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, CONFLICT, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void register_should_fail_with_401_if_password_is_invalid() {
		//given
		String invalidPassword = "invalidPass";
		@SuppressWarnings("deprecation")
		SecuredUserWriteRepresentation representation = new SecuredUserWriteRepresentation("name", "test@mail.com", PUBLIC.name(), invalidPassword);
		String expectedMessage = PROBLEM_PASSWORD_VALIDITY.getDevReadableMessage();
		doThrow(new ApplicationException(ERROR, UNSECURE_PASSWORD, expectedMessage)).when(mockedSecuredUserRepo).create(any(), eq(invalidPassword));
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		when(testedResource.getUriInfo()).thenReturn(mockedUriInfo);
		
		//when
		try {
			testedResource.register(representation);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
}