package com.dlapp.spaceships.game;

import com.dlapp.spaceships.game.desc.EntityDesc;
import com.dlapp.spaceships.game.object.GameObjectInfluence;
import com.dlapp.spaceships.game.object.GameEntity;
import com.dlapp.spaceships.game.object.GameObject;

import java.util.*;

public class GameWorld implements IGameWorld {

    private static final long UPDATE_FRAME_INTERVAL = 25;
    private static final long STATE_BROADCAST_INTERVAL = 1000;

    private final String id;

    private final WorldCollisionsHandler collisionsHandler = new WorldCollisionsHandler();
    private final List<GameObject> entities = new ArrayList(100); // TODO
    private final List<GamePlayer> players = new ArrayList();

    private Timer gameProcessingTimer;

    private long lastStateBroadcastTime;

    public GameWorld(String id) {
        this.id = id;
        addTestObjects();
    }

    public String getId() {
        return id;
    }

    @Override
    public void addEntity(GameObject entity, long time) {
        entities.add(entity);
        collisionsHandler.registerEntity(entity);
        onObjectAdded(entity, time);
    }

    @Override
    public GameObject getEntity(String id) {
        for (GameObject entity : entities) {
            if (entity.getId().equals(id)) return entity;
        }
        return null;
    }

    @Override
    public boolean checkPastCollisions(GameObject entity, long time, WorldCollisionsHandler.CollisionCallback callback) {
        return collisionsHandler.checkCollisions(entity, time, callback);
    }

    @Override
    public void onEntityApplyInfluence(GameObject entity, GameObjectInfluence influence, int... values) {
        StringBuilder msg = new StringBuilder(GameProtocol.SERVER_MSG_INFLUENCE_ON + ";" +
                System.currentTimeMillis() + ";" +
                entity.getId() + ";" +
                influence.attachTime + ";" +
                influence.type + "," +
                influence.skillType + "," +
                influence.ownerId + ",");
        for (int value : values) msg.append(value).append(",");
        broadcast(msg.toString());
    }

    @Override
    public void onEntityDetachInfluence(GameObject entity, GameObjectInfluence influence) {
        String msg = GameProtocol.SERVER_MSG_INFLUENCE_OFF + ";" +
                System.currentTimeMillis() + ";" +
                entity.getId() + ";" +
                influence.type + ";" +
                influence.skillType + ";" +
                influence.ownerId;
        broadcast(msg);
    }

    public synchronized void connectPlayer(GamePlayer player) {
        players.add(player);

        if (players.size() == 1) {
            startWorld();
        }
        // Response to client
        String serverInfo = System.currentTimeMillis() + "," + STATE_BROADCAST_INTERVAL;
        player.send(GameProtocol.SERVER_MSG_RESPONSE_CONNECTED + ";" + serverInfo + ";");
    }

    public synchronized void disconnectPlayer(GamePlayer player) {
        players.remove(player);
        if (players.size() == 0) {
            stopWorld();
        }
        if (player.getEntity() != null) {
            player.getEntity().destroy();
        }
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

    public synchronized void onClientMessage(GamePlayer player, String message) {
        System.out.println("@@@ handleMessage: " + message);
        try {
            String[] split = message.split(";");
            List<GameObject> addedObjects = new ArrayList<>();
            player.onMessage(split, addedObjects);

            if (split[0].equals(GameProtocol.CLIENT_MSG_JOIN)) {
                player.send(GameProtocol.SERVER_MSG_RESPONSE_JOIN + ";" +
                        System.currentTimeMillis() + ";" +
                        player.getEntity().getStateString());
            } else if (split[0].equals(GameProtocol.CLIENT_MSG_SKILL_ON) && !addedObjects.isEmpty()) {
                // Response client with new objects ids
                StringBuilder responseMsg = new StringBuilder(GameProtocol.SERVER_MSG_RESPONSE_SKILL_OBJECTS).append(";")
                        .append(split[1]).append(";"); // skillId
                for (GameObject object : addedObjects) {
                    responseMsg.append(object.getId()).append(";");
                }
                player.send(responseMsg.toString());
            }
        } catch (Exception e) {
            System.out.println("Error parsing client message: " + message + ". " + e);
        }
    }

    private synchronized void proceed(long time) {
        collisionsHandler.checkCollisions();

        Iterator<GameObject> objectsIt = entities.listIterator();
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
                object.getStateString());
    }

    private void onObjectDestroyed(GameObject object, long time) {
        broadcast(GameProtocol.SERVER_MSG_OBJECT_DESTROYED + ";" +
                System.currentTimeMillis() + ";" +
                time + ";" +
                object.getStateString());
    }

    private synchronized void updateWorldState(long time) {
        StringBuilder stateString = new StringBuilder()
                .append(GameProtocol.SERVER_MSG_STATE).append(";");
        stateString.append(time).append(";");

        for (GameObject obj : entities) {
            stateString.append(obj.getStateString()).append(";");
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
        collisionsHandler.registerEntity(staticObject);
        entities.add(staticObject);

        GameObject staticObject2 = new GameEntity(this, "test_static2", EntityDesc.SPACESHIP_DESC, -100, 230, 0);
        collisionsHandler.registerEntity(staticObject2);
        entities.add(staticObject2);
    }

}
