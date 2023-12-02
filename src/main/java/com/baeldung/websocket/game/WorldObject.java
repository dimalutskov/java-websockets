package com.baeldung.websocket.game;

import java.util.List;

public class WorldObject {
    private final String id;
    private final int type;

    private int xPos;
    private int yPos;

    private int angle;

    private long destroyTime;
    private boolean isDestroyed;

    private GameObjectMovement movement = new GameObjectMovement();

    public WorldObject(String id, int type) {
        this(id, type, 0, 0, 0);
    }

    public WorldObject(String id, int type, int x, int y, int angle) {
        this.id = id;
        this.type = type;
        this.xPos = x;
        this.yPos = y;
        this.angle = angle;
    }

    public WorldObject copy() {
        return new WorldObject(id, type, xPos, yPos, angle);
    }

    public String getId() {
        return id;
    }

    public int getX() {
        return xPos;
    }

    public int getY() {
        return yPos;
    }

    public int getAngle() {
        return angle;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    protected void destroy() {
        isDestroyed = true;
    }

    public void setDestroyTime(long destroyTime) {
        this.destroyTime = destroyTime;
    }

    void update(long time, int x, int y, int angle, int speed) {
        movement.update(x, y);
        update(time, angle, speed);
    }

    void update(long time, int angle) {
        movement.setAngle(angle);
        movement.step(time);
    }

    void update(long time, int angle, int speed) {
        movement.setAngle(angle);
        movement.setSpeed(speed);
        movement.step(time);
    }

    void proceed(long time, List<WorldObject> objectsToAdd) {
        movement.step(time);
        xPos = (int) movement.getCurX();
        yPos = (int) movement.getCurY();
        if (destroyTime > 0 && time > destroyTime) {
            destroy();
        }
    }

    String getStateString() {
        return id + "," +
                type + "," +
                xPos + "," +
                yPos + "," +
                angle + ",";
    }

}
