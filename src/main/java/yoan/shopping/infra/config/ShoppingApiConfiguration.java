package yoan.shopping.infra.config;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import io.dropwizard.Configuration;

public class ShoppingApiConfiguration extends Configuration {
	
	@Valid
	@NotNull
	public MongoConfiguration mongo = new MongoConfiguration();
	
	@NotEmpty
	public String swaggerBasePath = "/shopping/rest";
	
}
