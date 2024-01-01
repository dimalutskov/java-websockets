package com.dlapp.spaceships.game;

import com.dlapp.spaceships.game.entity.EntityInfluence;
import com.dlapp.spaceships.game.entity.WorldEntity;

public interface GameWorld {
    void addEntity(WorldEntity entity, long time);
    WorldEntity getEntity(String id);
    boolean checkPastCollisions(WorldEntity entity, long time, WorldCollisionsHandler.CollisionCallback callback);

    void onEntityApplyInfluence(WorldEntity entity, EntityInfluence influence, int... values);
    void onEntityDetachInfluence(WorldEntity entity, EntityInfluence influence);
}
