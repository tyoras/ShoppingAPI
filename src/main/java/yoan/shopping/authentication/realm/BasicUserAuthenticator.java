package yoan.shopping.authentication.realm;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import yoan.shopping.user.SecuredUser;
import yoan.shopping.user.repository.SecuredUserRepository;

@Singleton
public class BasicUserAuthenticator implements Authenticator<BasicCredentials, BasicUserPrincipal> {
	
	private final SecuredUserRepository userRepository;
	
	@Inject
	public BasicUserAuthenticator(SecuredUserRepository userRepository) {
		this.userRepository = userRepository;
	}

    @Override
    public Optional<BasicUserPrincipal> authenticate(BasicCredentials credentials) throws AuthenticationException {
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
