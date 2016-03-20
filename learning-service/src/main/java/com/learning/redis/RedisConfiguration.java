package com.learning.redis;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import java.util.List;

/**
 * Created by kaustubh on 3/20/16.
 */
@Getter
@Setter
public class RedisConfiguration {

    private String masterName;

    @DefaultValue("127.0.0.1")
    private String host;

    @DefaultValue("6379")
    private int port;

    @DefaultValue("1000")
    private int poolSize;

    @NotNull
    @NotEmpty
    private String prefix;

}
