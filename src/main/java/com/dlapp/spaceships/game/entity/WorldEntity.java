package com.dlapp.spaceships.game.entity;

import com.dlapp.spaceships.game.GameWorld;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class WorldEntity {

    // States will be kept by this time
    private static final long KEEP_STATES_TIME = 2000;

    protected final GameWorld gameWorld;

    private final String id;
    private final int type;

    private final Stack<EntityState> states = new Stack<>();

    private long destroyTime;
    private boolean isDestroyed;

    private final List<EntityInfluence> influences = new ArrayList<>();

    private final WorldEntityMovement movement = new WorldEntityMovement();

    public WorldEntity(GameWorld world, String id, int type, int size, int x, int y, int angle) {
        this(world, System.currentTimeMillis(), id, type, size, x, y, angle);
    }

    public WorldEntity(GameWorld world, long time, String id, int type, int size, int x, int y, int angle) {
        this.gameWorld = world;
        this.id = id;
        this.type = type;
        states.push(createEntityState(time, size, x, y, angle));
        movement.update(x, y);
        movement.setAngle(angle);
    }

    protected EntityState createEntityState(long time, int size, int x, int y, int angle) {
        return new EntityState(time, size, x, y, angle);
    }

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public EntityState getState() {
        return states.isEmpty() ? null : states.peek();
    }

    public EntityState findState(long time) {
        long timeDiff = Long.MAX_VALUE;
        EntityState nearestState = null;
        for (EntityState state : states) {
            long diff = Math.abs(state.createTime - time);
            if (diff > timeDiff) {
                return nearestState;
            }
            nearestState = state;
            timeDiff = diff;
        }
        return getState();
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public void destroy() {
        isDestroyed = true;
        if (destroyTime == 0) {
            destroyTime = System.currentTimeMillis();
        }
    }

    public long getDestroyTime() {
        return destroyTime;
    }

    public void setDestroyTime(long destroyTime) {
        this.destroyTime = destroyTime;
    }

    public void updateSize(int size) {
        getState().setSize(size);
    }

    public void update(long time, int x, int y, int angle, int speed) {
        movement.update(x, y);
        getState().setX(x);
        getState().setY(y);
        update(time, angle, speed);
    }

    public void update(long time, int angle) {
        movement.setAngle(angle);
        movement.step(time);
    }

    public void update(long time, int angle, int speed) {
        getState().setAngle(angle);
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

    public void proceed(long time) {
        movement.step(time);

        addNewState(time);
        if (time - states.get(states.size() - 1).createTime > KEEP_STATES_TIME) {
            states.remove(states.size() - 1);
        }

        influences.removeIf(i -> applyInfluence(i, time));

        if (destroyTime > 0 && time > destroyTime) {
            destroy();
        }
    }

    protected void addNewState(long time) {
        states.push(createEntityState(time, getState().getSize(),
                (int) movement.getCurX(),
                (int) movement.getCurY(),
                (int) movement.getAngle()));
    }

    protected boolean applyInfluence(EntityInfluence influence, long time) {
        return true;
    }

    public void onCollision(WorldEntity entity) {
        System.out.println("@@@ onCollision " + getId() + " " + getState().getRect()
                +  " || " + entity.getId() + " " + entity.getState().getRect());
    }

    public void onCollisionEnd(WorldEntity entity) {
        System.out.println("@@@ onCollisionEnd " + getId() + " " + entity.getId());
    }

    public final String getStateString() {
        return id + "," +
                type + "," +
                getState().toStateString();
    }

    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();
        result.put("id", id);
        result.put("type", type);
        result.put("size", getState().getSize());
        result.put("x", getState().getX());
        result.put("y", getState().getY());
        result.put("angle", getState().getAngle());
        return result;
    }

}
