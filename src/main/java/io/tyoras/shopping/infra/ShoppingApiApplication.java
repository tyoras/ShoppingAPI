package io.tyoras.shopping.infra;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.vavr.VavrBundle;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.tyoras.shopping.infra.config.ShoppingApiConfiguration;
import io.tyoras.shopping.infra.config.SwaggerConfiguration;
import io.tyoras.shopping.infra.config.guice.ShoppingModule;
import io.tyoras.shopping.infra.rest.error.GlobalExceptionMapper;
import io.tyoras.shopping.root.BuildInfo;
import io.tyoras.shopping.root.repository.properties.BuildInfoPropertiesRepository;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import ru.vyarus.dropwizard.guice.GuiceBundle;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

import static io.tyoras.shopping.root.repository.properties.BuildInfoPropertiesRepository.BUILD_INFO_DEFAULT_PROPERTIES_FILE_NAME;
import static org.eclipse.jetty.servlets.CrossOriginFilter.*;

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
                .enableAutoConfig("io.tyoras.shopping")
                .extensions(ApiListingResource.class)
                .noGuiceFilter()
                .printDiagnosticInfo()
                .build());
    	bootstrap.addBundle(new VavrBundle());
    }

    @Override
    public void run(ShoppingApiConfiguration configuration, Environment environment) {
        configureJackson(environment);
        configureExceptionMapping(environment);
        configureCORS(environment);
        configureSwagger(configuration, environment);
    }

    private void configureJackson(Environment environment) {
        ObjectMapper objectMapper = environment.getObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setSerializationInclusion(Include.NON_NULL);
    }

    private void configureCORS(Environment environment) {
        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        cors.setInitParameter(ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization,Content-Length");
        cors.setInitParameter(ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        cors.setInitParameter(CHAIN_PREFLIGHT_PARAM, Boolean.FALSE.toString());
    }

    private void configureExceptionMapping(Environment environment) {
        environment.jersey().register(new GlobalExceptionMapper());
    }

    private void configureSwagger(ShoppingApiConfiguration configuration, Environment environment) {
        BuildInfo buildInfo = new BuildInfoPropertiesRepository(BUILD_INFO_DEFAULT_PROPERTIES_FILE_NAME).getCurrentBuildInfos();
        SwaggerConfiguration swagger = configuration.swagger;
        BeanConfig config = new BeanConfig();
        config.setTitle("Shopping API");
        config.setVersion(buildInfo.getVersion());
        config.setContact("tyoras@gmail.com");
        config.setDescription("Rest API to manage and share a shopping list");
        config.setTermsOfServiceUrl("https://github.com/tyoras/ShoppingAPI");
        config.setResourcePackage("yoan.shopping");
        config.setScan(true);
        config.setPrettyPrint(true);
        config.setSchemes(new String[]{swagger.scheme});
        config.setHost(swagger.host + ":" + swagger.port);
        config.setBasePath(swagger.basePath);
    }
}
