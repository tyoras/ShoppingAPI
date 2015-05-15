/**
 * 
 */
package yoan.shopping.infra.config.guice;


import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import yoan.shopping.infra.config.filter.AuthenticationFilter;
import yoan.shopping.user.User;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;

/**
 * Guice module to configure servlet
 * @author yoan
 */
public class ShoppingWebModule extends ServletModule {
	public static final String CONNECTED_USER = "connectedUser";
	
	@Override
	protected void configureServlets() {
		
		//using Resteasy servlet dispatcher
		bind(HttpServletDispatcher.class).in(Singleton.class);
		serve("/api/*").with(HttpServletDispatcher.class);
				
		//filtering to authenticate the current user
		filter("/api/*").through(AuthenticationFilter.class);
	}
	
	@Provides
	@Named(CONNECTED_USER)
	@RequestScoped
	User provideConnectedUser() {
		throw new IllegalStateException("Connected user should have been seeded by an authentication filter");
	}
}
