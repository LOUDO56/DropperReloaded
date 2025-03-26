package fr.loudo.dropperReloaded.events;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.games.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for(Game game : DropperReloaded.getInstance().getGamesManager().getGameList()) {
            for(Player playerFromGame : game.getPlayerList()) {
                event.getPlayer().hidePlayer(playerFromGame);
                playerFromGame.hidePlayer(event.getPlayer());
            }
        }
    }
}
