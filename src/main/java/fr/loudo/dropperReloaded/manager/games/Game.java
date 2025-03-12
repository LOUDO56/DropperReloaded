package fr.loudo.dropperReloaded.manager.games;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.manager.maps.Map;
import fr.loudo.dropperReloaded.manager.waitlobby.WaitLobby;
import fr.loudo.dropperReloaded.manager.waitlobby.WaitLobbyConfiguration;
import fr.loudo.dropperReloaded.scoreboards.WaitLobbyScoreboard;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final WaitLobbyConfiguration WAIT_LOBBY_CONFIGURATION = DropperReloaded.getWaitLobbyConfiguration();

    private List<Player> playerList;
    private List<Map> mapList;
    private GameStatus gameStatus;
    private WaitLobby waitLobby;

    public Game() {
        this.playerList = new ArrayList<>();
        this.mapList = new ArrayList<>();
        this.gameStatus = GameStatus.WAITING;
        this.waitLobby = new WaitLobby(this);
    }

    public boolean addPlayer(Player player) {
        if(playerList.contains(player) && playerList.size() >= WAIT_LOBBY_CONFIGURATION.getMaxPlayer()) return false;
        playerList.add(player);
        if(!hasStarted()) {
            player.teleport(WAIT_LOBBY_CONFIGURATION.getSpawn());
            waitLobby.playerJoinedMessage(player.getDisplayName());
        }
        return true;
    }

    public boolean removePlayer(Player player) {
        if(!playerList.contains(player) && playerList.isEmpty()) return false;
        playerList.remove(player);
        if(!hasStarted()) {
            waitLobby.playerLeftMessage(player.getDisplayName());
        }
        return true;
    }

    public void start() {
        gameStatus = GameStatus.STARTING;
    }

    public void stop() {
        gameStatus = GameStatus.ENDED;
    }

    public void sendMessageToPlayers(String message) {
        for(Player player : playerList) {
            player.sendMessage(message);
        }
    }

    public boolean hasStarted() {
        return gameStatus == GameStatus.PLAYING;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public List<Map> getMapList() {
        return mapList;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public WaitLobby getWaitLobby() {
        return waitLobby;
    }
}
