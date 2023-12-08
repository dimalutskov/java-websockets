package com.dlapp.spaceships.game;

import com.dlapp.spaceships.game.entity.PlayerEntity;
import com.dlapp.spaceships.game.entity.WorldEntity;

import java.util.*;

public class GameRoom {

    private static final int WORLD_STATES_KEEP_COUNT = 3;

    private static final long UPDATE_STATE = 1000;

    private final String id;

    private final List<WorldEntity> gameObjects = new ArrayList(100); // TODO
    private final List<PlayerEntity> players = new ArrayList();

    private final List<WorldState> worldStates = new ArrayList<>();

    private Timer gameProcessingTimer;

    public GameRoom(String id) {
        this.id = id;
        addTestObjects();
    }

    public String getId() {
        return id;
    }

    public synchronized void connectPlayer(PlayerEntity player) {
        players.add(player);
        gameObjects.add(player);
        if (players.size() == 1) {
            gameProcessingTimer = new Timer();
            // Update game state task
            gameProcessingTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    long time = System.currentTimeMillis();
                    proceed(time);
                    updateWorldState(time);
                }
            }, 0, UPDATE_STATE);
        }
        // Response to client
        String serverInfo = System.currentTimeMillis() + "," + UPDATE_STATE;
        player.send(GameProtocol.SERVER_MSG_RESPONSE_CONNECTED + ";" + serverInfo + ";" + player.getId() + ";");
        // Send last state???
        onObjectAdded(player, System.currentTimeMillis());
    }

    public synchronized void disconnectPlayer(PlayerEntity player) {
        players.remove(player);
        gameObjects.remove(player);
        if (players.size() == 0) {
            if (gameProcessingTimer != null) gameProcessingTimer.cancel();
        }
        // Disconnect message
        onObjectDestroyed(player, System.currentTimeMillis());
    }

    public synchronized void onClientMessage(PlayerEntity player, String message) {
        System.out.println("@@@ handleMessage: " + message);
        try {
            String[] split = message.split(";");
            List<WorldEntity> objectsToAdd = new ArrayList<>();
            player.onMessage(split, objectsToAdd);
            gameObjects.addAll(objectsToAdd);

            if (!gameObjects.isEmpty() && split[0].equals(GameProtocol.CLIENT_MSG_SKILL_ON)) {
                // Response client with new objects ids
                StringBuilder responseMsg = new StringBuilder(GameProtocol.SERVER_MSG_RESPONSE_SKILL_OBJECTS).append(";")
                        .append(split[1]).append(";"); // skillId
                for (WorldEntity object : objectsToAdd) {
                    responseMsg.append(object.getId()).append(";");
                }
                player.send(responseMsg.toString());
            }
        } catch (Exception e) {
            System.out.println("Error parsing client message: " + message + ". " + e);
        }
    }

    private synchronized void proceed(long time) {
        List<WorldEntity> objectsToAdd = new ArrayList<>();
        Iterator<WorldEntity> objectsIt = gameObjects.listIterator();
        while (objectsIt.hasNext()) {
            WorldEntity object = objectsIt.next();
            object.proceed(time, objectsToAdd);
            if (object.isDestroyed()) {
                objectsIt.remove();
                onObjectDestroyed(object, time);
            }
        }
        for (WorldEntity addedObject : objectsToAdd) {
            gameObjects.add(addedObject);
            onObjectAdded(addedObject, time);
        }

        updateTestObjects(time);
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

        List<WorldEntity> objects = new ArrayList<>();
        for (WorldEntity obj : gameObjects) {
            objects.add(obj.copy());
            stateString.append(obj.getStateString()).append(";");
        }

        WorldState state = new WorldState(time, objects);
        worldStates.add(state);
        if (worldStates.size() > WORLD_STATES_KEEP_COUNT) {
            worldStates.remove(0);
        }

        broadcast(stateString.toString());
    }

    private synchronized void broadcast(String message) {
        for (PlayerEntity player : players) {
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
    private void addTestObjects() {
        for (int i = 0; i < 2; i++) {
            String id = "test_" + i;
            WorldEntity object = new WorldEntity(id, GameConstants.ENTITY_TYPE_SPACESHIP);
            int x = (int) (200 * Math.random());
            int y = (int) (200 * Math.random());
            int angle = (int) (180 * Math.random());
            int speed = (int) (30 + 50 * Math.random());
            object.update(System.currentTimeMillis(), x, y, angle, speed);
            testObjects.add(object);
            gameObjects.add(object);
        }
    }
    private void updateTestObjects(long time) {
        for (WorldEntity obj : testObjects) {
            double angle = 0;
            if (obj.getX() > 500) {
                if (obj.getY() < 0) {
                    angle = 270 - 90 * Math.random();
                } else {
                    angle = 270 + 90 * Math.random();
                }
            }
            if (obj.getX() < -500) {
                if (obj.getY() < 0) {
                    angle = 90 + 90 * Math.random();
                } else {
                    angle = 90 - 90 * Math.random();
                }
            }
            if (obj.getY() > 500) {
                if (obj.getX() < 0) {
                    angle = 0 + 90 * Math.random();
                } else {
                    angle = 270 + 90 * Math.random();
                }
            }
            if (obj.getY() < - 500) {
                if (obj.getX() < 0) {
                    angle = 90 + 90 * Math.random();
                } else {
                    angle = 180 + 90 * Math.random();
                }
            }
            if (angle != 0) {
                obj.update(time, (int) angle);
            }
        }
    }

}
