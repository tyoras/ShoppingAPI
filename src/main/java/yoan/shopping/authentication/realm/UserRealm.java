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
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthenticatingRealm;

import yoan.shopping.infra.config.guice.ShiroSecurityModule;
import yoan.shopping.user.User;
import yoan.shopping.user.repository.UserRepository;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 *
 * @author yoan
 */
public class UserRealm extends AuthenticatingRealm {
	
	private final UserRepository userRepository;
	
	
	@Inject
	public UserRealm(CacheManager cacheManager, @Named(SHA1) HashedCredentialsMatcher credentialsMatcher, UserRepository userRepository) {
		super(requireNonNull(cacheManager), /*requireNonNull(credentialsMatcher)*/ new SimpleCredentialsMatcher());
		setAuthenticationTokenClass(UsernamePasswordToken.class);
		this.userRepository = requireNonNull(userRepository);
	}
	
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken userToken = (UsernamePasswordToken) token;
		User foundUser = userRepository.getById(UUID.fromString(userToken.getUsername()));
		if (foundUser == null) {
			return null;
		}
		
		return new SimpleAuthenticationInfo(foundUser.getId().toString(), foundUser.getName(), getName());
	}
	
}
