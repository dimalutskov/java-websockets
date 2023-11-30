package com.baeldung.websocket;

import com.baeldung.websocket.game.GamePlayer;
import com.baeldung.websocket.game.GameProtocol;
import com.baeldung.websocket.game.WorldRoom;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket")
public class WebSocketEndpoint {

    private static WorldRoom sRoom = new WorldRoom();

    private GamePlayer player;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("@@@ onOpen " + this + " " + session.getId());
        this.player = new GamePlayer(session);
        sRoom.connectPlayer(player);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        sRoom.onClientMessage(player, message);

        // TODO
        String[] split = message.split(";");
        if (split.length > 0 && split[0].equals(GameProtocol.CLIENT_MSG_SET_SERVER_DELAY)) {
            sRoom.updateServerInterval(Long.parseLong(split[1]));
        }
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("@@@ onClose " + this + " " + session.getId());

        sRoom.disconnectPlayer(player);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("@@@ onError: " + session.getId() + " " + throwable);
        // Do error handling here
    }

}