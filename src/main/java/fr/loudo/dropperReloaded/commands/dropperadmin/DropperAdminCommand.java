package fr.loudo.dropperReloaded.commands.dropperadmin;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.commands.dropperadmin.actions.CommandMapActions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DropperAdminCommand implements TabExecutor {

    private final List<String> SECTIONS = Arrays.asList("map", "waitlobby", "mainlobby", "leaderboard");
    private final List<String> MAP_ACTIONS = Arrays.asList("create", "delete", "setdifficulty", "addspawn", "remlastspawn", "enable", "disable", "list");

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
                default:
                    return SECTIONS;
            }
        }

        if(args.length == 3 && !args[1].equalsIgnoreCase("create")) {
            return DropperReloaded.getMapsManager().getListMapName();
        }

        if(args.length == 4 && args[1].equalsIgnoreCase("setdifficulty")) {
            return Arrays.asList("easy", "medium", "hard");
        }

        return Collections.emptyList();
    }

}
