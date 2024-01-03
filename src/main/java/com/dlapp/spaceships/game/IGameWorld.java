package com.dlapp.spaceships.game;

import com.dlapp.spaceships.game.object.GameObjectInfluence;
import com.dlapp.spaceships.game.object.GameObject;

public interface IGameWorld {
    void addGameObject(GameObject entity, long time);
    GameObject getGameObject(String id);
    boolean checkPastCollisions(GameObject entity, long time, GameWorldCollisions.CollisionCallback callback);

    void onGameObjectApplyInfluence(GameObject entity, GameObjectInfluence influence, int... values);
    void onGameObjectDetachInfluence(GameObject entity, GameObjectInfluence influence);
}
