package com.dlapp.spaceships.game;

import com.dlapp.spaceships.game.entity.WorldEntity;

public interface GameWorld {
    void addEntity(WorldEntity entity);
    WorldEntity getEntity(String id);
}
