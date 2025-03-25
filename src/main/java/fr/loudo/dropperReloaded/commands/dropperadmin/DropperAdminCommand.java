package fr.loudo.dropperReloaded.commands.dropperadmin;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.commands.dropperadmin.actions.CommandMainLobbyActions;
import fr.loudo.dropperReloaded.commands.dropperadmin.actions.CommandMapActions;
import fr.loudo.dropperReloaded.commands.dropperadmin.actions.CommandWaitLobbyActions;
import fr.loudo.dropperReloaded.items.DropperItems;
import fr.loudo.dropperReloaded.waitlobby.WaitLobbyConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DropperAdminCommand implements TabExecutor {

    private static final HashMap<Player, DropperWandPos> WAND_POS_HASH_MAP = new HashMap<>();

    private final List<String> SECTIONS = Arrays.asList("map", "waitlobby", "mainlobby", "reload");
    private final List<String> MAP_ACTIONS = Arrays.asList("create", "delete", "rename", "setdifficulty", "addspawn", "remlastspawn", "tp", "enable", "disable", "wand", "setdoors", "list");
    private final List<String> WAITLOBBY_ACTIONS = Arrays.asList("setminplayer", "setmaxplayer", "setspawn");
    private final List<String> MAINLOBBY_ACTIONS = Arrays.asList("setnpc", "delnpc", "setspawn");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;

            String section = "";
            String action = "";
            String value = "";
            String value2 = "";

            switch (args.length) {
                case 0:
                    player.sendMessage(CommandHelpAdmin.send());
                    return true;
                case 1:
                    section = args[0];
                    break;
                case 2:
                    section = args[0];
                    action = args[1];
                    break;
                case 3:
                    section = args[0];
                    action = args[1];
                    value = args[2];
                    break;
                case 4:
                    section = args[0];
                    action = args[1];
                    value = args[2];
                    value2 = args[3];
                    break;
            }

            switch (section.toLowerCase()) {
                case "map":
                    CommandMapActions.execute(action, value, value2, player);
                    break;
                case "waitlobby":
                    CommandWaitLobbyActions.execute(action, value, player);
                    break;
                case "mainlobby":
                    CommandMainLobbyActions.execute(action, player);
                    break;
                case "reload":
                    DropperReloaded.getInstance().saveDefaultConfig();
                    DropperReloaded.getInstance().reloadConfig();
                    DropperItems.registerItems();
                    DropperReloaded.getJoinGameNPCManager().reloadHologramConfig();
                    DropperReloaded.getJoinGameNPCManager().updateNPCHologram();
                    DropperReloaded.getDatabase().initialize();
                    player.sendMessage(ChatColor.GREEN + "Config reloaded successfully!");
                    break;
                default:
                    player.sendMessage(CommandHelpAdmin.send());
                    break;

            }

        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length >= 1 && args.length <= 2) {
            switch (args[0].toLowerCase()) {
                case "map":
                    return MAP_ACTIONS;
                case "waitlobby":
                    return WAITLOBBY_ACTIONS;
                case "mainlobby":
                    return MAINLOBBY_ACTIONS;
                default:
                    return SECTIONS;
            }
        }

        if(args.length == 3
                && !args[1].equalsIgnoreCase("create")
                && !args[1].equalsIgnoreCase("wand")
                && !args[0].equalsIgnoreCase("waitlobby")
                && !args[0].equalsIgnoreCase("mainlobby")
                && !args[0].equalsIgnoreCase("leaderboard")
        ) {
            return DropperReloaded.getMapsManager().getListMapName();
        }

        if(args.length == 4 && args[1].equalsIgnoreCase("setdifficulty")) {
            return Arrays.asList("easy", "medium", "hard");
        }

        return Collections.emptyList();
    }

    public static HashMap<Player, DropperWandPos> getWAND_POS_HASH_MAP() {
        return WAND_POS_HASH_MAP;
    }
}
