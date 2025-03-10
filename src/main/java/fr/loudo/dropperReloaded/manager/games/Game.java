package fr.loudo.dropperReloaded.manager.games;

import fr.loudo.dropperReloaded.manager.maps.Map;
import org.bukkit.entity.Player;

import java.util.List;

public class Game {

    private List<Player> playerList;
    private List<Map> mapList;
    private GameStatus gameStatus;

    public Game(List<Player> playerList, List<Map> mapList) {
        this.playerList = playerList;
        this.mapList = mapList;
        this.gameStatus = GameStatus.STARTING;
    }

    public void start() {
        gameStatus = GameStatus.STARTING;
    }

    public void stop() {
        gameStatus = GameStatus.ENDED;
    }

}
