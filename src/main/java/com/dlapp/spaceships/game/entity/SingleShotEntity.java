package com.dlapp.spaceships.game.entity;

import com.dlapp.spaceships.game.GameConstants;
import com.dlapp.spaceships.game.GameWorld;
import com.dlapp.spaceships.game.desc.SkillDesc;

public class SingleShotEntity extends WorldEntity {

    private static final String ID_SEPARATOR = "::";

    private static long generatedShotId = 0;

    private final SkillDesc skillDesc;

    private SingleShotEntity(GameWorld world, String id, SkillDesc skillDesc, int x, int y, int angle) {
        super(world, id, GameConstants.ENTITY_TYPE_SHOT, skillDesc.values[0], x, y, angle);
        this.skillDesc = skillDesc;
    }

    public SingleShotEntity(GameWorld world, SkillDesc skillDesc, String ownerId, int x, int y, int angle) {
        this(world, ownerId + ID_SEPARATOR + generatedShotId++, skillDesc, x, y, angle);
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
