package com.baeldung.websocket.game;

import java.util.List;

public class WorldState {
    final long time;
    final List<WorldObject> objects;
    WorldState(long time, List<WorldObject> objects) {
        this.time = time;
        this.objects = objects;
    }
}
