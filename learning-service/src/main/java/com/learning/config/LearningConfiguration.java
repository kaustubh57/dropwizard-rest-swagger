package com.learning.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.ifar.dropwizard.shiro.ShiroConfiguration;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class LearningConfiguration extends Configuration {

    @JsonProperty("contextParameters")
    private Map<String, String> contextParameters;

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

    // https://github.com/Multifarious/dw-shiro-bundle
    @Valid
    @JsonProperty("shiro")
    private ShiroConfiguration shiroConfiguration = new ShiroConfiguration();
}
