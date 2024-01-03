package com.dlapp.spaceships.game.desc;

import com.dlapp.spaceships.game.GameConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Arrays;
import java.util.List;

public class EntityDesc {

    public static final EntityDesc SPACESHIP_DESC;

    static {
        SPACESHIP_DESC = new EntityDesc(GameConstants.ENTITY_TYPE_SPACESHIP, 100, 1000, 100,
                Arrays.asList(SkillDesc.SKILL_ENERGY_RECOVER, SkillDesc.SKILL_SHOT, SkillDesc.SKILL_ACCELERATION));
    }

    public final int type;
    public final int size;
    public final int health;
    public final int energy;
    public final List<SkillDesc> skills;

    public EntityDesc(int type, int size, int health, int energy, List<SkillDesc> skills) {
        this.type = type;
        this.size = size;
        this.health = health;
        this.energy = energy;
        this.skills = skills;
    }

    public SkillDesc getSkill(int skillType) {
        for (SkillDesc skillDesc : skills) {
            if (skillDesc.type == skillType) return skillDesc;
        }
        return null;
    }

    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();
        result.put("type", type);
        result.put("size", size);
        result.put("health", health);
        result.put("energy", energy);

        ArrayNode skillsNode = mapper.createArrayNode();
        for (SkillDesc skill : skills) skillsNode.add(skill.toJson());
        result.put("skills", skillsNode);

        return result;
    }

}
