package com.baeldung.websocket.game;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GamePlayer extends GameObject {
    private final Session session;

    private int shotSpeed = 300; // TODO
    private long shotId = 0;

    private List<GameObject> pendingObjects = new ArrayList<>();

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

    public synchronized void onMessage(String message) {
        try {
            handleMessage(message);
        } catch (Exception e) {
            System.out.println("Error parsing client message: " + message + ". " + e);
        }
    }

    private void handleMessage(String message) {
        long time = System.currentTimeMillis();
        System.out.println("@@@ handleMessage: " + message);

        String[] split = message.split(";");
        switch (split[0])  {
            case GameProtocol.CLIENT_MSG_MOVEMENT: {
                int x = Integer.parseInt(split[2]);
                int y = Integer.parseInt(split[3]);
                int angle = Integer.parseInt(split[4]);
                int speed = Integer.parseInt(split[5]);
                update(time, x, y, angle, speed);
                break;
            }

            case GameProtocol.CLIENT_MSG_SKILL_ON: {
                int skillId = Integer.parseInt(split[2]);
                if (true) { // TODO Shot skill
                    int x = Integer.parseInt(split[3]);
                    int y = Integer.parseInt(split[4]);
                    int angle = Integer.parseInt(split[5]);
                    handleShot(time, x, y, angle);
                }
                break;
            }

            case GameProtocol.CLIENT_MSG_SKILL_OFF: {
                int skillId = Integer.parseInt(split[2]);
                break;
            }

        }
    }

    @Override
    void proceed(long time, List<GameObject> objectsToAdd) {
        super.proceed(time, objectsToAdd);

        for (GameObject obj : pendingObjects) {
            obj.proceed(time, objectsToAdd);
        }
        objectsToAdd.addAll(pendingObjects);
        pendingObjects.clear();
    }

    private void handleShot(long time, int x, int y, int angle) {
        // Create shot object
        GameObject shot = new GameObject(getId() + "_" + shotId, GameProtocol.GAME_OBJECT_TYPE_SHOT, x, y, angle);
        shot.update(time, shot.getX(), shot.getY(), getAngle(), shotSpeed);
        shot.setDestroyTime(time + 5000);
        pendingObjects.add(shot);
        shotId++;
        System.out.println("@@@ createdShotObject " + shotId);
    }

    static class PlayerMessage {
        public final String message;
        public final long timestamp;
        PlayerMessage(String message, long timestamp) {
            this.message = message;
            this.timestamp = timestamp;
        }
    }
}
