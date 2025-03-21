package fr.loudo.dropperReloaded.npc;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.utils.Hologram;
import fr.loudo.dropperReloaded.utils.PlayerUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

public class JoinGameNPCManager {

    private NPC npc;
    private Hologram hologram;

    public void createJoinGameNPC(Player player) {
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "[NPC_DropperReloaded_joinGame]");
        npc.data().setPersistent(NPC.Metadata.NAMEPLATE_VISIBLE, false);

        Location playerLoc = player.getLocation();
        Location npcLoc = new Location(player.getWorld(), playerLoc.getBlockX() + 0.5, playerLoc.getBlockY(), playerLoc.getBlockZ() + 0.5);
        npcLoc.setYaw(PlayerUtils.getDefaultYaw(playerLoc.getYaw()));
        npcLoc.setPitch(0);
        npc.spawn(npcLoc);

        hologram = new Hologram(getHologramLinesJoinGame(), npc.getEntity().getLocation());
        hologram.spawn();

        DropperReloaded.getInstance().getConfig().set("main_lobby.npc.id", npc.getId());
        DropperReloaded.getInstance().saveConfig();

        player.sendMessage(ChatColor.GREEN + "Join game NPC set with success!");
    }

    public void createNPCHologramLines() {

        int npcId = DropperReloaded.getInstance().getConfig().getInt("main_lobby.npc.id", -1);
        NPC npc = npcId > - 1 ? CitizensAPI.getNPCRegistry().getById(npcId) : null;
        if(npc == null) {
            return;
        }

        hologram = new Hologram(getHologramLinesJoinGame(), npc.getEntity().getLocation());

    }

    private List<String> getHologramLinesJoinGame() {
        List<String> hologramLines = DropperReloaded.getInstance().getConfig().getStringList("main_lobby.npc.hologram_lines");
        for (int i = 0; i < hologramLines.size(); i++) {
            hologramLines.set(i, hologramLines.get(i).replace("%player_number_playing%", String.valueOf(DropperReloaded.getPlayersSessionManager().getPlayerSessionList().size())));
        }
        return hologramLines;
    }

    public void updateNPCHologram() {
        if(hologram != null) {
            hologram.update(getHologramLinesJoinGame());
        }
    }

}
