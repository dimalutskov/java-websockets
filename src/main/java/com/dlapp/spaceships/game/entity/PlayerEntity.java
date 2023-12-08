package com.dlapp.spaceships.game.entity;

import com.dlapp.spaceships.game.GameConstants;
import com.dlapp.spaceships.game.GameProtocol;
import com.dlapp.spaceships.game.GameWorld;
import com.dlapp.spaceships.game.desc.AliveEntityDesc;
import com.dlapp.spaceships.game.desc.SkillDesc;

import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class PlayerEntity extends WorldAliveEntity {
    private final AliveEntityDesc desc;
    private final Session session;

    public PlayerEntity(GameWorld world, AliveEntityDesc desc, Session session) {
        super(world, session.getId(), desc);
        this.desc = desc;
        this.session = session;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerEntity roomUser = (PlayerEntity) o;
        return session.getId().equals(roomUser.session.getId());
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

    public synchronized void onMessage(String[] split, List<WorldEntity> objectsToAdd) {
        long time = System.currentTimeMillis();
        switch (split[0])  {
            case GameProtocol.CLIENT_MSG_MOVEMENT: {
                int x = Integer.parseInt(split[2]);
                int y = Integer.parseInt(split[3]);
                int angle = Integer.parseInt(split[4]);
                int speed = Integer.parseInt(split[5]);
                update(time, x, y, angle, speed);
                break;
            }

            case GameProtocol.CLIENT_MSG_SKILL_ON: {
                long serverTime = Long.parseLong(split[1]);
                int skillType = Integer.parseInt(split[2]);
                SkillDesc skill = SkillDesc.find(desc.skills, skillType);
                if (skill == null || skill.energyPrice > energy) {
                    break;
                }

                if (skillType == GameConstants.SKILL_TYPE_SHOT) {
                    int x = Integer.parseInt(split[3]);
                    int y = Integer.parseInt(split[4]);
                    int angle = Integer.parseInt(split[5]);
                    objectsToAdd.add(handleShotSkill(serverTime, skill, x, y, angle));
                }
                break;
            }

            case GameProtocol.CLIENT_MSG_SKILL_OFF: {
                int skillId = Integer.parseInt(split[2]);
                long duration = Long.parseLong(split[3]);
                break;
            }
        }
    }



}
