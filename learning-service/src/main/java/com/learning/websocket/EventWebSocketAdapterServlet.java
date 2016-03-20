package com.learning.websocket;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.annotation.WebServlet;

/**
 * Created by kaustubh on 3/19/16.
 */
@WebServlet(name = "EventWebSocketAdapterServlet", urlPatterns = { "/learning/websocket/adapter/*" })
public class EventWebSocketAdapterServlet extends WebSocketServlet
{
    @Override
    public void configure(WebSocketServletFactory factory)
    {
        // factory.getPolicy().setIdleTimeout(10000); // 10 seconds
        factory.register(EventWebSocketAdapter.class);
    }
}
