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

/**
 * Guice Module to bootstrap swagger
 * @author yoan
 */
public class SwaggerModule extends AbstractModule {

	private final ServletContext servletContext;
	private final Reflections reflections;
	private final Config configAppli;

	public SwaggerModule(ServletContext servletContext, Reflections reflections, Config configAppli) {
		this.servletContext = requireNonNull(servletContext);
		this.reflections = requireNonNull(reflections);
		this.configAppli = requireNonNull(configAppli);
	}

	@Override
	protected void configure() {
		bind(ApiListingResource.class);
		bind(SwaggerSerializers.class);

		Info info = new Info()
        .title("Shopping API")
        .description("Rest API to manage and share a shopping list")
        .version("0.1.3-SNAPSHOT")
        .termsOfService("https://github.com/tyoras/ShoppingAPI")
        .contact(new Contact().email("tyoras@gmail.com"))
        .license(new License().name("No license for the moment").url("https://github.com/tyoras/ShoppingAPI"));

		MyReflectiveJaxrsScanner scanner = new MyReflectiveJaxrsScanner();
		scanner.setReflections(reflections);
		scanner.setInfo(info);
		scanner.setConfigAppli(configAppli);
		ScannerFactory.setScanner(scanner);
		
		Swagger swagger = new Swagger();
		String authorizationURL = "http://" + configAppli.getApiHost() + ":" + configAppli.getApiPort()+"/shopping/rest/auth/authorization";
		String tokenURL = "http://" + configAppli.getApiHost() + ":" + configAppli.getApiPort()+"/shopping/rest/auth/token";
		SecuritySchemeDefinition oauth2SecurityDefinition = new OAuth2Definition()
			//.implicit(authorizationURL) //to get directly the token from the authz endpoint
			.accessCode(authorizationURL, tokenURL);
		
		swagger.securityDefinition("oauth2", oauth2SecurityDefinition);
		servletContext.setAttribute("swagger", swagger);
	}

	private static class MyReflectiveJaxrsScanner extends ReflectiveJaxrsScanner {
		private Info info;
		private Config configAppli;

		@Override
		public Set<Class<?>> classes() {
			return reflections.getTypesAnnotatedWith(Api.class);
		}

		@Override
		public Swagger configure(Swagger swagger) {
			swagger.setInfo(info);
			swagger.scheme(Scheme.HTTP);
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
