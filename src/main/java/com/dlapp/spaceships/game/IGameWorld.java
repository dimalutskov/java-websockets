package com.dlapp.spaceships.game;

import com.dlapp.spaceships.game.desc.GameWorldDesc;
import com.dlapp.spaceships.game.object.GameObjectInfluence;
import com.dlapp.spaceships.game.object.GameObject;

public interface IGameWorld {

    GameWorldDesc getDesc();

    void joinPlayer(GamePlayer player);

    /**
     * @return true when player have left the game world. False can be returned when
     * is active battle state and can be detached from game world
     */
    boolean leavePlayer(GamePlayer player);

    void addGameObject(GameObject entity, long time);
    GameObject getGameObject(String id);
    boolean checkPastCollisions(GameObject entity, long time, GameWorldCollisions.CollisionCallback callback);

    void onGameObjectApplyInfluence(GameObject entity, GameObjectInfluence influence, int... values);
    void onGameObjectDetachInfluence(GameObject entity, GameObjectInfluence influence);
}
