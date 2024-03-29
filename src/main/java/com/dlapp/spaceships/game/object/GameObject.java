package com.dlapp.spaceships.game.object;

import com.dlapp.spaceships.game.IGameWorld;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameObject {

    // States will be kept by this time
    private static final long KEEP_STATES_TIME = 2000;

    protected final IGameWorld gameWorld;

    private final String id;
    private final int type;

    private final Stack<GameObjectState> states = new Stack<>();

    private long destroyTime;
    private boolean isDestroyed;

    private final List<GameObjectInfluence> influences = new ArrayList<>();

    private final GameObjectMovement movement = new GameObjectMovement();

    public GameObject(IGameWorld world, String id, int type, int size, int x, int y, int angle) {
        this(world, System.currentTimeMillis(), id, type, size, x, y, angle);
    }

    public GameObject(IGameWorld world, long time, String id, int type, int size, int x, int y, int angle) {
        this.gameWorld = world;
        this.id = id;
        this.type = type;
        states.push(createState(time, size, x, y, angle));
        movement.update(x, y);
        movement.setAngle(angle);
    }

    protected GameObjectState createState(long time, int size, int x, int y, int angle) {
        return new GameObjectState(time, size, x, y, angle);
    }

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public GameObjectState getState() {
        return states.isEmpty() ? null : states.peek();
    }

    public GameObjectState findState(long time) {
        long timeDiff = Long.MAX_VALUE;
        GameObjectState nearestState = null;
        synchronized (states) {
            for (GameObjectState state : states) {
                long diff = Math.abs(state.time - time);
                if (diff > timeDiff) {
                    return nearestState;
                }
                nearestState = state;
                timeDiff = diff;
            }
        }
        return getState();
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public void destroy() {
        isDestroyed = true;
        long time = System.currentTimeMillis();
        if (destroyTime == 0 || destroyTime > time) {
            destroyTime = time;
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

    public void attachInfluence(GameObjectInfluence influence) {
        synchronized (influences) {
            influences.add(influence);
        }
    }

    public void detachInfluence(GameObjectInfluence influence) {
        synchronized (influences) {
            influences.remove(influence);
            gameWorld.onGameObjectDetachInfluence(this, influence);
        }
    }

    public void proceed(long time) {
        movement.step(time);

        synchronized (states) {
            addNewState(time);
            if (time - states.get(states.size() - 1).time > KEEP_STATES_TIME) {
                states.remove(states.size() - 1);
            }
        }

        synchronized (influences) {
            influences.removeIf(i -> applyInfluence(i, time));
        }

        if (destroyTime > 0 && time > destroyTime) {
            destroy();
        }
    }

    protected void addNewState(long time) {
        states.push(createState(time, getState().getSize(),
                (int) movement.getCurX(),
                (int) movement.getCurY(),
                (int) movement.getAngle()));
    }

    protected boolean applyInfluence(GameObjectInfluence influence, long time) {
        return true;
    }

    public void onCollision(GameObject gameObject) {
        System.out.println("@@@ onCollision " + getId() + " " + getState().getRect()
                +  " || " + gameObject.getId() + " " + gameObject.getState().getRect());
    }

    public void onCollisionEnd(GameObject gameObject) {
        System.out.println("@@@ onCollisionEnd " + getId() + " " + gameObject.getId());
    }

    public final String toSocketString() {
        return id + "," +
                type + "," +
                getState().toSocketString();
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
