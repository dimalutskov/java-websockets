package com.dlapp.spaceships.game;

import com.dlapp.spaceships.game.entity.WorldEntity;

import java.util.List;

public class WorldState {
    final long time;
    final List<WorldEntity> objects;
    WorldState(long time, List<WorldEntity> objects) {
        this.time = time;
        this.objects = objects;
    }
}
