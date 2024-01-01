package com.dlapp.spaceships.game;

import com.dlapp.spaceships.game.desc.AliveEntityDesc;
import com.dlapp.spaceships.game.entity.EntityInfluence;
import com.dlapp.spaceships.game.entity.PlayerEntity;
import com.dlapp.spaceships.game.entity.WorldAliveEntity;
import com.dlapp.spaceships.game.entity.WorldEntity;

import java.util.*;

public class GameRoom implements GameWorld {

    private static final long UPDATE_FRAME_INTERVAL = 25;
    private static final long STATE_BROADCAST_INTERVAL = 1000;

    private final String id;

    private final WorldCollisionsHandler collisionsHandler = new WorldCollisionsHandler();
    private final List<WorldEntity> entities = new ArrayList(100); // TODO
    private final List<GamePlayer> players = new ArrayList();

    private Timer gameProcessingTimer;

    private long lastStateBroadcastTime;

    public GameRoom(String id) {
        this.id = id;
        addTestObjects();
    }

    public String getId() {
        return id;
    }

    @Override
    public void addEntity(WorldEntity entity, long time) {
        entities.add(entity);
        collisionsHandler.registerEntity(entity);
        onObjectAdded(entity, time);
    }

    @Override
    public WorldEntity getEntity(String id) {
        for (WorldEntity entity : entities) {
            if (entity.getId().equals(id)) return entity;
        }
        return null;
    }

    @Override
    public boolean checkPastCollisions(WorldEntity entity, long time, WorldCollisionsHandler.CollisionCallback callback) {
        return collisionsHandler.checkCollisions(entity, time, callback);
    }

    @Override
    public void onEntityApplyInfluence(WorldEntity entity, EntityInfluence influence, int... values) {
        StringBuilder msg = new StringBuilder(GameProtocol.SERVER_MSG_INFLUENCE_ON + ";" +
                System.currentTimeMillis() + ";" +
                entity.getId() + ";" +
                influence.type + ";" +
                influence.skillType + ";" +
                influence.ownerId + ";");
        for (int value : values) msg.append(value).append(";");
        broadcast(msg.toString());
    }

    @Override
    public void onEntityDetachInfluence(WorldEntity entity, EntityInfluence influence) {
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
            List<WorldEntity> addedObjects = new ArrayList<>();
            player.onMessage(split, addedObjects);

            if (split[0].equals(GameProtocol.CLIENT_MSG_JOIN)) {
                player.send(GameProtocol.SERVER_MSG_RESPONSE_JOIN + ";" +
                        System.currentTimeMillis() + ";" +
                        player.getEntity().getStateString());
            } else if (split[0].equals(GameProtocol.CLIENT_MSG_SKILL_ON) && !addedObjects.isEmpty()) {
                // Response client with new objects ids
                StringBuilder responseMsg = new StringBuilder(GameProtocol.SERVER_MSG_RESPONSE_SKILL_OBJECTS).append(";")
                        .append(split[1]).append(";"); // skillId
                for (WorldEntity object : addedObjects) {
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

        Iterator<WorldEntity> objectsIt = entities.listIterator();
        while (objectsIt.hasNext()) {
            WorldEntity object = objectsIt.next();
            object.proceed(time);
            if (object.isDestroyed()) {
                objectsIt.remove();
                onObjectDestroyed(object, object.getDestroyTime());
            }
        }
    }

    private void onObjectAdded(WorldEntity object, long time) {
        broadcast(GameProtocol.SERVER_MSG_OBJECT_ADDED + ";" + time + ";" + object.getStateString());
    }

    private void onObjectDestroyed(WorldEntity object, long time) {
        broadcast(GameProtocol.SERVER_MSG_OBJECT_DESTROYED + ";" + time + ";" + object.getStateString());
    }

    private synchronized void updateWorldState(long time) {
        StringBuilder stateString = new StringBuilder()
                .append(GameProtocol.SERVER_MSG_STATE).append(";");
        stateString.append(time).append(";");

        for (WorldEntity obj : entities) {
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
    public void updateServerInterval(long interval) {
        if (gameProcessingTimer != null) {
            gameProcessingTimer.cancel();
        }

        gameProcessingTimer = new Timer();
        gameProcessingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                proceed(time);
                updateWorldState(time);
            }
        }, 0, interval);
    }


    private List<WorldEntity> testObjects = new ArrayList<>();
    private long lastTestObjectsUpdate = 0;
    private void addTestObjects() {
//        for (int i = 0; i < 3; i++) {
//            String id = "test_" + i;
//            int x = (int) (200 * Math.random());
//            int y = (int) (200 * Math.random());
//            int angle = (int) (180 * Math.random());
//            int speed = (int) (30 + 50 * Math.random());
//            WorldEntity object = new WorldEntity.Simple(this, id, GameConstants.ENTITY_TYPE_SPACESHIP, 100, x, y, angle);
//            object.update(System.currentTimeMillis(), x, y, angle, speed);
//        }

        // Static object
        WorldEntity staticObject = new WorldAliveEntity(this, "test_static", AliveEntityDesc.SPACESHIP_DESC, 230, 230, 0);
        collisionsHandler.registerEntity(staticObject);
        entities.add(staticObject);

        WorldEntity staticObject2 = new WorldAliveEntity(this, "test_static2", AliveEntityDesc.SPACESHIP_DESC, -100, 230, 0);
        collisionsHandler.registerEntity(staticObject2);
        entities.add(staticObject2);
    }

//    private void updateTestObjects(long time) {
//        if (lastTestObjectsUpdate != 0 && time - lastTestObjectsUpdate > 5000) {
//            for (WorldEntity obj : testObjects) {
//                obj.update(time, 0, 0, 0, 0);
//                obj.update(time + 1, (int) (360 * Math.random()), (int) (30 + 50 * Math.random()));
//            }
//            lastTestObjectsUpdate = time;
//            return;
//        }
//
//        for (WorldEntity obj : testObjects) {
//            double angle = 0;
//            if (obj.getX() > 500) {
//                if (obj.getY() < 0) {
//                    angle = 270 - 90 * Math.random();
//                } else {
//                    angle = 270 + 90 * Math.random();
//                }
//            }
//            if (obj.getX() < -500) {
//                if (obj.getY() < 0) {
//                    angle = 90 + 90 * Math.random();
//                } else {
//                    angle = 90 - 90 * Math.random();
//                }
//            }
//            if (obj.getY() > 500) {
//                if (obj.getX() < 0) {
//                    angle = 0 + 90 * Math.random();
//                } else {
//                    angle = 270 + 90 * Math.random();
//                }
//            }
//            if (obj.getY() < - 500) {
//                if (obj.getX() < 0) {
//                    angle = 90 + 90 * Math.random();
//                } else {
//                    angle = 180 + 90 * Math.random();
//                }
//            }
//            if (angle != 0) {
//                obj.update(time, (int) angle);
//            }
//        }
//        lastTestObjectsUpdate = time;
//    }

}
