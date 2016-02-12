package com.learning;

import com.learning.config.*;

import com.learning.resources.LogResource;
import com.learning.resources.SampleResource;
import com.learning.resources.ProtectedResource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Optional;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import io.ifar.dropwizard.shiro.ShiroBundle;
import io.ifar.dropwizard.shiro.ShiroConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LearningService extends Application<LearningConfiguration> {
    private static final Logger LOG = LoggerFactory.getLogger(LearningService.class);

    public static void main(String[] args) throws Exception {
        new LearningService().run(args);
    }

    private final SwaggerBundle<LearningConfiguration> swaggerBundle = new SwaggerBundle<LearningConfiguration>() {
        @Override
        public SwaggerBundleConfiguration getSwaggerBundleConfiguration(final LearningConfiguration configuration) {
            return configuration.getSwaggerBundleConfiguration();
        }
    };

    // https://github.com/Multifarious/dw-shiro-bundle
    private final ShiroBundle shiroBundle =
        new ShiroBundle<LearningConfiguration>() {
            @Override public Optional<ShiroConfiguration> getShiroConfiguration(final LearningConfiguration configuration)
            { return Optional.<ShiroConfiguration>fromNullable(configuration.getShiroConfiguration()); }
    };

    private final HibernateBundle<LearningConfiguration> hibernateBundle = new HibernateBundle<LearningConfiguration>(

            Void.class
        ) {
        @Override
        public DataSourceFactory getDataSourceFactory(LearningConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    @Override
    public String getName() {
        return "learning";
    }

    @Override
    public void initialize(Bootstrap<LearningConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets/app/", "/", "index.html"));
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(swaggerBundle);
        bootstrap.addBundle(shiroBundle);
        ObjectMapper mapper = bootstrap.getObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void run(LearningConfiguration configuration,
                    Environment environment) throws Exception {
        environment.jersey().setUrlPattern("/learning/*");
        //environment.jersey().disable();
        environment.jersey().register(new LogResource());
        environment.jersey().register(new SampleResource());
        environment.jersey().register(new ProtectedResource());
    }

}
