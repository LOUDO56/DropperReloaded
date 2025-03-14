package fr.loudo.dropperReloaded.utils;

import fr.loudo.dropperReloaded.DropperReloaded;

public class MessageConfigUtils {

    public static String get(String path) {
        return DropperReloaded.getInstance().getConfig().getString(path);
    }

    public static String get(String path, String toReplace, String replaceTo) {
        String message = DropperReloaded.getInstance().getConfig().getString(path);
        message = message.replace(toReplace, replaceTo);
        return message;
    }

}
