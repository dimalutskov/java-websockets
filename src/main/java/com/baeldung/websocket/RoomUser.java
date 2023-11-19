package com.baeldung.websocket;

import javax.websocket.Session;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class RoomUser {
    private final Session session;

    private float xPos;
    private float yPos;
    private float speed;
    private int angle;

    RoomUser(Session session) {
        this.session = session;
    }

    void send(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomUser roomUser = (RoomUser) o;
        return session.getId().equals(roomUser.session.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(session.getId());
    }
}
