package com.baeldung.websocket.game;

import com.baeldung.websocket.GameProtocol;

import javax.websocket.Session;
import java.util.*;

public class GameRoom {

    private static final long UPDATE_GAME_STATE_INTERVAL = 200;
    private static final long BROADCAST_GAME_STATE_INTERVAL = 1000;

    // key - objectId
    private final List<GameObject> gameObjects = new ArrayList(100); // TODO

    private final List<GamePlayer> players = new ArrayList();

    private final List<String> pendingServerMessages = new ArrayList<>();

    private Timer gameProcessingTimer;
    private Timer stateBroadcastTimer;

    private long proceedCount;

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
            }, 0, UPDATE_GAME_STATE_INTERVAL);
            // Share game state task
            stateBroadcastTimer = new Timer();
            stateBroadcastTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    broadcast();
                }
            }, 0, BROADCAST_GAME_STATE_INTERVAL);
        }
        // Connect message
        pendingServerMessages.add(GameProtocol.SERVER_MSG_PLAYER_CONNECT + ";"  + player.getId());
    }

    public synchronized void disconnectPlayer(GamePlayer player) {
        players.remove(player);
        gameObjects.remove(player);
        if (players.size() == 0) {
            if (gameProcessingTimer != null) gameProcessingTimer.cancel();
            if (stateBroadcastTimer != null) stateBroadcastTimer.cancel();
        }
        // Disconnect message
        pendingServerMessages.add(GameProtocol.SERVER_MSG_PLAYER_DISCONNECT + ";"  + player.getId());
    }

    public void handleMessage(Session session, String message) {
        Optional<GamePlayer> player = players.stream()
                .filter(gamePlayer -> gamePlayer.getId().equals(session.getId())).findFirst();
        if (player.isPresent()) {
            player.get().addPendingMessage(message);
        }
    }

    private synchronized void proceed() {
        long time = System.currentTimeMillis();
        for (GameObject object : gameObjects) {
            // Handle messages
            // Update state
            object.proceed(time);
        }
        proceedCount++;
    }

    private synchronized void broadcast() {
        StringBuilder state = new StringBuilder().append(GameProtocol.SERVER_MSG_STATE).append(";");
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
