package com.baeldung.websocket.game;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class GamePlayer extends GameObject {
    private final Session session;

    // key - skillId, value - skill activation timestamp
    private Map<Integer, Long> activeSkills = new ConcurrentHashMap<>();

    private int maxSpeed = 100; // TODO
    private int shotSpeed = 200;
    private long fireRate = 1000; // TODO
    private long lastSkillTimestamp; // TODO
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
                int angle = Integer.parseInt(split[2]);
                int progress = Integer.parseInt(split[3]);
                update(angle, maxSpeed * (progress / 100.0f));
                break;
            }

            case GameProtocol.CLIENT_MSG_SKILL_ON: {
                int skillId = Integer.parseInt(split[2]);
                activeSkills.put(skillId, time);
                handleShot(time);
                break;
            }

            case GameProtocol.CLIENT_MSG_SKILL_OFF: {
                int skillId = Integer.parseInt(split[2]);
                activeSkills.remove(skillId);
                break;
            }

            // DEBUG
            case GameProtocol.CLIENT_MSG_SET_SPEED: {
                maxSpeed = Integer.parseInt(split[1]);
                break;
            }
            case GameProtocol.CLIENT_MSG_SET_SHOT_SPEED: {
                shotSpeed = Integer.parseInt(split[1]);
                break;
            }
            case GameProtocol.CLIENT_MSG_SET_FIRE_RATE: {
                fireRate = Integer.parseInt(split[1]);
                break;
            }
        }
    }

    @Override
    void proceed(long time, List<GameObject> objectsToAdd) {
        super.proceed(time, objectsToAdd);

        handleShot(time);
        objectsToAdd.addAll(pendingObjects);
        pendingObjects.clear();
    }

    private void handleShot(long time) {
        if (!activeSkills.isEmpty()) {
            if (time - lastSkillTimestamp > fireRate) {
                lastSkillTimestamp = time;
                // Create shot object
                GameObject shot = new GameObject(getId() + "_" + shotId, GameProtocol.GAME_OBJECT_TYPE_SHOT, getX(), getY(), getAngle());
                shot.update(getAngle(), shotSpeed);
                shot.setDestroyTime(time + 5000);
                pendingObjects.add(shot);
                shotId++;
            }
        }
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
