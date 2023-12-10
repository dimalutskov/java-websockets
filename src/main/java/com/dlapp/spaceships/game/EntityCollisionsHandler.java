package com.dlapp.spaceships.game;

import com.dlapp.spaceships.MathUtils;
import com.dlapp.spaceships.game.entity.WorldEntity;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Describes how the GameObject can be in collision with another GameObjects
 * Collisions handled separately for all object according to CollisionHandler. So, for example, collision between 2
 * objects will be handled twice by first and second object handlers accordingly.
 */
public class EntityCollisionsHandler {
    final WorldEntity entity;

    // Types of GameObjects which can be in collision
    private final Set<Integer> collideTypes = new HashSet<>();

    public EntityCollisionsHandler(WorldEntity entity, Integer... collisionTypes) {
        this.entity = entity;
        this.collideTypes.addAll(Arrays.asList(collisionTypes));
    }

    boolean canBeCollided(int type) {
        return collideTypes.contains(type);
    }

    boolean hasCollision(EntityCollisionsHandler handler) {
        if (canBeCollided(handler.entity.getType())) {
            Rectangle2D.Double obj1CollisionRect = entity.getRect();
            Rectangle2D.Double obj2CollisionRect = handler.entity.getRect();
            return MathUtils.intersects(obj1CollisionRect, obj2CollisionRect);
        }
        return false;
    }

}