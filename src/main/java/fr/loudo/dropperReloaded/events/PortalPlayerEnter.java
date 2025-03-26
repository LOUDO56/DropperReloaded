package fr.loudo.dropperReloaded.events;

import fr.loudo.dropperReloaded.DropperReloaded;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class PortalPlayerEnter implements Listener {
    @EventHandler
    public void onPlayerPortalEnter(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        if(DropperReloaded.getInstance().getPlayersSessionManager().isPlaying(player)) {
            event.setCancelled(true);
        }
    }
}
