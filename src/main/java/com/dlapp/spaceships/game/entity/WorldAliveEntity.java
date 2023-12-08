package com.dlapp.spaceships.game.entity;

import com.dlapp.spaceships.game.EntityCollisionsHandler;
import com.dlapp.spaceships.game.GameConstants;
import com.dlapp.spaceships.game.GameWorld;
import com.dlapp.spaceships.game.desc.AliveEntityDesc;
import com.dlapp.spaceships.game.desc.SkillDesc;

public class WorldAliveEntity extends WorldEntity {

    protected final GameWorld gameWorld;
    protected final AliveEntityDesc desc;

    protected float health;
    protected float energy;

    public WorldAliveEntity(GameWorld world, String id, AliveEntityDesc desc) {
        super(id, desc.type);
        this.gameWorld = world;
        this.desc = desc;
        this.health = desc.health;
        this.energy = desc.energy;
    }

    public WorldAliveEntity(GameWorld world, String id, AliveEntityDesc desc, int x, int y, int angle) {
        super(id, desc.type, x, y, angle);
        this.gameWorld = world;
        this.desc = desc;
    }

    WorldEntity handleShotSkill(long time, SkillDesc skill, int x, int y, int angle) {
        attachInfluence(new EntityInfluence(EntityInfluence.TYPE_SINGLE_ENERGY_CONSUMPTION, time, getId(), skill.energyPrice));
        // Create shot object
        SingleShotEntity shot = new SingleShotEntity(skill, getId(), x, y, angle);
        int speed = skill.values[2]; // TODO
        shot.update(time, x, y, angle, speed);
        shot.setDestroyTime(time + 5000);

        EntityCollisionsHandler entityCollisionsHandler = new EntityCollisionsHandler(shot,
                GameConstants.ENTITY_TYPE_SPACESHIP,
                GameConstants.SKILL_TYPE_SHOT);
        gameWorld.addEntity(shot, entityCollisionsHandler);

        return shot;
    }

    @Override
    protected boolean applyInfluence(EntityInfluence influence, long time) {
        switch (influence.type) {
            case EntityInfluence.TYPE_SINGLE_ENERGY_CONSUMPTION:
                energy -= influence.values[0];
                return true;

            case EntityInfluence.TYPE_SINGLE_DAMAGE:
                health -= influence.values[0];
                return true;

//            case EntityInfluence.TYPE_CONTINUOUS_ENERGY_CONSUMPTION:
//                float consumption = (time - influence.getApplyTime() / 1000.0f) * influence.values[0];
//                energy -= consumption;
//                influence.setApplyTime(time);
//                return false;
        }

        return super.applyInfluence(influence, time);
    }

    @Override
    public String getStateString() {
        return super.getStateString() +
                Math.round(health) + "," +
                Math.round(energy) + ",";
    }
}
