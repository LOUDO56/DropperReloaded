package fr.loudo.dropperReloaded.commands.dropper;

import org.bukkit.ChatColor;

public class CommandHelpPlayer {

    public static String send() {
        String prefixCmd = ChatColor.GREEN + "/dropper ";
        String separator = ChatColor.YELLOW + " - ";

        StringBuilder helpMessage = new StringBuilder();

        helpMessage.append(prefixCmd).append("map play").append(separator).append("Join a game.\n")
                .append("map leave").append(separator).append("Leave your current game.\n")
                .append("map stats").append(separator).append("Show your stats.\n");


        return helpMessage.toString();
    }

}
