package com.dlapp.spaceships.game;

import com.dlapp.spaceships.MathUtils;
import com.dlapp.spaceships.game.object.GameObjectState;
import com.dlapp.spaceships.game.object.GameObjectSingleShot;
import com.dlapp.spaceships.game.object.GameObject;

import java.util.*;

public class GameWorldCollisions {

    public interface CollisionCallback {
        void onCollision(GameObject gameObject1, GameObject gameObject2, long time);
    }

    private final List<GameObject> entities = new ArrayList<>();

    // Key - objectId, value - list of collided objects
    private final Map<String, Set<GameObject>> collisions = new HashMap<>();

    void registerObject(GameObject gameObject) {
        entities.add(gameObject);
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
                        for (GameObject gameObject : obj1Collisions) {
                            obj1Handler.onCollisionEnd(gameObject);
                            gameObject.onCollisionEnd(obj1Handler);
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

    boolean checkCollisions(GameObject gameObject, long serverTime, CollisionCallback callback) {
        System.out.println("@@@ CHECK " + serverTime);
        boolean result = false;
        for (GameObject otherObject : entities) {
            if (gameObject != otherObject) {
                if (canBeCollided(gameObject, otherObject) && hasCollision(gameObject, otherObject, serverTime)) {
                    callback.onCollision(gameObject, otherObject, serverTime);
                    result = true;
                }
            }
        }
        return result;
    }

    void checkCollision(GameObject gameObject, GameObject otherObject) {
        if (canBeCollided(gameObject, otherObject)) {
            // Obj1 collisions
            Set<GameObject> obj1Collisions = collisions.computeIfAbsent(gameObject.getId(), k -> new HashSet<>());

            if (hasCollision(gameObject, otherObject, 0)) {
                gameObject.onCollision(otherObject);
                obj1Collisions.add(otherObject);
            } else {
                // Check if it was collided before
                if (obj1Collisions.contains(otherObject)) {
                    gameObject.onCollisionEnd(otherObject);
                    obj1Collisions.remove(otherObject);
                }
            }
            collisions.put(gameObject.getId(), obj1Collisions);
        }
    }

    private static boolean canBeCollided(GameObject gameObject, GameObject otherObject) {
        switch (gameObject.getType()) {
            case GameConstants.ENTITY_TYPE_SPACESHIP:
                if (otherObject.getType() == GameConstants.ENTITY_TYPE_SHOT) {
                    String shotOwnerId = GameObjectSingleShot.getOwnerId(otherObject.getId());
                    return !shotOwnerId.equals(gameObject.getId());
                }
                break;
            case GameConstants.ENTITY_TYPE_SHOT:
                if (otherObject.getType() == GameConstants.ENTITY_TYPE_SPACESHIP) {
                    String shotOwnerId = GameObjectSingleShot.getOwnerId(gameObject.getId());
                    return !shotOwnerId.equals(otherObject.getId());
                }
                if (otherObject.getType() == GameConstants.ENTITY_TYPE_SHOT) {
                    return true;
                }
                break;
        }
        return false;
    }

    private static boolean hasCollision(GameObject gameObject1, GameObject gameObject2, long time) {
        GameObjectState state1 = time == 0 ? gameObject1.getState() : gameObject1.findState(time);
        GameObjectState state2 = time == 0 ? gameObject2.getState() : gameObject2.findState(time);

        if (time != 0) {
            String o1 = gameObject1.getId() + " " + gameObject1.findState(time).time + " " + gameObject1.findState(time).toSocketString();
            String o2 = gameObject2.getId() + " " + gameObject2.findState(time).time + " " + gameObject2.findState(time).toSocketString();
            System.out.println("@@@ Check collision " + o1 + " || " + o2);
        }

        return MathUtils.intersects(state1.getRect(), state2.getRect());
    }

}