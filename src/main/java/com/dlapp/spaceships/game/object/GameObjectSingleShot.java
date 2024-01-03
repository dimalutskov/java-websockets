package com.dlapp.spaceships.game.object;

import com.dlapp.spaceships.game.GameConstants;
import com.dlapp.spaceships.game.IGameWorld;
import com.dlapp.spaceships.game.GameWorldCollisions;
import com.dlapp.spaceships.game.desc.SkillDesc;

public class GameObjectSingleShot extends GameObject {

    private static final String ID_SEPARATOR = "::";

    private static long generatedShotId = 0;

    private final SkillDesc skillDesc;

    // Shots provided by clients - created in "past"
    private final long createTime;

    public GameObjectSingleShot(IGameWorld world, SkillDesc skillDesc, String ownerId, long createTime, int x, int y, int angle) {
        this(world, ownerId + ID_SEPARATOR + generatedShotId++, skillDesc, createTime, x, y, angle);
    }

    private GameObjectSingleShot(IGameWorld world, String id, SkillDesc skillDesc, long createTime, int x, int y, int angle) {
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
        GameWorldCollisions.CollisionCallback pastCollisionCallback = (gameObject1, gameObject2, time) -> {
            update(time, gameObject2.getState().getX(), gameObject2.getState().getY(), angle, 0);
            setDestroyTime(time);
            destroy();
            // Apply damage
            gameObject2.attachInfluence(new GameObjectInfluence(GameConstants.INFLUENCE_SINGLE_DAMAGE, time, skillDesc.type, getId(), skillDesc.values[1]));
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
    public GameObjectState findState(long time) {
        GameObjectState current = getState();
        GameObjectState old = super.findState(createTime);
        float progress = (time - old.time) / (float) (current.time - old.time);
        float x = old.getX() + (current.getX() - old.getX()) * progress;
        float y = old.getY() + (current.getY() - old.getY()) * progress;
        return new GameObjectState(time, current.getSize(), (int) x, (int) y, current.getAngle());
    }

    @Override
    public void onCollision(GameObject gameObject) {
        super.onCollision(gameObject);

        gameObject.attachInfluence(new GameObjectInfluence(GameConstants.INFLUENCE_SINGLE_DAMAGE, System.currentTimeMillis(), skillDesc.type, getId(), skillDesc.values[1]));
        destroy();
    }

    public static String getOwnerId(String shotId) {
        return shotId.split(ID_SEPARATOR)[0];
    }
}
