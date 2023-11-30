package com.baeldung.websocket.game;

import java.util.*;

public class WorldRoom {

    private static final int WORLD_STATES_KEEP_COUNT = 3;

    private static final long UPDATE_STATE = 1000;

    // key - objectId
    private final List<WorldObject> gameObjects = new ArrayList(100); // TODO

    private final List<GamePlayer> players = new ArrayList();

    private final List<WorldState> worldStates = new ArrayList<>();

    private Timer gameProcessingTimer;

    public synchronized void connectPlayer(GamePlayer player) {
        players.add(player);
        gameObjects.add(player);
        if (players.size() == 1) {
            gameProcessingTimer = new Timer();
            // Update game state task
            gameProcessingTimer.schedule(new TimerTask() {
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
        player.send(GameProtocol.SERVER_MSG_RESPONSE_CONNECTED + ";" + player.getId() + ";" + serverInfo + ";"); // TODO world state

        onObjectAdded(player, System.currentTimeMillis());
    }

    public synchronized void disconnectPlayer(GamePlayer player) {
        players.remove(player);
        gameObjects.remove(player);
        if (players.size() == 0) {
            if (gameProcessingTimer != null) gameProcessingTimer.cancel();
        }
        // Disconnect message
        onObjectDestroyed(player, System.currentTimeMillis());
    }

    public synchronized void onClientMessage(GamePlayer player, String message) {
        System.out.println("@@@ handleMessage: " + message);
        try {
            String[] split = message.split(";");
            List<WorldObject> objectsToAdd = new ArrayList<>();
            player.onMessage(split, objectsToAdd);
            gameObjects.addAll(objectsToAdd);

            if (!gameObjects.isEmpty() && split[0].equals(GameProtocol.CLIENT_MSG_SKILL_ON)) {
                // Response client with new objects ids
                StringBuilder responseMsg = new StringBuilder(GameProtocol.SERVER_MSG_RESPONSE_SKILL_OBJECTS).append(";")
                        .append(split[1]).append(";"); // skillId
                for (WorldObject object : objectsToAdd) {
                    responseMsg.append(object.getId()).append(";");
                }
                player.send(responseMsg.toString());
            }
        } catch (Exception e) {
            System.out.println("Error parsing client message: " + message + ". " + e);
        }
    }

    private synchronized void proceed(long time) {
        List<WorldObject> objectsToAdd = new ArrayList<>();
        Iterator<WorldObject> objectsIt = gameObjects.listIterator();
        while (objectsIt.hasNext()) {
            WorldObject object = objectsIt.next();
            object.proceed(time, objectsToAdd);
            if (object.isDestroyed()) {
                objectsIt.remove();
                onObjectDestroyed(object, time);
            }
        }
        for (WorldObject addedObject : objectsToAdd) {
            gameObjects.add(addedObject);
            onObjectAdded(addedObject, time);
        }
    }

    private void onObjectAdded(WorldObject object, long time) {
        broadcast(GameProtocol.SERVER_MSG_OBJECT_ADDED + ";" + time + ";" + object.getStateString());
    }

    private void onObjectDestroyed(WorldObject object, long time) {
        broadcast(GameProtocol.SERVER_MSG_OBJECT_DESTROYED + ";" + time + ";" + object.getStateString());
    }

    private synchronized void updateWorldState(long time) {
        StringBuilder stateString = new StringBuilder()
                .append(GameProtocol.SERVER_MSG_STATE).append(";");
        stateString.append(time).append(";");

        List<WorldObject> objects = new ArrayList<>();
        for (WorldObject obj : gameObjects) {
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
        gameProcessingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                proceed(time);
                updateWorldState(time);
            }
        }, 0, interval);
    }

}
