package fr.loudo.dropperReloaded.commands.dropperadmin;

import org.bukkit.ChatColor;

public class CommandHelpAdmin {

    public static String send() {
        String prefixCmd = ChatColor.GREEN + "/dropperadmin ";
        String separator = ChatColor.YELLOW + " - ";

        StringBuilder helpMessage = new StringBuilder();

        helpMessage.append(prefixCmd).append("map create [name]").append(separator).append("Create a map.\n")
                .append(prefixCmd).append("map delete [name]").append(separator).append("Delete a map.\n")
                .append(prefixCmd).append("map rename [old_name] [new_name]").append(separator).append("Rename your map.\n")
                .append(prefixCmd).append("map setdifficulty [name] {easy, medium, hard}").append(separator)
                .append("Set the difficulty of the map to easy, medium or hard.\n")
                .append(prefixCmd).append("map addspawn [name]").append(separator).append("Add a spawn to your map.\n")
                .append(prefixCmd).append("map remlastspawn [name]").append(separator)
                .append("Remove the last added spawn of your map.\n")
                .append(prefixCmd).append("map wand").append(separator)
                .append("Get a stick to set the doors.\n")
                .append(prefixCmd).append("map setdoors [name]").append(separator)
                .append("Set the doors of the map.\n")
                .append(prefixCmd).append("map enable [name]").append(separator)
                .append("Make your map playable.\n")
                .append(prefixCmd).append("map disable [name]").append(separator)
                .append("Make your map closed for players.\n")
                .append(prefixCmd).append("map list").append(separator)
                .append("List your map.\n")
                .append(prefixCmd).append("waitlobby setmaxplayer [number]").append(separator)
                .append("Set the maximum number of players for each game.\n")
                .append(prefixCmd).append("waitlobby setminplayer [number]").append(separator)
                .append("Set the minimum number of players to start a game.\n")
                .append(prefixCmd).append("waitlobby setspawn").append(separator)
                .append("Set the location of the wait lobby.\n")
                .append(prefixCmd).append("mainlobby setnpc").append(separator)
                .append("Create an NPC that lets you join a game. (Available if Citizens is installed.)\n")
                .append(prefixCmd).append("mainlobby delnpc").append(separator)
                .append("Remove the join game NPC.\n")
                .append(prefixCmd).append("mainlobby setspawn").append(separator)
                .append("Set the spawn of the main lobby.\n")
                .append(prefixCmd).append("reload").append(separator)
                .append("Reload the plugin configuration.");


        return helpMessage.toString();
    }

}
