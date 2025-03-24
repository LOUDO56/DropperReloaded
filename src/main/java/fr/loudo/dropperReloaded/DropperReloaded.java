package fr.loudo.dropperReloaded;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import fr.loudo.dropperReloaded.commands.RegisterCommands;
import fr.loudo.dropperReloaded.database.Database;
import fr.loudo.dropperReloaded.events.RegisterEvents;
import fr.loudo.dropperReloaded.games.GamesManager;
import fr.loudo.dropperReloaded.items.DropperItems;
import fr.loudo.dropperReloaded.maps.DropperMapsManager;
import fr.loudo.dropperReloaded.npc.JoinGameNPCManager;
import fr.loudo.dropperReloaded.players.PlayersSessionManager;
import fr.loudo.dropperReloaded.waitlobby.WaitLobbyConfiguration;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.stream.Stream;

public final class DropperReloaded extends JavaPlugin {

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(DropperReloaded.class);
    private static DropperReloaded instance;
    private static boolean isCitizenPluginEnabled;
    private static boolean isProtocolLibPluginEnabled;

    private static GamesManager gamesManager;
    private static DropperMapsManager dropperMapsManager;
    private static WaitLobbyConfiguration waitLobbyConfiguration;
    private static PlayersSessionManager playersSessionManager;
    private static JoinGameNPCManager joinGameNPCManager;
    private static Database database;

    private static ProtocolManager protocolManager;

    private static String version;

    @Override
    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onEnable() {

        version = getServer().getBukkitVersion();
        version = version.split("-")[0];

        //Configuration Init
        saveDefaultConfig();
        checkNoteSoundStringVersion();

        isCitizenPluginEnabled = Bukkit.getPluginManager().isPluginEnabled("Citizens");
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

    private void checkNoteSoundStringVersion() {
        if(!isNewerVersion()) {
            if(getConfig().getString("games.timer_sound", "BLOCK_NOTE_BLOCK_HARP").equals("BLOCK_NOTE_BLOCK_HARP")) {
                getConfig().set("games.timer_sound", "NOTE_HARP");
            }
            if(getConfig().getString("wait_lobby.timer_sound", "BLOCK_NOTE_BLOCK_HAT").equals("BLOCK_NOTE_BLOCK_HAT")) {
                getConfig().set("wait_lobby.timer_sound", "NOTE_HAT");
            }
        }
        saveConfig();
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

    public static boolean isIsProtocolLibPluginEnabled() {
        return isProtocolLibPluginEnabled;
    }

    public static boolean isIsCitizenPluginEnabled() {
        return isCitizenPluginEnabled;
    }

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public static JoinGameNPCManager getJoinGameNPCManager() {
        return joinGameNPCManager;
    }

    public static String getVersion() {
        return version;
    }

    public static boolean isNewerVersion() {
        return Stream.of("1.13", "1.14", "1.15", "1.15", "1.16", "1.17", "1.18", "1.19", "1.20", "1.21")
                .anyMatch(version::startsWith);
    }
}
