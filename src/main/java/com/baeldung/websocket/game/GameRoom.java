package com.baeldung.websocket.game;

import java.util.*;

public class GameRoom {

    private static final long UPDATE_GAME_STATE_INTERVAL = 1000;

    // key - objectId
    private final List<GameObject> gameObjects = new ArrayList(100); // TODO

    private final List<GamePlayer> players = new ArrayList();

    private final List<String> pendingServerMessages = new ArrayList<>();

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
                    broadcast();
                }
            }, 0, UPDATE_GAME_STATE_INTERVAL);
        }
        // Connect message
        pendingServerMessages.add(GameProtocol.SERVER_MSG_PLAYER_CONNECT + ";"  + player.getId());
        // Response to client
        player.send(GameProtocol.SERVER_MSG_CONNECT_ID + ";" + player.getId() + ";" + proceedIteration);
    }

    public synchronized void disconnectPlayer(GamePlayer player) {
        players.remove(player);
        gameObjects.remove(player);
        if (players.size() == 0) {
            if (gameProcessingTimer != null) gameProcessingTimer.cancel();
        }
        // Disconnect message
        pendingServerMessages.add(GameProtocol.SERVER_MSG_PLAYER_DISCONNECT + ";"  + player.getId());
    }

    private synchronized void proceed() {
        long time = System.currentTimeMillis();
        for (GamePlayer player : players) {
            player.handlePendingMessages();
        }
        List<GameObject> objectsToAdd = new ArrayList<>();
        Iterator<GameObject> objectsIt = gameObjects.listIterator();
        while (objectsIt.hasNext()) {
            GameObject object = objectsIt.next();
            object.proceed(time, objectsToAdd);
            if (object.isDestroyed()) {
                objectsIt.remove();
            }
        }
        gameObjects.addAll(objectsToAdd);
        proceedIteration++;
    }

    private synchronized void broadcast() {
        StringBuilder state = new StringBuilder()
                .append(GameProtocol.SERVER_MSG_STATE).append(";")
                .append(proceedIteration).append(";");
        for (GameObject object : gameObjects) {
            state.append(object.getStateString()).append(";");
        }
        String stateString = state.toString();
        for (GamePlayer player : players) {
            for (String serverMsg : pendingServerMessages) {
                player.send(serverMsg);
            }
            pendingServerMessages.clear();
            player.send(stateString);
        }
    }

}
