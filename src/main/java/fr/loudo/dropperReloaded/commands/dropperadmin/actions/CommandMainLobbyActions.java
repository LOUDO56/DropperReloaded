package fr.loudo.dropperReloaded.commands.dropperadmin.actions;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.commands.dropperadmin.CommandHelpAdmin;
import fr.loudo.dropperReloaded.utils.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CommandMainLobbyActions {

    public static void execute(String action, Player player) {
        switch (action.toLowerCase()) {
            case "setnpc":
                spawnMainLobbyNpc(player);
                break;
            case "delnpc":
                removeMainLobbyNpc(player);
                break;
            case "setspawn":
                setMainLobbySpawn(player);
                break;
            default:
                player.sendMessage(CommandHelpAdmin.send());
                break;
        }

        DropperReloaded.getInstance().saveConfig();

    }

    private static void spawnMainLobbyNpc(Player player) {
        if(!DropperReloaded.getInstance().isIsCitizenPluginEnabled()) {
            player.sendMessage(ChatColor.RED + "Plugin Citizens2 is required!");
            return;
        }
        DropperReloaded.getInstance().getJoinGameNPCManager().createJoinGameNPC(player);
        player.sendMessage(ChatColor.GREEN + "Join game NPC set with success!");
    }

    private static void removeMainLobbyNpc(Player player) {
        if(!DropperReloaded.getInstance().isIsCitizenPluginEnabled()) {
            player.sendMessage(ChatColor.RED + "Plugin Citizens2 is required!");
            return;
        }
        if(DropperReloaded.getInstance().getJoinGameNPCManager().deleteJoinGameNPC()) {
            player.sendMessage(ChatColor.GREEN + "Join game NPC deleted with success!");
        } else {
            player.sendMessage(ChatColor.RED + "Join game NPC does not exists!");
        }
    }

    private static void setMainLobbySpawn(Player player) {
        Location location = player.getLocation();
        location = new Location(player.getWorld(), location.getBlockX() + 0.5, location.getBlockY(), location.getZ() + 0.5);
        location.setPitch(0);
        location.setYaw(PlayerUtils.getDefaultYaw(player.getLocation().getYaw()));
        DropperReloaded.getInstance().getConfig().set("main_lobby.spawn", location);
        player.sendMessage(ChatColor.GREEN + "Main lobby spawn set!");
    }

}
