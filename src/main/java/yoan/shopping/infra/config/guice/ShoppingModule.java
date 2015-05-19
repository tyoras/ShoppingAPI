package yoan.shopping.infra.config.guice;

import org.reflections.Reflections;

import yoan.shopping.infra.config.api.Config;
import yoan.shopping.infra.config.api.repository.ConfigRepository;
import yoan.shopping.infra.config.api.repository.properties.ConfigPropertiesRepository;
import yoan.shopping.infra.config.jackson.JacksonConfigProvider;
import yoan.shopping.infra.rest.error.GlobalExceptionMapper;
import yoan.shopping.root.repository.BuildInfoRepository;
import yoan.shopping.root.repository.properties.BuildInfoPropertiesRepository;
import yoan.shopping.root.resource.RootResource;
import yoan.shopping.user.repository.UserRepository;
import yoan.shopping.user.repository.mongo.UserMongoRepository;
import yoan.shopping.user.resource.UserResource;

import com.google.inject.AbstractModule;

/**
 * Guice Module to configure bindings
 * @author yoan
 */
public class ShoppingModule extends AbstractModule {
	private static final Config configAppli;
	
	static {
		ConfigRepository configRepo = new ConfigPropertiesRepository();
		configAppli = configRepo.readConfig();
	}
	
	@Override
	protected void configure() {
		install(new SwaggerModule(new Reflections("yoan.shopping"), configAppli));
		
		//resources
		bind(RootResource.class);
		bind(UserResource.class);
		
		//providers
		bind(GlobalExceptionMapper.class);
		bind(JacksonConfigProvider.class);
		
		//bindings
		bind(Config.class).toInstance(configAppli);
		bind(UserRepository.class).to(UserMongoRepository.class);
		bind(BuildInfoRepository.class).to(BuildInfoPropertiesRepository.class);
		bind(ConfigRepository.class).to(ConfigPropertiesRepository.class);
	}
}
