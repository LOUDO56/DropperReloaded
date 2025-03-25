package fr.loudo.dropperReloaded.events;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.commands.dropperadmin.DropperAdminCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeave implements Listener {
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        DropperAdminCommand.getWAND_POS_HASH_MAP().remove(player);
        if(DropperReloaded.getPlayersSessionManager().isPlaying(player)) {
            DropperReloaded.getGamesManager().leaveGame(player);
        }
    }
}
