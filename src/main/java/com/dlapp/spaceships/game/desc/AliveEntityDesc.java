package com.dlapp.spaceships.game.desc;

import com.dlapp.spaceships.game.GameConstants;

import java.util.Arrays;
import java.util.List;

public class AliveEntityDesc {

    public static final AliveEntityDesc SPACESHIP_DESC;

    static {
        SPACESHIP_DESC = new AliveEntityDesc(GameConstants.ENTITY_TYPE_SPACESHIP, 20, 100, 100,
                Arrays.asList(SkillDesc.SKILL_SHOT, SkillDesc.SKILL_ACCELERATION));
    }

    public final int type;
    public final int size;
    public final int health;
    public final int energy;
    public final List<SkillDesc> skills;

    public AliveEntityDesc(int type, int size, int health, int energy, List<SkillDesc> skills) {
        this.type = type;
        this.size = size;
        this.health = health;
        this.energy = energy;
        this.skills = skills;
    }

}
