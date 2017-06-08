package yoan.shopping.infra.config;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SwaggerConfiguration {
	
	@NotEmpty
	@JsonProperty("base_path")
	public String basePath = "/shopping/rest";
	
	@NotEmpty
	public String scheme = "http";
	
	@NotEmpty
	public String host = "localhost";
	
	@Min(0)
	@Max(65535)
	public int port = 8080;
}
