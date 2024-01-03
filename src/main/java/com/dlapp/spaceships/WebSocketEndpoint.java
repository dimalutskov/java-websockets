package com.dlapp.spaceships;

import com.dlapp.spaceships.game.GamePlayer;
import com.dlapp.spaceships.game.GameWorld;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket")
public class WebSocketEndpoint {

    private GamePlayer player;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("@@@ onOpen " + this + " " + session.getId());
        GameWorld world = ServerApp.instance().getWorld();
        this.player = new GamePlayer(world, session);
        world.connectPlayer(player);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        GameWorld world = ServerApp.instance().getWorld();
        world.onClientMessage(player, message);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("@@@ onClose " + this + " " + session.getId());

        GameWorld world = ServerApp.instance().getWorld();
        world.disconnectPlayer(player);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("@@@ onError: " + session.getId() + " " + throwable);
        // Do error handling here
    }

}