package com.dlapp.spaceships.game;

public class GameConstants {

    ///////////////////// ENTITY TYPES //////////////////
    public static final int ENTITY_TYPE_SPACESHIP = 1;
    // object_id of shot types will be: "{owner_id}_{unique_id}"
    public static final int ENTITY_TYPE_SHOT = 2;

    /////////////////// SKILL TYPES ///////////////
    public static final int SKILL_TYPE_PASSIVE_ENERGY_RECOVER = 101;

    public static final int SKILL_TYPE_SHOT = 1;
    public static final int SKILL_TYPE_ACCELERATION = 2;
    public static final int SKILL_TYPE_SHIELD = 3;

    //////////////// INFLUENCES //////////////
    // values: energy
    public static final int INFLUENCE_SINGLE_ENERGY_CONSUMPTION = 1;
    // values: damage
    public static final int INFLUENCE_SINGLE_DAMAGE = 2;

    // values: energy
    public static final int INFLUENCE_CONTINUOUS_ENERGY_RECOVER = 101;
    // values: energy
    public static final int INFLUENCE_CONTINUOUS_ENERGY_CONSUMPTION = 102;
    // value: damage protection
    public static final int INFLUENCE_CONTINUOUS_SHIELD = 103;


}
