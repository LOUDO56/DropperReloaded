package fr.loudo.dropperReloaded.manager.players;

import fr.loudo.dropperReloaded.manager.games.Game;
import fr.loudo.dropperReloaded.utils.MessageConfigUtils;
import org.bukkit.entity.Player;

public class PlayerSession {

    private Player player;
    private Game playerGame;
    private int voteCount;

    public PlayerSession(Player player) {
        this.player = player;
        this.voteCount = Integer.parseInt(MessageConfigUtils.get("wait_lobby.map_vote_count"));
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

    public void reset() {
        voteCount = Integer.parseInt(MessageConfigUtils.get("wait_lobby.map_vote_count"));
    }

    public int getVoteCount() {
        return voteCount;
    }
}
