package com.dlapp.spaceships.game.entity;

public class EntityInfluence {

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
