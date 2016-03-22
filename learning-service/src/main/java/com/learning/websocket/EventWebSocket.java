package com.learning.websocket;

import com.learning.config.LearningConfiguration;
import com.learning.realtime.LockManager;
import com.learning.realtime.Operation;
import com.learning.realtime.OperationProcessor;
import com.learning.realtime.OperationType;
import com.learning.rediswebsocket.EventChannel;
import com.learning.rediswebsocket.EventDataChannelHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.UnknownSessionException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketFrame;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.core.RLock;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import java.io.IOException;

/**
 * Created by kaustubh on 3/19/16.
 */
@WebSocket
@Slf4j
@Getter
@Setter
public class EventWebSocket
{
    private Pool<Jedis> cacheConnectionPool;
    private Config redissonConfig;
    private LearningConfiguration configuration;
    private Redisson redisson = null;
    private LockManager lockManager = new LockManager();

    // Channels
    private EventChannel dataChannel = null;
    private String dataChannelName = "";

    public EventWebSocket(Pool<Jedis> cacheConnectionPool, Config redissonConfig, LearningConfiguration configuration) {
        this.cacheConnectionPool = cacheConnectionPool;
        this.redissonConfig = redissonConfig;
        this.configuration = configuration;
        this.redisson = Redisson.create(redissonConfig);
        log.info("*************************************************************** This is a new EventWebSocket Instance.");
    }

    @OnWebSocketConnect
    public void onConnect(Session websocketSession)
    {
        //System.out.println("Socket Connected: " + websocketSession);

        val redisPrefix = configuration.getRedisConfiguration().getPrefix();

        // Initialize channel subscription to event data channel
        dataChannel = new EventChannel(cacheConnectionPool, new EventDataChannelHandler(websocketSession, cacheConnectionPool));
        dataChannelName = redisPrefix+".websocket.100";
        dataChannel.subscribe(dataChannelName);

        log.info("Connection established. Proceed.");
    }

    @OnWebSocketMessage
    public void onMessage(final Session websocketSession, String message) throws IOException
    {
        try {
            keepSessionAlive(websocketSession);
        } catch (InvalidSessionException se) {
            websocketSession.close(4001, "Session expired.");
            return;
        }
        try {
            Operation operation = new Operation(OperationType.ADD_MESSAGE, message);
            OperationProcessor opProcessor = new OperationProcessor(dataChannel, cacheConnectionPool, configuration);
            opProcessor.handleNewOperation(operation);
            RLock lock = redisson.getLock(lockManager.getLockName(100L));
            if (lockManager.tryLock(redisson, cacheConnectionPool)) {
                opProcessor.processOperation();
                lock.unlock();
            }
        } catch (IllegalStateException ise) {
            // this can happen when a message is received but the session has expired
            // throwing the error will cause the websocket to close, which will trigger the UI to reload/alert the user
            if (null != ise.getCause()
                && (ise.getCause() instanceof UnknownSessionException
                || ise.getCause() instanceof ExpiredSessionException)) {
                log.info("Looks like the session has expired", ise);
                websocketSession.close(4001, "Session expired.");
            }
            log.error("Unable to deserialize from client: {}", ise.getMessage(), ise);
        } catch (Exception e) {
            log.error("Unable to deserialize from client: {}", e.getMessage(), e);
        }


        //System.out.println("***** Received TEXT message: " + message);

//        try {
//            websocketSession.getRemote().sendString("From Server >>> "+message);
//        } catch (IOException ioe) {
//            System.err.println("Error while sending message");
//        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason)
    {
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
        unsubscribe();
        redisson.shutdown();
    }

    @OnWebSocketError
    public void onWebSocketError(Throwable error)
    {
        log.error(error.getMessage(), error);
        error.printStackTrace(System.err);
        unsubscribe();
        redisson.shutdown();
    }

    @OnWebSocketFrame
    public void onWebSocketError(Frame frame)
    {
        // System.out.println("Inside FRAME #"+frame);
    }

    private void keepSessionAlive(final Session websocketSession) throws InvalidSessionException {
        if (websocketSession instanceof WebSocketSession) {
            ((WebSocketSession) websocketSession).open();
        } else {
            throw new UnknownSessionException();
        }
    }

    private void unsubscribe() {
        try {
            dataChannel.unsubscribe(dataChannelName);
        } catch (Exception e) {
            log.error("Unable to unsubscribe from channels: {}", e.getMessage(), e);
            throw e;
        }
    }
}
