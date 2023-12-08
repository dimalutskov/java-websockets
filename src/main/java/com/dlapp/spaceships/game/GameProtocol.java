package com.dlapp.spaceships.game;

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
     */
    public static final String SERVER_MSG_RESPONSE_CONNECTED = "connected";

    /**
     * Provides current game state with all required game objects, etc.
     * {MSG_TYPE};{SERVER_TIME};{OBJECT_STATE};{OBJECT_STATE};...{OBJECT_STATE};
     * {OBJECT_STATE} = id,type,x,y,angle...(rest type related props)
     * {SPACESHIP_STATE} = {OBJECT_STATE},health,energy
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
     * skillParams - {x};{y};{angle} - for single shots
     */
    public static final String CLIENT_MSG_SKILL_ON = "skillON";

    /**
     * Provides player skill attributes
     * {MSG_TYPE};{SERVER_ESTIMATED_TIME};{skillID};{skillDuration};
     */
    public static final String CLIENT_MSG_SKILL_OFF = "skillOFF";

    /// DEBUG MESSAGES
    public static final String CLIENT_MSG_SET_SERVER_DELAY = "setServerDelay";

};

// Client:
// setServerDelay;200
// move;0;20;20;45;50
// skillON;0;1;0;0;45
// skillOFF;0;1
