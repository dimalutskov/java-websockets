package com.baeldung.websocket.game;

/**
 * First int before first ; in string message(both client and server) defines message type
 *      "{MSG_TYPE};{MSG_CONTENT};"
 *
 * {SERVER_FRAME} - server iteration value to handle client/server event sequencing.
 */

public class GameProtocol {

    /////////////////// SERVER_MESSAGES //////////////////////

    /**
     * Provides server iteration and objectId for this connected client to retrieve current player state from server state message
     * {MSG_TYPE};{OBJECT_ID};{SERVER_FRAME}
     */
    public static final String SERVER_MSG_CONNECT_ID = "connected";

    /**
     * Provides current game state with all required game objects, etc.
     * {MSG_TYPE};{SERVER_FRAME};{OBJECT_STATE};{OBJECT_STATE};...{OBJECT_STATE};
     * {OBJECT_STATE} = id,type,health,energy,x,y,angle,speed...(rest type related props)
     */
    public static final String SERVER_MSG_STATE = "state";

    /**
     * Provides object which was added to the game world
     * {MSG_TYPE};{OBJECT_STATE};
     */
    public static final String SERVER_MSG_OBJECT_ADDED = "objectAdded";

    /**
     * Provides object which was removed from the game world
     * {MSG_TYPE};{OBJECT_STATE};
     */
    public static final String SERVER_MSG_OBJECT_DESTROYED = "objectDestroyed";


    /////////////////// CLIENT_MESSAGES ///////////////////////////

    /**
     * Provides player movement attributes
     * {MSG_TYPE};{SERVER_FRAME};{x};{y};{angle};{speed}
     */
    public static final String CLIENT_MSG_MOVEMENT = "move";

    /**
     * Provides player skill attributes
     * {MSG_TYPE};{SERVER_FRAME};{skillID};{skillParams};
     */
    public static final String CLIENT_MSG_SKILL_ON = "skillON";
    public static final String CLIENT_MSG_SKILL_OFF = "skillOFF";

    /// DEBUG MESSAGES
    public static final String CLIENT_MSG_SET_SERVER_DELAY = "setServerDelay";

    ///////////////////// GAME OBJECT TYPES /////////////////////////
    public static final int GAME_OBJECT_TYPE_PLAYER = 1;
    public static final int GAME_OBJECT_TYPE_SHOT = 2;
};

// Client:
// setServerDelay;200
// move;0;20;20;180;50
// skillON;0;1;0;0;180
// skillOFF;0;1
