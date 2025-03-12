package fr.loudo.dropperReloaded.commands.dropperadmin.actions;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.commands.dropperadmin.CommandHelpAdmin;
import fr.loudo.dropperReloaded.manager.waitlobby.WaitLobbyConfiguration;
import fr.loudo.dropperReloaded.utils.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CommandWaitLobbyActions {

    private static final WaitLobbyConfiguration WAIT_LOBBY_CONFIGURATION = DropperReloaded.getWaitLobbyConfiguration();
    private static final String PROVIDE_NUMBER = ChatColor.RED + "Please, provide a number!";

    public static void execute(String action, String value, Player player) {
        switch (action.toLowerCase()) {
            case "setminplayer":
                try {
                    setMinPlayer(Integer.parseInt(value), player);
                } catch (NumberFormatException e) {
                    player.sendMessage(PROVIDE_NUMBER);
                }
                break;
            case "setmaxplayer":
                try {
                    setMaxPlayer(Integer.parseInt(value), player);
                } catch (NumberFormatException e) {
                    player.sendMessage(PROVIDE_NUMBER);
                }
                break;
            case "setspawn":
                setSpawn(player);
                break;
            default:
                player.sendMessage(CommandHelpAdmin.send());
                break;
        }

        DropperReloaded.getInstance().saveConfig();

    }

    private static void setMinPlayer(int minPlayer, Player player) {
        WAIT_LOBBY_CONFIGURATION.setMinPlayer(minPlayer);
        player.sendMessage(ChatColor.GREEN + "Minimum number of player to start a game is now set to " + ChatColor.YELLOW + minPlayer);
    }

    private static void setMaxPlayer(int maxPlayer, Player player) {
        WAIT_LOBBY_CONFIGURATION.setMaxPlayer(maxPlayer);
        player.sendMessage(ChatColor.GREEN + "Maximum number of player is now set to " + ChatColor.YELLOW + maxPlayer);
    }
    private static void setSpawn(Player player) {
        Location pLoc = player.getLocation();
        Location blockLoc = new Location(player.getWorld(), pLoc.getBlockX(), pLoc.getBlockY() + 1.5, pLoc.getBlockZ());
        blockLoc.setPitch(0);
        blockLoc.setYaw(PlayerUtils.getDefaultYaw(pLoc.getYaw()));
        WAIT_LOBBY_CONFIGURATION.setSpawn(blockLoc);
        player.sendMessage(ChatColor.GREEN + "Wait lobby has been set!");
    }

}
