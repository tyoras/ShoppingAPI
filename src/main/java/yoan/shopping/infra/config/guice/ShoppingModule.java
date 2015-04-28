/**
 * 
 */
package yoan.shopping.infra.config.guice;

import yoan.shopping.user.User;
import yoan.shopping.user.repository.UserRepository;
import yoan.shopping.user.repository.mongo.UserMongoRepository;
import yoan.shopping.user.resource.UserResource;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.wordnik.swagger.config.ConfigFactory;
import com.wordnik.swagger.config.ScannerFactory;
import com.wordnik.swagger.config.SwaggerConfig;
import com.wordnik.swagger.jaxrs.config.ReflectiveJaxrsScanner;
import com.wordnik.swagger.jaxrs.json.JacksonJsonProvider;
import com.wordnik.swagger.jaxrs.listing.ApiDeclarationProvider;
import com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON;
import com.wordnik.swagger.jaxrs.listing.ResourceListingProvider;
import com.wordnik.swagger.jaxrs.reader.DefaultJaxrsApiReader;
import com.wordnik.swagger.model.ApiInfo;
import com.wordnik.swagger.reader.ClassReaders;

/**
 * Guice Module to configure bindings
 * @author yoan
 */
public class ShoppingModule extends AbstractModule {
	public static final String CONNECTED_USER = "connectedUser";
	private static final String SWAGGER_BASE_PATH_PROPERTY = "swagger.basePath";
	private static final String SWAGGER_DEFAULT_BASE_PATH = "http://localhost:8080/shopping/api";
	
	@Override
	protected void configure() {
		//Swagger resources & providers
		bind(ApiListingResourceJSON.class);
		bind(JacksonJsonProvider.class);
		bind(ApiDeclarationProvider.class);
		bind(ResourceListingProvider.class);
		
		//resources
		bind(UserResource.class);
		
		//bindings
		bind(UserRepository.class).to(UserMongoRepository.class);
		//FIXME faire marcher le named sur user
		bind(User.class).annotatedWith(Names.named(CONNECTED_USER)).toInstance(User.DEFAULT);
		bootstrapSwagger();
	}

	private void bootstrapSwagger() {
		ReflectiveJaxrsScanner scanner = new ReflectiveJaxrsScanner();
        scanner.setResourcePackage("yoan.shopping");
        ScannerFactory.setScanner(scanner);
        SwaggerConfig config = ConfigFactory.config();
        config.setApiVersion("1.0.0");
        
        String basePath = SWAGGER_DEFAULT_BASE_PATH;
        if (System.getProperties().contains(SWAGGER_BASE_PATH_PROPERTY)) {
            basePath = System.getProperty(SWAGGER_BASE_PATH_PROPERTY);
        }
        config.setBasePath(basePath);
        ConfigFactory.setConfig(config);

        //FilterFactory.setFilter(new ApiAuthorizationFilterImpl());
        //ScannerFactory.setScanner(new DefaultJaxrsScanner());
        ClassReaders.setReader(new DefaultJaxrsApiReader());
        
        ApiInfo info = new ApiInfo(
                "Shopping API",                             /* title */
                "API to manage and share a shopping list",
                "https://github.com/tyoras/ShoppingAPI",    /* TOS URL */
                "tyoras@gmail.com",                         /* Contact */
                "No license for the moment",                /* license */
                "https://github.com/tyoras/ShoppingAPI"		/* license URL */
        );

        ConfigFactory.config().setApiInfo(info);
    }
}
