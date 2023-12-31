package com.dlapp.spaceships.game.desc;

import com.dlapp.spaceships.game.GameConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collection;

public class SkillDesc {

    public enum SkillType {
        SINGLE, // Skill applied instantly when activated
        CONTINUOUS // Skill applied continuously while it's activated
    }

    public static final SkillDesc SKILL_ENERGY_RECOVER = new SkillDesc(GameConstants.SKILL_TYPE_PASSIVE_ENERGY_RECOVER, 0, 8);

    // values: size, damage, speed, fire rate
    public static final SkillDesc SKILL_SHOT = new SkillDesc(GameConstants.SKILL_TYPE_SHOT, 2, 20, 30, 300, 1000);
    public static final SkillDesc SKILL_ACCELERATION = new SkillDesc(GameConstants.SKILL_TYPE_ACCELERATION, 30, 20, 300);
    public static final SkillDesc SKILL_SHIELD = new SkillDesc(GameConstants.SKILL_TYPE_SHIELD, 50, 20);

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

    public static SkillType typeOf(int skillId) {
        switch (skillId) {
            case GameConstants.SKILL_TYPE_SHOT:
                return SkillType.SINGLE;
            case GameConstants.SKILL_TYPE_ACCELERATION:
            case GameConstants.SKILL_TYPE_SHIELD:
                return SkillType.CONTINUOUS;
        }
        return SkillType.SINGLE;
    }

    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();
        result.put("type", type);
        result.put("energyPrice", energyPrice);

        ArrayNode valuesNode = mapper.createArrayNode();
        for (int value : values) valuesNode.add(value);
        result.put("values", valuesNode);

        return result;
    }


}
