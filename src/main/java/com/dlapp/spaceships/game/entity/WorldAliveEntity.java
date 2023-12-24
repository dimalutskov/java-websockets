package com.dlapp.spaceships.game.entity;

import com.dlapp.spaceships.game.GameConstants;
import com.dlapp.spaceships.game.GameWorld;
import com.dlapp.spaceships.game.desc.AliveEntityDesc;
import com.dlapp.spaceships.game.desc.SkillDesc;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static com.dlapp.spaceships.game.GameConstants.INFLUENCE_CONTINUOUS_ENERGY_RECOVER;

public class WorldAliveEntity extends WorldEntity {

    protected final AliveEntityDesc desc;

    protected float health;
    protected float energy;

    public WorldAliveEntity(GameWorld world, String id, AliveEntityDesc desc, int x, int y, int angle) {
        super(world, id, desc.type, desc.size, x, y, angle);
        this.desc = desc;
        this.health = desc.health;
        this.energy = desc.energy;
        initConstantPassiveSkills();
    }

    @Override
    public WorldEntity copy() {
        WorldAliveEntity result = new WorldAliveEntity(gameWorld, getId(), desc, getX(), getY(), getAngle());
        result.health = health;
        result.energy = energy;
        return result;
    }

    private void initConstantPassiveSkills() {
        long time = System.currentTimeMillis();
        for (SkillDesc skill : desc.skills) {
            switch (skill.type) {
                case GameConstants.SKILL_TYPE_PASSIVE_ENERGY_RECOVER:
                    attachInfluence(new EntityInfluence(INFLUENCE_CONTINUOUS_ENERGY_RECOVER, time, skill.type, getId(), skill.values[0]));
                    break;
            }
        }
    }

    WorldEntity handleShotSkill(long time, SkillDesc skill, int x, int y, int angle) {
        attachInfluence(new EntityInfluence(GameConstants.INFLUENCE_SINGLE_ENERGY_CONSUMPTION, time, skill.type, getId(), skill.energyPrice));
        // Create shot object
        SingleShotEntity shot = new SingleShotEntity(gameWorld, skill, getId(), x, y, angle);
        int speed = skill.values[2]; // TODO
        shot.update(time, x, y, angle, speed);
        shot.setDestroyTime(time + 5000);

        // As player provides timestamp when shot was generated - need to check if any collisions
        // occurred between client time and current server time
        shot.update(System.currentTimeMillis(), angle);
        int newX = shot.getX();
        int newY = shot.getY();
        // TODO

        gameWorld.addEntity(shot);

        return shot;
    }

    @Override
    protected boolean applyInfluence(EntityInfluence influence, long time) {
        switch (influence.type) {
            case GameConstants.INFLUENCE_SINGLE_ENERGY_CONSUMPTION:
                this.energy = Math.max(0, this.energy - influence.values[0]);
                return true;

            case GameConstants.INFLUENCE_SINGLE_DAMAGE:
                int appliedValue = influence.values[0];
                this.health = Math.max(0, this.health - appliedValue);
                gameWorld.onEntityApplyInfluence(this, influence, appliedValue);
                if (health == 0) {
                    destroy();
                }
                return true;

            case GameConstants.INFLUENCE_CONTINUOUS_ENERGY_CONSUMPTION:
            case INFLUENCE_CONTINUOUS_ENERGY_RECOVER:
                float consumption = ((time - influence.getApplyTime()) / 1000.0f) * influence.values[0];
                int k = influence.type == GameConstants.INFLUENCE_CONTINUOUS_ENERGY_CONSUMPTION ? -1 : 1;
                energy = energy + consumption * k;
                if (energy < 0) energy = 0;
                if (energy > desc.energy) energy = desc.energy;
                influence.setApplyTime(time);
                return false;
        }

        return super.applyInfluence(influence, time);
    }

    @Override
    public void onCollision(WorldEntity entity) {
        super.onCollision(entity);
    }

    @Override
    public String getStateString() {
        return super.getStateString() +
                Math.round(health) + "," +
                Math.round(energy) + ",";
    }

    @Override
    public ObjectNode toJson() {
        ObjectNode result = super.toJson();
        result.put("heath", health);
        result.put("energy", energy);
        return result;
    }
}
