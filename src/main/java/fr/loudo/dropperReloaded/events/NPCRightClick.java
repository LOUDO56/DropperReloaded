package fr.loudo.dropperReloaded.events;

import fr.loudo.dropperReloaded.DropperReloaded;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class NPCRightClick implements Listener {
    @EventHandler
    public void onNPCHit(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        if(entity.hasMetadata("NPC")) {
            NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
            if(DropperReloaded.getInstance().getConfig().getInt("main_lobby.npc.id") == npc.getId()) {
                DropperReloaded.getInstance().getGamesManager().joinGame(player);
            }
        }
    }
}
