package com.baeldung.websocket.game;

/**
 * First int before first ; in string message(both client and server) defines message type
 * "{MSG_TYPE};{MSG_CONTENT};"
 *
 * {SERVER_TIME} - server time
 * {SERVER_ESTIMATED_TIME} - server time which is calculated on client side based on last server state
 */
public class GameProtocol {

    /////////////////// SERVER_MESSAGES //////////////////////

    /**
     * Provides server information and objectId for this connected client to retrieve current player state from server state message
     * {MSG_TYPE};{SERVER_INFO};{OBJECT_ID};{PLAYER_INFO}
     * SERVER_INFO: "{SERVER_TIME},{UPDATE_INTERVAL}"
     * PLAYER_INFO: "{HEALTH},{ENERGY},{SPEED}"
     */
    public static final String SERVER_MSG_RESPONSE_CONNECTED = "connected";

    /**
     * Provides current game state with all required game objects, etc.
     * {MSG_TYPE};{SERVER_TIME};{OBJECT_STATE};{OBJECT_STATE};...{OBJECT_STATE};
     * {OBJECT_STATE} = id,type,x,y,angle,speed...(rest type related props)
     */
    public static final String SERVER_MSG_STATE = "state";

    /**
     * Provides object which was added to the game world
     * {MSG_TYPE};{SERVER_TIME};{OBJECT_STATE};
     */
    public static final String SERVER_MSG_OBJECT_ADDED = "objectAdded";

    /**
     * Provides object which was removed from the game world
     * {MSG_TYPE};{SERVER_TIME};{OBJECT_STATE};
     */
    public static final String SERVER_MSG_OBJECT_DESTROYED = "objectDestroyed";

    /**
     * Response to client when some skill activated and new game objects created on server side. Proved new object server ids
     * {MSG_TYPE};{SERVER_TIME};{SKILL_ID};{OBJECT_ID};...{OBJECT_ID};
     */
    public static final String SERVER_MSG_RESPONSE_SKILL_OBJECTS = "skillObjects";


    /////////////////// CLIENT_MESSAGES ///////////////////////////

    /**
     * Provides player movement attributes
     * {MSG_TYPE};{SERVER_ESTIMATED_TIME};{x};{y};{angle};{speed}
     */
    public static final String CLIENT_MSG_MOVEMENT = "move";

    /**
     * Provides player skill attributes
     * {MSG_TYPE};{SERVER_ESTIMATED_TIME};{skillID};{skillParams};
     */
    public static final String CLIENT_MSG_SKILL_ON = "skillON";
    public static final String CLIENT_MSG_SKILL_OFF = "skillOFF";

    /// DEBUG MESSAGES
    public static final String CLIENT_MSG_SET_SERVER_DELAY = "setServerDelay";

    ///////////////////// GAME OBJECT TYPES /////////////////////////
    public static final int GAME_OBJECT_TYPE_PLAYER = 1;

    // object_id of shot types will be: "{owner_id}||{unique_id}"
    public static final int GAME_OBJECT_TYPE_SHOT = 2;

    public static final int GAME_OBJECT_TYPE_NPC = 3;

    public static final int SKILL_ID_SHOT = 1;
    public static final int SKILL_ID_SPEED = 2;
};

// Client:
// setServerDelay;200
// move;0;20;20;45;50
// skillON;0;1;0;0;45
// skillOFF;0;1
