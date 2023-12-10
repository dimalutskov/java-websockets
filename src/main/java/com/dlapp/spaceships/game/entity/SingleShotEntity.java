package com.dlapp.spaceships.game.entity;

import com.dlapp.spaceships.game.GameConstants;
import com.dlapp.spaceships.game.desc.SkillDesc;

public class SingleShotEntity extends WorldEntity {

    public static String ID_SEPARATOR = "::";

    private static long generatedShotId = 0;

    private final SkillDesc skillDesc;

    public SingleShotEntity(SkillDesc skillDesc, String ownerId, int x, int y, int angle) {
        super(ownerId + ID_SEPARATOR + generatedShotId++, GameConstants.ENTITY_TYPE_SHOT, x, y, angle);
        this.skillDesc = skillDesc;
        updateSize(skillDesc.values[1]);
    }

    @Override
    public void onCollision(WorldEntity entity) {
        if (getId().split(ID_SEPARATOR)[0].equals(entity.getId())) {
            // Collision with the owner - ignore it
            return;
        }

        super.onCollision(entity);
        entity.attachInfluence(new EntityInfluence(EntityInfluence.TYPE_SINGLE_DAMAGE, System.currentTimeMillis(), getId(), skillDesc.values[1]));
        destroy();
    }
}
