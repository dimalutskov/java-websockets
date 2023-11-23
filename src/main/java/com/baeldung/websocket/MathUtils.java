package com.baeldung.websocket;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MathUtils {

    // TODO thread local
    private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat();

    static {
        DECIMAL_FORMAT.setMaximumFractionDigits(2);
    }

    public static String formatFloat(float value) {
        return DECIMAL_FORMAT.format(value);
    }

    public static float validateAngle(float angle) {
        if (angle < 0) return 360 + angle;
        else if (angle >= 360) return angle - 360;
        else return angle;
    }

}
