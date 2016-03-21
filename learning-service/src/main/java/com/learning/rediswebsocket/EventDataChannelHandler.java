package com.learning.rediswebsocket;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.util.Pool;

import java.io.IOException;

/**
 * Created by kaustubh on 3/21/16.
 */
@Slf4j
public class EventDataChannelHandler extends JedisPubSub {

    protected Session webSocketSession = null;
    protected Pool<Jedis> pool = null;

    public EventDataChannelHandler(final Session session, final Pool<Jedis> connPool) {
        this.webSocketSession = session;
        this.pool = connPool;
    }

    @Override
    public void onMessage(final String channel, final String message) {
        log.debug("Message received. Channel: {}, Msg: {}", channel, message);
        if (null != webSocketSession && webSocketSession.isOpen()) {
            try {
                webSocketSession.getRemote().sendString(message);
            } catch (final IOException e) {
                log.error("Unable to publish message: '{}' to channel: '{}' because of '{}'", message, channel, e.getMessage(), e);
                //TODO should we do what StoryboardActiveUsersChannelHandler does, and throw new WebSocketException(e); ?
            }

        }
    }

    @Override
    public void onPMessage(final String pattern, final String channel, final String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSubscribe(final String channel, final int subscribedChannels) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUnsubscribe(final String channel, final int subscribedChannels) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPUnsubscribe(final String pattern, final int subscribedChannels) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPSubscribe(final String pattern, final int subscribedChannels) {
        // TODO Auto-generated method stub

    }

}

