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
    //TODO: fix index out of bound if we click outside the inventory
    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        if(event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if(!DropperReloaded.getPlayersSessionManager().isPlaying(player)) return;
            if(event.getInventory().getItem(event.getSlot()) == null) return;
            PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
            if(playerSession.getCurrentGui() instanceof TeleportMapGui) {
                TeleportMapGui teleportMapGui = (TeleportMapGui) playerSession.getCurrentGui();
                int slot = event.getSlot();
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
