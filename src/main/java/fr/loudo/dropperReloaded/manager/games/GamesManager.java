package fr.loudo.dropperReloaded.manager.games;

import fr.loudo.dropperReloaded.DropperReloaded;

import java.util.ArrayList;
import java.util.List;

public class GamesManager {

    private List<Game> gameList;

    public GamesManager(DropperReloaded instance) {
        this.gameList = new ArrayList<>();
    }

    public List<Game> getGameList() {
        return gameList;
    }

}
