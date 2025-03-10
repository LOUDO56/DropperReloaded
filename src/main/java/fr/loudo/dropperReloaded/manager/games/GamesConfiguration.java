package fr.loudo.dropperReloaded.manager.games;

import fr.loudo.dropperReloaded.DropperReloaded;

import java.util.ArrayList;

public class GamesConfiguration {

    private int mapPerGames;
    private int maxPlayer;
    private int minPlayer;

    public GamesConfiguration(DropperReloaded instance) {
        this.mapPerGames = instance.getConfig().getInt("games.map_per_games");
        this.maxPlayer = instance.getConfig().getInt("games.max_player");
        this.minPlayer = instance.getConfig().getInt("games.min_player");
    }

    public int getMapPerGames() {
        return mapPerGames;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public int getMinPlayer() {
        return minPlayer;
    }
}
