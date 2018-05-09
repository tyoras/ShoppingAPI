package io.tyoras.shopping.authentication.realm;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.tyoras.shopping.authentication.repository.OAuth2AccessTokenRepository;
import io.tyoras.shopping.user.User;
import io.tyoras.shopping.user.repository.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class OAuthAuthenticator implements Authenticator<String, User> {

    private final OAuth2AccessTokenRepository accessTokenRepository;
    private final UserRepository userRepository;


    @Inject
    public OAuthAuthenticator(OAuth2AccessTokenRepository accessTokenRepository, UserRepository userRepository) {
        this.accessTokenRepository = accessTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> authenticate(String accessToken) throws AuthenticationException {
        Optional<UUID> userId = extractUserIdFromAccessToken(accessToken);
        return userId.flatMap(id -> Optional.ofNullable(userRepository.getById(id)));
    }

    private Optional<UUID> extractUserIdFromAccessToken(String token) {
        return Optional.ofNullable(accessTokenRepository.getUserIdByAccessToken(token));
    }
}
