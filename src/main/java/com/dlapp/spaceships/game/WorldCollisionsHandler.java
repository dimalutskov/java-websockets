package com.dlapp.spaceships.game;

import com.dlapp.spaceships.MathUtils;
import com.dlapp.spaceships.game.entity.EntityState;
import com.dlapp.spaceships.game.entity.SingleShotEntity;
import com.dlapp.spaceships.game.entity.WorldEntity;

import java.util.*;

public class WorldCollisionsHandler {
    private final List<WorldEntity> entities = new ArrayList<>();

    // Key - objectId, value - list of collided objects
    private final Map<String, Set<WorldEntity>> collisions = new HashMap<>();

    void registerEntity(WorldEntity entity) {
        entities.add(entity);
    }

    void checkCollisions() {
        List<Integer> positionsToRemove = new ArrayList<>();

        for (int i = 0; i < entities.size(); i++) {
            for (int j = 0; j < entities.size(); j++) {
                if (j == i) continue;

                WorldEntity obj1Handler = entities.get(i);
                WorldEntity obj2Handler = entities.get(j);

                // Notify collision end
                if (obj1Handler.isDestroyed()) {
                    // Obj1 collisions
                    Set<WorldEntity> obj1Collisions = collisions.remove(obj1Handler.getId());
                    if (obj1Collisions != null) {
                        for (WorldEntity entity : obj1Collisions) {
                            obj1Handler.onCollisionEnd(entity);
                            entity.onCollisionEnd(obj1Handler);
                        }
                    }
                    positionsToRemove.add(i);
                    break;
                }

                checkCollision(obj1Handler, obj2Handler, 0);
            }
        }
        // Detach destroyed objects
        for (int i = positionsToRemove.size() - 1; i >= 0; i--) {
            int positionToRemove = positionsToRemove.get(i);
            entities.remove(positionToRemove);
        }
    }

    void checkCollisions(WorldEntity entity, long serverTime) {
        System.out.println("@@@ CHECK " + serverTime);
        for (WorldEntity otherEntity : entities) {
            if (entity != otherEntity) {
                checkCollision(entity, otherEntity, serverTime);
            }
        }
    }

    void checkCollision(WorldEntity entity, WorldEntity otherEntity, long time) {
        if (canBeCollided(entity, otherEntity)) {
            // Obj1 collisions
            Set<WorldEntity> obj1Collisions = collisions.computeIfAbsent(entity.getId(), k -> new HashSet<>());

            if (hasCollision(entity, otherEntity, time)) {
                entity.onCollision(otherEntity);
                obj1Collisions.add(otherEntity);
            } else {
                // Check if it was collided before
                if (obj1Collisions.contains(otherEntity)) {
                    entity.onCollisionEnd(otherEntity);
                    obj1Collisions.remove(otherEntity);
                }
            }
            collisions.put(entity.getId(), obj1Collisions);
        }
    }

    private static boolean canBeCollided(WorldEntity entity, WorldEntity otherEntity) {
        switch (entity.getType()) {
            case GameConstants.ENTITY_TYPE_SPACESHIP:
                if (otherEntity.getType() == GameConstants.ENTITY_TYPE_SHOT) {
                    String shotOwnerId = SingleShotEntity.getOwnerId(otherEntity.getId());
                    return !shotOwnerId.equals(entity.getId());
                }
                break;
            case GameConstants.ENTITY_TYPE_SHOT:
                if (otherEntity.getType() == GameConstants.ENTITY_TYPE_SPACESHIP) {
                    String shotOwnerId = SingleShotEntity.getOwnerId(entity.getId());
                    return !shotOwnerId.equals(otherEntity.getId());
                }
                if (otherEntity.getType() == GameConstants.ENTITY_TYPE_SHOT) {
                    return true;
                }
                break;
        }
        return false;
    }

    private static boolean hasCollision(WorldEntity entity1, WorldEntity entity2, long time) {
        EntityState state1 = time == 0 ? entity1.getState() : entity1.findState(time);
        EntityState state2 = time == 0 ? entity2.getState() : entity2.findState(time);

        if (time != 0) {
            String o1 = entity1.getId() + " " + entity1.findState(time).createTime + " " + entity1.findState(time).toStateString();
            String o2 = entity2.getId() + " " + entity2.findState(time).createTime + " " + entity2.findState(time).toStateString();
            System.out.println("@@@ Check collision " + o1 + " || " + o2);
        }

        return MathUtils.intersects(state1.getRect(), state2.getRect());
    }

}