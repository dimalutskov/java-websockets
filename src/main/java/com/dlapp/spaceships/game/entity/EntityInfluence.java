package com.dlapp.spaceships.game.entity;

public class EntityInfluence {

    public static final int TYPE_SINGLE_ENERGY_CONSUMPTION = 1;
    public static final int TYPE_SINGLE_DAMAGE = 2;

    public static final int TYPE_CONTINUOUS_ENERGY_RECOVER = 101;
    public static final int TYPE_CONTINUOUS_ENERGY_CONSUMPTION = 102;

    public final int type;
    public final long attachTime;
    public final int skillType;
    public final String ownerId;

    public final int[] values;

    private long applyTime;

    public EntityInfluence(int type, long attachTime, int skillType, String ownerId, int... values) {
        this.type = type;
        this.attachTime = attachTime;
        this.skillType = skillType;
        this.ownerId = ownerId;
        this.values = values;
        this.applyTime = attachTime;
    }

    public long getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(long applyTime) {
        this.applyTime = applyTime;
    }
}
