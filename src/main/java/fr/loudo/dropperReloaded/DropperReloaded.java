package fr.loudo.dropperReloaded;

import fr.loudo.dropperReloaded.commands.RegisterCommands;
import fr.loudo.dropperReloaded.database.Database;
import fr.loudo.dropperReloaded.events.RegisterEvents;
import fr.loudo.dropperReloaded.games.GamesManager;
import fr.loudo.dropperReloaded.items.DropperItems;
import fr.loudo.dropperReloaded.maps.DropperMapsManager;
import fr.loudo.dropperReloaded.npc.JoinGameNPCManager;
import fr.loudo.dropperReloaded.players.PlayersSessionManager;
import fr.loudo.dropperReloaded.utils.CheckVersion;
import fr.loudo.dropperReloaded.waitlobby.WaitLobbyConfiguration;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public final class DropperReloaded extends JavaPlugin {

    private static DropperReloaded instance;
    private boolean isCitizenPluginEnabled;

    private GamesManager gamesManager;
    private DropperMapsManager dropperMapsManager;
    private WaitLobbyConfiguration waitLobbyConfiguration;
    private PlayersSessionManager playersSessionManager;
    private JoinGameNPCManager joinGameNPCManager;
    private Database database;


    @Override
    public void onEnable() {


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

        if(getConfig().getBoolean("check-version", true)) {
            try {
                CheckVersion.verify();
            } catch (IOException | ParseException e) {
                getLogger().info("Couldn't check for a new update, passing.");
            }
        }

        if(getConfig().getInt("main_lobby.npc.id", -1) > -1) {
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

        int pluginId = 25251;
        new Metrics(this, pluginId);

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

    public GamesManager getGamesManager() {
        return gamesManager;
    }

    public DropperMapsManager getMapsManager() {
        return dropperMapsManager;
    }

    public WaitLobbyConfiguration getWaitLobbyConfiguration() {
        return waitLobbyConfiguration;
    }

    public PlayersSessionManager getPlayersSessionManager() {
        return playersSessionManager;
    }

    public Database getDatabase() {
        return database;
    }

    public boolean isIsCitizenPluginEnabled() {
        return isCitizenPluginEnabled;
    }

    public JoinGameNPCManager getJoinGameNPCManager() {
        return joinGameNPCManager;
    }

}
