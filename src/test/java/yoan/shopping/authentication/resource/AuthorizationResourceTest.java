package yoan.shopping.authentication.resource;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FOUND;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static yoan.shopping.authentication.resource.OAuthResourceErrorMessage.MISSING_REDIRECT_URI;
import static yoan.shopping.infra.rest.error.Level.WARNING;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import yoan.shopping.authentication.repository.OAuth2AccessTokenRepository;
import yoan.shopping.authentication.repository.OAuth2AuthorizationCodeRepository;
import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.test.OauthMockRequestBuilder;
import yoan.shopping.test.TestHelper;
import yoan.shopping.user.User;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationResourceTest {
	@Mock
	OAuth2AuthorizationCodeRepository mockedAuthorizationCodeRepo;
	@Mock
	OAuth2AccessTokenRepository mockedAccessTokenRepo;
	
	private static final String VALID_REDIRECT_URI = "http://www.google.fr";
	
	private AuthorizationResource getAuthorizationResource(User connectedUser) {
		AuthorizationResource testedResource = new AuthorizationResource(connectedUser, mockedAuthorizationCodeRepo, mockedAccessTokenRepo);
		return spy(testedResource);
	}
	
	@Test(expected = WebApiException.class)
	public void authorize_should_handle_empty_authz_request() throws OAuthSystemException {
		//given
		AuthorizationResource testedResource = getAuthorizationResource(TestHelper.generateRandomUser());
		HttpServletRequest invalidRequest = new OauthMockRequestBuilder().build();
		
		//when
		try {
			testedResource.authorize(invalidRequest);
		} catch(WebApiException wae) {
		//then
			verify(testedResource, never()).handleOauthRequest(any(), any());
			TestHelper.assertWebApiException(wae, BAD_REQUEST, WARNING, API_RESPONSE, MISSING_REDIRECT_URI);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void authorize_should_handle_authz_request_with_invalid_redirect_URI() throws OAuthSystemException {
		//given
		AuthorizationResource testedResource = getAuthorizationResource(TestHelper.generateRandomUser());
		String invalidRedirectURI = "http:/bad  ";
		HttpServletRequest invalidRequest = new OauthMockRequestBuilder().withRedirectUri(invalidRedirectURI).build();
		
		//when
		try {
			testedResource.authorize(invalidRequest);
		} catch(WebApiException wae) {
		//then
			verify(testedResource, never()).handleOauthRequest(any(), any());
			TestHelper.assertWebApiException(wae, BAD_REQUEST, WARNING, API_RESPONSE, OAuthResourceErrorMessage.INVALID_REDIRECT_URI.getDevReadableMessage(invalidRedirectURI));
			throw wae;
		}
	}
	
	@Test
	public void authorize_should_handle_invalid_authz_request() throws OAuthSystemException {
		//given
		AuthorizationResource testedResource = getAuthorizationResource(TestHelper.generateRandomUser());
		HttpServletRequest invalidRequest = new OauthMockRequestBuilder().withRedirectUri(VALID_REDIRECT_URI).build();
		
		//when
		Response response = testedResource.authorize(invalidRequest);
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(FOUND.getStatusCode());
		assertThat(response.getLocation().toString()).startsWith(VALID_REDIRECT_URI);
		
		verify(testedResource, never()).handleOauthRequest(any(), any());
	}
	
	@Test
	public void authorize_should_work_with_auth_code_request() throws OAuthSystemException {
		//given
		AuthorizationResource testedResource = getAuthorizationResource(TestHelper.generateRandomUser());
		HttpServletRequest authCodeRequest = new OauthMockRequestBuilder()
			.withRedirectUri(VALID_REDIRECT_URI)
			.withHttpMethod(OAuth.HttpMethod.GET)
			.withClientId("0")
			.withOauthResponseType(ResponseType.CODE.toString())
			.build();
		
		//when
		Response response = testedResource.authorize(authCodeRequest);
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(FOUND.getStatusCode());
		assertThat(response.getLocation().toString()).startsWith(VALID_REDIRECT_URI);
		
		verify(testedResource).generateAuthorizationCode(any());
	}
	
	@Test
	public void authorize_should_work_with_token_request() throws OAuthSystemException {
		//given
		AuthorizationResource testedResource = getAuthorizationResource(TestHelper.generateRandomUser());
		HttpServletRequest tokenRequest = new OauthMockRequestBuilder()
			.withRedirectUri(VALID_REDIRECT_URI)
			.withHttpMethod(OAuth.HttpMethod.GET)
			.withClientId("0")
			.withOauthResponseType(ResponseType.TOKEN.toString())
			.build();
		
		//when
		Response response = testedResource.authorize(tokenRequest);
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(FOUND.getStatusCode());
		assertThat(response.getLocation().toString()).startsWith(VALID_REDIRECT_URI);
		
		verify(testedResource).generateAccessToken(any());
	}
}
