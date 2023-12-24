package com.dlapp.spaceships.game;

import com.dlapp.spaceships.MathUtils;
import com.dlapp.spaceships.game.entity.SingleShotEntity;
import com.dlapp.spaceships.game.entity.WorldEntity;

/**
 * Describes how the GameObject can be in collision with another GameObjects
 * Collisions handled separately for all object according to CollisionHandler. So, for example, collision between 2
 * objects will be handled twice by first and second object handlers accordingly.
 */
public class EntityCollisionsHandler {
    final WorldEntity entity;

    public EntityCollisionsHandler(WorldEntity entity) {
        this.entity = entity;
    }

    boolean canBeCollided(WorldEntity otherEntity) {
        switch (this.entity.getType()) {
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

    boolean hasCollision(EntityCollisionsHandler handler) {
        return MathUtils.intersects(entity.getRect(), handler.entity.getRect());
    }

}