package com.baeldung.websocket.game;

import java.util.*;

public class GameRoom {

    private static final long UPDATE_STATE = 100;
    private static final long BROADCAST_STATE_INTERVAL = 1000;

    // key - objectId
    private final List<GameObject> gameObjects = new ArrayList(100); // TODO

    private final List<GamePlayer> players = new ArrayList();

    private Timer gameProcessingTimer;
    private Timer stateBroadcastTimer;

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
                }
            }, 0, UPDATE_STATE);
            // Broadcast state
            stateBroadcastTimer = new Timer();
            stateBroadcastTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    broadcastState();
                }
            }, 0, BROADCAST_STATE_INTERVAL);
        }
        // Response to client
        player.send(GameProtocol.SERVER_MSG_CONNECT_ID + ";" + player.getId() + ";" + proceedIteration);
        // Connect message
        broadcast(GameProtocol.SERVER_MSG_PLAYER_CONNECT + ";"  + player.getId());
    }

    public synchronized void disconnectPlayer(GamePlayer player) {
        players.remove(player);
        gameObjects.remove(player);
        if (players.size() == 0) {
            if (gameProcessingTimer != null) gameProcessingTimer.cancel();
            if (stateBroadcastTimer != null) stateBroadcastTimer.cancel();
        }
        // Disconnect message
        broadcast(GameProtocol.SERVER_MSG_PLAYER_DISCONNECT + ";"  + player.getId());
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
            }
        }
        gameObjects.addAll(objectsToAdd);
        proceedIteration++;
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
        if (stateBroadcastTimer != null) {
            stateBroadcastTimer.cancel();
        }

        stateBroadcastTimer = new Timer();
        stateBroadcastTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                broadcastState();
            }
        }, 0, BROADCAST_STATE_INTERVAL);
    }

}
