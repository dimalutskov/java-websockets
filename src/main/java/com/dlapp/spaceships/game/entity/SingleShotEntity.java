package com.dlapp.spaceships.game.entity;

import com.dlapp.spaceships.game.GameConstants;
import com.dlapp.spaceships.game.GameWorld;
import com.dlapp.spaceships.game.WorldCollisionsHandler;
import com.dlapp.spaceships.game.desc.SkillDesc;

public class SingleShotEntity extends WorldEntity {

    private static final String ID_SEPARATOR = "::";

    private static long generatedShotId = 0;

    private final SkillDesc skillDesc;

    // Shots provided by clients - created in "past"
    private final long createTime;

    public SingleShotEntity(GameWorld world, SkillDesc skillDesc, String ownerId, long createTime, int x, int y, int angle) {
        this(world, ownerId + ID_SEPARATOR + generatedShotId++, skillDesc, createTime, x, y, angle);
    }

    private SingleShotEntity(GameWorld world, String id, SkillDesc skillDesc, long createTime, int x, int y, int angle) {
        super(world, createTime, id, GameConstants.ENTITY_TYPE_SHOT, skillDesc.values[0], x, y, angle);
        this.skillDesc = skillDesc;
        this.createTime = createTime;

        int speed = skillDesc.values[2]; // TODO
        update(createTime, x, y, angle, speed);
        setDestroyTime(createTime + 5000);

        // Add actual state
        long currentTime = System.currentTimeMillis();
        update(currentTime, angle);
        addNewState(currentTime);

        // As player provides timestamp when shot was generated - need to check if any collisions
        // occurred in "past" between client time and current server time
        WorldCollisionsHandler.CollisionCallback pastCollisionCallback = (entity1, entity2, time) -> {
            update(time, entity2.getState().getX(), entity2.getState().getY(), angle, 0);
            setDestroyTime(time);
        };
        int checkPastCollisionsCount = 3; // TODO
        long timeStep = (currentTime - createTime) / (checkPastCollisionsCount + 1);
        for (int i = 0; i < checkPastCollisionsCount; i++) {
            if (gameWorld.checkPastCollisions(this, createTime + timeStep * i, pastCollisionCallback)) {
                break;
            }
        }
    }

    @Override
    public EntityState findState(long time) {
        EntityState current = getState();
        EntityState old = super.findState(createTime);
        float progress = (time - old.createTime) / (float) (current.createTime - old.createTime);
        float x = old.getX() + (current.getX() - old.getX()) * progress;
        float y = old.getY() + (current.getY() - old.getY()) * progress;
        return new EntityState(time, current.getSize(), (int) x, (int) y, current.getAngle());
    }

    @Override
    public void onCollision(WorldEntity entity) {
        super.onCollision(entity);

        entity.attachInfluence(new EntityInfluence(GameConstants.INFLUENCE_SINGLE_DAMAGE, System.currentTimeMillis(), skillDesc.type, getId(), skillDesc.values[1]));
        destroy();
    }

    public static String getOwnerId(String shotId) {
        return shotId.split(ID_SEPARATOR)[0];
    }
}
