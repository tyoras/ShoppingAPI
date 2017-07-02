package io.tyoras.shopping.user.resource;

import static io.tyoras.shopping.infra.rest.error.Level.ERROR;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static io.tyoras.shopping.user.ProfileVisibility.PUBLIC;
import static io.tyoras.shopping.user.repository.UserRepositoryErrorCode.UNSECURE_PASSWORD;
import static io.tyoras.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_PASSWORD_VALIDITY;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import io.tyoras.shopping.infra.rest.Link;
import io.tyoras.shopping.infra.rest.RestRepresentation;
import io.tyoras.shopping.infra.rest.error.WebApiException;
import io.tyoras.shopping.infra.util.error.ApplicationException;
import io.tyoras.shopping.test.TestHelper;
import io.tyoras.shopping.user.User;
import io.tyoras.shopping.user.repository.SecuredUserRepository;
import io.tyoras.shopping.user.repository.UserRepository;
import io.tyoras.shopping.user.representation.SecuredUserWriteRepresentation;
import io.tyoras.shopping.user.representation.UserRepresentation;
import io.tyoras.shopping.user.resource.RegisterUserResource;

@RunWith(MockitoJUnitRunner.Silent.class)
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
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		
		//when
		Response response = testedResource.root(TestHelper.generateRandomUser());
		
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
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		
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
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		
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
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		
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