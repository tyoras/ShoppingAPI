package io.tyoras.shopping.authentication.resource;

import static io.tyoras.shopping.authentication.resource.OAuthResourceErrorMessage.INVALID_AUTHZ_CODE;
import static io.tyoras.shopping.authentication.resource.OAuthResourceErrorMessage.INVALID_CLIENT_SECRET;
import static io.tyoras.shopping.authentication.resource.OAuthResourceErrorMessage.MISSING_CLIENT_SECRET;
import static io.tyoras.shopping.authentication.resource.OAuthResourceErrorMessage.UNKNOWN_CLIENT;
import static io.tyoras.shopping.infra.rest.error.Level.INFO;
import static io.tyoras.shopping.infra.rest.error.Level.WARNING;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import javax.ws.rs.core.Response;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import io.tyoras.shopping.authentication.OAuthTokenRequest;
import io.tyoras.shopping.authentication.repository.OAuth2AccessTokenRepository;
import io.tyoras.shopping.authentication.repository.OAuth2AuthorizationCodeRepository;
import io.tyoras.shopping.authentication.resource.TokenResource;
import io.tyoras.shopping.client.app.ClientApp;
import io.tyoras.shopping.client.app.repository.ClientAppRepository;
import io.tyoras.shopping.infra.rest.error.WebApiException;
import io.tyoras.shopping.test.TestHelper;
import io.tyoras.shopping.user.SecuredUser;
import io.tyoras.shopping.user.User;
import io.tyoras.shopping.user.repository.SecuredUserRepository;

@RunWith(MockitoJUnitRunner.class)
public class TokenResourceTest {
	@Mock
	OAuth2AuthorizationCodeRepository mockedAuthorizationCodeRepo;
	@Mock
	OAuth2AccessTokenRepository mockedAccessTokenRepo;
	@Mock
	ClientAppRepository mockedClientAppRepo;
	@Mock
	SecuredUserRepository mockedUserRepo;
	
	@Spy
	@InjectMocks
	private TokenResource testedResource;
	
	private static final String VALID_REDIRECT_URI = "http://www.google.fr";
	
	@Before
	public void before() {
		when(mockedClientAppRepo.getById(ClientApp.DEFAULT_ID)).thenReturn(ClientApp.DEFAULT);
		when(mockedClientAppRepo.hashSecret(eq(ClientApp.DEFAULT.getSecret()), any())).thenReturn(ClientApp.DEFAULT.getSecret());
	}
	
	@Test
	public void authorize_should_return_bad_request_with_empty_token_request() throws OAuthSystemException, OAuthProblemException {
		//given
		OAuthTokenRequest invalidRequest = new OAuthTokenRequest();
		
		//when
		Response response = testedResource.authorize(invalidRequest);
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getEntity()).isNotNull();
		verify(testedResource, never()).handleTokenRequest(any());
	}
	
	@Test(expected = WebApiException.class)
	public void authorize_should_return_bad_request_when_client_id_is_not_a_valid_UUID() throws OAuthSystemException {
		//given
		String invalidClientId = "not uuid";
		String expectedMessage = "Invalid Param named client_id : " + invalidClientId;
		OAuthTokenRequest invalidRequest = new OAuthTokenRequest();
		invalidRequest.setClientId(invalidClientId);
		invalidRequest.setClientSecret("secret");
		invalidRequest.setGrantType(GrantType.AUTHORIZATION_CODE.toString());
		invalidRequest.setRedirectUri(VALID_REDIRECT_URI);
		invalidRequest.setCode("code");
		
		//when
		try {
			testedResource.authorize(invalidRequest);
		} catch(WebApiException wae) {
		//then
			verify(testedResource, never()).generateAccessToken(any());
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void authorize_should_return_bad_request_with_unknown_client_id() throws OAuthSystemException {
		//given
		String unknownClientId = UUID.randomUUID().toString();
		String expectedMessage = UNKNOWN_CLIENT.getDevReadableMessage(unknownClientId);
		OAuthTokenRequest requestWithUnknownClientId = new OAuthTokenRequest();
		requestWithUnknownClientId.setClientId(unknownClientId);
		requestWithUnknownClientId.setClientSecret("secret");
		requestWithUnknownClientId.setGrantType(GrantType.AUTHORIZATION_CODE.toString());
		requestWithUnknownClientId.setRedirectUri(VALID_REDIRECT_URI);
		requestWithUnknownClientId.setCode("code");
		
		//when
		try {
			testedResource.authorize(requestWithUnknownClientId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, WARNING, API_RESPONSE, expectedMessage);
			verify(testedResource, never()).generateAccessToken(any());
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void authorize_should_return_bad_request_with_missing_client_secret() throws OAuthSystemException {
		//given
		UUID clientId = ClientApp.DEFAULT_ID;
		OAuthTokenRequest requestWithBlankSecret = new OAuthTokenRequest();
		requestWithBlankSecret.setClientId(clientId.toString());
		requestWithBlankSecret.setClientSecret(" ");
		requestWithBlankSecret.setGrantType(GrantType.AUTHORIZATION_CODE.toString());
		requestWithBlankSecret.setRedirectUri(VALID_REDIRECT_URI);
		requestWithBlankSecret.setCode("code");
		
		//when
		try {
			testedResource.authorize(requestWithBlankSecret);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, MISSING_CLIENT_SECRET);
			verify(testedResource, never()).generateAccessToken(any());
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void authorize_should_return_bad_request_with_invalid_client_secret() throws OAuthSystemException {
		//given
		UUID clientId = ClientApp.DEFAULT_ID;
		String expectedMessage = INVALID_CLIENT_SECRET.getDevReadableMessage(clientId);
		OAuthTokenRequest requestWithInvalidSecret = new OAuthTokenRequest();
		requestWithInvalidSecret.setClientId(clientId.toString());
		requestWithInvalidSecret.setClientSecret("invalid secret");
		requestWithInvalidSecret.setGrantType(GrantType.AUTHORIZATION_CODE.toString());
		requestWithInvalidSecret.setRedirectUri(VALID_REDIRECT_URI);
		requestWithInvalidSecret.setCode("code");
		
		//when
		try {
			testedResource.authorize(requestWithInvalidSecret);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, WARNING, API_RESPONSE, expectedMessage);
			verify(testedResource, never()).generateAccessToken(any());
			throw wae;
		}
	}
	
	@Test
	public void authorize_should_fail_with_unknown_authz_code_request() throws OAuthSystemException {
		//given
		String unknownAuthzCode = "unknown code";
		String expectedMessage = INVALID_AUTHZ_CODE.getDevReadableMessage(unknownAuthzCode);
		OAuthTokenRequest requestWithInvalidAuthzCode = new OAuthTokenRequest();
		requestWithInvalidAuthzCode.setClientId(ClientApp.DEFAULT_ID.toString());
		requestWithInvalidAuthzCode.setClientSecret(ClientApp.DEFAULT.getSecret());
		requestWithInvalidAuthzCode.setGrantType(GrantType.AUTHORIZATION_CODE.toString());
		requestWithInvalidAuthzCode.setRedirectUri(VALID_REDIRECT_URI);
		requestWithInvalidAuthzCode.setCode(unknownAuthzCode);
		
		//when
		Response response = testedResource.authorize(requestWithInvalidAuthzCode);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getEntity()).isNotNull();
		assertThat((String) response.getEntity()).contains(expectedMessage);
		verify(testedResource, never()).generateAccessToken(any());
	}
	
	@Test
	public void authorize_should_handle_valid_authz_code_request() throws OAuthSystemException {
		//given
		String validAuthzCode = "valid";
		when(mockedAuthorizationCodeRepo.getUserIdByAuthorizationCode(validAuthzCode)).thenReturn(User.DEFAULT_ID);
		OAuthTokenRequest requestWithValidAuthzCode = new OAuthTokenRequest();
		requestWithValidAuthzCode.setClientId(ClientApp.DEFAULT_ID.toString());
		requestWithValidAuthzCode.setClientSecret(ClientApp.DEFAULT.getSecret());
		requestWithValidAuthzCode.setGrantType(GrantType.AUTHORIZATION_CODE.toString());
		requestWithValidAuthzCode.setRedirectUri(VALID_REDIRECT_URI);
		requestWithValidAuthzCode.setCode(validAuthzCode);
		
		//when
		Response response = testedResource.authorize(requestWithValidAuthzCode);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
		assertThat(response.getEntity()).isNotNull();
	}
	
	@Test
	public void authorize_should_handle_valid_password_request() throws OAuthSystemException {
		//given
		User user = TestHelper.generateRandomUser();
		String password = "password";
		SecuredUser securedUser = SecuredUser.Builder.createFrom(user)
				.withSalt("salt")
				.withRawPassword(password)
				.build();
			
		when(mockedUserRepo.getByEmail(securedUser.getEmail())).thenReturn(securedUser);
		when(mockedUserRepo.hashPassword(password, "salt")).thenReturn(securedUser.getPassword());
		OAuthTokenRequest requestWithValidPassword = new OAuthTokenRequest();
		requestWithValidPassword.setClientId(ClientApp.DEFAULT_ID.toString());
		//FIXME apche otlu bug => client secret should not be required for passw ord flow
		requestWithValidPassword.setClientSecret(ClientApp.DEFAULT.getSecret());
		requestWithValidPassword.setGrantType(GrantType.PASSWORD.toString());
		requestWithValidPassword.setUserName(securedUser.getEmail());
		requestWithValidPassword.setPassword(password);
		
		//when
		Response response = testedResource.authorize(requestWithValidPassword);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
		assertThat(response.getEntity()).isNotNull();
	}
}
