package com.learning.websocket;

import com.google.inject.Inject;
import com.learning.config.LearningConfiguration;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.redisson.Config;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import javax.servlet.annotation.WebServlet;

/**
 * Created by kaustubh on 3/19/16.
 */
public class EventWebSocketServlet extends WebSocketServlet
{
    @Inject
    private Pool cacheConnectionPool;

    @Inject
    private Config redissonConfig;

    @Inject
    private LearningConfiguration configuration;

    @Override
    public void configure(WebSocketServletFactory factory)
    {
        // factory.getPolicy().setIdleTimeout(10000); // 10 seconds
        // factory.register(EventWebSocket.class);
        factory.setCreator(new WebSocketCreator() {

            @Override
            public Object createWebSocket(final ServletUpgradeRequest request, final ServletUpgradeResponse response) {
                return new EventWebSocket(cacheConnectionPool, redissonConfig, configuration);
            }
        });
    }
}
