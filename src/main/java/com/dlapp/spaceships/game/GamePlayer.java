package com.dlapp.spaceships.game;

import com.dlapp.spaceships.ServerApp;
import com.dlapp.spaceships.game.desc.EntityDesc;
import com.dlapp.spaceships.game.desc.SkillDesc;
import com.dlapp.spaceships.game.object.GameObjectInfluence;
import com.dlapp.spaceships.game.object.PlayerEntity;

import javax.websocket.Session;
import java.io.IOException;
import java.util.*;

public class GamePlayer {

    private final Session session;

    private String authToken;

    private IGameWorld world;
    private PlayerEntity entity;

    // key - skillType, value - list of entity's influences when skill is activated
    private final Map<Integer, List<GameObjectInfluence>> mSkillInfluences = new HashMap<>();

    public GamePlayer(Session session) {
        this.session = session;
    }

    public PlayerEntity getEntity() {
        return entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GamePlayer player = (GamePlayer) o;
        return session.getId().equals(player.session.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(session.getId());
    }

    public void send(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDisconnect() {
        if (world != null) {
            world.leavePlayer(this);
        }
    }

    // TODO check auth exceptions
    public synchronized void onMessage(String[] split) {
        long time = System.currentTimeMillis();
        switch (split[0]) {
            case GameProtocol.CLIENT_MSG_AUTH: {
                handleMessageAuth(split, time);
                break;
            }
            case GameProtocol.CLIENT_MSG_JOIN: {
                handleMessageJoin(split, time);
                break;
            }
            case GameProtocol.CLIENT_MSG_LEAVE: {
                handleMessageLeave(split, time);
                break;
            }
            case GameProtocol.CLIENT_MSG_MOVEMENT: {
                handleMessageMove(split, time);
                break;
            }
            case GameProtocol.CLIENT_MSG_SKILL_ON: {
                handleMessageSkillON(split, time);
                break;
            }
            case GameProtocol.CLIENT_MSG_SKILL_OFF: {
                handleMessageSkillOFF(split, time);
                break;
            }
        }
    }

    private void handleMessageAuth(String[] split, long currentTime) {
        this.authToken = split[1];

        // TODO response to client
    }

    private void handleMessageJoin(String[] split, long currentTime) {
        if (entity != null) {
            entity.destroy();
        }

        world = ServerApp.instance().getWorld();
        entity = new PlayerEntity(world, EntityDesc.SPACESHIP_DESC, session.getId());
        world.addGameObject(entity, System.currentTimeMillis());
        world.joinPlayer(this);

        // Response to client
        send(GameProtocol.SERVER_MSG_RESPONSE_JOIN + ";" +
                System.currentTimeMillis() + ";" +
                world.getDesc().toSocketString() + ";" +
                entity.toSocketString());
    }

    private void handleMessageLeave(String[] split, long currentTime) {
        boolean result = world.leavePlayer(this);

        // Response to client
        send(GameProtocol.SERVER_MSG_RESPONSE_LEAVE + ";" +
                result + ";");
    }

    private void handleMessageMove(String[] split, long currentTime) {
        int x = Integer.parseInt(split[2]);
        int y = Integer.parseInt(split[3]);
        int angle = Integer.parseInt(split[4]);
        int speed = Integer.parseInt(split[5]);
        entity.update(currentTime, x, y, angle, speed);
    }

    private void handleMessageSkillON(String[] split, long currentTime) {
        long serverEstimatedTime = Long.parseLong(split[1]);
        // TODO test
        if (serverEstimatedTime == 0) {
            serverEstimatedTime = System.currentTimeMillis() - 2000;
        }

        int skillType = Integer.parseInt(split[2]);
        SkillDesc skill = SkillDesc.find(entity.desc.skills, skillType);
        int requiredEnergy = SkillDesc.typeOf(skill.type) == SkillDesc.SkillType.CONTINUOUS
                ? skill.energyPrice / 2 : skill.energyPrice;
        if (requiredEnergy > entity.getState().getEnergy()) {
            return;
        }

        if (SkillDesc.typeOf(skillType) == SkillDesc.SkillType.CONTINUOUS) {
            List<GameObjectInfluence> influences = new ArrayList<>();
            // Energy consumption
            influences.add(new GameObjectInfluence(GameConstants.INFLUENCE_CONTINUOUS_ENERGY_CONSUMPTION, currentTime, skillType, entity.getId(), skill.energyPrice));
            if (skillType == GameConstants.SKILL_TYPE_ACCELERATION) {
                // TODO
            } else if (skillType == GameConstants.SKILL_TYPE_SHIELD) {
                influences.add(new GameObjectInfluence(GameConstants.INFLUENCE_CONTINUOUS_SHIELD, currentTime, skillType, entity.getId(), skill.values[0]));
            }
            mSkillInfluences.put(skillType, influences);
            for (GameObjectInfluence influence : influences) entity.attachInfluence(influence);
        } else {
            // Consume energy
            entity.attachInfluence(new GameObjectInfluence(GameConstants.INFLUENCE_SINGLE_ENERGY_CONSUMPTION, currentTime, skillType, entity.getId(), skill.energyPrice));

            if (skillType == GameConstants.SKILL_TYPE_SHOT) {
                int x = Integer.parseInt(split[3]);
                int y = Integer.parseInt(split[4]);
                int angle = Integer.parseInt(split[5]);
                entity.handleShotSkill(serverEstimatedTime, skill, x, y, angle);
            }
        }
    }

    private void handleMessageSkillOFF(String[] split, long currentTime) {
        int skillId = Integer.parseInt(split[2]);
        List<GameObjectInfluence> influences = mSkillInfluences.get(skillId);
        if (influences != null) {
            for (GameObjectInfluence influence : influences) entity.detachInfluence(influence);
        }
    }

}
