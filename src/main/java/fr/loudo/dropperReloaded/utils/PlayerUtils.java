package fr.loudo.dropperReloaded.utils;

public class PlayerUtils {
    public static int getDefaultYaw(float yaw) {
        yaw = (yaw % 360 + 360) % 360;

        if (yaw >= 315 || yaw < 45) {
            return 0;
        } else if (yaw >= 45 && yaw < 135) {
            return 90;
        } else if (yaw >= 135 && yaw < 225) {
            return 180;
        } else {
            return -90;
        }
    }
}
