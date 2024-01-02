package com.dlapp.spaceships.game.entity;

import com.dlapp.spaceships.game.GameConstants;
import com.dlapp.spaceships.game.GameWorld;
import com.dlapp.spaceships.game.WorldCollisionsHandler;
import com.dlapp.spaceships.game.desc.AliveEntityDesc;
import com.dlapp.spaceships.game.desc.SkillDesc;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static com.dlapp.spaceships.game.GameConstants.INFLUENCE_CONTINUOUS_ENERGY_RECOVER;

public class WorldAliveEntity extends WorldEntity {

    protected final AliveEntityDesc desc;

    public WorldAliveEntity(GameWorld world, String id, AliveEntityDesc desc, int x, int y, int angle) {
        super(world, id, desc.type, desc.size, x, y, angle);
        this.desc = desc;
        initConstantPassiveSkills();
        getState().health = desc.health;
        getState().energy = desc.energy;
    }

    @Override
    public State getState() {
        return (State) super.getState();
    }

    @Override
    protected EntityState createEntityState(long time, int size, int x, int y, int angle) {
        State result = new State(super.createEntityState(time, size, x, y, angle));
        if (getState() != null) {
            result.health = getState().health;
            result.energy = getState().energy;
        }
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

    public WorldEntity handleShotSkill(long shotCreatedTime, SkillDesc skill, int x, int y, int angle) {
        // Create shot object
        SingleShotEntity shot = new SingleShotEntity(gameWorld, skill, getId(), shotCreatedTime, x, y, angle);
        // As shot's state already adjusted to server time - pass current time if shot was not destroyed in client's "past"
        long addTime = shot.isDestroyed() ? shotCreatedTime : System.currentTimeMillis();
        gameWorld.addEntity(shot, addTime);
        return shot;
    }

    @Override
    protected boolean applyInfluence(EntityInfluence influence, long time) {
        switch (influence.type) {
            case GameConstants.INFLUENCE_SINGLE_ENERGY_CONSUMPTION:
                this.getState().energy = Math.max(0, this.getState().energy - influence.values[0]);
                return true;

            case GameConstants.INFLUENCE_SINGLE_DAMAGE:
                int appliedValue = influence.values[0];
                this.getState().health = Math.max(0, this.getState().health - appliedValue);
                gameWorld.onEntityApplyInfluence(this, influence, appliedValue);
                if (getState().health == 0) {
                    destroy();
                }
                return true;

            case GameConstants.INFLUENCE_CONTINUOUS_ENERGY_CONSUMPTION:
            case GameConstants.INFLUENCE_CONTINUOUS_ENERGY_RECOVER:
                float consumption = ((time - influence.getApplyTime()) / 1000.0f) * influence.values[0];
                int k = influence.type == GameConstants.INFLUENCE_CONTINUOUS_ENERGY_CONSUMPTION ? -1 : 1;
                getState().setEnergy(getState().energy + consumption * k);
                if (getState().energy < 0) getState().setEnergy(0);
                if (getState().energy > desc.energy) getState().setEnergy(desc.energy);
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
    public ObjectNode toJson() {
        ObjectNode result = super.toJson();
        result.put("heath", getState().health);
        result.put("energy", getState().energy);
        return result;
    }

    public static class State extends EntityState {

        private float health;
        private float energy;

        public State(EntityState originalState) {
            super(originalState.createTime, originalState.getSize(), originalState.getX(), originalState.getY(), originalState.getAngle());
        }

        public float getHealth() {
            return health;
        }

        public float getEnergy() {
            return energy;
        }

        void setHealth(float health) {
            this.health = health;
        }

        void setEnergy(float energy) {
            this.energy = energy;
        }

        public void update(int health, int energy) {
            this.health = health;
            this.energy = energy;
        }

        @Override
        public String toStateString() {
            return super.toStateString() + "," +
                    (int) health + "," +
                    (int) energy;
        }
    }
}
