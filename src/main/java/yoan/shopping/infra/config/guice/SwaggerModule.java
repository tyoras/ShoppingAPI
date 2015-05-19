/**
 * 
 */
package yoan.shopping.infra.config.guice;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import org.reflections.Reflections;

import yoan.shopping.infra.config.api.Config;

import com.google.inject.AbstractModule;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.config.ScannerFactory;
import com.wordnik.swagger.jaxrs.config.ReflectiveJaxrsScanner;
import com.wordnik.swagger.jaxrs.listing.ApiListingResource;
import com.wordnik.swagger.jaxrs.listing.SwaggerSerializers;
import com.wordnik.swagger.models.Contact;
import com.wordnik.swagger.models.Info;
import com.wordnik.swagger.models.License;
import com.wordnik.swagger.models.Scheme;
import com.wordnik.swagger.models.Swagger;

/**
 *
 * @author yoan
 */
public class SwaggerModule extends AbstractModule {

	private final Reflections reflections;
	private final Config configAppli;

	public SwaggerModule(Reflections reflections, Config configAppli) {
		this.reflections = requireNonNull(reflections);
		this.configAppli = requireNonNull(configAppli);
	}

	@Override
	protected void configure() {
		bind(ApiListingResource.class);
		bind(SwaggerSerializers.class);

		Info info = new Info()
        .title("Shopping API")
        .description("API to manage and share a shopping list")
        .version("0.0.1")
        .termsOfService("https://github.com/tyoras/ShoppingAPI")
        .contact(new Contact().email("tyoras@gmail.com"))
        .license(new License().name("No license for the moment").url("https://github.com/tyoras/ShoppingAPI"));

		MyReflectiveJaxrsScanner scanner = new MyReflectiveJaxrsScanner();
		scanner.setReflections(reflections);
		scanner.setInfo(info);
		scanner.setConfigAppli(configAppli);
		ScannerFactory.setScanner(scanner);
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
