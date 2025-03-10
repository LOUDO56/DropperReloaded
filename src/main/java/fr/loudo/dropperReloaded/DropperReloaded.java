package fr.loudo.dropperReloaded;

import fr.loudo.dropperReloaded.commands.RegisterCommands;
import fr.loudo.dropperReloaded.manager.games.GamesManager;
import fr.loudo.dropperReloaded.manager.maps.MapsManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class DropperReloaded extends JavaPlugin {

    private static DropperReloaded instance;
    private static boolean isCitizenPluginEnabled;

    private static GamesManager gamesManager;
    private static MapsManager mapsManager;

    @Override
    public void onEnable() {

        isCitizenPluginEnabled = Bukkit.getPluginManager().isPluginEnabled("Citizen");
        if(!isCitizenPluginEnabled) {
            getLogger().info("Citizens isn't on the server, npc feature disabled.");
        }

        instance = this;

        //Commands
        RegisterCommands.register(this);

        //Manager
        gamesManager = new GamesManager(instance);
        mapsManager = new MapsManager();

        //Configutation
        saveDefaultConfig();

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
}
