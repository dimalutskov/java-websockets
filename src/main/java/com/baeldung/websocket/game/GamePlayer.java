package com.baeldung.websocket.game;

import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class GamePlayer extends GameObject {
    private final Session session;

    private final List<String> pendingClientMessages = new CopyOnWriteArrayList<>();

    private final int maxSpeed = 20; // TODO

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

    void send(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addPendingMessage(String message) {
        pendingClientMessages.add(message);
    }

    synchronized void handlePendingMessages() {
        for (String message : pendingClientMessages) {
            try {
                handleMessage(message);
            } catch (Exception e) {
                System.out.println("Error parsing client message: " + message + ". " + e);
            }
        }
        pendingClientMessages.clear();
    }

    private void handleMessage(String message) {
        System.out.println("@@@ handleMessage: " + message);

        String[] split = message.split(";");
        switch (split[0])  {
            case GameProtocol.CLIENT_MSG_MOVEMENT:
                int angle = Integer.parseInt(split[2]);
                int progress = Integer.parseInt(split[3]);
                update(angle, maxSpeed * (progress / 100.0f));
                break;
        }
    }

}
