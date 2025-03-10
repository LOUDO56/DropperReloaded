package fr.loudo.dropperReloaded.commands.dropperadmin.actions;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.commands.dropperadmin.CommandHelpAdmin;
import fr.loudo.dropperReloaded.manager.maps.Map;
import fr.loudo.dropperReloaded.manager.maps.MapsManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandMapActions {

    private static final MapsManager MAPS_MANAGER = DropperReloaded.getMapsManager();
    private static final String PUT_A_NAME_MESSAGE = ChatColor.RED + "You need to put a name!";

    public static void execute(String action, String value, Player player) {
        switch (action.toLowerCase()) {
            case "create":
                createMap(value, player);
                break;
            case "delete":
                removeMap(value, player);
                break;
            default:
                player.sendMessage(CommandHelpAdmin.send());
        }
    }

    private static void createMap(String mapName, Player player) {
        if(mapName.isEmpty()) {
            player.sendMessage(PUT_A_NAME_MESSAGE);
            return;
        }

        Map map = new Map(mapName);
        if(MAPS_MANAGER.addMap(map)) {
            player.sendMessage(ChatColor.GREEN + "The map" + ChatColor.YELLOW + " " + mapName + ChatColor.GREEN + " has been successfully created.");
        } else {
            player.sendMessage(ChatColor.RED + "This map already exist.");
        }
    }

    private static void removeMap(String mapName, Player player) {
        if(mapName.isEmpty()) {
            player.sendMessage(PUT_A_NAME_MESSAGE);
            return;
        }

        if(MAPS_MANAGER.removeMap(mapName)) {
            player.sendMessage(ChatColor.GREEN + "The map" + ChatColor.YELLOW + " " + mapName + ChatColor.GREEN + " has been successfully removed.");
        } else {
            player.sendMessage(ChatColor.RED + "This map doesn't exist.");
        }
    }

}
