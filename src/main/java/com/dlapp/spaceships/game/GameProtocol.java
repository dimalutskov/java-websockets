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
     * Provides server information and
     * {MSG_TYPE};{SERVER_INFO};
     */
    public static final String SERVER_MSG_RESPONSE_CONNECTED = "connected";

    /**
     * Provides new auth token and player game entities description
     * {MSG_TYPE};{SERVER_INFO};
     */
    public static final String SERVER_MSG_RESPONSE_AUTH = "auth";

    /**
     * Provides objectId for this joined client to retrieve current player state from server state message
     * {MSG_TYPE};{SERVER_TIME};{GAME_WORLD_INFO};{OBJECT_STATE}
     * GAME_WORLD_INFO: stateBroadcastInterval,worldWidth,worldHeight
     */
    public static final String SERVER_MSG_RESPONSE_JOIN= "join";

    /**
     * Sent when client detached from game world. When client leave request was performed in active state(
     * (e.g. battle) - client won't be detached and "false" will be returned
     * {MSG_TYPE};true(false);
     */
    public static final String SERVER_MSG_RESPONSE_LEAVE= "leave";

    /**
     * Provides current game state with all required game objects, etc.
     * {MSG_TYPE};{SERVER_TIME};{OBJECT_STATE};{OBJECT_STATE};...{OBJECT_STATE};
     * {OBJECT_STATE} = id,type,size,x,y,angle...(rest type related props)
     * {SPACESHIP_STATE} = {OBJECT_STATE},health,energy
     */
    public static final String SERVER_MSG_WORLD_STATE = "state";

    /**
     * Provides object which was added to the game world
     * {MSG_TYPE};{SERVER_TIME};{OBJECT_CREATED_TIME};{OBJECT_STATE};
     */
    public static final String SERVER_MSG_OBJECT_ADDED = "objectAdded";

    /**
     * Provides object which was removed from the game world
     * {MSG_TYPE};{SERVER_TIME};{OBJECT_DESTROY_TIME};{OBJECT_STATE};
     */
    public static final String SERVER_MSG_OBJECT_DESTROYED = "objectDestroyed";

    /**
     * Notify about any new active influence(skill, shot, recover, etc) applied to any world entity
     * {MSG_TYPE};{SERVER_TIME};{ENTITY_RECEIVER_ID};{INFLUENCE_ATTACH_TIME};{INFLUENCE_DESC}
     * INFLUENCE_DESC - influenceType,skillType,ownerId,values(described for each type)
     */
    public static final String SERVER_MSG_INFLUENCE_ON = "influenceOn";

    /**
     * Notify about stop applying influence. Notice - for single influences (like single shot or one time skills)
     * this message wont be sent
     * {MSG_TYPE};{SERVER_TIME};{ENTITY_RECEIVER_ID};{INFLUENCE_DESC};
     * INFLUENCE_DESC - influenceType,skillType,ownerId,values(described for each type)
     */
    public static final String SERVER_MSG_INFLUENCE_OFF = "influenceOff";


    /////////////////// CLIENT_MESSAGES ///////////////////////////

    /**
     * Login client. Client game entity's desc will be provided in response
     * {MSG_TYPE};{AUTH_TOKEN}
     */
    public static final String CLIENT_MSG_AUTH = "auth";

    /**
     * Join client as a world entity. World info and player entity's info will be provided in response message
     * Client will receive world updates after join
     * {MSG_TYPE};
     */
    public static final String CLIENT_MSG_JOIN = "join";

    /**
     * Leave game world. If client is in active battle - client won't be detached from game world
     * {MSG_TYPE};
     */
    public static final String CLIENT_MSG_LEAVE = "leave";

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
     * {MSG_TYPE};{SERVER_ESTIMATED_TIME};{skillID};
     */
    public static final String CLIENT_MSG_SKILL_OFF = "skillOFF";

}

// Client:
// setServerDelay;200
// move;0;20;20;45;50

// skillON;0;1;-100;-100;135
// skillOFF;0;1

// skillON;0;2;
// skillOFF;0;2