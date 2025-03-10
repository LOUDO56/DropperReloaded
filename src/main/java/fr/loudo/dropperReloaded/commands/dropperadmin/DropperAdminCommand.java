package fr.loudo.dropperReloaded.commands.dropperadmin;

import fr.loudo.dropperReloaded.commands.dropperadmin.actions.CommandMapActions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DropperAdminCommand implements TabExecutor {

    private final List<String> SECTIONS = Arrays.asList("map", "waitlobby", "mainlobby", "leaderboard");
    private final List<String> MAP_ACTIONS = Arrays.asList("create", "delete", "setdifficulty", "addspawn", "enable", "disable", "list");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;

            String section = "";
            String action = "";
            String value = "";

            switch (args.length) {
                case 0:
                    player.sendMessage(CommandHelpAdmin.send());
                    break;
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
            }

            switch (section.toLowerCase()) {
                case "map":
                    CommandMapActions.execute(action, value, player);
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

        return Collections.emptyList();
    }

}
