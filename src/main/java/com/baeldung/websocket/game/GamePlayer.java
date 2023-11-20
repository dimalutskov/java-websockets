package com.baeldung.websocket.game;

import com.baeldung.websocket.GameProtocol;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GamePlayer extends GameObject {
    private final Session session;

    private final List<String> pendingClientMessages = new ArrayList<>();

    public GamePlayer(Session session) {
        super(session.getId(), GameProtocol.GAME_OBJECT_TYPE_PLAYER);
        this.session = session;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GamePlayer roomUser = (GamePlayer) o;
        return session.getId().equals(roomUser.session.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(session.getId());
    }

    void addPendingMessage(String message) {
        pendingClientMessages.add(message);
    }

    void send(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
