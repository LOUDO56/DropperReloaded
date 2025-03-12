package fr.loudo.dropperReloaded.manager.players;

import fr.loudo.dropperReloaded.manager.games.Game;
import org.bukkit.entity.Player;

public class PlayerSession {

    private Player player;
    private Game playerGame;

    public PlayerSession(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getPlayerGame() {
        return playerGame;
    }

    public void setPlayerGame(Game playerGame) {
        this.playerGame = playerGame;
    }
}
