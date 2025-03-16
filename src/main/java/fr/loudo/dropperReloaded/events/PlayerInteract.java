package fr.loudo.dropperReloaded.events;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.games.GameStatus;
import fr.loudo.dropperReloaded.items.DropperItems;
import fr.loudo.dropperReloaded.players.PlayerSession;
import fr.loudo.dropperReloaded.utils.MessageConfigUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class PlayerInteract implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(DropperReloaded.getPlayersSessionManager().isPlaying(player)) {
            PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
            if(playerSession.getPlayerGame().getGameStatus() != GameStatus.PLAYING && playerSession.getPlayerGame().getGameStatus() != GameStatus.DOOR_COUNTDOWN) {
                if(Objects.equals(player.getItemInHand(), DropperItems.leaveBed.getItem())) {
                    DropperReloaded.getGamesManager().leaveGame(player);
                }
            } else {
                if(Objects.equals(player.getItemInHand(), DropperItems.resetLocation.getItem())) {
                    playerSession.setInvincible(true);
                    player.teleport(playerSession.getCurrentMap().getRandomSpawn());
                    player.sendMessage(MessageConfigUtils.get("games.reset_location"));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            playerSession.setInvincible(false);
                        }
                    }.runTaskLater(DropperReloaded.getInstance(), 10L);
                }
            }
        }

    }
}
