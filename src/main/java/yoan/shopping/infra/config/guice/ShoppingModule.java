package yoan.shopping.infra.config.guice;

import static java.util.Objects.requireNonNull;
import static yoan.shopping.root.repository.properties.BuildInfoPropertiesRepository.BUILD_INFO_DEFAULT_PROPERTIES_FILE_NAME;

import javax.servlet.ServletContext;

import org.reflections.Reflections;

import yoan.shopping.authentication.repository.OAuth2AccessTokenRepository;
import yoan.shopping.authentication.repository.OAuth2AuthorizationCodeRepository;
import yoan.shopping.authentication.repository.mongo.OAuth2AccessTokenMongoRepository;
import yoan.shopping.authentication.repository.mongo.OAuth2AuthorizationCodeMongoRepository;
import yoan.shopping.authentication.resource.AuthorizationResource;
import yoan.shopping.authentication.resource.RedirectResource;
import yoan.shopping.authentication.resource.TokenResource;
import yoan.shopping.client.app.repository.ClientAppRepository;
import yoan.shopping.client.app.repository.mongo.ClientAppMongoRepository;
import yoan.shopping.infra.config.api.Config;
import yoan.shopping.infra.config.api.repository.ConfigRepository;
import yoan.shopping.infra.config.api.repository.properties.ConfigPropertiesRepository;
import yoan.shopping.infra.config.jackson.JacksonConfigProvider;
import yoan.shopping.infra.rest.error.GlobalExceptionMapper;
import yoan.shopping.list.repository.ShoppingItemRepository;
import yoan.shopping.list.repository.ShoppingListRepository;
import yoan.shopping.list.repository.mongo.ShoppingItemMongoRepository;
import yoan.shopping.list.repository.mongo.ShoppingListMongoRepository;
import yoan.shopping.list.resource.ShoppingItemResource;
import yoan.shopping.list.resource.ShoppingListResource;
import yoan.shopping.root.repository.BuildInfoRepository;
import yoan.shopping.root.repository.properties.BuildInfoPropertiesRepository;
import yoan.shopping.root.resource.RootResource;
import yoan.shopping.user.repository.SecuredUserRepository;
import yoan.shopping.user.repository.UserRepository;
import yoan.shopping.user.repository.mongo.SecuredUserMongoRepository;
import yoan.shopping.user.repository.mongo.UserMongoRepository;
import yoan.shopping.user.resource.UserResource;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Guice Module to configure bindings
 * @author yoan
 */
public class ShoppingModule extends AbstractModule {
	private static final Config configAppli;
	
	private final ServletContext servletContext;
	
	public ShoppingModule(ServletContext servletContext) {
		this.servletContext = requireNonNull(servletContext);
	}
	
	static {
		ConfigRepository configRepo = new ConfigPropertiesRepository();
		configAppli = configRepo.readConfig();
	}
	
	@Override
	protected void configure() {
		install(new SwaggerModule(servletContext, new Reflections("yoan.shopping"), configAppli));
		
		//resources
		bind(RootResource.class);
		bind(UserResource.class);
		bind(ShoppingListResource.class);
		bind(ShoppingItemResource.class);
		bind(AuthorizationResource.class);
		bind(TokenResource.class);
		
		//providers
		bind(GlobalExceptionMapper.class);
		bind(JacksonConfigProvider.class);
		
		//bindings
		bind(Config.class).toInstance(configAppli);
		bind(UserRepository.class).to(UserMongoRepository.class);
		bind(SecuredUserRepository.class).to(SecuredUserMongoRepository.class);
		bind(ConfigRepository.class).to(ConfigPropertiesRepository.class);
		bind(ShoppingListRepository.class).to(ShoppingListMongoRepository.class);
		bind(ShoppingItemRepository.class).to(ShoppingItemMongoRepository.class);
		bind(ClientAppRepository.class).to(ClientAppMongoRepository.class);
		
		bind(OAuth2AuthorizationCodeRepository.class).to(OAuth2AuthorizationCodeMongoRepository.class);
		bind(OAuth2AccessTokenRepository.class).to(OAuth2AccessTokenMongoRepository.class);
		
		bindForLocalHostOnly();
	}
	
	@Provides
	BuildInfoRepository provideBuildInfoRepository() {
		return new BuildInfoPropertiesRepository(BUILD_INFO_DEFAULT_PROPERTIES_FILE_NAME);
	}
	
	/**
	 * Binding only for development purpose
	 */
	private void bindForLocalHostOnly() {
		String host = configAppli.getApiHost();
		if ("localhost".equals(host) || "127.0.0.1".equals(host)) {
			bind(RedirectResource.class);
		}
	}
}
