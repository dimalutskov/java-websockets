package com.dlapp.spaceships.game;

import com.dlapp.spaceships.MathUtils;
import com.dlapp.spaceships.game.object.GameObjectState;
import com.dlapp.spaceships.game.object.GameObjectSingleShot;
import com.dlapp.spaceships.game.object.GameObject;

import java.util.*;

public class WorldCollisionsHandler {

    public interface CollisionCallback {
        void onCollision(GameObject entity1, GameObject entity2, long time);
    }

    private final List<GameObject> entities = new ArrayList<>();

    // Key - objectId, value - list of collided objects
    private final Map<String, Set<GameObject>> collisions = new HashMap<>();

    void registerEntity(GameObject entity) {
        entities.add(entity);
    }

    void checkCollisions() {
        List<Integer> positionsToRemove = new ArrayList<>();

        for (int i = 0; i < entities.size(); i++) {
            for (int j = 0; j < entities.size(); j++) {
                if (j == i) continue;

                GameObject obj1Handler = entities.get(i);
                GameObject obj2Handler = entities.get(j);

                // Notify collision end
                if (obj1Handler.isDestroyed()) {
                    // Obj1 collisions
                    Set<GameObject> obj1Collisions = collisions.remove(obj1Handler.getId());
                    if (obj1Collisions != null) {
                        for (GameObject entity : obj1Collisions) {
                            obj1Handler.onCollisionEnd(entity);
                            entity.onCollisionEnd(obj1Handler);
                        }
                    }
                    positionsToRemove.add(i);
                    break;
                }

                checkCollision(obj1Handler, obj2Handler);
            }
        }
        // Detach destroyed objects
        for (int i = positionsToRemove.size() - 1; i >= 0; i--) {
            int positionToRemove = positionsToRemove.get(i);
            entities.remove(positionToRemove);
        }
    }

    boolean checkCollisions(GameObject entity, long serverTime, CollisionCallback callback) {
        System.out.println("@@@ CHECK " + serverTime);
        boolean result = false;
        for (GameObject otherEntity : entities) {
            if (entity != otherEntity) {
                if (canBeCollided(entity, otherEntity) && hasCollision(entity, otherEntity, serverTime)) {
                    callback.onCollision(entity, otherEntity, serverTime);
                    result = true;
                }
            }
        }
        return result;
    }

    void checkCollision(GameObject entity, GameObject otherEntity) {
        if (canBeCollided(entity, otherEntity)) {
            // Obj1 collisions
            Set<GameObject> obj1Collisions = collisions.computeIfAbsent(entity.getId(), k -> new HashSet<>());

            if (hasCollision(entity, otherEntity, 0)) {
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

    private static boolean canBeCollided(GameObject entity, GameObject otherEntity) {
        switch (entity.getType()) {
            case GameConstants.ENTITY_TYPE_SPACESHIP:
                if (otherEntity.getType() == GameConstants.ENTITY_TYPE_SHOT) {
                    String shotOwnerId = GameObjectSingleShot.getOwnerId(otherEntity.getId());
                    return !shotOwnerId.equals(entity.getId());
                }
                break;
            case GameConstants.ENTITY_TYPE_SHOT:
                if (otherEntity.getType() == GameConstants.ENTITY_TYPE_SPACESHIP) {
                    String shotOwnerId = GameObjectSingleShot.getOwnerId(entity.getId());
                    return !shotOwnerId.equals(otherEntity.getId());
                }
                if (otherEntity.getType() == GameConstants.ENTITY_TYPE_SHOT) {
                    return true;
                }
                break;
        }
        return false;
    }

    private static boolean hasCollision(GameObject entity1, GameObject entity2, long time) {
        GameObjectState state1 = time == 0 ? entity1.getState() : entity1.findState(time);
        GameObjectState state2 = time == 0 ? entity2.getState() : entity2.findState(time);

        if (time != 0) {
            String o1 = entity1.getId() + " " + entity1.findState(time).time + " " + entity1.findState(time).toSocketString();
            String o2 = entity2.getId() + " " + entity2.findState(time).time + " " + entity2.findState(time).toSocketString();
            System.out.println("@@@ Check collision " + o1 + " || " + o2);
        }

        return MathUtils.intersects(state1.getRect(), state2.getRect());
    }

}