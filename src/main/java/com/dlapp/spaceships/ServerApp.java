package com.dlapp.spaceships;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServerApp implements ServletContextListener {

    private static ServerApp sInstance;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("@@@ contextInitialized");
        sInstance = this;
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {}

    private void init() {

    }

}
// heroku git:remote -a dl-websockets
// git push heroku master