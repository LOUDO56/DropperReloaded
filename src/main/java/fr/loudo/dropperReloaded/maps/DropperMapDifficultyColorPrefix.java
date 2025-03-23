package fr.loudo.dropperReloaded.maps;

import org.bukkit.ChatColor;

public class DropperMapDifficultyColorPrefix {

    public static ChatColor get(DropperMapDifficulty dropperMapDifficulty) {
        switch (dropperMapDifficulty) {
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
