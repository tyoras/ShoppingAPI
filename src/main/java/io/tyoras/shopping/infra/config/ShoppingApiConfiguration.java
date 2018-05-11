package io.tyoras.shopping.infra.config;


import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ShoppingApiConfiguration extends Configuration {

    @Valid
    @NotNull
    public MongoConfiguration mongo = new MongoConfiguration();

    @Valid
    @NotNull
    public SwaggerConfiguration swagger = new SwaggerConfiguration();

}
