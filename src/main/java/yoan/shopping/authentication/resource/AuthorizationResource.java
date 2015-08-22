package yoan.shopping.authentication.resource;

import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FOUND;
import static yoan.shopping.authentication.resource.OAuthResourceErrorMessage.INVALID_REDIRECT_URI;
import static yoan.shopping.authentication.resource.OAuthResourceErrorMessage.MISSING_REDIRECT_URI;
import static yoan.shopping.infra.config.guice.ShoppingWebModule.CONNECTED_USER;
import static yoan.shopping.infra.rest.error.Level.WARNING;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ResponseType;

import com.google.inject.name.Named;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import yoan.shopping.authentication.repository.OAuth2AccessTokenRepository;
import yoan.shopping.authentication.repository.OAuth2AuthorizationCodeRepository;
import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.user.User;

/**
 * API to generate OAuth2 authorization code
 * @author yoan
 */
@Path("/auth/authorization")
@Api(value = "/auth/authorization", description = "OAuth2 Authorization endpoint")
public class AuthorizationResource {
	
	/** Currently connected user */
	private final User authenticatedUser;
	private final OAuth2AuthorizationCodeRepository authzCodeRepository;
	private final OAuth2AccessTokenRepository accessTokenRepository;
	
	@Inject
	public AuthorizationResource(@Named(CONNECTED_USER) User authenticatedUser, OAuth2AuthorizationCodeRepository authzCodeRepository, OAuth2AccessTokenRepository accessTokenRepository) {
		this.authenticatedUser = requireNonNull(authenticatedUser);
		this.authzCodeRepository = requireNonNull(authzCodeRepository);
		this.accessTokenRepository = requireNonNull(accessTokenRepository);
	}
	
	@GET
	@ApiOperation(value = "Get Oauth2 authorization", notes = "This will can only be done by an authenticated client")
	@ApiResponses(value = { @ApiResponse(code = 302, message = "Redirection to provided redirect_uri"), @ApiResponse(code = 401, message = "Not authenticated") })
    public Response authorize(@Context HttpServletRequest request) throws OAuthSystemException {
        try {
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
            final OAuthResponse response = handleOauthRequest(request, oauthRequest);
            URI uri = URI.create(response.getLocationUri());
            return Response.status(response.getResponseStatus()).location(uri).build();
        } catch (OAuthProblemException problem) {
            return handleOAuthProblem(problem);
        }
    }

	protected OAuthResponse handleOauthRequest(HttpServletRequest request, OAuthAuthzRequest oauthRequest) throws OAuthSystemException {
		OAuthIssuer oauthIssuer = new OAuthIssuerImpl(new MD5Generator());
		//TODO check if client id is valid
		
		//build response according to response_type
		OAuthASResponse.OAuthAuthorizationResponseBuilder oAuthResponseBuilder = OAuthASResponse.authorizationResponse(request, FOUND.getStatusCode());
		ResponseType responseType = extractResponseType(oauthRequest);
		switch(responseType) {
		    case CODE : 
		    	String authorizationCode = generateAuthorizationCode(oauthIssuer);
		        oAuthResponseBuilder.setCode(authorizationCode);
		        break;
		    case TOKEN :
		    	String accessToken = generateAccessToken(oauthIssuer);
		        oAuthResponseBuilder.setAccessToken(accessToken);
		        oAuthResponseBuilder.setExpiresIn(3600l);
		        break;
		    default :
		    	break;
		}

		String redirectURI = oauthRequest.getRedirectURI();
		ensureValidRedirectURI(redirectURI);
		oAuthResponseBuilder.location(redirectURI);
		
		return oAuthResponseBuilder.buildQueryMessage();
	}

	private static ResponseType extractResponseType(OAuthAuthzRequest oauthRequest) {
		String responseTypeParam = oauthRequest.getResponseType();
        return ResponseType.valueOf(responseTypeParam.toUpperCase());
	}
	
	protected String generateAuthorizationCode(OAuthIssuer oauthIssuer) throws OAuthSystemException {
		String authorizationCode = oauthIssuer.authorizationCode();
		authzCodeRepository.insert(authorizationCode, authenticatedUser.getId());
		return authorizationCode;
	}
	
	protected String generateAccessToken(OAuthIssuer oauthIssuer) throws OAuthSystemException {
		String accessToken = oauthIssuer.accessToken();
		accessTokenRepository.insertAccessToken(accessToken, authenticatedUser.getId());
		return accessToken;
	}
	
	private Response handleOAuthProblem(OAuthProblemException problem) throws OAuthSystemException {
		final Response.ResponseBuilder responseBuilder = Response.status(FOUND);
        String redirectUri = problem.getRedirectUri();
        ensureValidRedirectURI(redirectUri);
        
        final OAuthResponse response = OAuthASResponse.errorResponse(FOUND.getStatusCode()).error(problem).location(redirectUri).buildQueryMessage();
        final URI location = URI.create(response.getLocationUri());
        return responseBuilder.location(location).build();
	}
	
	private void ensureValidRedirectURI(String redirectURI) {
		if (StringUtils.isBlank(redirectURI)) {
			throw new WebApiException(BAD_REQUEST, WARNING, API_RESPONSE, MISSING_REDIRECT_URI);
        }
		
		try {
			new URI(redirectURI);
		} catch(URISyntaxException e) {
			throw new WebApiException(BAD_REQUEST, WARNING, API_RESPONSE, INVALID_REDIRECT_URI.getDevReadableMessage(redirectURI), e);
		}
	}
}