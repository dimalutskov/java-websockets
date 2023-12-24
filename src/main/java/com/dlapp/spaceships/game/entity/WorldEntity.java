package com.dlapp.spaceships.game.entity;

import com.dlapp.spaceships.game.GameWorld;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public abstract class WorldEntity {

    protected final GameWorld gameWorld;

    private final String id;
    private final int type;

    private int size;

    private int x;
    private int y;
    private int angle;

    private int prevX;
    private int prevY;

    private long destroyTime;
    private boolean isDestroyed;

    private final List<EntityInfluence> influences = new ArrayList<>();

    private final WorldEntityMovement movement = new WorldEntityMovement();

    private final Rectangle2D.Double rect = new Rectangle2D.Double();

    public WorldEntity(GameWorld world, String id, int type, int size, int x, int y, int angle) {
        this.gameWorld = world;
        this.id = id;
        this.type = type;
        this.size = size;
        this.x = x;
        this.y = y;
        this.angle = angle;
        movement.update(x, y);
        movement.setAngle(angle);
    }

    public abstract WorldEntity copy();

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPrevX() {
        return prevX;
    }

    public int getPrevY() {
        return prevY;
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

    public void destroy() {
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
        this.y = y;
        this.x = x;
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

    public void detachInfluence(EntityInfluence influence) {
        influences.remove(influence);
        gameWorld.onEntityDetachInfluence(this, influence);
    }

    public void proceed(long time, List<WorldEntity> objectsToAdd) {
        movement.step(time);
        prevX = x;
        prevY = y;
        x = (int) movement.getCurX();
        y = (int) movement.getCurY();
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
        System.out.println("@@@ onCollision " + getId() + " " + getRect()
                +  " || " + entity.getId() + " " + entity.getRect());
    }

    public void onCollisionEnd(WorldEntity entity) {
        System.out.println("@@@ onCollisionEnd " + getId() + " " + entity.getId());
    }

    public String getStateString() {
        return id + "," +
                type + "," +
                size + "," +
                x + "," +
                y + "," +
                angle + ",";
    }

    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();
        result.put("id", id);
        result.put("type", type);
        result.put("size", size);
        result.put("x", x);
        result.put("y", y);
        result.put("angle", angle);
        return result;
    }

}
