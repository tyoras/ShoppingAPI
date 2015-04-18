/**
 * 
 */
package yoan.shopping.config;

import org.glassfish.jersey.server.ResourceConfig;

import com.wordnik.swagger.jersey.listing.ApiListingResourceJSON;
import com.wordnik.swagger.jersey.listing.JerseyApiDeclarationProvider;
import com.wordnik.swagger.jersey.listing.JerseyResourceListingProvider;

/**
 * Jersey application configuration
 * @author yoan
 */
public class Application extends ResourceConfig {
	public Application() {
		packages("com.wordnik.swagger.jaxrs.json").
	    packages("yoan.shopping.tmp");
	    register(ApiListingResourceJSON.class).
	    register(JerseyApiDeclarationProvider.class).
	    register(JerseyResourceListingProvider.class);
	  }
}