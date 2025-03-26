package fr.loudo.dropperReloaded.events;

import fr.loudo.dropperReloaded.DropperReloaded;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class DropItem implements Listener {
    @EventHandler
    public void onDropItgem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if(DropperReloaded.getInstance().getPlayersSessionManager().isPlaying(player)) {
            event.setCancelled(true);
        }
    }
}
