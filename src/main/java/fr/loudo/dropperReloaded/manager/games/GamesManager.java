package fr.loudo.dropperReloaded.manager.games;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.manager.players.PlayerSession;
import fr.loudo.dropperReloaded.manager.players.PlayersSessionManager;
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
        if(playersSessionManager.getPlayerGameList().contains(playerSession)) {
            leaveGame(playerSession);
        };

        Game game = gameList.stream()
            .filter(currentGame -> currentGame.getGameStatus() == GameStatus.WAITING || currentGame.getGameStatus() == GameStatus.STARTING)
            .findFirst()
            .orElseGet(Game::new);

        playerSession.setPlayerGame(game);
        gameList.add(game);
        game.addPlayer(player);
        playersSessionManager.getPlayerGameList().add(playerSession);

    }

    public boolean leaveGame(Player player) {
        PlayerSession playerSession = playersSessionManager.getPlayerSession(player);
        if(!playersSessionManager.getPlayerGameList().contains(playerSession)) return false;

        playersSessionManager.getPlayerGameList().remove(playerSession);
        playerSession.getPlayerGame().removePlayer(player);
        //TODO: Teleport to main lobby

        return true;
    }

    public void leaveGame(PlayerSession playerSession) {
        playersSessionManager.getPlayerGameList().remove(playerSession);
        playerSession.getPlayerGame().removePlayer(playerSession.getPlayer());
        //TODO: Teleport to main lobby
    }

    public List<Game> getGameList() {
        return gameList;
    }

}
