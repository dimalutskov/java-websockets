package com.dlapp.spaceships;

import com.dlapp.spaceships.game.GamePlayer;
import com.dlapp.spaceships.game.GameProtocol;
import com.dlapp.spaceships.game.GameWorld;
import com.dlapp.spaceships.game.object.GameObject;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;

@ServerEndpoint(value = "/websocket")
public class WebSocketEndpoint {

    private static List<GamePlayer> connectedPlayers = new ArrayList<>();

    private GamePlayer player;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("@@@ onOpen " + this + " " + session.getId());
        player = new GamePlayer(session);
        connectedPlayers.add(player);

        // Response to client
        player.send(GameProtocol.SERVER_MSG_RESPONSE_CONNECTED + ";");
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println("@@@ onMessage: " + message);
        try {
            String[] split = message.split(";");
            player.onMessage(split);
        } catch (Exception e) {
            System.out.println("Error processing client message: " + message + ". " + e);
        }

    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("@@@ onClose " + this + " " + session.getId());

        player.onDisconnect();
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("@@@ onError: " + session.getId() + " " + throwable);
        // Do error handling here
    }

}