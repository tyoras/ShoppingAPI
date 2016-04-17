/**
 * 
 */
package yoan.shopping.infra.config.guice;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import javax.servlet.ServletContext;

import org.reflections.Reflections;

import com.google.inject.AbstractModule;

import io.swagger.annotations.Api;
import io.swagger.config.ScannerFactory;
import io.swagger.jaxrs.config.ReflectiveJaxrsScanner;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.License;
import io.swagger.models.Scheme;
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
	private final Reflections reflections;
	private final Config configAppli;
	private final BuildInfo buildInfo;

	public SwaggerModule(ServletContext servletContext, Reflections reflections, Config configAppli, BuildInfo buildInfo) {
		this.servletContext = requireNonNull(servletContext);
		this.reflections = requireNonNull(reflections);
		this.configAppli = requireNonNull(configAppli);
		this.buildInfo = requireNonNull(buildInfo);
	}

	@Override
	protected void configure() {
		bind(ApiListingResource.class);
		bind(SwaggerSerializers.class);

		Info info = new Info()
        .title("Shopping API")
        .description("Rest API to manage and share a shopping list")
        .version(buildInfo.getVersion())
        .termsOfService("https://github.com/tyoras/ShoppingAPI")
        .contact(new Contact().email("tyoras@gmail.com"))
        .license(new License().name("No license for the moment").url("https://github.com/tyoras/ShoppingAPI"));

		CustomReflectiveJaxrsScanner scanner = new CustomReflectiveJaxrsScanner();
		scanner.setReflections(reflections);
		scanner.setInfo(info);
		scanner.setConfigAppli(configAppli);
		ScannerFactory.setScanner(scanner);
		
		Swagger swagger = new Swagger();
		String authorizationURL = configAppli.getApiScheme() + "://" + configAppli.getApiHost() + ":" + configAppli.getApiPort()+"/shopping/rest/auth/authorization";
		String tokenURL = configAppli.getApiScheme() + "://" + configAppli.getApiHost() + ":" + configAppli.getApiPort()+"/shopping/rest/auth/token";
		SecuritySchemeDefinition oauth2SecurityDefinition = new OAuth2Definition()
			//.implicit(authorizationURL) //to get directly the token from the authz endpoint
			.accessCode(authorizationURL, tokenURL);
		
		swagger.securityDefinition(SECURITY_DEFINITION_OAUTH2, oauth2SecurityDefinition);
		servletContext.setAttribute("swagger", swagger);
	}

	private static class CustomReflectiveJaxrsScanner extends ReflectiveJaxrsScanner {
		private Info info;
		private Config configAppli;

		@Override
		public Set<Class<?>> classes() {
			return reflections.getTypesAnnotatedWith(Api.class);
		}

		@Override
		public Swagger configure(Swagger swagger) {
			swagger.setInfo(info);
			swagger.scheme(Scheme.forValue(configAppli.getApiScheme()));
			swagger.setBasePath(configAppli.getSwaggerBasePath());
			return swagger;
		}

		public void setInfo(Info info) {
			this.info = info;
		}
		
		public void setConfigAppli(Config configAppli) {
			this.configAppli = configAppli;
		}
	}

}
