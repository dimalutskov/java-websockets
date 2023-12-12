package com.dlapp.spaceships;

import com.dlapp.spaceships.game.GameRoom;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServerApp implements ServletContextListener {

    private static ServerApp sInstance;

    public static ServerApp instance() {
        return sInstance;
    }

    private GameRoom gameRoom = new GameRoom("room_id");

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("@@@ contextInitialized");
        sInstance = this;
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {}

    private void init() {

    }

    public GameRoom getRoom() {
        return gameRoom;
    }

}
// heroku git:remote -a dl-websockets
// git push heroku master