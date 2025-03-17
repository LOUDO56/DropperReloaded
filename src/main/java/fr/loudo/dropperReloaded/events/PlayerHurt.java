package fr.loudo.dropperReloaded.events;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.games.GameStatus;
import fr.loudo.dropperReloaded.players.PlayerSession;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerHurt implements Listener {
    @EventHandler
    public void onPlayerHurt(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if(DropperReloaded.getPlayersSessionManager().isPlaying(player)) {
                if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    event.setCancelled(true);
                    return;
                }
                PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
                if(playerSession.getPlayerGame().getGameStatus() != GameStatus.PLAYING || playerSession.isInvincible() || playerSession.isSpectator()) {
                    event.setCancelled(true);
                    if(playerSession.getPlayerGame().getGameStatus() == GameStatus.STARTING || playerSession.getPlayerGame().getGameStatus() == GameStatus.WAITING) {
                        if(event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                            player.teleport(DropperReloaded.getWaitLobbyConfiguration().getSpawn());
                        }
                    }
                } else {
                    if(event.getDamage() >= player.getHealth()) {
                        event.setCancelled(true);
                        playerSession.addDeath();
                    }
                }
            }
        }
    }
}
