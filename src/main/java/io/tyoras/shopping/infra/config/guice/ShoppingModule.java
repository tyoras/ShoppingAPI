package io.tyoras.shopping.infra.config.guice;

import com.google.inject.Provides;
import io.tyoras.shopping.authentication.repository.OAuth2AccessTokenRepository;
import io.tyoras.shopping.authentication.repository.OAuth2AuthorizationCodeRepository;
import io.tyoras.shopping.authentication.repository.mongo.OAuth2AccessTokenMongoRepository;
import io.tyoras.shopping.authentication.repository.mongo.OAuth2AuthorizationCodeMongoRepository;
import io.tyoras.shopping.client.app.repository.ClientAppRepository;
import io.tyoras.shopping.client.app.repository.mongo.ClientAppMongoRepository;
import io.tyoras.shopping.infra.config.ShoppingApiConfiguration;
import io.tyoras.shopping.list.repository.ShoppingItemRepository;
import io.tyoras.shopping.list.repository.ShoppingListRepository;
import io.tyoras.shopping.list.repository.mongo.ShoppingItemMongoRepository;
import io.tyoras.shopping.list.repository.mongo.ShoppingListMongoRepository;
import io.tyoras.shopping.root.repository.BuildInfoRepository;
import io.tyoras.shopping.root.repository.properties.BuildInfoPropertiesRepository;
import io.tyoras.shopping.user.repository.SecuredUserRepository;
import io.tyoras.shopping.user.repository.UserRepository;
import io.tyoras.shopping.user.repository.mongo.SecuredUserMongoRepository;
import io.tyoras.shopping.user.repository.mongo.UserMongoRepository;
import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;

import static io.tyoras.shopping.root.repository.properties.BuildInfoPropertiesRepository.BUILD_INFO_DEFAULT_PROPERTIES_FILE_NAME;

/**
 * Guice Module to configure bindings
 *
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
