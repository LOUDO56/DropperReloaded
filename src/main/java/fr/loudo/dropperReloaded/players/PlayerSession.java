package fr.loudo.dropperReloaded.players;

import fr.loudo.dropperReloaded.games.Game;
import fr.loudo.dropperReloaded.utils.MessageConfigUtils;
import org.bukkit.entity.Player;

import java.util.Date;

public class PlayerSession {

    private Player player;
    private Game playerGame;
    private Date stopwatch;

    private int voteCount;
    private int currentMapCount;

    public PlayerSession(Player player) {
        this.player = player;
        this.voteCount = Integer.parseInt(MessageConfigUtils.get("wait_lobby.map_vote_count"));
        this.currentMapCount = 0;
    }

    public void startStopwatch() {
        stopwatch = new Date();
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

    public Date getStopwatch() {
        return stopwatch;
    }

    public int getCurrentMapCount() {
        return currentMapCount;
    }

    public void setCurrentMapCount(int currentMapCount) {
        this.currentMapCount = currentMapCount;
    }
}
