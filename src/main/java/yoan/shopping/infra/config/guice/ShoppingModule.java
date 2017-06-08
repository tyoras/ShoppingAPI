package yoan.shopping.infra.config.guice;

import static yoan.shopping.root.repository.properties.BuildInfoPropertiesRepository.BUILD_INFO_DEFAULT_PROPERTIES_FILE_NAME;

import com.google.inject.Provides;

import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;
import yoan.shopping.authentication.repository.OAuth2AccessTokenRepository;
import yoan.shopping.authentication.repository.OAuth2AuthorizationCodeRepository;
import yoan.shopping.authentication.repository.mongo.OAuth2AccessTokenMongoRepository;
import yoan.shopping.authentication.repository.mongo.OAuth2AuthorizationCodeMongoRepository;
import yoan.shopping.client.app.repository.ClientAppRepository;
import yoan.shopping.client.app.repository.mongo.ClientAppMongoRepository;
import yoan.shopping.infra.config.ShoppingApiConfiguration;
import yoan.shopping.list.repository.ShoppingItemRepository;
import yoan.shopping.list.repository.ShoppingListRepository;
import yoan.shopping.list.repository.mongo.ShoppingItemMongoRepository;
import yoan.shopping.list.repository.mongo.ShoppingListMongoRepository;
import yoan.shopping.root.repository.BuildInfoRepository;
import yoan.shopping.root.repository.properties.BuildInfoPropertiesRepository;
import yoan.shopping.user.repository.SecuredUserRepository;
import yoan.shopping.user.repository.UserRepository;
import yoan.shopping.user.repository.mongo.SecuredUserMongoRepository;
import yoan.shopping.user.repository.mongo.UserMongoRepository;

/**
 * Guice Module to configure bindings
 * @author yoan
 */
public class ShoppingModule extends DropwizardAwareModule<ShoppingApiConfiguration> {
	
	@Override
	protected void configure() {
		
		//bindings
		bind(UserRepository.class).to(UserMongoRepository.class);
		bind(SecuredUserRepository.class).to(SecuredUserMongoRepository.class);
		bind(ShoppingListRepository.class).to(ShoppingListMongoRepository.class);
		bind(ShoppingItemRepository.class).to(ShoppingItemMongoRepository.class);
		bind(ClientAppRepository.class).to(ClientAppMongoRepository.class);
		
		bind(OAuth2AuthorizationCodeRepository.class).to(OAuth2AuthorizationCodeMongoRepository.class);
		bind(OAuth2AccessTokenRepository.class).to(OAuth2AccessTokenMongoRepository.class);
	}
	
	@Provides
	BuildInfoRepository provideBuildInfoRepository() {
		return new BuildInfoPropertiesRepository(BUILD_INFO_DEFAULT_PROPERTIES_FILE_NAME);
	}
}
