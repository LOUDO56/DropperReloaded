package fr.loudo.dropperReloaded;

import fr.loudo.dropperReloaded.commands.RegisterCommands;
import fr.loudo.dropperReloaded.manager.games.GamesManager;
import fr.loudo.dropperReloaded.manager.maps.MapsManager;
import fr.loudo.dropperReloaded.manager.players.PlayersSessionManager;
import fr.loudo.dropperReloaded.manager.waitlobby.WaitLobbyConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class DropperReloaded extends JavaPlugin {

    private static DropperReloaded instance;
    private static boolean isCitizenPluginEnabled;

    private static GamesManager gamesManager;
    private static MapsManager mapsManager;
    private static WaitLobbyConfiguration waitLobbyConfiguration;
    private static PlayersSessionManager playersSessionManager;

    @Override
    public void onEnable() {

        //Configuration Init
        saveDefaultConfig();

        isCitizenPluginEnabled = Bukkit.getPluginManager().isPluginEnabled("Citizen");
        if(!isCitizenPluginEnabled) {
            getLogger().info("Citizens isn't on the server, npc feature disabled.");
        }

        instance = this;

        //Commands
        RegisterCommands.register(this);

        //Manager
        playersSessionManager = new PlayersSessionManager();
        gamesManager = new GamesManager();
        mapsManager = new MapsManager();

        //Configuration class
        waitLobbyConfiguration = new WaitLobbyConfiguration(this);

    }

    public static DropperReloaded getInstance() {
        return instance;
    }

    public static GamesManager getGamesManager() {
        return gamesManager;
    }

    public static MapsManager getMapsManager() {
        return mapsManager;
    }

    public static WaitLobbyConfiguration getWaitLobbyConfiguration() {
        return waitLobbyConfiguration;
    }

    public static PlayersSessionManager getPlayersSessionManager() {
        return playersSessionManager;
    }
}
