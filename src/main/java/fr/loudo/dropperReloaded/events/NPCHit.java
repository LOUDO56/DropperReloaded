package fr.loudo.dropperReloaded.events;

import fr.loudo.dropperReloaded.DropperReloaded;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class NPCHit implements Listener {
    @EventHandler
    public void onNPCHit(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if(entity.hasMetadata("NPC") && event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
            if(DropperReloaded.getInstance().getConfig().getInt("main_lobby.npc.id") == npc.getId()) {
                DropperReloaded.getGamesManager().joinGame(player);
            }
        }
    }
}
