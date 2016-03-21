package com.learning.rediswebsocket;

import com.learning.util.JsonUtilities;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Pool;

import java.util.List;

/**
 * Created by kaustubh on 3/20/16.
 */
@Slf4j
@Setter
@Getter
public class EventChannel {

    protected Pool<Jedis> cacheConnectionPool = null;
    protected Jedis subscriberJedi = null;
    protected Jedis publisherJedi = null;
    @Getter
    protected EventDataChannelHandler channelHandler = null;
    protected JsonUtilities jsonUtil = new JsonUtilities();

    public EventChannel(final Pool<Jedis> pool, final EventDataChannelHandler handler) {
        this.cacheConnectionPool = pool;
        this.channelHandler = handler;
    }

    public void subscribe(final String channel) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    subscriberJedi = cacheConnectionPool.getResource();
                    subscriberJedi.clientSetname("SUBSCRIBE." + channel);
                    log.info("Beginning subscription to channel : '{}'. This is a blocking thread until unsubscribed. "
                        + "Please remember to unsubscribe when done.", channel);
                    // The above blocks this thread. Once above statement
                    // execution completes,
                    // subscription has effectively ended.
                    subscriberJedi.subscribe(channelHandler, channel);
                    log.info("Unsubscribed from channel '{}'.", channel);
                } catch (Throwable t) {
                    log.error("Subscription to channel '{}' failed. {}", channel, t.getMessage(), t);
                    cacheConnectionPool.returnResource(subscriberJedi);
                } finally {
                    try {
                        cacheConnectionPool.returnResource(subscriberJedi);
                        subscriberJedi = null;

                    } catch (JedisException je) {
                        cacheConnectionPool.returnBrokenResource(subscriberJedi);
                        subscriberJedi = null;

                    }

                }
            }
        }).start();

    }

    public void unsubscribe(final String channelName) {
        if (channelHandler.isSubscribed()) {
            try {
                channelHandler.unsubscribe(channelName);
            } catch (final Throwable t) {
                log.error("Exception while trying to unsubscribe from Event Channel: {}", t.getMessage(), t);
            }
        }
    }

    public void publish(final String channelName, final String message) {
        try {
            publisherJedi = cacheConnectionPool.getResource();
            if (null != publisherJedi) {
                publisherJedi.clientSetname("PUBLISH." + channelName);
                log.info("Publishing to Channel : " + channelName + ".");
                final Transaction transaction = publisherJedi.multi();
                transaction.publish(channelName, message);
                final List<Object> result = transaction.exec();
                if (log.isInfoEnabled()) {
                    log.info("****************************************************Transaction Executed: Output :");
                    if (null != result && !result.isEmpty()) {
                        for (Object object : result) {
                            log.info(object.toString());
                        }
                    }
                }
            }
            cacheConnectionPool.returnResource(publisherJedi);
        } catch (final Throwable t) {
            log.error("Publish to channel '" + channelName + "' failed.");
            cacheConnectionPool.returnResource(publisherJedi);
        }
    }

    public boolean isSubscribed() {
        return channelHandler.isSubscribed();
    }
}
