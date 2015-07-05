package yoan.shopping.authentication.realm;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static yoan.shopping.infra.rest.error.Level.INFO;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.test.TestHelper.assertWebApiException;

import java.util.UUID;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.test.TestHelper;
import yoan.shopping.user.SecuredUser;
import yoan.shopping.user.User;
import yoan.shopping.user.repository.SecuredUserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserRealmTest {

	@Mock
	CacheManager mockedCacheManager;
	@Mock
	HashedCredentialsMatcher mockedCredentialsMatcher;
	@Mock
	SecuredUserRepository mockedUserRepository;
	@InjectMocks
	UserRealm testedRealm;
	
	@Test
	public void doGetAuthenticationInfo_should_return_infos_with_existing_user() {
		//given
		SecuredUser existingUser = TestHelper.generateRandomSecuredUser();
		User expectedPrincipal = User.Builder.createFrom(existingUser).build();
		when(mockedUserRepository.getById(existingUser.getId())).thenReturn(existingUser);
		UsernamePasswordToken userToken = new UsernamePasswordToken(existingUser.getId().toString(), "password");
		
		//when
		AuthenticationInfo result = testedRealm.doGetAuthenticationInfo(userToken);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.getPrincipals().getPrimaryPrincipal()).isEqualTo(expectedPrincipal);
	}
	
	@Test
	public void doGetAuthenticationInfo_should_return_null_with_not_existing_user() {
		//given
		UsernamePasswordToken userToken = new UsernamePasswordToken(UUID.randomUUID().toString(), "password");
		
		//when
		AuthenticationInfo result = testedRealm.doGetAuthenticationInfo(userToken);
		
		//then
		assertThat(result).isNull();
	}
	
	@Test(expected = WebApiException.class)
	public void doGetAuthenticationInfo_should_fail_with_invalid_userName_in_token() {
		//given
		UsernamePasswordToken userToken = new UsernamePasswordToken("invalid user name", "password");
		
		//when
		try {
			testedRealm.doGetAuthenticationInfo(userToken);
		} catch(WebApiException wae) {
		//then
			assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, "Invalid Param named user id : invalid user name");
			throw wae;
		}
		
	}
}