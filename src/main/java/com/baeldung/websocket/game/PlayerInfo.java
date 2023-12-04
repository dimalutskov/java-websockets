package com.baeldung.websocket.game;

public class PlayerInfo {
    public final int health;
    public final int energy;
    public final int speed;
    public final int shotSpeed; // temp
    public PlayerInfo(int health, int energy, int speed, int shotSpeed) {
        this.health = health;
        this.energy = energy;
        this.speed = speed;
        this.shotSpeed = shotSpeed;
    }

    @Override
    public String toString() {
        return health + "," + energy + "," + speed + "," + shotSpeed;
    }

    public static PlayerInfo defaultInfo() {
        return new PlayerInfo(100, 100, 100, 300);
    }
}
