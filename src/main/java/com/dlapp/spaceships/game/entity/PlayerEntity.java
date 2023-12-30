package com.dlapp.spaceships.game.entity;

import com.dlapp.spaceships.game.GameWorld;
import com.dlapp.spaceships.game.desc.AliveEntityDesc;


public class PlayerEntity extends WorldAliveEntity {
    public final AliveEntityDesc desc;

    public PlayerEntity(GameWorld world, AliveEntityDesc desc, String sessionId) {
        super(world, "player_" + sessionId, desc, 0, 0, 0);
        this.desc = desc;
    }

}
