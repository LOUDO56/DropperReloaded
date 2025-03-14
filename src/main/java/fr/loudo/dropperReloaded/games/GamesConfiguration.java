package fr.loudo.dropperReloaded.games;

import fr.loudo.dropperReloaded.DropperReloaded;

public class GamesConfiguration {

    private int mapPerGames;

    public GamesConfiguration(DropperReloaded instance) {
        this.mapPerGames = instance.getConfig().getInt("games.map_per_games");
    }

    public int getMapPerGames() {
        return mapPerGames;
    }

}
