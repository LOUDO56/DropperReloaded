package fr.loudo.dropperReloaded.players;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.games.Game;
import fr.loudo.dropperReloaded.maps.DropperMap;
import fr.loudo.dropperReloaded.utils.Gui;
import fr.loudo.dropperReloaded.utils.MessageConfigUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PlayerSession {

    private Player player;
    private Game playerGame;
    private DropperMap currentDropperMap;

    private Gui currentGui;
    private List<DropperMap> votedDropperMaps;

    private long stopwatchTotal;
    private long finalStopwatchTotal;
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
    private boolean canEnterPortal;

    public PlayerSession(Player player) {
        this.player = player;
        this.voteCount = Integer.parseInt(MessageConfigUtils.get("wait_lobby.map_vote_count"));
        this.currentMapCount = 1;
        this.totalFails = 0;
        this.isPlayersVisible = true;
        this.isInvincible = false;
        this.canResetLocation = true;
        this.isSpectator = false;
        this.canEnterPortal = false;
        this.votedDropperMaps = new ArrayList<>();
    }

    public void startStopwatchTotal() {
        stopwatchTotal = System.currentTimeMillis();
    }
    public void startStopwatchMap() {
        stopwatchMap = System.currentTimeMillis();
    }

    public boolean hasFinishedGame() {
        return currentMapCount > playerGame.getMapList().size();
    }

    public void addDeath() {
        totalFails += 1;
        playerGame.getInGameScoreboard().updateTotalFails(player);
        player.teleport(currentDropperMap.getRandomSpawn());
        player.damage(0.001);
        player.setHealth(20);
    }

    public void addDeath(boolean silent) {
        totalFails += 1;
        playerGame.getInGameScoreboard().updateTotalFails(player);
        player.teleport(currentDropperMap.getRandomSpawn());
        player.setHealth(20);
    }

    public void startSession() {
        startStopwatchTotal();
        startStopwatchMap();
        startSendMapTimeActionBar();
        canEnterPortal = true;
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

    public boolean canEnterPortal() {
        return canEnterPortal;
    }

    public void setCanEnterPortal(boolean canEnterPortal) {
        this.canEnterPortal = canEnterPortal;
    }

    public long getStopwatchTotal() {
        return stopwatchTotal;
    }

    public void startSendMapTimeActionBar() {
        actionBarTask = new BukkitRunnable() {
            @Override
            public void run() {
                String actionBarMessage = MessageConfigUtils.get("games.player_action_bar").replace("%map_time%", getTimeCurrentMapFormatted());
                actionBarMessage = actionBarMessage.replace("%total_time%", getTotalTimeFormatted());
                playerGame.sendActionBar(player, actionBarMessage);
            }
        }.runTaskTimerAsynchronously(DropperReloaded.getInstance(), 0L, 1L);
    }

    public void setFinalStopwatchTotal(long finalStopwatchTotal) {
        this.finalStopwatchTotal = finalStopwatchTotal;
    }

    public String getFinalTimeStopwatchFormatted() {
        return new SimpleDateFormat("mm:ss:SSS").format(finalStopwatchTotal);
    }

    public String getTimeCurrentMapFormatted() {
        return new SimpleDateFormat("mm:ss:SSS").format(System.currentTimeMillis() - stopwatchMap);
    }

    public String getTotalTimeFormatted() {
        return new SimpleDateFormat("mm:ss:SSS").format(System.currentTimeMillis() - stopwatchTotal);
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
        currentDropperMap = null;
        totalFails = 0;
        isPlayersVisible = true;
        isInvincible = false;
        canResetLocation = true;
        isSpectator = false;
        actionBarTask = null;
        canEnterPortal = false;
        votedDropperMaps = new ArrayList<>();
    }

    public int getVoteCount() {
        return voteCount;
    }

    public int getCurrentMapCount() {
        return currentMapCount;
    }

    public Gui getCurrentGui() {
        return currentGui;
    }

    public void setCurrentGui(Gui currentGui) {
        this.currentGui = currentGui;
    }

    public void setCurrentMapCount(int currentMapCount) {
        this.currentMapCount = currentMapCount;
    }

    public void setCurrentMap(DropperMap currentDropperMap) {
        this.currentDropperMap = currentDropperMap;
    }

    public int getTotalFails() {
        return totalFails;
    }

    public DropperMap getCurrentMap() {
        return currentDropperMap;
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

    public List<DropperMap> getVotedMaps() {
        return votedDropperMaps;
    }
}
