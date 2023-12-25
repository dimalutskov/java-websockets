package com.dlapp.spaceships.game.entity;

import java.awt.geom.Rectangle2D;

public class EntityState {

    public final long createTime;

    private int size;
    private int x;
    private int y;
    private int angle;

    private final Rectangle2D.Double rect = new Rectangle2D.Double();

    public EntityState(long createTime, int size, int x, int y, int angle) {
        this.createTime = createTime;
        this.size = size;
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public int getSize() {
        return size;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getAngle() {
        return angle;
    }

    void setSize(int size) {
        this.size = size;
    }

    void setX(int x) {
        this.x = x;
    }

    void setY(int y) {
        this.y = y;
    }

    void setAngle(int angle) {
        this.angle = angle;
    }

    public Rectangle2D.Double getRect() {
        float halfSize = getSize() / 2.0f;
        rect.x = getX() - halfSize;
        rect.y = getY() - halfSize;
        rect.width =getSize();
        rect.height = getSize();
        return rect;
    }

    public String toStateString() {
        return size + "," +
                x + "," +
                y + "," +
                angle;
    }
}
