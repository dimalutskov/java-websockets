package com.dlapp.spaceships.game;

import com.dlapp.spaceships.game.object.GameObjectInfluence;
import com.dlapp.spaceships.game.object.GameObject;

public interface IGameWorld {
    void addEntity(GameObject entity, long time);
    GameObject getEntity(String id);
    boolean checkPastCollisions(GameObject entity, long time, WorldCollisionsHandler.CollisionCallback callback);

    void onEntityApplyInfluence(GameObject entity, GameObjectInfluence influence, int... values);
    void onEntityDetachInfluence(GameObject entity, GameObjectInfluence influence);
}
