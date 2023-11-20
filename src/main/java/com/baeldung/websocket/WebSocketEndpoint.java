package com.baeldung.websocket;

import com.baeldung.websocket.game.GamePlayer;
import com.baeldung.websocket.game.GameRoom;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket")
public class WebSocketEndpoint {

    private static GameRoom sRoom = new GameRoom();

    private GamePlayer user;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("@@@ onOpen " + this + " " + session.getId());
        this.user = new GamePlayer(session);
        sRoom.connectPlayer(user);
    }

    @OnMessage
    public void onMessage(Session session, String message) {

    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("@@@ onClose " + this + " " + session.getId());

        sRoom.disconnectPlayer(user);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("@@@ onError: " + session.getId() + " " + throwable);
        // Do error handling here
    }

}