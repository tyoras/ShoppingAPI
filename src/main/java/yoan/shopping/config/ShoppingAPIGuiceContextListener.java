/**
 * 
 */
package yoan.shopping.config;

import java.util.HashMap;
import java.util.Map;

import org.glassfish.jersey.servlet.ServletContainer;

import yoan.shopping.tmp.ApiOriginFilter;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.wordnik.swagger.config.ConfigFactory;
import com.wordnik.swagger.config.ScannerFactory;
import com.wordnik.swagger.config.SwaggerConfig;
import com.wordnik.swagger.jaxrs.config.DefaultJaxrsScanner;
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
		return Guice.createInjector(new ServletModule() {
            @Override
            protected void configureServlets() {
                bind(ServletContainer.class).in(Singleton.class);
                bind(ApiOriginFilter.class).in(Singleton.class);

                Map<String, String> props = new HashMap<String, String>();
                props.put("javax.ws.rs.Application", Application.class.getName());
                props.put("jersey.config.server.wadl.disableWadl", "true");
                serve("/api/*").with(ServletContainer.class, props);
                
                ReflectiveJaxrsScanner scanner = new ReflectiveJaxrsScanner();
                scanner.setResourcePackage("yoan.shopping");
                ScannerFactory.setScanner(scanner);
                SwaggerConfig config = ConfigFactory.config();
                config.setApiVersion("1.0.0");
                
                String basePath = "http://localhost:8080/ShoppinAPI/api";
                if (System.getProperties().contains("swagger.basePath")) {
                    basePath = System.getProperty("swagger.basePath");
                }
                config.setBasePath(basePath);
                ConfigFactory.setConfig(config);

                //FilterFactory.setFilter(new ApiAuthorizationFilterImpl());
                //ScannerFactory.setScanner(new DefaultJaxrsScanner());
                ClassReaders.setReader(new DefaultJaxrsApiReader());
                
                bootstrap();
                
                filter("/*").through(ApiOriginFilter.class);
            }
        });
	}

	private void bootstrap() {

        ApiInfo info = new ApiInfo(
                "Shopping API",                             /* title */
                "Description",
                "http://helloreverb.com/terms/",                  /* TOS URL */
                "tyoras@gmail.com",                            /* Contact */
                "None",                                     /* license */
                "http://www.google.fr" /* license URL */
        );

        ConfigFactory.config().setApiInfo(info);
    }
}
