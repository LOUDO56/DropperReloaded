package fr.loudo.dropperReloaded.games;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.items.DropperItems;
import fr.loudo.dropperReloaded.maps.DropperMap;
import fr.loudo.dropperReloaded.players.PlayerSession;
import fr.loudo.dropperReloaded.players.PlayersSessionManager;
import fr.loudo.dropperReloaded.scoreboards.InGameScoreboard;
import fr.loudo.dropperReloaded.utils.MessageConfigUtils;
import fr.loudo.dropperReloaded.waitlobby.WaitLobby;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class Game {

    private final PlayersSessionManager playersSessionManager = DropperReloaded.getInstance().getPlayersSessionManager();

    private int id;
    private int timeLeft;
    private int countdownStartTimer;

    private List<Player> playerList;
    private List<Player> spectatorList;
    private List<Player> playerFinished;
    private List<DropperMap> dropperMapList;
    private GameStatus gameStatus;
    private WaitLobby waitLobby;
    private InGameScoreboard inGameScoreboard;

    private BukkitTask countdownStart;
    private BukkitTask countdownGame;

    public Game() {
        this.playerList = new ArrayList<>();
        this.dropperMapList = new ArrayList<>();
        this.spectatorList = new ArrayList<>();
        this.playerFinished = new ArrayList<>();
        this.gameStatus = GameStatus.WAITING;
        this.waitLobby = new WaitLobby(this);
        this.id = DropperReloaded.getInstance().getGamesManager().getGameList().size();
        this.inGameScoreboard = new InGameScoreboard(this);
    }

    public boolean addPlayer(Player player) {
        if(playerList.contains(player) || playerList.size() >= DropperReloaded.getInstance().getWaitLobbyConfiguration().getMaxPlayer()) return false;
        PlayerSession playerSession = DropperReloaded.getInstance().getPlayersSessionManager().getPlayerSession(player);
        playerSession.reset();
        playerSession.setPlayerGame(this);
        playerList.add(player);
        if(!hasStarted()) {
            player.teleport(DropperReloaded.getInstance().getWaitLobbyConfiguration().getSpawn());
            waitLobby.getWaitLobbyScoreboard().setup(player);
            waitLobby.playerJoinedMessage(player.getDisplayName());
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.getInventory().clear();
                    player.getInventory().setItem(DropperItems.mapVote.getSlot(), DropperItems.mapVote.getItem());
                    player.getInventory().setItem(DropperItems.leaveBed.getSlot(), DropperItems.leaveBed.getItem());
                    player.setGameMode(GameMode.ADVENTURE);
                }
            }.runTaskLater(DropperReloaded.getInstance(), 1L);
            for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                player.hidePlayer(onlinePlayer);
                onlinePlayer.hidePlayer(player);
            }
            for(Player playerFromGame : playerList) {
                player.showPlayer(playerFromGame);
                playerFromGame.showPlayer(player);
            }
        }
        return true;
    }

    public boolean removePlayer(Player player) {
        if(!playerList.contains(player) && playerList.isEmpty()) return false;
        playerList.remove(player);
        if(!hasStarted() && !hasEnded()) {
            waitLobby.playerLeftMessage(player.getDisplayName());
        }
        if(gameStatus == GameStatus.DOOR_COUNTDOWN) {
            resetDoorBlock(player);
        }
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(!DropperReloaded.getInstance().getPlayersSessionManager().isPlaying(onlinePlayer)) {
                player.showPlayer(onlinePlayer);
                onlinePlayer.showPlayer(player);
            }
        }
        for(Player playerFromGame : playerList) {
            player.hidePlayer(playerFromGame);
            playerFromGame.hidePlayer(player);
        }
        player.setAllowFlight(false);
        if(playerList.isEmpty()) {
            reset();
        }
        player.sendTitle(" ", " ", 0, 0, 0);
        return true;
    }

    public void setup() {
        gameStatus = GameStatus.DOOR_COUNTDOWN;
        timeLeft = Integer.parseInt(MessageConfigUtils.get("games.timer_in_game"));
        dropperMapList = DropperReloaded.getInstance().getMapsManager().getMapsFromPlayersVote(playerList);
        inGameScoreboard = new InGameScoreboard(this);
        sendTitle(" ", " ", 0, 0, 0);
        for(Player player : playerList) {
            player.closeInventory();
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 9999999, 1, false, false));
            inGameScoreboard.setup(player);
            PlayerSession playerSession = DropperReloaded.getInstance().getPlayersSessionManager().getPlayerSession(player);
            playerSession.setCurrentMap(dropperMapList.get(0));
            player.teleport(dropperMapList.get(0).getRandomSpawn());
            player.getInventory().clear();
            player.getInventory().setItem(DropperItems.resetLocation.getSlot(), DropperItems.resetLocation.getItem());
            player.getInventory().setItem(DropperItems.playerVisibilityOn.getSlot(), DropperItems.playerVisibilityOn.getItem());
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setGameMode(GameMode.ADVENTURE);
                }
            }.runTaskLater(DropperReloaded.getInstance(), 5L);
        }
        startCountdownBeginning();
    }

    private void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                gameStatus = GameStatus.PLAYING;
            }
        }.runTaskLater(DropperReloaded.getInstance(), 5L);
        sendDoorBlock();
        for(Player player : playerList) {
            DropperReloaded.getInstance().getPlayersSessionManager().getPlayerSession(player).startSession();
        }
        countdownGame = new BukkitRunnable() {
            @Override
            public void run() {
                if(timeLeft == 0) {
                    stop();
                    this.cancel();
                }
                inGameScoreboard.updateTimeLeft();
                timeLeft--;
            }
        }.runTaskTimer(DropperReloaded.getInstance(), 0L, 20L);
    }

    public void stop() {
        gameStatus = GameStatus.ENDED;
        countdownStart.cancel();
        countdownStart = null;
        countdownGame.cancel();
        countdownGame = null;
        if(playerList.isEmpty()) {
            reset();
            return;
        }

        sendWinTitleToPlayers();

        for(Player player : playerList) {
            if(!playerFinished.contains(player)) {
                PlayerSession playerSession = DropperReloaded.getInstance().getPlayersSessionManager().getPlayerSession(player);
                playerSession.getDropperStats().setTotalLosses(playerSession.getDropperStats().getTotalLosses() + 1);
            }
            addPlayerSpectator(player);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if(hasEnded()) {
                    List<Player> copyList = new ArrayList<>(playerList);
                    for (Player player : copyList) {
                        DropperReloaded.getInstance().getGamesManager().leaveGame(player);
                    }
                    reset();
                }
            }
        }.runTaskLater(DropperReloaded.getInstance(), 10L * 20L);
    }

    private void sendWinTitleToPlayers() {
        sendTitle(MessageConfigUtils.get("games.game_finish_game_finished"), "", 10, 80, 40);
        for(int i = 0; i < playerFinished.size(); i++) {
            if(i == 0) {
                playerFinished.get(i).sendTitle(MessageConfigUtils.get("games.game_finish_win"), " ", 10, 80, 40);
            } else {
                playerFinished.get(i).sendTitle(MessageConfigUtils.get("games.game_finish_game_over"), " ", 10, 80, 40);
            }
        }
    }

    public void reset() {
        for(Player player : playerList) {
            DropperReloaded.getInstance().getGamesManager().leaveGame(player);
        }
        if(countdownStart != null) {
            countdownStart.cancel();
        }
        if(countdownGame != null) {
            countdownGame.cancel();
        }
        gameStatus = GameStatus.WAITING;
        playerList = new ArrayList<>();
        dropperMapList = new ArrayList<>();
        spectatorList = new ArrayList<>();
        playerFinished = new ArrayList<>();
    }

    public void sendMessageToPlayers(String message) {
        for(Player player : playerList) {
            player.sendMessage(message);
        }
    }

    public void playSoundToPlayers(Sound sound) {
        for(Player player : playerList) {
            player.playSound(player.getLocation(), sound, 1, 1);
        }
    }

    public void playSoundToPlayers(Sound sound, float volume, float pitch) {
        for(Player player : playerList) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for(Player playerFromGame : playerList) {
            playerFromGame.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public void teleportPlayerToNextMap(Player player) {
        PlayerSession playerSession = playersSessionManager.getPlayerSession(player);
        if(playerSession == null) return;
        if(playerSession.isSpectator()) return;
        playerSession.getDropperStats().setTotalMapCompleted(playerSession.getDropperStats().getTotalMapCompleted() + 1);
        int currentMapCount = playerSession.getCurrentMapCount() + 1;

        playerSession.setCurrentMapCount(currentMapCount);

        String mapFinishedMessage = MessageConfigUtils.get("games.map_finished_message");
        mapFinishedMessage = mapFinishedMessage.replace("%map_count%", String.valueOf(currentMapCount - 1));
        mapFinishedMessage = mapFinishedMessage.replace("%map_time%", playerSession.getTimeCurrentMapFormatted());
        mapFinishedMessage = mapFinishedMessage.replace("%map_name%",  playerSession.getCurrentMap().getColoredName());
        player.sendMessage(mapFinishedMessage);

        if(currentMapCount <= dropperMapList.size()) {
            DropperMap currentDropperMap = dropperMapList.get(currentMapCount- 1);
            playerSession.setCurrentMap(currentDropperMap);
            player.teleport(currentDropperMap.getRandomSpawn());
            playerSession.startStopwatchMap();
        } else {
            playerSession.setFinalStopwatchTotal(System.currentTimeMillis() - playerSession.getStopwatchTotal());
            String mapTimeFormatted = playerSession.getTimeCurrentMapFormatted();

            String finalTotalTimeFormatted = playerSession.getFinalTimeStopwatchFormatted();

            String actionBarMessage = MessageConfigUtils.get("games.player_action_bar").replace("%map_time%", mapTimeFormatted);
            actionBarMessage = actionBarMessage.replace("%total_time%", finalTotalTimeFormatted);
            sendActionBar(player, actionBarMessage);

            String finishedAllMapsMessage = MessageConfigUtils.get("games.finished_all_maps_message");
            finishedAllMapsMessage = finishedAllMapsMessage.replace("%map_time%", finalTotalTimeFormatted);

            String playerFinishedAllMaps = MessageConfigUtils.get("games.player_finished_all_maps");
            playerFinishedAllMaps = playerFinishedAllMaps.replace("%player%", player.getDisplayName());
            playerFinishedAllMaps = playerFinishedAllMaps.replace("%time%", finalTotalTimeFormatted);

            for(Player playerFromGame : playerList) {
                if(!playerFromGame.getDisplayName().equals(player.getDisplayName())) {
                    playerFromGame.sendMessage(playerFinishedAllMaps);
                } else {
                    playerFromGame.sendMessage(finishedAllMapsMessage);
                }
            }

            if(playerSession.getFinalStopwatchTotal() < playerSession.getDropperStats().getBestTime() || playerSession.getDropperStats().getBestTime() == 0) {
                DropperReloaded.getInstance().getDatabase().setNewBestTime(player, playerSession.getFinalStopwatchTotal());
                player.sendMessage(MessageConfigUtils.get("games.new_personal_best"));
            }

            String mapFinishedTitle = MessageConfigUtils.get("games.map_finished_title");
            String mapFinishedSubtitle = MessageConfigUtils.get("games.map_finished_subtitle");
            mapFinishedSubtitle = mapFinishedSubtitle.replace("%place%", String.valueOf(playerFinished.size() + 1));

            player.sendTitle(mapFinishedTitle, mapFinishedSubtitle, 0, 2 * 20, 3 * 20);
            inGameScoreboard.setup(player);

            addPlayerSpectator(player);
            if(playerFinished.isEmpty()) {
                playerSession.getDropperStats().setTotalWins(playerSession.getDropperStats().getTotalWins() + 1);
                reduceTimer();
            }
            playerFinished.add(player);

            if(playerFinished.size() == playerList.size()) {
                timeLeft = 0;
            }
        }

        Sound sound = Sound.valueOf(MessageConfigUtils.get("games.portal_enter_sound"));
        player.playSound(player.getLocation(), sound, 1, 1);

        inGameScoreboard.updateCurrentMapPlayer(player);

    }

    public boolean reduceTimer() {
        int reduceTimer = Integer.parseInt(MessageConfigUtils.get("games.first_done_cut_timer"));
        if(timeLeft < reduceTimer || !playerFinished.isEmpty()) return false;

        timeLeft = reduceTimer;
        inGameScoreboard.updateTimeLeft();
        List<String> messages = DropperReloaded.getInstance().getConfig().getStringList("games.first_player_finished");
        for(String message : messages) {
            message = message.replace("%timer%", getTimeFormatted());
            sendMessageToPlayers(message);
        }

        return true;

    }

    public boolean addPlayerSpectator(Player player){
        if(spectatorList.contains(player)) return false;
        spectatorList.add(player);
        PlayerSession playerSession = playersSessionManager.getPlayerSession(player);
        playerSession.stopSession();
        playerSession.setSpectator(true);
        if(timeLeft > 0) {
            player.teleport(dropperMapList.get(dropperMapList.size() - 1).getRandomSpawn());
        }
        player.getInventory().clear();
        player.getInventory().setItem(DropperItems.spectatorPlayerList.getSlot(), DropperItems.spectatorPlayerList.getItem());
        player.getInventory().setItem(DropperItems.viewMap.getSlot(), DropperItems.viewMap.getItem());
        player.getInventory().setItem(DropperItems.playAgain.getSlot(), DropperItems.playAgain.getItem());
        player.getInventory().setItem(DropperItems.leaveBed.getSlot(), DropperItems.leaveBed.getItem());
        player.setAllowFlight(true);
        for(Player playerFromGame : playerList) {
            playerFromGame.hidePlayer(player);
        }
        return true;
    }

    public boolean removePlayerSpectator(Player player){
        if(!spectatorList.contains(player)) return false;
        spectatorList.remove(player);
        player.setAllowFlight(false);
        return true;
    }

    private void startCountdownBeginning() {
        countdownStartTimer = DropperReloaded.getInstance().getConfig().getInt("games.timer_before_drop") * 20 + 60;
        int cooldownTimer = DropperReloaded.getInstance().getConfig().getInt("games.timer_before_drop") * 20;
        Sound sound = Sound.valueOf(MessageConfigUtils.get("games.timer_sound"));

        int doorY = dropperMapList.get(0).getDoorLocations().get(0).getBlockY() + 1;

        countdownStart = new BukkitRunnable() {
            @Override
            public void run() {
                sendDoorBlock();
                for(Player player : playerList) {
                    if(player.getLocation().getY() < doorY) {
                        player.teleport(dropperMapList.get(0).getRandomSpawn());
                    }
                }
                if(countdownStartTimer == 0) {
                    playSoundToPlayers(sound, 1f, 2f);
                    sendMessageToPlayers(MessageConfigUtils.get("games.go_message"));
                    start();
                    this.cancel();
                } else if(countdownStartTimer % 20 == 0 && countdownStartTimer <= cooldownTimer) {
                    sendMessageToPlayers(MessageConfigUtils.get("games.timer_message", "%timer%", String.valueOf(countdownStartTimer / 20)));
                    playSoundToPlayers(sound);
                }
                countdownStartTimer--;
            }
        }.runTaskTimer(DropperReloaded.getInstance(), 0L, 1L);
    }

    private void sendDoorBlock() {
        List<Location> doorLocations = dropperMapList.get(0).getDoorLocations();
        if (doorLocations == null) return;
        int syncTimer = countdownStartTimer + 20;
        for (Player player : playerList) {
            for (Location location : doorLocations) {
                Material glassType = Material.GLASS;
                if (syncTimer / 20 >= 5) {
                    glassType = Material.RED_STAINED_GLASS;
                } else if (syncTimer / 20 >= 3) {
                    glassType = Material.YELLOW_STAINED_GLASS;
                } else if (syncTimer / 20 >= 1 && countdownStartTimer != 0) {
                    glassType = Material.LIME_STAINED_GLASS;
                } else if(countdownStartTimer == 0) {
                    glassType = Material.AIR;
                }

                player.sendBlockChange(location, glassType.createBlockData());

            }
        }
    }

    public void resetDoorBlock(Player player) {
        List<Location> doorLocations = dropperMapList.get(0).getDoorLocations();
        if (doorLocations == null) return;
        for (Location location : doorLocations) {
            player.sendBlockChange(location, location.getBlock().getType().createBlockData());
        }
    }

    public boolean hasStarted() {
        return gameStatus == GameStatus.PLAYING || gameStatus == GameStatus.DOOR_COUNTDOWN;
    }

    public boolean hasEnded() {
        return gameStatus == GameStatus.ENDED;
    }

    public String getTimeFormatted() {
        Date date = new Date(0);
        date.setTime(date.getTime() + timeLeft * 1000L);
        return new SimpleDateFormat(MessageConfigUtils.get("games.time_format")).format(date.getTime());
    }

    public int getId() {
        return id;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public List<DropperMap> getMapList() {
        return dropperMapList;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public WaitLobby getWaitLobby() {
        return waitLobby;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public InGameScoreboard getInGameScoreboard() {
        return inGameScoreboard;
    }
}
