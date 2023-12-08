package com.dlapp.spaceships;

import java.awt.geom.Rectangle2D;
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

    public static boolean belongs(Rectangle2D.Double rect, double x, double y) {
        return x >= rect.x && x <= rect.x + rect.width && y >= rect.y && y <= rect.y + rect.height;
    }

    public static boolean intersects(Rectangle2D.Double rect1, Rectangle2D.Double rect2) {
        return belongs(rect1, rect2.x, rect2.y) ||
                belongs(rect1, rect2.x, rect2.y + rect2.height) ||
                belongs(rect1, rect2.x + rect2.width, rect2.y) ||
                belongs(rect1, rect2.x + rect2.width, rect2.y + rect2.height) ||
               belongs(rect2, rect1.x, rect1.y) ||
                belongs(rect2, rect1.x, rect1.y + rect1.height) ||
                belongs(rect2, rect1.x + rect1.width, rect1.y) ||
                belongs(rect2, rect1.x + rect1.width, rect1.y + rect1.height);
    }

}
