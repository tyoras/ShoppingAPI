/**
 * 
 */
package yoan.shopping.infra.config.guice;

import yoan.shopping.user.User;
import yoan.shopping.user.repository.UserRepository;
import yoan.shopping.user.repository.mongo.UserMongoRepository;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * Guice Module to configure bindings
 * @author yoan
 */
public class ShoppingModule extends AbstractModule {
	public static final String CONNECTED_USER = "connectedUser";
	
	@Override
	protected void configure() {
		bind(UserRepository.class).to(UserMongoRepository.class);
		//FIXME faire marcher le named sur user
		bind(User.class).annotatedWith(Names.named(CONNECTED_USER)).toInstance(User.DEFAULT);
	}

}
