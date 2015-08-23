package yoan.shopping.authentication.realm;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import yoan.shopping.authentication.Oauth2AccessToken;
import yoan.shopping.authentication.repository.OAuth2AccessTokenRepository;
import yoan.shopping.test.TestHelper;
import yoan.shopping.user.SecuredUser;
import yoan.shopping.user.User;
import yoan.shopping.user.repository.SecuredUserRepository;

@RunWith(MockitoJUnitRunner.class)
public class OAuth2AccessTokenRealmTest {
	
	@Mock
	CacheManager mockedCacheManager;
	@Mock
	HashedCredentialsMatcher mockedCredentialsMatcher;
	@Mock
	SecuredUserRepository mockedUserRepository;
	@Mock
	OAuth2AccessTokenRepository mockedAccessTokenRepository;
	@InjectMocks
	OAuth2AccessTokenRealm testedRealm;
	
	@Test
	public void doGetAuthenticationInfo_should_return_infos_with_valid_access_token() {
		//given
		SecuredUser existingUser = TestHelper.generateRandomSecuredUser();
		User expectedPrincipal = User.Builder.createFrom(existingUser).build();
		when(mockedUserRepository.getById(existingUser.getId())).thenReturn(existingUser);
		String validAccessToken = UUID.randomUUID().toString();
		when(mockedAccessTokenRepository.getUserIdByAccessToken(validAccessToken)).thenReturn(existingUser.getId());
		Oauth2AccessToken accessToken = new Oauth2AccessToken(validAccessToken, "host");
		
		//when
		AuthenticationInfo result = testedRealm.doGetAuthenticationInfo(accessToken);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.getPrincipals().getPrimaryPrincipal()).isEqualTo(expectedPrincipal);
	}
	
	@Test
	public void doGetAuthenticationInfo_should_return_null_with_invalid_access_token() {
		//given
		Oauth2AccessToken accessToken = new Oauth2AccessToken("invalid token", "host");
		
		//when
		AuthenticationInfo result = testedRealm.doGetAuthenticationInfo(accessToken);
		
		//then
		assertThat(result).isNull();
	}
}