package fr.loudo.dropperReloaded.utils;

import fr.loudo.dropperReloaded.DropperReloaded;

public class MessageConfigUtils {

    public static String get(String path) {
        return DropperReloaded.getInstance().getConfig().getString(path);
    }

}
