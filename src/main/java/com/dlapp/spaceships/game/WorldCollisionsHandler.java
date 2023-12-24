package com.dlapp.spaceships.game;

import com.dlapp.spaceships.game.entity.WorldEntity;

import java.util.*;

public class WorldCollisionsHandler {
    private final List<EntityCollisionsHandler> handlers = new ArrayList<>();
    // Key - objectId, value - list of collided objects
    private final Map<String, Set<WorldEntity>> collisions = new HashMap<>();

    void registerHandler(EntityCollisionsHandler handler) {
        handlers.add(handler);
    }

    void checkCollisions() {
        List<Integer> positionsToRemove = new ArrayList<>();

        for (int i = 0; i < handlers.size(); i++) {
            for (int j = 0; j < handlers.size(); j++) {
                if (j == i) continue;

                EntityCollisionsHandler obj1Handler = handlers.get(i);
                EntityCollisionsHandler obj2Handler = handlers.get(j);

                // Notify collision end
                if (obj1Handler.entity.isDestroyed()) {
                    // Obj1 collisions
                    Set<WorldEntity> obj1Collisions = collisions.remove(obj1Handler.entity.getId());
                    if (obj1Collisions != null) {
                        for (WorldEntity entity : obj1Collisions) {
                            obj1Handler.entity.onCollisionEnd(entity);
                            entity.onCollisionEnd(obj1Handler.entity);
                        }
                        positionsToRemove.add(i);
                    }
                    break;
                }

                checkCollision(obj1Handler, obj2Handler);
            }
        }
        // Detach destroyed objects
        for (int i = positionsToRemove.size() - 1; i >= 0; i--) {
            int positionToRemove = positionsToRemove.get(i);
            handlers.remove(positionToRemove);
        }
    }

    void checkCollision(EntityCollisionsHandler handler1, EntityCollisionsHandler handler2) {
        if (handler1.canBeCollided(handler2.entity)) {
            // Obj1 collisions
            Set<WorldEntity> obj1Collisions = collisions.computeIfAbsent(handler1.entity.getId(), k -> new HashSet<>());

            if (handler1.hasCollision(handler2)) {
                handler1.entity.onCollision(handler2.entity);
                obj1Collisions.add(handler2.entity);
            } else {
                // Check if it was collided before
                if (obj1Collisions.contains(handler2.entity)) {
                    handler1.entity.onCollisionEnd(handler2.entity);
                    obj1Collisions.remove(handler2.entity);
                }
            }
            collisions.put(handler1.entity.getId(), obj1Collisions);
        }
    }

}