package fr.loudo.dropperReloaded.games;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.players.PlayerSession;
import fr.loudo.dropperReloaded.players.PlayersSessionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GamesManager {

    private final PlayersSessionManager playersSessionManager = DropperReloaded.getPlayersSessionManager();

    private List<Game> gameList;

    public GamesManager() {
        this.gameList = new ArrayList<>();
    }

    public void joinGame(Player player) {
        PlayerSession playerSession = playersSessionManager.getPlayerSession(player);
        if(playerSession == null) {
            playerSession = new PlayerSession(player);
        } else {
            leaveGame(playerSession);
            playerSession.reset();
        }

        Game game = gameList.stream()
            .filter(currentGame -> currentGame.getGameStatus() == GameStatus.WAITING || currentGame.getGameStatus() == GameStatus.STARTING)
            .findFirst()
            .orElseGet(Game::new);

        if(!gameList.contains(game)) gameList.add(game);

        playerSession.setPlayerGame(game);
        playersSessionManager.getPlayerSessionList().add(playerSession);
        game.addPlayer(player);

    }

    public boolean leaveGame(Player player) {
        PlayerSession playerSession = playersSessionManager.getPlayerSession(player);
        if(!playersSessionManager.getPlayerSessionList().contains(playerSession)) return false;

        playersSessionManager.getPlayerSessionList().remove(playerSession);
        playerSession.getPlayerGame().removePlayer(player);
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        //TODO: Teleport to main lobby

        return true;
    }

    public void leaveGame(PlayerSession playerSession) {
        playersSessionManager.getPlayerSessionList().remove(playerSession);
        playerSession.getPlayerGame().removePlayer(playerSession.getPlayer());
        playerSession.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        //TODO: Teleport to main lobby
    }

    public List<Game> getGameList() {
        return gameList;
    }

}
