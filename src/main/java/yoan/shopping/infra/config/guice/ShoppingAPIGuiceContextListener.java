/**
 * 
 */
package yoan.shopping.infra.config.guice;

import java.util.HashMap;
import java.util.Map;

import org.glassfish.jersey.servlet.ServletContainer;

import yoan.shopping.infra.config.Application;
import yoan.shopping.infra.config.filter.ApiOriginFilter;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.wordnik.swagger.config.ConfigFactory;
import com.wordnik.swagger.config.ScannerFactory;
import com.wordnik.swagger.config.SwaggerConfig;
import com.wordnik.swagger.jaxrs.config.ReflectiveJaxrsScanner;
import com.wordnik.swagger.jaxrs.reader.DefaultJaxrsApiReader;
import com.wordnik.swagger.model.ApiInfo;
import com.wordnik.swagger.reader.ClassReaders;

/**
 * Guice Application configuration
 * @author yoan
 */
public class ShoppingAPIGuiceContextListener extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new ShoppingModule(), new ServletModule() {
            @Override
            protected void configureServlets() {
                bind(ServletContainer.class).in(Singleton.class);

                Map<String, String> initParams = new HashMap<>();
                initParams.put("javax.ws.rs.Application", Application.class.getName());
                initParams.put("jersey.config.server.wadl.disableWadl", "true");
                serve("/api/*").with(ServletContainer.class, initParams);
                
                bootstrapSwagger();
                
                filter("/*").through(ApiOriginFilter.class);
            }
        });
	}

	private void bootstrapSwagger() {
		ReflectiveJaxrsScanner scanner = new ReflectiveJaxrsScanner();
        scanner.setResourcePackage("yoan.shopping");
        ScannerFactory.setScanner(scanner);
        SwaggerConfig config = ConfigFactory.config();
        config.setApiVersion("1.0.0");
        
        String basePath = "http://localhost:8080/shopping/api";
        if (System.getProperties().contains("swagger.basePath")) {
            basePath = System.getProperty("swagger.basePath");
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
