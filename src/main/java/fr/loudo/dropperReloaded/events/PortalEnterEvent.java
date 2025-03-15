package fr.loudo.dropperReloaded.events;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.players.PlayerSession;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class PortalEnterEvent implements Listener {
    @EventHandler
    public void onPlayerPortalEvent(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        if(DropperReloaded.getPlayersSessionManager().isPlaying(player)) {
            event.setCancelled(true);
            PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
            playerSession.getPlayerGame().teleportPlayerToNextMap(player);
        }
    }
}
