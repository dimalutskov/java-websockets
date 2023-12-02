package com.baeldung.websocket.game;

import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class GamePlayer extends WorldObject {
    private final Session session;

    private float health;
    private float energy;

    private int shotSpeed = 300; // TODO
    private long shotId = 0;

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

    public synchronized void onMessage(String[] split, List<WorldObject> objectsToAdd) {
        long time = System.currentTimeMillis();
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
                    objectsToAdd.add(handleShot(time, x, y, angle));
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
    void proceed(long time, List<WorldObject> objectsToAdd) {
        super.proceed(time, objectsToAdd);
    }

    private WorldObject handleShot(long time, int x, int y, int angle) {
        // Create shot object
        WorldObject shot = new WorldObject(getId() + "_" + shotId, GameProtocol.GAME_OBJECT_TYPE_SHOT, x, y, angle);
        shot.update(time, x, y, angle, shotSpeed);
        shot.setDestroyTime(time + 5000);
        shotId++;
        return shot;
    }

    @Override
    String getStateString() {
        return super.getStateString() +
                Math.round(health) + "," +
                Math.round(energy) + ",";
    }
}
