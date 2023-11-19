package com.baeldung.websocket;

public class MathUtils {

    public static float validateAngle(float angle) {
        if (angle < 0) return 360 + angle;
        else if (angle >= 360) return angle - 360;
        else return angle;
    }

}
