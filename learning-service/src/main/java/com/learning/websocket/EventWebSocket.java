package com.learning.websocket;

import com.google.inject.Inject;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketFrame;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.redisson.Config;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import java.io.IOException;

/**
 * Created by kaustubh on 3/19/16.
 */
@WebSocket
public class EventWebSocket
{
    private Pool<Jedis> cacheConnectionPool;
    private Config redissonConfig;

    public EventWebSocket(Pool<Jedis> cacheConnectionPool, Config redissonConfig) {
        this.cacheConnectionPool = cacheConnectionPool;
        this.redissonConfig = redissonConfig;
    }

    @OnWebSocketConnect
    public void onConnect(Session sess)
    {
        System.out.println("Socket Connected: " + sess);
    }

    @OnWebSocketMessage
    public void onMessage(final Session sess, String message)
    {
        System.out.println("Received TEXT message: " + message);

        try {
            sess.getRemote().sendString("From Server >>> "+message);
        } catch (IOException ioe) {
            System.err.println("Error while sending message");
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason)
    {
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
    }

    @OnWebSocketError
    public void onWebSocketError(Throwable cause)
    {
        cause.printStackTrace(System.err);
    }

    @OnWebSocketFrame
    public void onWebSocketError(Frame frame)
    {
        System.out.println("Inside FRAME #"+frame);
    }
}
