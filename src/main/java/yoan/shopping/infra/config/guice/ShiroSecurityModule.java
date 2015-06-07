/**
 * 
 */
package yoan.shopping.infra.config.guice;

import javax.servlet.ServletContext;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.guice.web.ShiroWebModule;

import yoan.shopping.authentication.realm.UserRealm;

import com.google.inject.name.Names;

/**
 * 
 * @author yoan
 */
public class ShiroSecurityModule extends ShiroWebModule {
	public static final String SHA1 = "SHA1";
	
	public ShiroSecurityModule(ServletContext servletContext) {
		super(servletContext);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void configureShiroWeb() {
		bind(CacheManager.class).to(MemoryConstrainedCacheManager.class);
		//using expose because it is a private module
		expose(CacheManager.class);
		
		bind(HashedCredentialsMatcher.class).annotatedWith(Names.named(SHA1)).toInstance(new HashedCredentialsMatcher(SHA1));
		expose(HashedCredentialsMatcher.class).annotatedWith(Names.named(SHA1));
		
		bindRealm().to(UserRealm.class);
		
		addFilterChain("/rest/api/**", config(NO_SESSION_CREATION, "true"), AUTHC_BASIC);
	}

}
