package com.dlapp.spaceships.game;

import com.dlapp.spaceships.game.desc.EntityDesc;
import com.dlapp.spaceships.game.desc.GameWorldDesc;
import com.dlapp.spaceships.game.object.GameObjectInfluence;
import com.dlapp.spaceships.game.object.GameEntity;
import com.dlapp.spaceships.game.object.GameObject;

import java.util.*;

public class GameWorld implements IGameWorld {

    private static final long UPDATE_FRAME_INTERVAL = 25;
    private static final long STATE_BROADCAST_INTERVAL = 1000;

    private final String id;
    private final GameWorldDesc desc;

    private final GameWorldCollisions collisionsHandler = new GameWorldCollisions();
    private final List<GameObject> gameObjects = new ArrayList(100); // TODO
    private final List<GamePlayer> players = new ArrayList();

    private Timer gameProcessingTimer;

    private long lastStateBroadcastTime;

    public GameWorld(String id, GameWorldDesc desc) {
        this.id = id;
        this.desc = desc;
        addTestObjects();
    }

    public String getId() {
        return id;
    }

    @Override
    public GameWorldDesc getDesc() {
        return desc;
    }

    @Override
    public void addGameObject(GameObject gameObject, long time) {
        gameObjects.add(gameObject);
        collisionsHandler.registerObject(gameObject);
        onObjectAdded(gameObject, time);
    }

    @Override
    public GameObject getGameObject(String id) {
        for (GameObject gameObject : gameObjects) {
            if (gameObject.getId().equals(id)) return gameObject;
        }
        return null;
    }

    @Override
    public boolean checkPastCollisions(GameObject gameObject, long time, GameWorldCollisions.CollisionCallback callback) {
        return collisionsHandler.checkCollisions(gameObject, time, callback);
    }

    @Override
    public void onGameObjectApplyInfluence(GameObject gameObject, GameObjectInfluence influence, int... values) {
        StringBuilder msg = new StringBuilder(GameProtocol.SERVER_MSG_INFLUENCE_ON + ";" +
                System.currentTimeMillis() + ";" +
                gameObject.getId() + ";" +
                influence.attachTime + ";" +
                influence.type + "," +
                influence.skillType + "," +
                influence.ownerId + ",");
        for (int value : values) msg.append(value).append(",");
        broadcast(msg.toString());
    }

    @Override
    public void onGameObjectDetachInfluence(GameObject gameObject, GameObjectInfluence influence) {
        String msg = GameProtocol.SERVER_MSG_INFLUENCE_OFF + ";" +
                System.currentTimeMillis() + ";" +
                gameObject.getId() + ";" +
                influence.type + ";" +
                influence.skillType + ";" +
                influence.ownerId;
        broadcast(msg);
    }

    @Override
    public synchronized void joinPlayer(GamePlayer player) {
        players.add(player);
        if (players.size() == 1) {
            startWorld();
        }
    }

    @Override
    public synchronized boolean leavePlayer(GamePlayer player) {
        players.remove(player);
        if (players.size() == 0) {
            stopWorld();
        }
        return true;
    }

    private void startWorld() {
        gameProcessingTimer = new Timer();
        // Update game state task
        gameProcessingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                proceed(time);
                if (time - lastStateBroadcastTime >= STATE_BROADCAST_INTERVAL) {
                    updateWorldState(time);
                    lastStateBroadcastTime = time;
                }
            }
        }, 0, UPDATE_FRAME_INTERVAL);
    }

    private void stopWorld() {
        if (gameProcessingTimer != null) gameProcessingTimer.cancel();
    }

    private synchronized void proceed(long time) {
        collisionsHandler.checkCollisions();

        Iterator<GameObject> objectsIt = gameObjects.listIterator();
        while (objectsIt.hasNext()) {
            GameObject object = objectsIt.next();
            object.proceed(time);
            if (object.isDestroyed()) {
                objectsIt.remove();
                onObjectDestroyed(object, object.getDestroyTime());
            }
        }
    }

    private void onObjectAdded(GameObject object, long time) {
        broadcast(GameProtocol.SERVER_MSG_OBJECT_ADDED + ";" +
                System.currentTimeMillis() + ";" +
                time + ";" +
                object.toSocketString());
    }

    private void onObjectDestroyed(GameObject object, long time) {
        broadcast(GameProtocol.SERVER_MSG_OBJECT_DESTROYED + ";" +
                System.currentTimeMillis() + ";" +
                time + ";" +
                object.toSocketString());
    }

    private synchronized void updateWorldState(long time) {
        StringBuilder stateString = new StringBuilder()
                .append(GameProtocol.SERVER_MSG_WORLD_STATE).append(";");
        stateString.append(time).append(";");

        for (GameObject obj : gameObjects) {
            stateString.append(obj.toSocketString()).append(";");
        }

        broadcast(stateString.toString());
    }

    private synchronized void broadcast(String message) {
        for (GamePlayer player : players) {
            player.send(message);
        }
    }

    /// temp
    private void addTestObjects() {
        // Static object
        GameObject staticObject = new GameEntity(this, "test_static", EntityDesc.SPACESHIP_DESC, 230, 230, 0);
        collisionsHandler.registerObject(staticObject);
        gameObjects.add(staticObject);

        GameObject staticObject2 = new GameEntity(this, "test_static2", EntityDesc.SPACESHIP_DESC, -100, 230, 0);
        collisionsHandler.registerObject(staticObject2);
        gameObjects.add(staticObject2);
    }

}
