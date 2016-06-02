/**
 * 
 */
package yoan.shopping.infra.config.guice;

import static java.util.Objects.requireNonNull;

import javax.servlet.ServletContext;

import com.google.inject.AbstractModule;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.AcceptHeaderApiListingResource;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import io.swagger.models.Swagger;
import io.swagger.models.auth.OAuth2Definition;
import io.swagger.models.auth.SecuritySchemeDefinition;
import yoan.shopping.infra.config.api.Config;
import yoan.shopping.root.BuildInfo;

/**
 * Guice Module to bootstrap swagger
 * @author yoan
 */
public class SwaggerModule extends AbstractModule {

	public static final String SECURITY_DEFINITION_OAUTH2 = "oauth2";
	private final ServletContext servletContext;
	private final Config configAppli;
	private final BuildInfo buildInfo;

	public SwaggerModule(ServletContext servletContext, Config configAppli, BuildInfo buildInfo) {
		this.servletContext = requireNonNull(servletContext);
		this.configAppli = requireNonNull(configAppli);
		this.buildInfo = requireNonNull(buildInfo);
	}

	@Override
	protected void configure() {
		bind(ApiListingResource.class);
		bind(AcceptHeaderApiListingResource.class);
		bind(SwaggerSerializers.class);

		Swagger swagger = new Swagger();
		String authorizationURL = configAppli.getApiScheme() + "://" + configAppli.getApiHost() + ":" + configAppli.getApiPort()+"/shopping/rest/auth/authorization";
		String tokenURL = configAppli.getApiScheme() + "://" + configAppli.getApiHost() + ":" + configAppli.getApiPort()+"/shopping/rest/auth/token";
		SecuritySchemeDefinition oauth2SecurityDefinition = new OAuth2Definition()
			//.implicit(authorizationURL) //to get directly the token from the authz endpoint
			.accessCode(authorizationURL, tokenURL);
		
		swagger.securityDefinition(SECURITY_DEFINITION_OAUTH2, oauth2SecurityDefinition);
		
		servletContext.setAttribute("swagger", swagger);
		
		BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion(buildInfo.getVersion());
        beanConfig.setSchemes(new String[]{configAppli.getApiScheme()});
        beanConfig.setHost(configAppli.getApiHost() + ":" + configAppli.getApiPort());
        beanConfig.setBasePath(configAppli.getSwaggerBasePath());
        beanConfig.setResourcePackage("yoan.shopping");
        beanConfig.setContact("tyoras@gmail.com");
        beanConfig.setDescription("Rest API to manage and share a shopping list");
        beanConfig.setTermsOfServiceUrl("https://github.com/tyoras/ShoppingAPI");
        beanConfig.setScan(true);
        beanConfig.setPrettyPrint(true);
	}

}
