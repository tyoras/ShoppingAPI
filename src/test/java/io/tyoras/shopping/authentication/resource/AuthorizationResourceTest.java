package io.tyoras.shopping.authentication.resource;

import io.tyoras.shopping.authentication.realm.BasicUserPrincipal;
import io.tyoras.shopping.authentication.repository.OAuth2AccessTokenRepository;
import io.tyoras.shopping.authentication.repository.OAuth2AuthorizationCodeRepository;
import io.tyoras.shopping.client.app.ClientApp;
import io.tyoras.shopping.client.app.repository.ClientAppRepository;
import io.tyoras.shopping.infra.rest.error.WebApiException;
import io.tyoras.shopping.test.OauthMockRequestBuilder;
import io.tyoras.shopping.test.TestHelper;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.UUID;

import static io.tyoras.shopping.authentication.resource.OAuthResourceErrorMessage.MISSING_REDIRECT_URI;
import static io.tyoras.shopping.authentication.resource.OAuthResourceErrorMessage.UNKNOWN_CLIENT;
import static io.tyoras.shopping.infra.rest.error.Level.INFO;
import static io.tyoras.shopping.infra.rest.error.Level.WARNING;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationResourceTest {
    private static final String VALID_REDIRECT_URI = ClientApp.DEFAULT_REDIRECT_URI.toString();
    private static final BasicUserPrincipal BASIC_USER_PRINCIPAL = new BasicUserPrincipal(TestHelper.generateRandomUser());
    @Mock
    OAuth2AuthorizationCodeRepository mockedAuthorizationCodeRepo;
    @Mock
    OAuth2AccessTokenRepository mockedAccessTokenRepo;
    @Mock
    ClientAppRepository mockedClientAppRepo;
    @Spy
    @InjectMocks
    private AuthorizationResource testedResource;

    @Before
    public void before() {
        when(mockedClientAppRepo.getById(ClientApp.DEFAULT_ID)).thenReturn(ClientApp.DEFAULT);
    }

    @Test(expected = WebApiException.class)
    public void authorize_should_handle_empty_authz_request() throws OAuthSystemException {
        //given
        HttpServletRequest invalidRequest = new OauthMockRequestBuilder().build();

        //when
        try {
            testedResource.authorize(BASIC_USER_PRINCIPAL, invalidRequest);
        } catch (WebApiException wae) {
            //then
            verify(testedResource, never()).handleOauthRequest(any(), any(), any());
            TestHelper.assertWebApiException(wae, BAD_REQUEST, WARNING, API_RESPONSE, MISSING_REDIRECT_URI);
            throw wae;
        }
    }

    @Test(expected = WebApiException.class)
    public void authorize_should_handle_authz_request_with_invalid_redirect_URI() throws OAuthSystemException {
        //given
        String invalidRedirectURI = "http:/bad  ";
        HttpServletRequest invalidRequest = new OauthMockRequestBuilder().withRedirectUri(invalidRedirectURI).build();

        //when
        try {
            testedResource.authorize(BASIC_USER_PRINCIPAL, invalidRequest);
        } catch (WebApiException wae) {
            //then
            verify(testedResource, never()).handleOauthRequest(any(), any(), any());
            TestHelper.assertWebApiException(wae, BAD_REQUEST, WARNING, API_RESPONSE, OAuthResourceErrorMessage.INVALID_REDIRECT_URI.getDevReadableMessage(invalidRedirectURI));
            throw wae;
        }
    }

    @Test
    public void authorize_should_handle_invalid_authz_request() throws OAuthSystemException {
        //given
        HttpServletRequest invalidRequest = new OauthMockRequestBuilder().withRedirectUri(VALID_REDIRECT_URI).build();

        //when
        Response response = testedResource.authorize(BASIC_USER_PRINCIPAL, invalidRequest);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(FOUND.getStatusCode());
        assertThat(response.getLocation().toString()).startsWith(VALID_REDIRECT_URI);

        verify(testedResource, never()).handleOauthRequest(any(), any(), any());
    }

    @Test
    public void authorize_should_work_with_auth_code_request() throws OAuthSystemException {
        //given
        HttpServletRequest authCodeRequest = new OauthMockRequestBuilder()
                .withRedirectUri(VALID_REDIRECT_URI)
                .withHttpMethod(OAuth.HttpMethod.GET)
                .withClientId(ClientApp.DEFAULT_ID.toString())
                .withOauthResponseType(ResponseType.CODE.toString())
                .build();

        //when
        Response response = testedResource.authorize(BASIC_USER_PRINCIPAL, authCodeRequest);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(FOUND.getStatusCode());
        assertThat(response.getLocation().toString()).startsWith(VALID_REDIRECT_URI);

        verify(testedResource).generateAuthorizationCode(eq(BASIC_USER_PRINCIPAL));
    }

    @Test
    public void authorize_should_work_with_token_request() throws OAuthSystemException {
        //given
        HttpServletRequest tokenRequest = new OauthMockRequestBuilder()
                .withRedirectUri(VALID_REDIRECT_URI)
                .withHttpMethod(OAuth.HttpMethod.GET)
                .withClientId(ClientApp.DEFAULT_ID.toString())
                .withOauthResponseType(ResponseType.TOKEN.toString())
                .build();

        //when
        Response response = testedResource.authorize(BASIC_USER_PRINCIPAL, tokenRequest);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(FOUND.getStatusCode());
        assertThat(response.getLocation().toString()).startsWith(VALID_REDIRECT_URI);

        verify(testedResource).generateAccessToken(eq(BASIC_USER_PRINCIPAL));
    }

    @Test(expected = WebApiException.class)
    public void authorize_should_return_bad_request_when_client_id_is_not_a_valid_UUID() throws OAuthSystemException {
        //given
        String invalidClientId = "not uuid";
        String expectedMessage = "Invalid Param named client_id : " + invalidClientId;
        HttpServletRequest invalidRequest = new OauthMockRequestBuilder()
                .withRedirectUri(VALID_REDIRECT_URI)
                .withHttpMethod(OAuth.HttpMethod.GET)
                .withClientId(invalidClientId)
                .withOauthResponseType(ResponseType.TOKEN.toString())
                .build();

        //when
        try {
            testedResource.authorize(BASIC_USER_PRINCIPAL, invalidRequest);
        } catch (WebApiException wae) {
            //then
            verify(testedResource, never()).generateAccessToken(eq(BASIC_USER_PRINCIPAL));
            TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
            throw wae;
        }
    }

    @Test(expected = WebApiException.class)
    public void authorize_should_return_bad_request_when_client_id_is_unknown() throws OAuthSystemException {
        //given
        String unknownClientId = UUID.randomUUID().toString();
        String expectedMessage = UNKNOWN_CLIENT.getDevReadableMessage(unknownClientId);
        HttpServletRequest invalidRequest = new OauthMockRequestBuilder()
                .withRedirectUri(VALID_REDIRECT_URI)
                .withHttpMethod(OAuth.HttpMethod.GET)
                .withClientId(unknownClientId)
                .withOauthResponseType(ResponseType.TOKEN.toString())
                .build();

        //when
        try {
            testedResource.authorize(BASIC_USER_PRINCIPAL, invalidRequest);
        } catch (WebApiException wae) {
            //then
            verify(testedResource, never()).generateAccessToken(eq(BASIC_USER_PRINCIPAL));
            TestHelper.assertWebApiException(wae, BAD_REQUEST, WARNING, API_RESPONSE, expectedMessage);
            throw wae;
        }
    }
}
