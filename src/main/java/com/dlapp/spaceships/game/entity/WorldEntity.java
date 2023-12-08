package com.dlapp.spaceships.game.entity;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class WorldEntity {

    private final String id;
    private final int type;

    private int size = 1;

    private int xPos;
    private int yPos;

    private int angle;

    private long destroyTime;
    private boolean isDestroyed;

    private final List<EntityInfluence> influences = new ArrayList<>();

    private final WorldEntityMovement movement = new WorldEntityMovement();

    private final Rectangle2D.Double rect = new Rectangle2D.Double();

    public WorldEntity(String id, int type) {
        this(id, type, 0, 0, 0);
    }

    public WorldEntity(String id, int type, int x, int y, int angle) {
        this.id = id;
        this.type = type;
        this.xPos = x;
        this.yPos = y;
        this.angle = angle;
    }

    public WorldEntity copy() {
        return new WorldEntity(id, type, xPos, yPos, angle);
    }

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
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

    public int getSize() {
        return size;
    }

    public Rectangle2D.Double getRect() {
        float halfSize = size / 2.0f;
        rect.x = getX() - halfSize;
        rect.y = getY() - halfSize;
        rect.width = size;
        rect.height = size;
        return rect;
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

    public void updateSize(int size) {
        this.size = size;
    }

    public void update(long time, int x, int y, int angle, int speed) {
        movement.update(x, y);
        update(time, angle, speed);
    }

    public void update(long time, int angle) {
        movement.setAngle(angle);
        movement.step(time);
    }

    public void update(long time, int angle, int speed) {
        movement.setAngle(angle);
        movement.setSpeed(speed);
        movement.step(time);
    }

    public void attachInfluence(EntityInfluence influence) {
        influences.add(influence);
    }

//    public void detachInfluence(String id) {
//        influences.removeIf(i -> i.id.equals(id));
//    }

    public void proceed(long time, List<WorldEntity> objectsToAdd) {
        movement.step(time);
        xPos = (int) movement.getCurX();
        yPos = (int) movement.getCurY();
        angle = (int) movement.getAngle();

        influences.removeIf(i -> applyInfluence(i, time));

        if (destroyTime > 0 && time > destroyTime) {
            destroy();
        }
    }

    protected boolean applyInfluence(EntityInfluence influence, long time) {
        return true;
    }

    public void onCollision(WorldEntity entity) {
        System.out.println("@@@ onCollision " + getId() + " " + entity.getId());
    }

    public void onCollisionEnd(WorldEntity entity) {
        System.out.println("@@@ onCollisionEnd " + getId() + " " + entity.getId());
    }

    public String getStateString() {
        return id + "," +
                type + "," +
                size + "," +
                xPos + "," +
                yPos + "," +
                angle + ",";
    }

}
