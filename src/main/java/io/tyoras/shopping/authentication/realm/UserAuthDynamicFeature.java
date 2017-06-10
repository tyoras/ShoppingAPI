package io.tyoras.shopping.authentication.realm;


import java.security.Principal;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.dropwizard.auth.PolymorphicAuthDynamicFeature;
import io.dropwizard.auth.PolymorphicAuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.setup.Environment;
import io.tyoras.shopping.user.User;

@Provider
public class UserAuthDynamicFeature extends PolymorphicAuthDynamicFeature<Principal> {
	
	private final AbstractBinder binder = new PolymorphicAuthValueFactoryProvider.Binder<>(
        ImmutableSet.of(
    		BasicUserPrincipal.class, 
    		User.class
		)
    );
	
	@Inject
	public UserAuthDynamicFeature(BasicUserAuthenticator basicUserAuthenticator, OAuthAuthenticator oauthAuthenticator, Environment environment) {
		super(ImmutableMap.of(
			BasicUserPrincipal.class, new BasicCredentialAuthFilter.Builder<BasicUserPrincipal>()
	            .setAuthenticator(basicUserAuthenticator)
	            .setRealm("Basic")
	            .buildAuthFilter(),
            User.class, new OAuthCredentialAuthFilter.Builder<User>()
	            .setAuthenticator(oauthAuthenticator)
	            .setPrefix("Bearer")
	            .buildAuthFilter()
		));
		
		environment.jersey().register(binder);
	}
	
}
