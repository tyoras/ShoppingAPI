package io.tyoras.shopping.authentication.realm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;
import io.tyoras.shopping.authentication.realm.BasicUserAuthenticator;
import io.tyoras.shopping.authentication.realm.BasicUserPrincipal;
import io.tyoras.shopping.test.TestHelper;
import io.tyoras.shopping.user.SecuredUser;
import io.tyoras.shopping.user.repository.SecuredUserRepository;

@RunWith(MockitoJUnitRunner.class)
public class BasicUserAuthenticatorTest {

	@Mock
	SecuredUserRepository mockedSecuredUserRepository;
	
	@InjectMocks
	BasicUserAuthenticator testedAuthenticator;
	
	private static final SecuredUser KNOWN_USER = TestHelper.generateRandomSecuredUser();
	
	@Test
	public void authenticate_should_return_empty_with_unknown_user() throws AuthenticationException {
		//given
		BasicCredentials unknownUserCredentials = new BasicCredentials("unknown@unknown.com", "password");
		
		//when
		Optional<BasicUserPrincipal> result = testedAuthenticator.authenticate(unknownUserCredentials);
		
		//then
		assertThat(result).isNotPresent();
	}
	
	@Test
	public void authenticate_should_return_empty_with_invalid_credentials() throws AuthenticationException {
		//given
		BasicCredentials unknownUserCredentials = new BasicCredentials(KNOWN_USER.getEmail(), "invalid_password");
		when(mockedSecuredUserRepository.getByEmail(KNOWN_USER.getEmail())).thenReturn(KNOWN_USER);
		when(mockedSecuredUserRepository.hashPassword(anyString(), any())).thenReturn("invalid_hash");
		
		//when
		Optional<BasicUserPrincipal> result = testedAuthenticator.authenticate(unknownUserCredentials);
		
		//then
		assertThat(result).isNotPresent();
	}
	
	@Test
	public void authenticate_should_return_user_with_valid_credentials() throws AuthenticationException {
		//given
		BasicCredentials unknownUserCredentials = new BasicCredentials(KNOWN_USER.getEmail(), KNOWN_USER.getPassword());
		when(mockedSecuredUserRepository.getByEmail(KNOWN_USER.getEmail())).thenReturn(KNOWN_USER);
		when(mockedSecuredUserRepository.hashPassword(anyString(), any())).thenReturn(KNOWN_USER.getPassword());
		
		//when
		Optional<BasicUserPrincipal> result = testedAuthenticator.authenticate(unknownUserCredentials);
		
		//then
		assertThat(result).isPresent();
		BasicUserPrincipal foundPrincipal = result.get();
		assertThat(foundPrincipal.getName()).isEqualTo(KNOWN_USER.getId().toString());
		assertThat(foundPrincipal.getUserId()).isEqualTo(KNOWN_USER.getId());
	}
}
