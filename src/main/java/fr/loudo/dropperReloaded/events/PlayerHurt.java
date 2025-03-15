package fr.loudo.dropperReloaded.events;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.maps.Map;
import fr.loudo.dropperReloaded.players.PlayerSession;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPortalEvent;

public class PlayerHurt implements Listener {
    @EventHandler
    public void onPlayerHurt(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if(DropperReloaded.getPlayersSessionManager().isPlaying(player)) {
                if(event.getDamage() >= player.getHealth()) {
                    event.setCancelled(true);
                    PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
                    playerSession.addDeath();
                }
            }
        }
    }
}
