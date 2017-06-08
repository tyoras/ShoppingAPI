package yoan.shopping.infra.config;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.dropwizard.Configuration;

public class ShoppingApiConfiguration extends Configuration {
	
	@Valid
	@NotNull
	public MongoConfiguration mongo = new MongoConfiguration();
	
	@Valid
	@NotNull
	public SwaggerConfiguration swagger = new SwaggerConfiguration();
	
}
