package fr.loudo.dropperReloaded.events;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.players.PlayerSession;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PortalEnter implements Listener {

    @EventHandler
    public void onPlayerPortalEnter(EntityPortalEnterEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if(DropperReloaded.getPlayersSessionManager().isPlaying(player)) {
                PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
                if(!playerSession.canEnterPortal()) return;
                playerSession.getPlayerGame().teleportPlayerToNextMap(player);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        playerSession.setCanEnterPortal(true);
                    }
                }.runTaskLater(DropperReloaded.getInstance(), 5L);
            }
        }
    }

}
