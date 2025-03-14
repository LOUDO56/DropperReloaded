package fr.loudo.dropperReloaded;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import fr.loudo.dropperReloaded.commands.RegisterCommands;
import fr.loudo.dropperReloaded.games.GamesManager;
import fr.loudo.dropperReloaded.maps.MapsManager;
import fr.loudo.dropperReloaded.players.PlayersSessionManager;
import fr.loudo.dropperReloaded.waitlobby.WaitLobbyConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Stream;

public final class DropperReloaded extends JavaPlugin {

    private static DropperReloaded instance;
    private static boolean isCitizenPluginEnabled;
    private static boolean isProtocolLibPluginEnabled;

    private static GamesManager gamesManager;
    private static MapsManager mapsManager;
    private static WaitLobbyConfiguration waitLobbyConfiguration;
    private static PlayersSessionManager playersSessionManager;

    private static ProtocolManager protocolManager;

    private static String version;

    @Override
    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onEnable() {

        //Configuration Init
        saveDefaultConfig();

        isCitizenPluginEnabled = Bukkit.getPluginManager().isPluginEnabled("Citizen");
        if(!isCitizenPluginEnabled) {
            getLogger().info("Citizens isn't on the server, npc feature disabled.");
        }

        isProtocolLibPluginEnabled = Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");
        if(!isProtocolLibPluginEnabled) {
            getLogger().info("ProtocolLib isn't on the server. Transparent player and proper player's title disabled.");
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

        version = getServer().getBukkitVersion();
        version = version.split("-")[0];
        System.out.println(version);

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

    public static boolean isIsProtocolLibPluginEnabled() {
        return isProtocolLibPluginEnabled;
    }

    public static boolean isIsCitizenPluginEnabled() {
        return isCitizenPluginEnabled;
    }

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public static String getVersion() {
        return version;
    }

    public static boolean isNewerVersion() {
        return Stream.of("1.17", "1.18", "1.19", "1.20", "1.21")
                .anyMatch(version::startsWith);
    }
}
