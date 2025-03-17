package fr.loudo.dropperReloaded.players;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.games.Game;
import fr.loudo.dropperReloaded.maps.Map;
import fr.loudo.dropperReloaded.utils.MessageConfigUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class PlayerSession {

    private Player player;
    private Game playerGame;
    private Map currentMap;

    private long stopwatchTotal;
    private long stopwatchMap;

    private BukkitTask detectPortal;
    private BukkitTask actionBarTask;

    private int voteCount;
    private int currentMapCount;
    private int totalFails;

    private boolean isPlayersVisible;
    private boolean isInvincible;
    private boolean canResetLocation;
    private boolean isSpectator;

    public PlayerSession(Player player) {
        this.player = player;
        this.voteCount = Integer.parseInt(MessageConfigUtils.get("wait_lobby.map_vote_count"));
        this.currentMapCount = 1;
        this.totalFails = 0;
        this.isPlayersVisible = true;
        this.isInvincible = false;
        this.canResetLocation = true;
        this.isSpectator = false;
    }

    public void startStopwatchTotal() {
        stopwatchTotal = System.currentTimeMillis();
    }
    public void startStopwatchMap() {
        stopwatchMap = System.currentTimeMillis();
    }

    public boolean hasFinishedGame() {
        return currentMapCount == playerGame.getMapList().size();
    }

    public void addDeath() {
        totalFails += 1;
        playerGame.getInGameScoreboard().updateTotalFails(player);
        player.teleport(currentMap.getRandomSpawn());
        player.damage(0.001);
        player.setHealth(20);
    }

    public void addDeath(boolean silent) {
        totalFails += 1;
        playerGame.getInGameScoreboard().updateTotalFails(player);
        player.teleport(currentMap.getRandomSpawn());
        player.setHealth(20);
    }

    public void startSession() {
        startStopwatchTotal();
        startStopwatchMap();
        startDetectingPortal();
        startSendMapTimeActionBar();
    }

    public void stopSession() {
        if(detectPortal != null) {
            detectPortal.cancel();
        }
        detectPortal = null;
        if(actionBarTask != null) {
            actionBarTask.cancel();
        }
        actionBarTask = null;
        stopwatchTotal = 0;
        stopwatchMap = 0;
    }

    public void startDetectingPortal() {
        detectPortal = new BukkitRunnable() {
            @Override
            public void run() {
                if(currentMapCount > playerGame.getMapList().size()) {
                    this.cancel();
                }

                Material netherPortal;
                if(DropperReloaded.isNewerVersion()) {
                    netherPortal = Material.valueOf("NETHER_PORTAL");
                } else {
                    netherPortal = Material.valueOf("PORTAL");
                }

                Location checkLoc;
                for (int x = -1; x <= 1; x += 2) {
                    for (int z = -1; z <= 1; z += 2) {
                        checkLoc = new Location(player.getWorld(), player.getLocation().getX() + 0.299 * x, player.getLocation().getBlockY(), player.getLocation().getZ() + 0.299 * z);
                        if(checkLoc.getBlock().getType() == netherPortal) {
                            playerGame.teleportPlayerToNextMap(player);
                        }
                    }
                }

            }
        }.runTaskTimer(DropperReloaded.getInstance(), 0L, 1L);
    }

    public void startSendMapTimeActionBar() {
        actionBarTask = new BukkitRunnable() {
            @Override
            public void run() {
                String actionBarMessage = MessageConfigUtils.get("games.player_action_bar").replace("%time%", getTimeCurrentMapFormatted());
                playerGame.sendActionBar(player, actionBarMessage);
            }
        }.runTaskTimerAsynchronously(DropperReloaded.getInstance(), 0L, 1L);
    }

    public String getTimeCurrentMapFormatted() {
        long elapsedTime = System.currentTimeMillis() - stopwatchMap;

        long minutes = (elapsedTime / 60000) % 60;
        long seconds = (elapsedTime / 1000) % 60;
        long milliseconds = elapsedTime % 1000;

        return String.format("%02d:%02d:%03d", minutes, seconds, milliseconds);
    }

    public String getTotalTimeFormatted() {
        long elapsedTime = System.currentTimeMillis() - stopwatchTotal;

        long minutes = (elapsedTime / 60000) % 60;
        long seconds = (elapsedTime / 1000) % 60;
        long milliseconds = elapsedTime % 1000;

        return String.format("%02d:%02d:%03d", minutes, seconds, milliseconds);
    }

    public Player getPlayer() {
        return player;
    }

    public Game getPlayerGame() {
        return playerGame;
    }

    public void setPlayerGame(Game playerGame) {
        this.playerGame = playerGame;
    }

    public void reset() {
        stopSession();
        voteCount = Integer.parseInt(MessageConfigUtils.get("wait_lobby.map_vote_count"));
        currentMapCount = 1;
        currentMap = null;
        totalFails = 0;
        isPlayersVisible = true;
        isInvincible = false;
        canResetLocation = true;
        isSpectator = false;
        actionBarTask = null;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public int getCurrentMapCount() {
        return currentMapCount;
    }

    public void setCurrentMapCount(int currentMapCount) {
        this.currentMapCount = currentMapCount;
    }

    public void setCurrentMap(Map currentMap) {
        this.currentMap = currentMap;
    }

    public int getTotalFails() {
        return totalFails;
    }

    public Map getCurrentMap() {
        return currentMap;
    }

    public boolean isInvincible() {
        return isInvincible;
    }

    public void setInvincible(boolean invincible) {
        isInvincible = invincible;
    }

    public boolean canResetLocation() {
        return canResetLocation;
    }

    public void setCanResetLocation(boolean canResetLocation) {
        this.canResetLocation = canResetLocation;
    }

    public boolean isSpectator() {
        return isSpectator;
    }

    public void setSpectator(boolean spectator) {
        isSpectator = spectator;
    }
}
