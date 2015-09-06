package yoan.shopping.authentication.realm;

import static java.util.Objects.requireNonNull;

import java.util.UUID;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthenticatingRealm;

import com.google.inject.Inject;

import yoan.shopping.authentication.Oauth2AccessToken;
import yoan.shopping.authentication.repository.OAuth2AccessTokenRepository;
import yoan.shopping.user.SecuredUser;
import yoan.shopping.user.User;
import yoan.shopping.user.repository.SecuredUserRepository;

/**
 * Authentication realm using the OAuth2 access token 
 * @author yoan
 */
public class OAuth2AccessTokenRealm extends AuthenticatingRealm {
	
	private final OAuth2AccessTokenRepository accessTokenRepository;
	private final SecuredUserRepository userRepository;
	
	@Inject
	public OAuth2AccessTokenRealm(CacheManager cacheManager, OAuth2AccessTokenRepository accessTokenRepository, SecuredUserRepository userRepository) {
		super(requireNonNull(cacheManager), new SimpleCredentialsMatcher());
		setAuthenticationTokenClass(Oauth2AccessToken.class);
		this.accessTokenRepository = requireNonNull(accessTokenRepository);
		this.userRepository = requireNonNull(userRepository);
	}
	
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		Oauth2AccessToken accessToken = (Oauth2AccessToken) token;
		UUID userId = extractUserIdFromAccessToken(accessToken);
		
		SecuredUser foundUser = userRepository.getById(userId);
		if (foundUser == null) {
			return null;
		}
		
		User user = User.Builder.createFrom(foundUser).build();
		return new SimpleAuthenticationInfo(user, accessToken.getAccessToken(), getName());
	}

	private UUID extractUserIdFromAccessToken(Oauth2AccessToken accessToken) {
		String token = accessToken.getAccessToken();
		return accessTokenRepository.getUserIdByAccessToken(token);
	}
}