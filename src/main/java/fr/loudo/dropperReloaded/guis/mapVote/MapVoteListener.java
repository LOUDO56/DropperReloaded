package fr.loudo.dropperReloaded.guis.mapVote;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.maps.Map;
import fr.loudo.dropperReloaded.maps.MapDifficultyColorPrefix;
import fr.loudo.dropperReloaded.players.PlayerSession;
import fr.loudo.dropperReloaded.utils.MessageConfigUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MapVoteListener implements Listener {
    //TODO: fix index out of bound if we click outside the inventory
    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        if(event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if(!DropperReloaded.getPlayersSessionManager().isPlaying(player)) return;
            if(event.getInventory().getItem(event.getSlot()) == null) return;
            PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
            if(playerSession.getCurrentGui() instanceof MapVoteGui) {
                MapVoteGui mapVoteGui = (MapVoteGui) playerSession.getCurrentGui();
                int slot = event.getSlot();
                Map map = mapVoteGui.getSlotAndMaps().get(slot);
                if(map != null) {
                    if(playerSession.getVotedMaps().contains(map)) {
                        playerSession.getVotedMaps().remove(map);
                        String message = MessageConfigUtils.get("wait_lobby.guis.map_voting.items.map.on_click.message_revoked_vote");
                        message = message.replace("%map_name%", MapDifficultyColorPrefix.get(map.getDifficulty()) + map.getName());
                        player.sendMessage(message);
                    } else {
                        if(playerSession.getVotedMaps().size() >= DropperReloaded.getInstance().getConfig().getInt("wait_lobby.map_vote_count")) {
                            player.sendMessage(MessageConfigUtils.get("wait_lobby.guis.map_voting.items.map.on_click.message_no_remaining_vote"));
                            return;
                        }
                        playerSession.getVotedMaps().add(map);
                        String message = MessageConfigUtils.get("wait_lobby.guis.map_voting.items.map.on_click.message_voted");
                        message = message.replace("%map_name%", MapDifficultyColorPrefix.get(map.getDifficulty()) + map.getName());
                        player.sendMessage(message);
                    }
                    for(int slotGui : mapVoteGui.getSlotAndMaps().keySet()) {
                        mapVoteGui.updateItemMap(slotGui, mapVoteGui.getSlotAndMaps().get(slotGui));
                    }
                    playerSession.getPlayerGame().getWaitLobby().getWaitLobbyScoreboard().updateMapCount(player);
                }
                if(slot == 53) {
                    mapVoteGui.nextPage();
                }
                if(slot == 45) {
                    mapVoteGui.previousPage();
                }
            }
        }
    }

}
