package com.baeldung.websocket.game;

/**
 * First int before first ; in string message(both client and server) defines message type
 *      "{MSG_TYPE};{MSG_CONTENT};"
 *
 * {SERVER_ITERATION} - server iteration value to handle client/server event sequencing.
 */

public class GameProtocol {

    /////////////////// SERVER_MESSAGES //////////////////////

    /**
     * Provides server iteration and objectId for this connected client to retrieve current player state from server state message
     * {MSG_TYPE};{OBJECT_ID};{SERVER_ITERATION}
     */
    public static final String SERVER_MSG_CONNECT_ID = "connected";

    /**
     * Provides current game state with all required game objects, etc.
     * {MSG_TYPE};{SERVER_ITERATION};{OBJECT_STATE};{OBJECT_STATE};...{OBJECT_STATE};
     * {OBJECT_STATE} = id;type;x;y;angle;...(rest type related props)
     */
    public static final String SERVER_MSG_STATE = "state";

    /**
     * Provides objectId for player which was connected to server
     * {MSG_TYPE};{OBJECT_ID};
     */
    public static final String SERVER_MSG_PLAYER_CONNECT = "playerConnected";

    /**
     * Provides objectId for player which was disconnected to server
     * {MSG_TYPE};{OBJECT_ID};
     */
    public static final String SERVER_MSG_PLAYER_DISCONNECT = "playerDisconnected";


    /////////////////// CLIENT_MESSAGES ///////////////////////////

    /**
     * Provides player movement attributes
     * {MSG_TYPE};{SERVER_ITERATION};{angle(int)};{progress(int)};
     */
    public static final String CLIENT_MSG_MOVEMENT = "move";

    /**
     * Provides player skill attributes
     * {MSG_TYPE};{SERVER_ITERATION};{skillID};{skillParams};
     */
    public static final String CLIENT_MSG_SKILL_ON = "skillON";
    public static final String CLIENT_MSG_SKILL_OFF = "skillOFF";

    /// DEBUG MESSAGES
    public static final String CLIENT_MSG_SET_SERVER_DELAY = "setServerDelay";
    public static final String CLIENT_MSG_SET_SPEED = "setSpeed";
    public static final String CLIENT_MSG_SET_SHOT_SPEED = "setShotSpeed";
    public static final String CLIENT_MSG_SET_FIRE_RATE = "setFireRate";

    ///////////////////// GAME OBJECT TYPES /////////////////////////
    public static final int GAME_OBJECT_TYPE_PLAYER = 1;
    public static final int GAME_OBJECT_TYPE_SHOT = 2;
};

// Client:
// setServerDelay;200
// move;0;180;50
// skillON;0;1
// skillOFF;0;1
