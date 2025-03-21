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
        int npcId = DropperReloaded.getInstance().getConfig().getInt("main_lobby.npc.id");
        NPC npc = npcId > -1 ? CitizensAPI.getNPCRegistry().getById(npcId) : null;
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

        List<String> hologramLines = DropperReloaded.getInstance().getConfig().getStringList("main_lobby.npc.hologram");
        for(int i = 0; i < hologramLines.size(); i++) {
            hologramLines.set(i, hologramLines.get(i).replace("%player_number_playing%", String.valueOf(DropperReloaded.getPlayersSessionManager().getPlayerSessionList().size())));
        }

        Hologram joinGameHologram = new Hologram(hologramLines, npc.getEntity().getLocation());
        joinGameHologram.spawn();

        DropperReloaded.getInstance().getConfig().set("main_lobby.npc.id", npc.getId());
        DropperReloaded.getInstance().saveConfig();
        player.sendMessage(ChatColor.GREEN + "Join game NPC set with success!");
    }

}
