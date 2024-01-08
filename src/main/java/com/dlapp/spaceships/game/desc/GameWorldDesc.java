package com.dlapp.spaceships.game.desc;

public class GameWorldDesc {
    private final long stateBroadcastInterval;
    private final int worldWidth;
    private final int worldHeight;

    public GameWorldDesc(long stateBroadcastInterval, int worldWidth, int worldHeight) {
        this.stateBroadcastInterval = stateBroadcastInterval;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public String toSocketString() {
        return stateBroadcastInterval + "," +
                worldWidth + "," +
                worldHeight;
    }

}
