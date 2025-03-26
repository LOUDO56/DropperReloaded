package fr.loudo.dropperReloaded.events;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.filters.KickPlayerFilter;
import fr.loudo.dropperReloaded.games.GameStatus;
import fr.loudo.dropperReloaded.players.PlayerSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

public class PlayerKick implements Listener {
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        if(DropperReloaded.getInstance().getPlayersSessionManager().isPlaying(player)) {
            PlayerSession playerSession = DropperReloaded.getInstance().getPlayersSessionManager().getPlayerSession(player);
            if(playerSession.getPlayerGame().getGameStatus() == GameStatus.DOOR_COUNTDOWN) {
                if(event.getReason().contains("Flying")) {
                    Logger logger = (Logger) LogManager.getRootLogger();
                    KickPlayerFilter filter = new KickPlayerFilter();
                    logger.addFilter(filter);
                    event.setCancelled(true);
                    logger.get().removeFilter(filter);
                }
            }
        }
    }
}
