/**
 * 
 */
package yoan.shopping.authentication.realm;

import static java.util.Objects.requireNonNull;
import static yoan.shopping.infra.config.guice.ShiroSecurityModule.SHA1;

import java.util.UUID;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.crypto.hash.Sha1Hash;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.util.SimpleByteSource;

import yoan.shopping.user.SecuredUser;
import yoan.shopping.user.repository.SecuredUserRepository;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 *
 * @author yoan
 */
public class UserRealm extends AuthenticatingRealm {
	
	private final SecuredUserRepository userRepository;
	
	
	@Inject
	public UserRealm(CacheManager cacheManager, @Named(SHA1) HashedCredentialsMatcher credentialsMatcher, SecuredUserRepository userRepository) {
		super(requireNonNull(cacheManager), requireNonNull(credentialsMatcher));
		setAuthenticationTokenClass(UsernamePasswordToken.class);
		this.userRepository = requireNonNull(userRepository);
	}
	
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken userToken = (UsernamePasswordToken) token;
		SecuredUser foundUser = userRepository.getById(UUID.fromString(userToken.getUsername()));
		if (foundUser == null) {
			return null;
		}
		Sha1Hash hashedPassword = Sha1Hash.fromBase64String(foundUser.getPassword());
		SimpleAuthenticationInfo saltedCredentials = new SimpleAuthenticationInfo(foundUser.getId().toString(), hashedPassword, getName());
		saltedCredentials.setCredentialsSalt(new SimpleByteSource(foundUser.getSalt().toString()));
		return saltedCredentials;
	}
	
}
