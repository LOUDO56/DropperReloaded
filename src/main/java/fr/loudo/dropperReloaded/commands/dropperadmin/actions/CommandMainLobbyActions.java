package fr.loudo.dropperReloaded.commands.dropperadmin.actions;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.commands.dropperadmin.CommandHelpAdmin;
import fr.loudo.dropperReloaded.utils.Hologram;
import fr.loudo.dropperReloaded.utils.PlayerUtils;
import fr.loudo.dropperReloaded.waitlobby.WaitLobbyConfiguration;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CommandMainLobbyActions {

    public static void execute(String action, Player player) {
        switch (action.toLowerCase()) {
            case "setnpc":
                spawnMainLobbyNpc(player);
                break;
            default:
                player.sendMessage(CommandHelpAdmin.send());
                break;
        }

        DropperReloaded.getInstance().saveConfig();

    }

    private static void spawnMainLobbyNpc(Player player) {
        if(!DropperReloaded.isIsCitizenPluginEnabled()) {
            player.sendMessage(ChatColor.RED + "Plugin Citizens2 is required!");
            return;
        }
        DropperReloaded.getJoinGameNPCManager().createJoinGameNPC(player);
        player.sendMessage(ChatColor.GREEN + "Join game NPC set with success!");
    }

}
