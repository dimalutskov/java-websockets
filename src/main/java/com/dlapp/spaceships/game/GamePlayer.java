package com.dlapp.spaceships.game;

import com.dlapp.spaceships.game.desc.AliveEntityDesc;
import com.dlapp.spaceships.game.desc.SkillDesc;
import com.dlapp.spaceships.game.entity.EntityInfluence;
import com.dlapp.spaceships.game.entity.PlayerEntity;
import com.dlapp.spaceships.game.entity.WorldEntity;

import javax.websocket.Session;
import java.io.IOException;
import java.util.*;

public class GamePlayer {

    private final GameWorld world;
    private final Session session;

    private PlayerEntity entity;

    // key - skillType, value - list of entity's influences when skill is activated
    private final Map<Integer, List<EntityInfluence>> mSkillInfluences = new HashMap<>();

    public GamePlayer(GameWorld world, Session session) {
        this.world = world;
        this.session = session;
    }

    void createEntity(PlayerEntity entity) {
        this.entity = entity;
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

    public synchronized void onMessage(String[] split, List<WorldEntity> addedObjects) {
        long time = System.currentTimeMillis();
        switch (split[0])  {
            case GameProtocol.CLIENT_MSG_JOIN: {
                if (entity != null) {
                    entity.destroy();
                }
                entity = new PlayerEntity(world, AliveEntityDesc.SPACESHIP_DESC, session.getId());
                break;
            }

            case GameProtocol.CLIENT_MSG_MOVEMENT: {
                int x = Integer.parseInt(split[2]);
                int y = Integer.parseInt(split[3]);
                int angle = Integer.parseInt(split[4]);
                int speed = Integer.parseInt(split[5]);
                entity.update(time, x, y, angle, speed);
                break;
            }

            case GameProtocol.CLIENT_MSG_SKILL_ON: {
                long serverTime = Long.parseLong(split[1]);
                // TODO test
                if (serverTime == 0) {
                    serverTime = System.currentTimeMillis() - 2000;
                }
                int skillType = Integer.parseInt(split[2]);
                SkillDesc skill = SkillDesc.find(entity.desc.skills, skillType);
                int requiredEnergy = SkillDesc.typeOf(skill.type) == SkillDesc.SkillType.CONTINUOUS
                        ? skill.energyPrice / 2 : skill.energyPrice;
                if (requiredEnergy > entity.getState().getEnergy()) {
                    break;
                }

                if (skillType == GameConstants.SKILL_TYPE_SHOT) {
                    int x = Integer.parseInt(split[3]);
                    int y = Integer.parseInt(split[4]);
                    int angle = Integer.parseInt(split[5]);
                    addedObjects.add(entity.handleShotSkill(serverTime, skill, x, y, angle));
                } else if (skillType == GameConstants.SKILL_TYPE_ACCELERATION) {
                    EntityInfluence energyConsumption = new EntityInfluence(GameConstants.INFLUENCE_CONTINUOUS_ENERGY_CONSUMPTION, time, skillType, entity.getId(), skill.energyPrice);
                    mSkillInfluences.put(skillType, Collections.singletonList(energyConsumption));
                    entity.attachInfluence(energyConsumption);
                }
                break;
            }

            case GameProtocol.CLIENT_MSG_SKILL_OFF: {
                int skillId = Integer.parseInt(split[2]);

                List<EntityInfluence> influences = mSkillInfluences.get(skillId);
                if (influences != null) {
                    for (EntityInfluence influence : influences) entity.detachInfluence(influence);
                }

                break;
            }
        }
    }


}
