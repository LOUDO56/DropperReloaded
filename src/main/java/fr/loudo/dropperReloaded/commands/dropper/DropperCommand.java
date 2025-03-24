package fr.loudo.dropperReloaded.commands.dropper;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.stats.DropperStats;
import fr.loudo.dropperReloaded.utils.MessageConfigUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class DropperCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(args.length == 0) {
                player.sendMessage(CommandHelpPlayer.send());
                return true;
            }

            switch (args[0]) {
                case "play":
                    DropperReloaded.getGamesManager().joinGame(player);
                    break;
                case "leave":
                    if(!DropperReloaded.getGamesManager().leaveGame(player)) {
                        player.sendMessage(MessageConfigUtils.get("player.not_in_a_game"));
                    }
                    break;
                case "stats":
                    DropperStats dropperStats = DropperReloaded.getDatabase().getPlayerStats(player);
                    if(dropperStats != null) {
                        player.sendMessage(dropperStats.toString());
                    } else {
                        player.sendMessage(ChatColor.RED + "Database is down. Please contact the admins.");
                    }
                    break;
                default:
                    player.sendMessage(CommandHelpPlayer.send());
                    break;
            }

        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Arrays.asList("play", "leave", "stats");
    }
}
