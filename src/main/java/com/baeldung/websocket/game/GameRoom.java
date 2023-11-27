package com.baeldung.websocket.game;

import java.util.*;

public class GameRoom {

    private static final long UPDATE_STATE = 1000;

    // key - objectId
    private final List<GameObject> gameObjects = new ArrayList(100); // TODO

    private final List<GamePlayer> players = new ArrayList();

    private Timer gameProcessingTimer;

    private long proceedIteration;

    public synchronized void connectPlayer(GamePlayer player) {
        players.add(player);
        gameObjects.add(player);
        if (players.size() == 1) {
            gameProcessingTimer = new Timer();
            // Update game state task
            gameProcessingTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    proceed();
                    broadcastState();
                }
            }, 0, UPDATE_STATE);
        }
        // Response to client
        player.send(GameProtocol.SERVER_MSG_CONNECT_ID + ";" + player.getId() + ";" + proceedIteration);

        onObjectAdded(player);
    }

    public synchronized void disconnectPlayer(GamePlayer player) {
        players.remove(player);
        gameObjects.remove(player);
        if (players.size() == 0) {
            if (gameProcessingTimer != null) gameProcessingTimer.cancel();
        }
        // Disconnect message
        onObjectDestroyed(player);
    }

    public synchronized void onClientMessage(GamePlayer player, String message) {
        player.onMessage(message);
    }

    private synchronized void proceed() {
        long time = System.currentTimeMillis();
        List<GameObject> objectsToAdd = new ArrayList<>();
        Iterator<GameObject> objectsIt = gameObjects.listIterator();
        while (objectsIt.hasNext()) {
            GameObject object = objectsIt.next();
            object.proceed(time, objectsToAdd);
            if (object.isDestroyed()) {
                objectsIt.remove();
                onObjectDestroyed(object);
            }
        }
        for (GameObject addedObject : objectsToAdd) {
            gameObjects.add(addedObject);
            onObjectAdded(addedObject);
        }
        proceedIteration++;
    }

    private void onObjectAdded(GameObject object) {
        broadcast(GameProtocol.SERVER_MSG_OBJECT_ADDED + ";" + object.getStateString());
    }

    private void onObjectDestroyed(GameObject object) {
        broadcast(GameProtocol.SERVER_MSG_OBJECT_DESTROYED + ";" + object.getStateString());
    }

    private synchronized void broadcastState() {
        StringBuilder state = new StringBuilder()
                .append(GameProtocol.SERVER_MSG_STATE).append(";")
                .append(proceedIteration).append(";");
        for (GameObject object : gameObjects) {
            state.append(object.getStateString()).append(";");
        }
        String stateString = state.toString();
        broadcast(stateString);
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
                proceed();
                broadcastState();
            }
        }, 0, UPDATE_STATE);
    }

}
