package fr.loudo.dropperReloaded.utils;

public class PlayerUtils {
    public static String getCardinalDirection(float yaw) {
        yaw = (yaw % 360 + 360) % 360;

        if (yaw >= 315 || yaw < 45) {
            return "South";
        } else if (yaw >= 45 && yaw < 135) {
            return "West";
        } else if (yaw >= 135 && yaw < 225) {
            return "North";
        } else {
            return "East";
        }
    }
}
