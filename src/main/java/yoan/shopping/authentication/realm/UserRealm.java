/**
 * 
 */
package yoan.shopping.authentication.realm;

import static java.util.Objects.requireNonNull;
import static yoan.shopping.infra.config.guice.ShiroSecurityModule.SHA256;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.util.SimpleByteSource;

import yoan.shopping.user.SecuredUser;
import yoan.shopping.user.User;
import yoan.shopping.user.repository.SecuredUserRepository;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Authentication realm using the application credentials
 * @author yoan
 */
public class UserRealm extends AuthenticatingRealm {
	
	private final SecuredUserRepository userRepository;
	
	@Inject
	public UserRealm(CacheManager cacheManager, @Named(SHA256) HashedCredentialsMatcher credentialsMatcher, SecuredUserRepository userRepository) {
		super(requireNonNull(cacheManager), requireNonNull(credentialsMatcher));
		setAuthenticationTokenClass(UsernamePasswordToken.class);
		this.userRepository = requireNonNull(userRepository);
	}
	
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken userToken = (UsernamePasswordToken) token;
		String userEmail = extractUserEmailFromToken(userToken);
		SecuredUser foundUser = userRepository.getByEmail(userEmail);
		if (foundUser == null) {
			return null;
		}
		Sha256Hash hashedPassword = extractHashedPasswordFromUser(foundUser);
		SimpleByteSource salt = extractSaltFromUser(foundUser);
		SimpleAuthenticationInfo saltedCredentials = generateSaltedCredentials(foundUser, hashedPassword, salt);
		return saltedCredentials;
	}
	
	private String extractUserEmailFromToken(UsernamePasswordToken userToken) {
		return userToken.getUsername();
	}
	
	private Sha256Hash extractHashedPasswordFromUser(SecuredUser user) {
		String base64EncodedHashedpassword = user.getPassword();
		return Sha256Hash.fromBase64String(base64EncodedHashedpassword);
	}
	
	private SimpleByteSource extractSaltFromUser(SecuredUser user) {
		String stringifiedSalt = user.getSalt().toString();
		return new SimpleByteSource(stringifiedSalt);
	}
	
	private SimpleAuthenticationInfo generateSaltedCredentials(SecuredUser foundUser, Sha256Hash hashedPassword, SimpleByteSource salt) {
		User user = User.Builder.createFrom(foundUser).build();
		
		SimpleAuthenticationInfo saltedCredentials = new SimpleAuthenticationInfo(user, hashedPassword, getName());
		saltedCredentials.setCredentialsSalt(salt);
		return saltedCredentials;
	}
}
