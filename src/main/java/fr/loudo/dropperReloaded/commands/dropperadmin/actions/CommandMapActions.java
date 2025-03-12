package fr.loudo.dropperReloaded.commands.dropperadmin.actions;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.commands.dropperadmin.CommandHelpAdmin;
import fr.loudo.dropperReloaded.manager.maps.Map;
import fr.loudo.dropperReloaded.manager.maps.MapDifficulty;
import fr.loudo.dropperReloaded.manager.maps.MapDifficultyColorPrefix;
import fr.loudo.dropperReloaded.manager.maps.MapsManager;
import fr.loudo.dropperReloaded.utils.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CommandMapActions {

    private static final MapsManager MAPS_MANAGER = DropperReloaded.getMapsManager();
    private static final String PUT_A_NAME_MESSAGE = ChatColor.RED + "You need to put a name!";
    private static final String MAP_DONT_EXIST = ChatColor.RED + "This map doesn't exist.";

    public static void execute(String action, String value, String value2, Player player) {
        switch (action.toLowerCase()) {
            case "create":
                createMap(value, player);
                break;
            case "delete":
                removeMap(value, player);
                break;
            case "setdifficulty":
                setDifficulty(value, value2, player);
                break;
            case "addspawn":
                addSpawn(value, player);
                break;
            case "remlastspawn":
                removeLastSpawn(value, player);
                break;
            case "enable":
                enableMap(value, player);
                break;
            case "disable":
                disableMap(value, player);
                break;
            //TODO: List with a gui
            default:
                player.sendMessage(CommandHelpAdmin.send());
                break;
        }

        MAPS_MANAGER.serialize();
    }

    private static void createMap(String mapName, Player player) {
        if(!validateMapName(mapName, player, false)) return;

        Map map = new Map(mapName);
        if(MAPS_MANAGER.addMap(map)) {
            player.sendMessage(ChatColor.GREEN + "The map" + ChatColor.YELLOW + " " + mapName + ChatColor.GREEN + " has been successfully created.");
        } else {
            player.sendMessage(ChatColor.RED + "This map already exist.");
        }
    }

    private static void removeMap(String mapName, Player player) {
        if(!validateMapName(mapName, player)) return;

        Map map = MAPS_MANAGER.getFromName(mapName);
        if(MAPS_MANAGER.removeMap(map)) {
            player.sendMessage(ChatColor.GREEN + "The map" + ChatColor.YELLOW + " " + map.getName() + ChatColor.GREEN + " has been successfully removed.");
        } else {
            player.sendMessage(MAP_DONT_EXIST);
        }
    }

    private static void setDifficulty(String mapName, String difficulty, Player player) {
        if(!validateMapName(mapName, player)) return;

        try {
            MapDifficulty mapDifficulty = MapDifficulty.valueOf(difficulty.toUpperCase());
            Map map = MAPS_MANAGER.getFromName(mapName);
            map.setDifficulty(mapDifficulty);
            player.sendMessage(ChatColor.GREEN + "The map " + ChatColor.YELLOW + map.getName() + ChatColor.GREEN + " has been set to " + MapDifficultyColorPrefix.get(mapDifficulty) + mapDifficulty.toString().toLowerCase() + ChatColor.GREEN + ".");
        } catch (Exception e) {
            player.sendMessage(
                    ChatColor.RED + "Not a valid difficulty, choose "
                            + MapDifficultyColorPrefix.get(MapDifficulty.EASY) + "easy"
                            + ChatColor.RED + ", " + MapDifficultyColorPrefix.get(MapDifficulty.MEDIUM) + "medium"
                            + MapDifficultyColorPrefix.get(MapDifficulty.HARD) + " or hard.");
        }

    }

    private static void addSpawn(String mapName, Player player) {
        if(!validateMapName(mapName, player)) return;

        Map map = MAPS_MANAGER.getFromName(mapName);
        Location pLoc = player.getLocation();
        if(DropperReloaded.getInstance().getConfig().getBoolean("games.add_y_cord_on_spawns")) {
            pLoc.setY(pLoc.getY() + 0.5);
        }
        pLoc.setPitch(0);
        pLoc.setYaw(PlayerUtils.getDefaultYaw(pLoc.getYaw()));
        if(map.addSpawn(pLoc)) {
            player.sendMessage(ChatColor.GREEN + "You added a new spawn for " + ChatColor.YELLOW + map.getName() + ChatColor.GREEN + " (" + ChatColor.YELLOW + map.getSpawns().size() + ChatColor.GREEN + " in total)");
        } else {
            player.sendMessage(ChatColor.RED + "You already added this spawn.");
        }
    }

    private static void removeLastSpawn(String mapName, Player player) {
        if(!validateMapName(mapName, player)) return;

        Map map = MAPS_MANAGER.getFromName(mapName);
        if(map.removeLastSpawn()) {
            player.sendMessage(ChatColor.GREEN + "You removed the last spawn for " + ChatColor.YELLOW + map.getName() + ChatColor.GREEN + " (" + ChatColor.YELLOW + map.getSpawns().size() + ChatColor.GREEN + " in total)");
        } else {
            player.sendMessage(ChatColor.RED + "There's currently no spawn added!");
        }

    }

    private static void enableMap(String mapName, Player player) {
        if(!validateMapName(mapName, player)) return;

        Map map = MAPS_MANAGER.getFromName(mapName);
        if(map.isEnabled()) {
            player.sendMessage(ChatColor.YELLOW + map.getName() + ChatColor.RED + " is already enabled!");
            return;
        }

        if(map.getSpawns().isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + map.getName() + ChatColor.RED + " have no spawn!");
            return;
        }

        map.setEnabled(true);
        player.sendMessage(ChatColor.YELLOW + map.getName() + ChatColor.GREEN + " has been enabled!" );

    }

    private static void disableMap(String mapName, Player player) {
        if(!validateMapName(mapName, player)) return;

        Map map = MAPS_MANAGER.getFromName(mapName);
        if(!map.isEnabled()) {
            player.sendMessage(ChatColor.YELLOW + map.getName() + ChatColor.RED + " is already disabled!");
            return;
        }

        map.setEnabled(false);
        player.sendMessage(ChatColor.YELLOW + map.getName() + ChatColor.GREEN + " has been disabled!" );

    }

    private static boolean validateMapName(String mapName, Player player) {
        return validateMapName(mapName, player, true);
    }

    private static boolean validateMapName(String mapName, Player player, boolean checkExistence) {
        if (mapName.isEmpty()) {
            player.sendMessage(PUT_A_NAME_MESSAGE);
            return false;
        }
        if (checkExistence && !MAPS_MANAGER.mapExists(mapName)) {
            player.sendMessage(MAP_DONT_EXIST);
            return false;
        }
        return true;
    }

}
