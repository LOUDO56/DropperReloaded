package fr.loudo.dropperReloaded;

import fr.loudo.dropperReloaded.commands.RegisterCommands;
import fr.loudo.dropperReloaded.database.Database;
import fr.loudo.dropperReloaded.events.RegisterEvents;
import fr.loudo.dropperReloaded.games.GamesManager;
import fr.loudo.dropperReloaded.items.DropperItems;
import fr.loudo.dropperReloaded.maps.DropperMapsManager;
import fr.loudo.dropperReloaded.npc.JoinGameNPCManager;
import fr.loudo.dropperReloaded.players.PlayersSessionManager;
import fr.loudo.dropperReloaded.waitlobby.WaitLobbyConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class DropperReloaded extends JavaPlugin {

    private static DropperReloaded instance;
    private static boolean isCitizenPluginEnabled;

    private static GamesManager gamesManager;
    private static DropperMapsManager dropperMapsManager;
    private static WaitLobbyConfiguration waitLobbyConfiguration;
    private static PlayersSessionManager playersSessionManager;
    private static JoinGameNPCManager joinGameNPCManager;
    private static Database database;

    private static String version;

    @Override
    public void onEnable() {

        version = getServer().getBukkitVersion();
        version = version.split("-")[0];

        //Configuration Init
        saveDefaultConfig();

        isCitizenPluginEnabled = Bukkit.getPluginManager().isPluginEnabled("Citizens");
        if(!isCitizenPluginEnabled) {
            getLogger().info("Citizens isn't on the server, npc feature disabled.");
        }

        instance = this;

        //Commands
        RegisterCommands.register(this);

        //Listeners
        RegisterEvents.register(getServer().getPluginManager(), this);

        //Manager
        playersSessionManager = new PlayersSessionManager();
        gamesManager = new GamesManager();
        dropperMapsManager = new DropperMapsManager();
        joinGameNPCManager = new JoinGameNPCManager();
        database = new Database(this, getConfig().getString("database.type"));
        database.initialize();

        //Configuration class
        waitLobbyConfiguration = new WaitLobbyConfiguration();

        //Items
        DropperItems.registerItems();

        if(getConfig().getInt("main_lobby.npc.id") > -1) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(joinGameNPCManager.createNPCHologramLines()) {
                        getLogger().info("Initialized NPC and holograms.");
                    } else {
                        getLogger().severe("NPC and his holograms couldn't be loaded, maybe the NPC is wrong?");
                    }
                }
            }.runTaskLater(this, 20L);
        }

    }

    @Override
    public void onDisable() {
        if(getConfig().getInt("main_lobby.npc.id") > -1) {
            joinGameNPCManager.getHologram().remove();
            getLogger().info("Removed holograms of NPC...");
        }
    }

    public static DropperReloaded getInstance() {
        return instance;
    }

    public static GamesManager getGamesManager() {
        return gamesManager;
    }

    public static DropperMapsManager getMapsManager() {
        return dropperMapsManager;
    }

    public static WaitLobbyConfiguration getWaitLobbyConfiguration() {
        return waitLobbyConfiguration;
    }

    public static PlayersSessionManager getPlayersSessionManager() {
        return playersSessionManager;
    }

    public static Database getDatabase() {
        return database;
    }

    public static boolean isIsCitizenPluginEnabled() {
        return isCitizenPluginEnabled;
    }

    public static JoinGameNPCManager getJoinGameNPCManager() {
        return joinGameNPCManager;
    }

    public static String getVersion() {
        return version;
    }
}
