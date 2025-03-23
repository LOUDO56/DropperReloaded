package fr.loudo.dropperReloaded.guis.teleportPlayer;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.maps.DropperMap;
import fr.loudo.dropperReloaded.maps.DropperMapDifficultyColorPrefix;
import fr.loudo.dropperReloaded.players.PlayerSession;
import fr.loudo.dropperReloaded.utils.MessageConfigUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TeleportPlayerListener implements Listener {
    //TODO: fix index out of bound if we click outside the inventory
    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        if(event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if(!DropperReloaded.getPlayersSessionManager().isPlaying(player)) return;
            if(event.getInventory().getItem(event.getSlot()) == null) return;
            PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
            if(playerSession.getCurrentGui() instanceof TeleportPlayerGui) {
                TeleportPlayerGui teleportPlayerGui = (TeleportPlayerGui) playerSession.getCurrentGui();
                int slot = event.getSlot();
                Player playerHead = teleportPlayerGui.getSlotAndPlayers().get(slot);
                PlayerSession playerHeadSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(playerHead);
                if(playerHead != null && playerHeadSession != null && !playerHeadSession.isSpectator()) {
                    player.teleport(playerHead.getLocation());
                    player.sendMessage(MessageConfigUtils.get("games.guis.teleporter.items.player_head.on_click.tped_to_player", "%player_name%", playerHead.getDisplayName()));
                }
                if(slot == 53) {
                    teleportPlayerGui.nextPage();
                }
                if(slot == 45) {
                    teleportPlayerGui.previousPage();
                }
            }
        }
    }

}
