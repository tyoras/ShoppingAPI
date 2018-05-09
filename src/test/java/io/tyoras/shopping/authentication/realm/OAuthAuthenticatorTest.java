package io.tyoras.shopping.authentication.realm;

import io.dropwizard.auth.AuthenticationException;
import io.tyoras.shopping.authentication.repository.OAuth2AccessTokenRepository;
import io.tyoras.shopping.test.TestHelper;
import io.tyoras.shopping.user.User;
import io.tyoras.shopping.user.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OAuthAuthenticatorTest {

    private static final User KNOWN_USER = TestHelper.generateRandomUser();
    @Mock
    UserRepository mockedUserRepository;
    @Mock
    OAuth2AccessTokenRepository mockedTokenRepository;
    @InjectMocks
    OAuthAuthenticator testedAuthenticator;

    @Test
    public void authenticate_should_return_empty_with_invalid_token() throws AuthenticationException {
        //given
        String invalidToken = "invalid_token";

        //when
        Optional<User> result = testedAuthenticator.authenticate(invalidToken);

        //then
        assertThat(result).isNotPresent();
    }

    @Test
    public void authenticate_should_return_empty_with_token_for_unknown_user() throws AuthenticationException {
        //given
        String unknownUserIdToken = "token_for_unknown_user_id";
        when(mockedTokenRepository.getUserIdByAccessToken(unknownUserIdToken)).thenReturn(UUID.randomUUID());

        //when
        Optional<User> result = testedAuthenticator.authenticate(unknownUserIdToken);

        //then
        assertThat(result).isNotPresent();
    }

    @Test
    public void authenticate_should_return_user_with_valid_token() throws AuthenticationException {
        //given
        String validToken = "invalid_token";
        when(mockedTokenRepository.getUserIdByAccessToken(validToken)).thenReturn(KNOWN_USER.getId());
        when(mockedUserRepository.getById(KNOWN_USER.getId())).thenReturn(KNOWN_USER);

        //when
        Optional<User> result = testedAuthenticator.authenticate(validToken);

        //then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(KNOWN_USER);
    }


}
