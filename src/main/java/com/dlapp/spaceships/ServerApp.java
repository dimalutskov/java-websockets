package com.dlapp.spaceships;

import com.dlapp.spaceships.game.GamePlayer;
import com.dlapp.spaceships.game.GameWorld;
import com.dlapp.spaceships.game.desc.GameWorldDesc;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.ArrayList;
import java.util.List;

@WebListener
public class ServerApp implements ServletContextListener {

    private static ServerApp sInstance;

    public static ServerApp instance() {
        return sInstance;
    }

    private GameWorld gameWorld = new GameWorld("room_id",
            new GameWorldDesc(1000, 2000, 2000));

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("@@@ contextInitialized");
        sInstance = this;
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {}

    private void init() {

    }

    public GameWorld getWorld() {
        return gameWorld;
    }

}
// heroku git:remote -a dl-websockets
// git push heroku master