package com.dlapp.spaceships.game.desc;

import com.dlapp.spaceships.game.GameConstants;

import java.util.Collection;

public class SkillDesc {

    // size, damage, speed, fire rate
    public static final SkillDesc SKILL_SHOT = new SkillDesc(GameConstants.SKILL_TYPE_SHOT, 2, 8, 20, 300, 1000);
    public static final SkillDesc SKILL_ACCELERATION = new SkillDesc(GameConstants.SKILL_TYPE_ACCELERATION, 30, 20, 300);

    public final int type;
    public final int energyPrice;
    public final int[] values;

    public SkillDesc(int type, int energyPrice, int... values) {
        this.type = type;
        this.energyPrice = energyPrice;
        this.values = values;
    }

    public static SkillDesc find(Collection<SkillDesc> skills, int type) {
        for (SkillDesc skillDesc : skills) {
            if (skillDesc.type == type) return skillDesc;
        }
        return null;
    }

}