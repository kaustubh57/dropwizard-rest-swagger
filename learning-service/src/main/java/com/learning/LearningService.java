package com.learning;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Optional;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.learning.config.LearningConfiguration;
import com.learning.redis.RedisBundle;
import com.learning.redis.RedisConfiguration;
import com.learning.resources.HTTPMethodResource;
import com.learning.resources.LogResource;
import com.learning.resources.ProtectedResource;
import com.learning.resources.SampleResource;
import com.learning.resources.ShiroLoginCheck;
import com.learning.websocket.EventWebSocketAdapterServlet;
import com.learning.websocket.EventWebSocketServlet;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import io.ifar.dropwizard.shiro.ShiroBundle;
import io.ifar.dropwizard.shiro.ShiroConfiguration;
import lombok.val;
import org.hibernate.SessionFactory;
import org.redisson.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import javax.servlet.Servlet;
import javax.servlet.ServletRegistration;

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

    private final RedisBundle<LearningConfiguration> cacheBundle = new RedisBundle<LearningConfiguration>() {
        @Override
        public Optional<RedisConfiguration> getRedisConfiguration(final LearningConfiguration configuration) {
            return Optional.<RedisConfiguration>fromNullable(configuration.getRedisConfiguration());
        }
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
        bootstrap.addBundle(cacheBundle);
        ObjectMapper mapper = bootstrap.getObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void run(LearningConfiguration configuration, Environment environment) throws Exception {
        Injector injector = createInjector(configuration);
        registerResources(environment, injector);
        registerWebSocketServlet(environment, injector);
    }

    private Injector createInjector(final LearningConfiguration configuration) {
        val sessionFactory = hibernateBundle.getSessionFactory();
        Pool<Jedis> jedisPool = cacheBundle.getPool();
        val redissonConfig = cacheBundle.getRedissonConfig();
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(LearningConfiguration.class).toInstance(configuration);
                bind(SessionFactory.class).toInstance(sessionFactory);
                bind(Pool.class).toInstance(jedisPool);
                bind(Config.class).toInstance(redissonConfig);
            }
        });
    }

    private void registerResources(final Environment environment, Injector injector) {
        environment.jersey().setUrlPattern("/learning/*");
        //environment.jersey().disable();
        environment.jersey().regis ter(injector.getInstance(LogResource.class));
        environment.jersey().register(injector.getInstance(SampleResource.class));
        environment.jersey().register(injector.getInstance(ProtectedResource.class));
        environment.jersey().register(injector.getInstance(ShiroLoginCheck.class));
        environment.jersey().register(injector.getInstance(HTTPMethodResource.class));
    }

    private void registerWebSocketServlet(final Environment environment, Injector injector) {

        setupWebSocketServletUsingInstance(environment, "EventWebSocketServlet", injector.getInstance(EventWebSocketServlet.class), "/learning/websocket/*");
        setupWebSocketServletUsingClass(environment, "EventWebSocketAdapterServlet", EventWebSocketAdapterServlet.class, "/learning/websocket/adapter/*");
    }

    private void setupWebSocketServletUsingClass(final Environment environment, final String servletName,
                                       final Class<? extends Servlet> servletClass, final String mapping) {
        final ServletRegistration.Dynamic websocketServlet = environment.servlets().addServlet(servletName, servletClass);
        websocketServlet.addMapping(mapping);
        websocketServlet.setAsyncSupported(true);
    }

    private void setupWebSocketServletUsingInstance(final Environment environment, final String servletName,
                                       final Servlet servlet, final String mapping) {
        final ServletRegistration.Dynamic websocketServlet = environment.servlets().addServlet(servletName, servlet);
        websocketServlet.addMapping(mapping);
        websocketServlet.setAsyncSupported(true);
    }
}
