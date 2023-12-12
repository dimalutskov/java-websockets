package com.dlapp.spaceships;

import com.dlapp.spaceships.game.desc.AliveEntityDesc;
import com.dlapp.spaceships.game.entity.PlayerEntity;
import com.dlapp.spaceships.game.GameProtocol;
import com.dlapp.spaceships.game.GameRoom;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket")
public class WebSocketEndpoint {

    private PlayerEntity player;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("@@@ onOpen " + this + " " + session.getId());
        GameRoom room = ServerApp.instance().getRoom();
        this.player = new PlayerEntity(room, AliveEntityDesc.SPACESHIP_DESC, session);
        room.connectPlayer(player);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        GameRoom room = ServerApp.instance().getRoom();
        room.onClientMessage(player, message);

        // TODO
        String[] split = message.split(";");
        if (split.length > 0 && split[0].equals(GameProtocol.CLIENT_MSG_SET_SERVER_DELAY)) {
            room.updateServerInterval(Long.parseLong(split[1]));
        }
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("@@@ onClose " + this + " " + session.getId());

        GameRoom room = ServerApp.instance().getRoom();
        room.disconnectPlayer(player);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("@@@ onError: " + session.getId() + " " + throwable);
        // Do error handling here
    }

}