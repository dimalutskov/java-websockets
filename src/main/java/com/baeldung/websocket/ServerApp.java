package com.baeldung.websocket;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServerApp implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("@@@ contextInitialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {}

}
