package com.learning.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LearningConfiguration extends Configuration {

    @NotNull
    @JsonProperty
    private String sampleProperty;

    @Valid
    @NotNull
    @JsonProperty
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();

    @NotNull
    @JsonProperty("swagger")
    private SwaggerBundleConfiguration swaggerBundleConfiguration;
}
