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

    // Describes collision rect ratio to view rect. When value is 0.5 and view size is 100, collision rect size will be 50
    private final Point2D.Double collisionRectRatio = new Point2D.Double(1.0f, 1.0f);

    private final Rectangle2D.Double collisionRect = new Rectangle2D.Double();

    public EntityCollisionsHandler(WorldEntity entity, Integer... collisionTypes) {
        this.entity = entity;
        this.collideTypes.addAll(Arrays.asList(collisionTypes));
    }

    boolean canBeCollided(int type) {
        return collideTypes.contains(type);
    }

    Rectangle2D.Double getCollisionRect() {
        Rectangle2D.Double objRect = entity.getRect();
//        double collisionWidth = objRect.getWidth() * collisionRectRatio.getX();
//        double collisionHeight = objRect.getHeight() * collisionRectRatio.getY();
//        double collisionLeft = objRect.x + (objRect.getWidth() - collisionWidth) / 2;
//        double collisionTop = objRect.y + (objRect.getHeight() - collisionHeight) / 2;
//        collisionRect.setRect(collisionLeft, collisionTop, collisionLeft + collisionWidth, collisionTop + collisionHeight);
        return objRect;
    }

    boolean hasCollision(EntityCollisionsHandler handler) {
        if (canBeCollided(handler.entity.getType())) {
            Rectangle2D.Double obj1CollisionRect = getCollisionRect();
            Rectangle2D.Double obj2CollisionRect = handler.getCollisionRect();
            return MathUtils.intersects(obj1CollisionRect, obj2CollisionRect);
        }
        return false;
    }

}