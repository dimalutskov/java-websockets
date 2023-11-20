package com.baeldung.websocket;

public class GameProtocol {
    public static final int CLIENT_MSG_MOVEMENT = 1;

    public static final int SERVER_MSG_STATE = 1;
    public static final int SERVER_MSG_PLAYER_CONNECT = 2;
    public static final int SERVER_MSG_PLAYER_DISCONNECT = 3;

    public static final int GAME_OBJECT_TYPE_PLAYER = 1;
}
