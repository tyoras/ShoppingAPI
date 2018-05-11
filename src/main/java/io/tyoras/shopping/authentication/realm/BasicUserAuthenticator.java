package io.tyoras.shopping.authentication.realm;

import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import io.tyoras.shopping.user.SecuredUser;
import io.tyoras.shopping.user.repository.SecuredUserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class BasicUserAuthenticator implements Authenticator<BasicCredentials, BasicUserPrincipal> {

    private final SecuredUserRepository userRepository;

    @Inject
    public BasicUserAuthenticator(SecuredUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<BasicUserPrincipal> authenticate(BasicCredentials credentials) {
        String userEmail = extractUserEmailFromCredentials(credentials);
        SecuredUser foundUser = userRepository.getByEmail(userEmail);
        if (foundUser == null) {
            return Optional.empty();
        }
        String hashedPassword = userRepository.hashPassword(credentials.getPassword(), foundUser.getSalt());
        return Optional.ofNullable(foundUser.getPassword().equals(hashedPassword) ? new BasicUserPrincipal(foundUser) : null);
    }


    private String extractUserEmailFromCredentials(BasicCredentials credentials) {
        return credentials.getUsername();
    }

}
