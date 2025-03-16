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
                if(!playerSession.canResetLocation()) {
                    player.sendMessage(MessageConfigUtils.get("games.items.reset_location.extra.cant_reset_location"));
                    return;
                }
                if(Objects.equals(player.getItemInHand(), DropperItems.resetLocation.getItem())) {
                    playerSession.setInvincible(true);
                    playerSession.setCanResetLocation(false);
                    playerSession.addDeath(true);
                    player.sendMessage(MessageConfigUtils.get("games.reset_location"));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            playerSession.setInvincible(false);
                        }
                    }.runTaskLater(DropperReloaded.getInstance(), 10L);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            playerSession.setCanResetLocation(true);
                        }
                    }.runTaskLater(DropperReloaded.getInstance(), Long.parseLong(MessageConfigUtils.get("games.items.reset_location.extra.countdown_before_new_click")) * 20L);
                }

                if(playerSession.isSpectator() && Objects.equals(player.getItemInHand(), DropperItems.playAgain.getItem())) {
                    DropperReloaded.getGamesManager().joinGame(player);
                }

                if(Objects.equals(player.getItemInHand(), DropperItems.playerVisibilityOn.getItem())) {
                    for(Player playerOfGame : playerSession.getPlayerGame().getPlayerList()) {
                        player.hidePlayer(playerOfGame);
                    }
                    player.getInventory().setItem(DropperItems.playerVisibilityOff.getSlot(), DropperItems.playerVisibilityOff.getItem());
                    player.sendMessage(MessageConfigUtils.get("games.items.player_visibility_on.extra.players_hidden"));
                    return;
                }

                if(Objects.equals(player.getItemInHand(), DropperItems.playerVisibilityOff.getItem())) {
                    for(Player playerOfGame : playerSession.getPlayerGame().getPlayerList()) {
                        player.showPlayer(playerOfGame);
                    }
                    player.getInventory().setItem(DropperItems.playerVisibilityOn.getSlot(), DropperItems.playerVisibilityOn.getItem());
                    player.sendMessage(MessageConfigUtils.get("games.items.player_visibility_off.extra.players_shown"));
                }
            }
        }

    }
}
