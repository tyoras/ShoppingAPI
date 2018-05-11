package io.tyoras.shopping.infra.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

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
