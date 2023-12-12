package com.dlapp.spaceships.game.entity;

import com.dlapp.spaceships.game.GameConstants;
import com.dlapp.spaceships.game.desc.SkillDesc;

public class SingleShotEntity extends WorldEntity {

    private static final String ID_SEPARATOR = "::";

    private static long generatedShotId = 0;

    private final SkillDesc skillDesc;

    private SingleShotEntity(String id, SkillDesc skillDesc, int x, int y, int angle) {
        super(id, GameConstants.ENTITY_TYPE_SHOT, skillDesc.values[0], x, y, angle);
        this.skillDesc = skillDesc;
    }

    public SingleShotEntity(SkillDesc skillDesc, String ownerId, int x, int y, int angle) {
        this(ownerId + ID_SEPARATOR + generatedShotId++, skillDesc, x, y, angle);
    }

    @Override
    public WorldEntity copy() {
        return new SingleShotEntity(getId(),skillDesc, getX(), getY(), getAngle());
    }

    @Override
    public void onCollision(WorldEntity entity) {
        if (getOwnerId(getId()).equals(entity.getId())) {
            // Collision with the owner - ignore it
            return;
        }

        super.onCollision(entity);
        entity.attachInfluence(new EntityInfluence(EntityInfluence.TYPE_SINGLE_DAMAGE, System.currentTimeMillis(), getId(), skillDesc.values[1]));
        destroy();
    }

    public static String getOwnerId(String shotId) {
        return shotId.split(ID_SEPARATOR)[0];
    }
}
