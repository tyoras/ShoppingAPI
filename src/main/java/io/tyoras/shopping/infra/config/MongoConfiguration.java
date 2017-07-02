package io.tyoras.shopping.infra.config;

import javax.annotation.Nullable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

public class MongoConfiguration {
	
	@NotEmpty
	public String host = "localhost";
	
	@Min(1)
    @Max(65535)
	public Integer port = 27017;
	
	@Nullable
	public String user;
	@Nullable
	public String password;
}
