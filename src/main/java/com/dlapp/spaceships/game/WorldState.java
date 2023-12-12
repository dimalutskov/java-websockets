package com.dlapp.spaceships.game;

import com.dlapp.spaceships.game.entity.WorldEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class WorldState {
    public final long time;
    public final List<WorldEntity> entities;

    WorldState(long time, List<WorldEntity> objects) {
        this.time = time;
        this.entities = objects;
    }

    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();
        result.put("time", time);

        ArrayNode objectsNode = mapper.createArrayNode();
        for (WorldEntity entity : entities) {
            objectsNode.add(entity.toJson());
        }
        result.put("entities", objectsNode);
        return result;
    }
}
