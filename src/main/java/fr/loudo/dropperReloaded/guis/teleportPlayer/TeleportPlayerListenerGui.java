package fr.loudo.dropperReloaded.guis.teleportPlayer;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.players.PlayerSession;
import fr.loudo.dropperReloaded.utils.MessageConfigUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TeleportPlayerListenerGui implements Listener {
    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        if(event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            if(!DropperReloaded.getInstance().getPlayersSessionManager().isPlaying(player)) return;
            if(slot < 0 || slot > event.getInventory().getSize()) return;
            if(event.getInventory().getItem(event.getSlot()) == null) return;
            PlayerSession playerSession = DropperReloaded.getInstance().getPlayersSessionManager().getPlayerSession(player);
            if(playerSession.getCurrentGui() instanceof TeleportPlayerGui) {
                TeleportPlayerGui teleportPlayerGui = (TeleportPlayerGui) playerSession.getCurrentGui();
                Player playerHead = (Player) teleportPlayerGui.getObjectFromSlot().get(slot);
                if(playerHead != null) {
                    PlayerSession playerHeadSession = DropperReloaded.getInstance().getPlayersSessionManager().getPlayerSession(playerHead);
                    if(playerHeadSession != null && !playerHeadSession.isSpectator()) {
                        player.teleport(playerHead.getLocation());
                        player.sendMessage(MessageConfigUtils.get("games.guis.teleporter_player.items.player_head.on_click.tped_to_player", "%player_name%", playerHead.getDisplayName()));
                    }
                }
                if(slot == 49) {
                    player.closeInventory();
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
