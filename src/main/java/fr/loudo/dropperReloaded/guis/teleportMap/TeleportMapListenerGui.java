package fr.loudo.dropperReloaded.guis.teleportMap;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.maps.DropperMap;
import fr.loudo.dropperReloaded.players.PlayerSession;
import fr.loudo.dropperReloaded.utils.MessageConfigUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TeleportMapListenerGui implements Listener {
    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        if(event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            if(!DropperReloaded.getInstance().getPlayersSessionManager().isPlaying(player)) return;
            if(slot < 0 || slot > event.getInventory().getSize()) return;
            if(event.getInventory().getItem(event.getSlot()) == null) return;
            PlayerSession playerSession = DropperReloaded.getInstance().getPlayersSessionManager().getPlayerSession(player);
            if(playerSession.getCurrentGui() instanceof TeleportMapGui) {
                TeleportMapGui teleportMapGui = (TeleportMapGui) playerSession.getCurrentGui();
                DropperMap dropperMap = (DropperMap) teleportMapGui.getObjectFromSlot().get(slot);
                if(dropperMap != null) {
                    player.teleport(dropperMap.getRandomSpawn());
                    player.sendMessage(MessageConfigUtils.get("games.guis.teleporter_map.items.dropper_map.on_click.tped_to_map", "%map_name%", dropperMap.getName()));
                }
                if(slot == 49) {
                    player.closeInventory();
                }
                if(slot == 53) {
                    teleportMapGui.nextPage();
                }
                if(slot == 45) {
                    teleportMapGui.previousPage();
                }
            }
        }
    }

}
