package com.learning.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;

/**
 * Created by kaustubh on 3/19/16.
 */
public class EventWebSocketAdapter extends WebSocketAdapter
{
    @Override
    public void onWebSocketConnect(Session sess)
    {
        super.onWebSocketConnect(sess);
        System.out.println("#ADAPTER Socket Connected: " + sess);
    }

    @Override
    public void onWebSocketText(String message)
    {
        super.onWebSocketText(message);
        System.out.println("#ADAPTER Received TEXT message: " + message);

        try {
            super.getSession().getRemote().sendString("#ADAPTER From Server >>> "+message);
        } catch (IOException ioe) {
            System.err.println("#ADAPTER Error while sending message");
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode,reason);
        System.out.println("#ADAPTER Socket Closed: [" + statusCode + "] " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause)
    {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }
}
