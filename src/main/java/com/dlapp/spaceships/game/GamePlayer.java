package com.dlapp.spaceships.game;

import com.dlapp.spaceships.game.desc.EntityDesc;
import com.dlapp.spaceships.game.desc.SkillDesc;
import com.dlapp.spaceships.game.object.GameObjectInfluence;
import com.dlapp.spaceships.game.object.PlayerEntity;
import com.dlapp.spaceships.game.object.GameObject;

import javax.websocket.Session;
import java.io.IOException;
import java.util.*;

public class GamePlayer {

    private final IGameWorld world;
    private final Session session;

    private PlayerEntity entity;

    // key - skillType, value - list of entity's influences when skill is activated
    private final Map<Integer, List<GameObjectInfluence>> mSkillInfluences = new HashMap<>();

    public GamePlayer(IGameWorld world, Session session) {
        this.world = world;
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

    public synchronized void onMessage(String[] split, List<GameObject> addedObjects) {
        long time = System.currentTimeMillis();
        switch (split[0])  {
            case GameProtocol.CLIENT_MSG_JOIN: {
                if (entity != null) {
                    entity.destroy();
                }
                entity = new PlayerEntity(world, EntityDesc.SPACESHIP_DESC, session.getId());
                world.addGameObject(entity, System.currentTimeMillis());
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

                if (SkillDesc.typeOf(skillType) == SkillDesc.SkillType.CONTINUOUS) {
                    List<GameObjectInfluence> influences = new ArrayList<>();
                    // Energy consumption
                    influences.add(new GameObjectInfluence(GameConstants.INFLUENCE_CONTINUOUS_ENERGY_CONSUMPTION, time, skillType, entity.getId(), skill.energyPrice));
                    if (skillType == GameConstants.SKILL_TYPE_ACCELERATION) {

                    } else if (skillType == GameConstants.SKILL_TYPE_SHIELD) {
                        influences.add(new GameObjectInfluence(GameConstants.INFLUENCE_CONTINUOUS_SHIELD, time, skillType, entity.getId(), skill.values[0]));
                    }
                    mSkillInfluences.put(skillType, influences);
                    for (GameObjectInfluence influence : influences) entity.attachInfluence(influence);
                } else {
                    // Consume energy
                    entity.attachInfluence(new GameObjectInfluence(GameConstants.INFLUENCE_SINGLE_ENERGY_CONSUMPTION, time, skillType, entity.getId(), skill.energyPrice));

                    if (skillType == GameConstants.SKILL_TYPE_SHOT) {
                        int x = Integer.parseInt(split[3]);
                        int y = Integer.parseInt(split[4]);
                        int angle = Integer.parseInt(split[5]);
                        addedObjects.add(entity.handleShotSkill(serverTime, skill, x, y, angle));
                    }
                }
                break;
            }

            case GameProtocol.CLIENT_MSG_SKILL_OFF: {
                int skillId = Integer.parseInt(split[2]);

                List<GameObjectInfluence> influences = mSkillInfluences.get(skillId);
                if (influences != null) {
                    for (GameObjectInfluence influence : influences) entity.detachInfluence(influence);
                }

                break;
            }
        }
    }


}
