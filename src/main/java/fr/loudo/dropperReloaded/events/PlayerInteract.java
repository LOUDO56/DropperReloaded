package fr.loudo.dropperReloaded.events;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.commands.dropperadmin.DropperAdminCommand;
import fr.loudo.dropperReloaded.commands.dropperadmin.DropperWandPos;
import fr.loudo.dropperReloaded.games.Game;
import fr.loudo.dropperReloaded.guis.mapVote.MapVoteGui;
import fr.loudo.dropperReloaded.guis.teleportPlayer.TeleportPlayerGui;
import fr.loudo.dropperReloaded.items.DropperItems;
import fr.loudo.dropperReloaded.players.PlayerSession;
import fr.loudo.dropperReloaded.utils.MessageConfigUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class PlayerInteract implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(Objects.equals(player.getItemInHand(), DropperItems.stickWand.getItem()) && event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_AIR) {
            if(player.hasPermission("dropper_reloaded.admin")) {
                if(DropperAdminCommand.getWAND_POS_HASH_MAP().containsKey(player)) {
                    event.setCancelled(true);
                    DropperWandPos dropperWandPos = DropperAdminCommand.getWAND_POS_HASH_MAP().get(player);
                    if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        dropperWandPos.setPos1(event.getClickedBlock().getLocation());
                        player.sendMessage("Position 1 set.");
                    } else if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        if(!DropperReloaded.getVersion().startsWith("1.8")) {
                            if(Objects.equals(event.getHand(), EquipmentSlot.OFF_HAND)) {
                                return;
                            }
                        }
                        dropperWandPos.setPos2(event.getClickedBlock().getLocation());
                        player.sendMessage("Position 2 set.");
                    }
                }
            }
        }

        if(DropperReloaded.getPlayersSessionManager().isPlaying(player)) {
            if(!DropperReloaded.getVersion().startsWith("1.8")) {
                if(Objects.equals(event.getHand(), EquipmentSlot.OFF_HAND)) {
                    return;
                }
            }

            PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);

            if(Objects.equals(player.getItemInHand(), DropperItems.spectatorPlayerList.getItem())) {
                TeleportPlayerGui teleportPlayerGui = new TeleportPlayerGui(player);
                teleportPlayerGui.open();
                teleportPlayerGui.showCurrentPage();
            }

            if(Objects.equals(player.getItemInHand(), DropperItems.mapVote.getItem())) {
                MapVoteGui mapVoteGui = new MapVoteGui(player);
                mapVoteGui.open();
                mapVoteGui.showCurrentPage();
            }

            if(Objects.equals(player.getItemInHand(), DropperItems.leaveBed.getItem())) {
                DropperReloaded.getGamesManager().leaveGame(player);
            }

            if(Objects.equals(player.getItemInHand(), DropperItems.playAgain.getItem())) {
                playerSession.getPlayerGame().removePlayer(player);
                Game newGame = DropperReloaded.getGamesManager().getAvalaibleGame();
                newGame.addPlayer(player);
            }

            if(Objects.equals(player.getItemInHand(), DropperItems.playerVisibilityOn.getItem())) {
                for(Player playerOfGame : playerSession.getPlayerGame().getPlayerList()) {
                    player.hidePlayer(playerOfGame);
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.getInventory().setItem(DropperItems.playerVisibilityOff.getSlot(), DropperItems.playerVisibilityOff.getItem());
                    }
                }.runTaskLater(DropperReloaded.getInstance(), 1L);
                player.sendMessage(MessageConfigUtils.get("games.items.player_visibility_on.extra.players_hidden"));
                return;
            }

            if(Objects.equals(player.getItemInHand(), DropperItems.playerVisibilityOff.getItem())) {
                for(Player playerOfGame : playerSession.getPlayerGame().getPlayerList()) {
                    player.showPlayer(playerOfGame);
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.getInventory().setItem(DropperItems.playerVisibilityOn.getSlot(), DropperItems.playerVisibilityOn.getItem());
                    }
                }.runTaskLater(DropperReloaded.getInstance(), 1L);
                player.sendMessage(MessageConfigUtils.get("games.items.player_visibility_off.extra.players_shown"));
            }

            if(!playerSession.canResetLocation()) {
                player.sendMessage(MessageConfigUtils.get("games.items.reset_location.extra.cant_reset_location"));
                return;
            }

            if(Objects.equals(player.getItemInHand(), DropperItems.resetLocation.getItem())) {
               new BukkitRunnable() {
                   @Override
                   public void run() {
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
               }.runTaskLater(DropperReloaded.getInstance(), 1L);
            }
        }
    }
}
