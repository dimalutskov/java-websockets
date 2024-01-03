package com.dlapp.spaceships.game.object;

import com.dlapp.spaceships.game.IGameWorld;
import com.dlapp.spaceships.game.desc.EntityDesc;


public class PlayerEntity extends GameEntity {
    public final EntityDesc desc;

    public PlayerEntity(IGameWorld world, EntityDesc desc, String sessionId) {
        super(world, "player_" + sessionId, desc, 0, 0, 0);
        this.desc = desc;
    }

}
