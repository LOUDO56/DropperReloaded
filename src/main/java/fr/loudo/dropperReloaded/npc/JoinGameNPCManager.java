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
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

public class JoinGameNPCManager {

    private NPC npc;
    private Hologram hologram;

    public void createJoinGameNPC(Player player) {
        if(hologram != null) {
            hologram.remove();
        }

        Location playerLoc = player.getLocation();
        Location npcLoc = new Location(player.getWorld(), playerLoc.getBlockX() + 0.5, playerLoc.getBlockY(), playerLoc.getBlockZ() + 0.5);
        npcLoc.setYaw(PlayerUtils.getDefaultYaw(playerLoc.getYaw()));
        npcLoc.setPitch(0);

        if(npc == null) {
            npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "[NPC_DropperReloaded_joinGame]");
            npc.data().setPersistent(NPC.Metadata.NAMEPLATE_VISIBLE, false);
            npc.spawn(npcLoc);
        } else {
            npc.teleport(npcLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }

        hologram = new Hologram(getHologramLinesJoinGame(), npc.getEntity().getLocation());
        hologram.spawn();

        DropperReloaded.getInstance().getConfig().set("main_lobby.npc.id", npc.getId());
        DropperReloaded.getInstance().saveConfig();
    }

    public boolean deleteJoinGameNPC() {
        if(hologram == null || npc == null) {
            return false;
        }
        hologram.remove();
        CitizensAPI.getNPCRegistry().deregister(npc);
        hologram = null;
        npc = null;
        DropperReloaded.getInstance().getConfig().set("main_lobby.npc.id", -1);
        return true;
    }

    public boolean createNPCHologramLines() {

        int npcId = DropperReloaded.getInstance().getConfig().getInt("main_lobby.npc.id", -1);
        npc = npcId > - 1 ? CitizensAPI.getNPCRegistry().getById(npcId) : null;
        if(npc == null) {
            return false;
        }

        hologram = new Hologram(getHologramLinesJoinGame(), npc.getEntity().getLocation());
        hologram.spawn();

        return true;

    }

    public void reloadHologramConfig() {
        hologram.setLineGap(DropperReloaded.getInstance().getConfig().getDouble("main_lobby.npc.hologram.line_gap"));
        hologram.setOffsetY(DropperReloaded.getInstance().getConfig().getDouble("main_lobby.npc.hologram.offset_y"));
    }

    private List<String> getHologramLinesJoinGame() {
        List<String> hologramLines = DropperReloaded.getInstance().getConfig().getStringList("main_lobby.npc.hologram.lines");
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

    public Hologram getHologram() {
        return hologram;
    }
}
