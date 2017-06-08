package yoan.shopping.authentication;

import static org.apache.oltu.oauth2.common.OAuth.OAUTH_CLIENT_ID;
import static org.apache.oltu.oauth2.common.OAuth.OAUTH_CLIENT_SECRET;
import static org.apache.oltu.oauth2.common.OAuth.OAUTH_CODE;
import static org.apache.oltu.oauth2.common.OAuth.OAUTH_GRANT_TYPE;
import static org.apache.oltu.oauth2.common.OAuth.OAUTH_PASSWORD;
import static org.apache.oltu.oauth2.common.OAuth.OAUTH_REDIRECT_URI;
import static org.apache.oltu.oauth2.common.OAuth.OAUTH_USERNAME;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.FormParam;

import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;

public class OAuthTokenRequest {

	@FormParam(OAUTH_GRANT_TYPE)
	private String grantType;
	
	@FormParam(OAUTH_USERNAME)
	private String userName;
	
	@FormParam(OAUTH_PASSWORD)
	private String password;
	
	@FormParam(OAUTH_CLIENT_ID)
	private String clientId;
	
	@FormParam(OAUTH_CLIENT_SECRET)
	private String clientSecret;
	
	@FormParam(OAUTH_CODE)
	private String code;
	
	@FormParam(OAUTH_REDIRECT_URI)
	private String redirectUri;

	
	public String getGrantType() {
		return grantType;
	}

	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public void ensureGrantType() throws OAuthProblemException {
		final String requestTypeValue = getGrantType();
	    if (OAuthUtils.isEmpty(requestTypeValue)) {
	      throw OAuthUtils.handleOAuthProblemException("Missing grant_type parameter value");
	    }
	    try {
	    	GrantType.valueOf(requestTypeValue.toUpperCase());
	    } catch(IllegalArgumentException e) {
	      throw OAuthUtils.handleOAuthProblemException("Invalid grant_type parameter value");
	    }
	}
	
	public void validateCodeRequiredParameters() throws OAuthProblemException {
        final Set<String> missingParameters = new HashSet<String>();
        if (OAuthUtils.isEmpty(getCode())) {
            missingParameters.add(OAuth.OAUTH_CODE);
        }
        if (OAuthUtils.isEmpty(getRedirectUri())) {
            missingParameters.add(OAuth.OAUTH_REDIRECT_URI);
        }
        if (!missingParameters.isEmpty()) {
            throw OAuthUtils.handleMissingParameters(missingParameters);
        }
    }
	
	public void validatePasswordRequiredParameters() throws OAuthProblemException {
        final Set<String> missingParameters = new HashSet<String>();
        if (OAuthUtils.isEmpty(getUserName())) {
            missingParameters.add(OAuth.OAUTH_USERNAME);
        }
        if (OAuthUtils.isEmpty(getPassword())) {
            missingParameters.add(OAuth.OAUTH_PASSWORD);
        }
        if (!missingParameters.isEmpty()) {
            throw OAuthUtils.handleMissingParameters(missingParameters);
        }
    }
	
	public void validateClientAuthenticationCredentials() throws OAuthProblemException {
        Set<String> missingParameters = new HashSet<String>();
        if (OAuthUtils.isEmpty(getClientId())) {
            missingParameters.add(OAuth.OAUTH_CLIENT_ID);
        }
        if (OAuthUtils.isEmpty(getClientSecret())) {
            missingParameters.add(OAuth.OAUTH_CLIENT_SECRET);
        }
        if (!missingParameters.isEmpty()) {
            throw OAuthUtils.handleMissingParameters(missingParameters);
        }
    }
}
