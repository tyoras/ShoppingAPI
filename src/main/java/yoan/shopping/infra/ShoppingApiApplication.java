package yoan.shopping.infra;

import static org.eclipse.jetty.servlets.CrossOriginFilter.ALLOWED_HEADERS_PARAM;
import static org.eclipse.jetty.servlets.CrossOriginFilter.ALLOWED_METHODS_PARAM;
import static org.eclipse.jetty.servlets.CrossOriginFilter.CHAIN_PREFLIGHT_PARAM;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.servlets.CrossOriginFilter;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import yoan.shopping.infra.config.ShoppingApiConfiguration;
import yoan.shopping.infra.config.guice.ShoppingModule;
import yoan.shopping.infra.rest.error.GlobalExceptionMapper;

public class ShoppingApiApplication extends Application<ShoppingApiConfiguration> {
	
	public static void main(String[] args) throws Exception {
        new ShoppingApiApplication().run(args);
    }

    @Override
    public String getName() {
        return "Shopping API";
    }

    @Override
    public void initialize(Bootstrap<ShoppingApiConfiguration> bootstrap) {
    	bootstrap.addBundle(GuiceBundle.builder()
    			.modules(new ShoppingModule())
                .enableAutoConfig("yoan.shopping")
                .noGuiceFilter()
                .printDiagnosticInfo()
                .build());
    }

    @Override
    public void run(ShoppingApiConfiguration configuration, Environment environment) {
    	configureJackson(environment);
    	configureExceptionMapping(environment);
    	configureCORS(environment);
    }

	private void configureJackson(Environment environment) {
		ObjectMapper objectMapper = environment.getObjectMapper()
	    	.registerModule(new JavaTimeModule())
	    	.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.setSerializationInclusion(Include.NON_NULL);
	}
	
	private void configureCORS(Environment environment) {
		final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        cors.setInitParameter(ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization");
        cors.setInitParameter(ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        cors.setInitParameter(CHAIN_PREFLIGHT_PARAM, Boolean.FALSE.toString());
	}
	
	private void configureExceptionMapping(Environment environment) {
		environment.jersey().register(new GlobalExceptionMapper());
	}
}
