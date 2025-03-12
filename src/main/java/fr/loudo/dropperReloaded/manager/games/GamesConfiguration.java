package fr.loudo.dropperReloaded.manager.games;

import fr.loudo.dropperReloaded.DropperReloaded;

import java.util.ArrayList;

public class GamesConfiguration {

    private int mapPerGames;

    public GamesConfiguration(DropperReloaded instance) {
        this.mapPerGames = instance.getConfig().getInt("games.map_per_games");
    }

    public int getMapPerGames() {
        return mapPerGames;
    }

}
