package com.learning.redis;

import com.google.common.base.Optional;
import io.dropwizard.Configuration;

/**
 * Created by kaustubh on 3/20/16.
 */
public interface RedisConfigurationStrategy<T extends Configuration> {
    Optional<RedisConfiguration> getRedisConfiguration(T var1);
}
