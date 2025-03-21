package fr.loudo.dropperReloaded.commands.dropperadmin.actions;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.commands.dropperadmin.CommandHelpAdmin;
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
        int npcId = DropperReloaded.getInstance().getConfig().getInt("main_lobby.npc_id");
        NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if(npc != null) {
            CitizensAPI.getNPCRegistry().deregister(npc);
        }
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "[NPC_DropperReloaded_joinGame]");
        npc.data().setPersistent(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        Location playerLoc = player.getLocation();
        Location npcLoc = new Location(player.getWorld(), playerLoc.getBlockX() + 0.5, playerLoc.getBlockY(), playerLoc.getBlockZ() + 0.5);
        npcLoc.setYaw(PlayerUtils.getDefaultYaw(playerLoc.getYaw()));
        npcLoc.setPitch(0);
        npc.spawn(npcLoc);
        DropperReloaded.getInstance().getConfig().set("main_lobby.npc_id", npc.getId());
        DropperReloaded.getInstance().saveConfig();
        player.sendMessage(ChatColor.GREEN + "Join game NPC set with success!");
    }

}
