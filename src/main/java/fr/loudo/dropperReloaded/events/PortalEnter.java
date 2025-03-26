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
            if(DropperReloaded.getInstance().getPlayersSessionManager().isPlaying(player)) {
                PlayerSession playerSession = DropperReloaded.getInstance().getPlayersSessionManager().getPlayerSession(player);
                if(!playerSession.canEnterPortal()) return;
                playerSession.setCanEnterPortal(false);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        playerSession.getPlayerGame().teleportPlayerToNextMap(player);
                    }
                }.runTaskLater(DropperReloaded.getInstance(), 1L);
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
