package fr.loudo.dropperReloaded.manager.maps;

import org.bukkit.ChatColor;

public class MapDifficultyColorPrefix {

    public static ChatColor get(MapDifficulty mapDifficulty) {
        switch (mapDifficulty) {
            case EASY:
                return ChatColor.GREEN;
            case MEDIUM:
                return ChatColor.YELLOW;
            case HARD:
                return ChatColor.RED;
        }
        return null;
    }

}
