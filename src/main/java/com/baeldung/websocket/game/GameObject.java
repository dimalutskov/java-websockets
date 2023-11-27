package com.baeldung.websocket.game;

import java.util.List;

public class GameObject {
    private final String id;
    private final int type;

    private int health;
    private int energy;

    private int xPos;
    private int yPos;

    private int angle;
    private int speed;

    private long destroyTime;
    private boolean isDestroyed;

    private GameObjectMovement movement = new GameObjectMovement();

    public GameObject(String id, int type) {
        this(id, type, 0, 0, 0);
    }

    public GameObject(String id, int type, int x, int y, int angle) {
        this.id = id;
        this.type = type;
        this.xPos = x;
        this.yPos = y;
        this.angle = angle;
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
        movement.setAngle(angle);
        movement.setSpeed(speed);
        movement.step(time);
    }

    void proceed(long time, List<GameObject> objectsToAdd) {
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
                health + "," +
                energy + "," +
                xPos + "," +
                yPos + "," +
                angle + "," +
                speed;
    }

}
