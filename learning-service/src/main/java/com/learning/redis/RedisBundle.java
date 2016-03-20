package com.learning.redis;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Config;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.Pool;

/**
 * Created by kaustubh on 3/20/16.
 */
@Slf4j
public class RedisBundle<T extends Configuration> implements ConfiguredBundle<T>, RedisConfigurationStrategy<T> {

    @Getter
    private final Config redissonConfig = new Config();
    @Getter
    private Pool<Jedis> pool;
    private JedisPoolConfig poolConfig = new JedisPoolConfig();

    @Override
    public void run(final T configuration, final Environment environment) throws Exception {
        final Optional<RedisConfiguration> config = getRedisConfiguration(configuration);
        if (config.isPresent()) {
            RedisConfiguration cacheConfig = config.get();

            // Set config for Jedis Pool
            poolConfig.setMaxTotal(cacheConfig.getPoolSize());
            poolConfig.setMaxWaitMillis(4000);
            poolConfig.setBlockWhenExhausted(false);
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestOnReturn(true);
            poolConfig.setTestWhileIdle(true);

            redissonConfig.useSingleServer().setAddress(cacheConfig.getHost() + ":" + cacheConfig.getPort());
            redissonConfig.useSingleServer().setConnectionPoolSize(cacheConfig.getPoolSize());
            pool = new JedisPool(poolConfig, cacheConfig.getHost(), cacheConfig.getPort());

            // Add Health check
            // commented out because there is a thread/memory leak
            // environment.healthChecks().register("Redis HealthCheck", new RedisHealthCheck(redissonConfig));
            environment.lifecycle().manage(new Managed() {

                @Override
                public void stop() throws Exception {
                    pool.destroy();
                    log.info("Jedis Pool Destroyed.");
                }

                @Override
                public void start() throws Exception {
                    log.info("Initializing Jedis Pool.");
                }
            });
        }
        environment.jersey().getResourceConfig().registerInstances(pool);
    }

    @Override
    public void initialize(final Bootstrap<?> bootstrap) {
    }

    @Override
    public Optional<RedisConfiguration> getRedisConfiguration(final T configuration) {
        return Optional.absent();
    }
}
