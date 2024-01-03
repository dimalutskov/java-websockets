package com.dlapp.spaceships.game.object;

import com.dlapp.spaceships.game.GameConstants;
import com.dlapp.spaceships.game.IGameWorld;
import com.dlapp.spaceships.game.desc.EntityDesc;
import com.dlapp.spaceships.game.desc.SkillDesc;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static com.dlapp.spaceships.game.GameConstants.INFLUENCE_CONTINUOUS_ENERGY_RECOVER;

/**
 * Extended "alive" GameObject which can perform actions
 */
public class GameEntity extends GameObject {

    protected final EntityDesc desc;

    public GameEntity(IGameWorld world, String id, EntityDesc desc, int x, int y, int angle) {
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
    protected GameObjectState createState(long time, int size, int x, int y, int angle) {
        State result = new State(super.createState(time, size, x, y, angle));
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
                    attachInfluence(new GameObjectInfluence(INFLUENCE_CONTINUOUS_ENERGY_RECOVER, time, skill.type, getId(), skill.values[0]));
                    break;
            }
        }
    }

    public GameObject handleShotSkill(long shotCreatedTime, SkillDesc skill, int x, int y, int angle) {
        // Create shot object
        GameObjectSingleShot shot = new GameObjectSingleShot(gameWorld, skill, getId(), shotCreatedTime, x, y, angle);
        // As shot's state already adjusted to server time - pass current time if shot was not destroyed in client's "past"
        long addTime = shot.isDestroyed() ? shotCreatedTime : System.currentTimeMillis();
        gameWorld.addGameObject(shot, addTime);
        return shot;
    }

    @Override
    protected boolean applyInfluence(GameObjectInfluence influence, long time) {
        switch (influence.type) {
            case GameConstants.INFLUENCE_SINGLE_ENERGY_CONSUMPTION:
                this.getState().energy = Math.max(0, this.getState().energy - influence.values[0]);
                return true;

            case GameConstants.INFLUENCE_SINGLE_DAMAGE:
                int appliedValue = influence.values[0];
                this.getState().health = Math.max(0, this.getState().health - appliedValue);
                gameWorld.onGameObjectApplyInfluence(this, influence, appliedValue);
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
    public void onCollision(GameObject gameObject) {
        super.onCollision(gameObject);
    }

    @Override
    public ObjectNode toJson() {
        ObjectNode result = super.toJson();
        result.put("heath", getState().health);
        result.put("energy", getState().energy);
        return result;
    }

    public static class State extends GameObjectState {

        private float health;
        private float energy;

        public State(GameObjectState originalState) {
            super(originalState.time, originalState.getSize(), originalState.getX(), originalState.getY(), originalState.getAngle());
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
        public String toSocketString() {
            return super.toSocketString() + "," +
                    (int) health + "," +
                    (int) energy;
        }
    }
}
