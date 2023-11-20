package com.baeldung.websocket.game;

public class GameObject {
    private final String id;
    private final int type;

    private float xPos;
    private float yPos;

    private int angle;
    private float speed;

    private GameObjectMovement movement = new GameObjectMovement();

    public GameObject(String id, int type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    void update(int angle, float speed) {
        movement.setAngle(angle);
        movement.setSpeed(speed);
    }

    void proceed(long time) {
        movement.step(time);
    }

    String getStateString() {
        return id + "," + type + "," + xPos + "," + yPos;
    }

}
