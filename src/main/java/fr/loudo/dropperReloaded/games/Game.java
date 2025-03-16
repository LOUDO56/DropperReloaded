package fr.loudo.dropperReloaded.games;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.items.DropperItems;
import fr.loudo.dropperReloaded.maps.Map;
import fr.loudo.dropperReloaded.players.PlayerSession;
import fr.loudo.dropperReloaded.players.PlayersSessionManager;
import fr.loudo.dropperReloaded.scoreboards.InGameScoreboard;
import fr.loudo.dropperReloaded.utils.MessageConfigUtils;
import fr.loudo.dropperReloaded.waitlobby.WaitLobby;
import fr.loudo.dropperReloaded.waitlobby.WaitLobbyConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Game {

    private final WaitLobbyConfiguration waitLobbyConfiguration = DropperReloaded.getWaitLobbyConfiguration();
    private final PlayersSessionManager playersSessionManager = DropperReloaded.getPlayersSessionManager();

    private int id;
    private int timeLeft;

    private boolean onePlayerFinished;

    private List<Player> playerList;
    private List<Player> spectatorList;
    private List<Map> mapList;
    private GameStatus gameStatus;
    private WaitLobby waitLobby;
    private BukkitTask countdownStart;
    private BukkitTask countdownGame;
    private InGameScoreboard inGameScoreboard;


    public Game() {
        this.playerList = new ArrayList<>();
        this.mapList = new ArrayList<>();
        this.spectatorList = new ArrayList<>();
        this.gameStatus = GameStatus.WAITING;
        this.waitLobby = new WaitLobby(this);
        this.id = DropperReloaded.getGamesManager().getGameList().size();
        this.inGameScoreboard = new InGameScoreboard(this);
        this.onePlayerFinished = false;
    }

    public boolean addPlayer(Player player) {
        if(playerList.contains(player) && playerList.size() >= waitLobbyConfiguration.getMaxPlayer()) return false;
        playerList.add(player);
        if(!hasStarted()) {
            player.teleport(waitLobbyConfiguration.getSpawn());
            waitLobby.getWaitLobbyScoreboard().setup(player);
            waitLobby.playerJoinedMessage(player.getDisplayName());
            player.getInventory().clear();
            player.getInventory().setItem(DropperItems.mapVote.getSlot(), DropperItems.mapVote.getItem());
            player.getInventory().setItem(DropperItems.leaveBed.getSlot(), DropperItems.leaveBed.getItem());
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setGameMode(GameMode.ADVENTURE);
                }
            }.runTaskLater(DropperReloaded.getInstance(), 10L);
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
        if(!hasStarted()) {
            waitLobby.playerLeftMessage(player.getDisplayName());
        }
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            player.showPlayer(onlinePlayer);
            onlinePlayer.showPlayer(player);
        }
        for(Player playerFromGame : playerList) {
            player.hidePlayer(playerFromGame);
            playerFromGame.hidePlayer(player);
        }
        player.setAllowFlight(false);
        if(playerList.isEmpty()) {
            reset();
        }
        return true;
    }

    public void setup() {
        gameStatus = GameStatus.DOOR_COUNTDOWN;
        timeLeft = Integer.parseInt(MessageConfigUtils.get("games.timer_in_game"));
        mapList = DropperReloaded.getMapsManager().getRandomMaps();
        for(Player player : playerList) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 1, false, false));
            inGameScoreboard.setup(player);
            PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
            playerSession.setCurrentMap(mapList.get(0));
            player.teleport(mapList.get(0).getRandomSpawn());
            player.getInventory().clear();
            player.getInventory().setItem(DropperItems.resetLocation.getSlot(), DropperItems.resetLocation.getItem());
            player.getInventory().setItem(DropperItems.playerVisibilityOn.getSlot(), DropperItems.playerVisibilityOn.getItem());
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setGameMode(GameMode.ADVENTURE);
                }
            }.runTaskLater(DropperReloaded.getInstance(), 10L);
        }
        startCountdownBeginning();
    }

    private void start() {
        gameStatus = GameStatus.PLAYING;
        for(Player player : playerList) {
            PlayerSession playerSession = playersSessionManager.getPlayerSession(player);
            playerSession.startStopwatch();
            playerSession.startDetectingPortal();

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

        for(Player player : playerList) {
            addPlayerSpectator(player);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                reset();
            }
        }.runTaskLater(DropperReloaded.getInstance(), 5L * 20L);
    }

    public void reset() {
        for(Player player : playerList) {
            removePlayer(player);
        }
        if(countdownStart != null) {
            countdownStart.cancel();
        }
        if(countdownGame != null) {
            countdownGame.cancel();
        }
        gameStatus = GameStatus.WAITING;
        playerList = new ArrayList<>();
        mapList = new ArrayList<>();
        spectatorList = new ArrayList<>();
        onePlayerFinished = false;
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

    public void playSoundToPlayers(Sound sound, float pitch, float volume) {
        for(Player player : playerList) {
            player.playSound(player.getLocation(), sound, pitch, volume);
        }
    }

    public void sendTitleToPlayers(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if(DropperReloaded.isIsProtocolLibPluginEnabled()) {
            PacketContainer titleTimesPacket;
            PacketContainer titlePacket;
            PacketContainer subtitlePacket;

            if(DropperReloaded.isNewerVersion()) {
                titlePacket = new PacketContainer(PacketType.Play.Server.SET_TITLE_TEXT);
                titlePacket.getChatComponents().write(0, WrappedChatComponent.fromText(title));

                subtitlePacket = new PacketContainer(PacketType.Play.Server.SET_SUBTITLE_TEXT);
                subtitlePacket.getChatComponents().write(0, WrappedChatComponent.fromText(subtitle));

                titleTimesPacket = new PacketContainer(PacketType.Play.Server.SET_TITLES_ANIMATION);
                titleTimesPacket.getIntegers()
                        .write(0, fadeIn)
                        .write(1, stay)
                        .write(2, fadeOut);
            } else {

                titleTimesPacket = new PacketContainer(PacketType.Play.Server.TITLE);
                titleTimesPacket.getTitleActions().write(0, EnumWrappers.TitleAction.TIMES);
                titleTimesPacket.getIntegers()
                        .write(0, fadeIn)
                        .write(1, stay)
                        .write(2, fadeOut);

                titlePacket = new PacketContainer(PacketType.Play.Server.TITLE);
                titlePacket.getTitleActions().write(0, EnumWrappers.TitleAction.TITLE);
                titlePacket.getChatComponents().write(0, WrappedChatComponent.fromText(title));

                subtitlePacket = new PacketContainer(PacketType.Play.Server.TITLE);
                subtitlePacket.getTitleActions().write(0, EnumWrappers.TitleAction.SUBTITLE);
                subtitlePacket.getChatComponents().write(0, WrappedChatComponent.fromText(subtitle));

            }
            for(Player player : playerList) {
                DropperReloaded.getProtocolManager().sendServerPacket(player, titleTimesPacket);
                DropperReloaded.getProtocolManager().sendServerPacket(player, titlePacket);
                DropperReloaded.getProtocolManager().sendServerPacket(player, subtitlePacket);
            }
        } else {
            for(Player player : playerList) {
                player.sendTitle(title, subtitle);
            }
        }
    }

    public void teleportPlayerToNextMap(Player player) {
        PlayerSession playerSession = playersSessionManager.getPlayerSession(player);
        if(playerSession == null) return;
        if(playerSession.isSpectator()) return;
        int currentMapCount = playerSession.getCurrentMapCount() + 1;

        playerSession.setCurrentMapCount(currentMapCount);

        if(currentMapCount <= mapList.size()) {
            Map currentMap = mapList.get(currentMapCount- 1);
            playerSession.setCurrentMap(currentMap);
            player.teleport(currentMap.getRandomSpawn());
        } else {
            addPlayerSpectator(player);
            if(!onePlayerFinished) {
                reduceTimer();
            }
        }

        Sound sound = Sound.valueOf(MessageConfigUtils.get("games.portal_enter_sound"));
        player.playSound(player.getLocation(), sound, 1, 1);

        inGameScoreboard.updateCurrentMapPlayer(player);

    }

    public boolean reduceTimer() {
        int reduceTimer = Integer.parseInt(MessageConfigUtils.get("games.first_done_cut_timer"));
        if(timeLeft < reduceTimer || onePlayerFinished) return false;

        timeLeft = reduceTimer;
        inGameScoreboard.updateTimeLeft();
        List<String> messages = DropperReloaded.getInstance().getConfig().getStringList("games.first_player_finished");
        for(String message : messages) {
            message = message.replace("%timer%", getTimeFormatted());
            sendMessageToPlayers(message);
        }

        onePlayerFinished = true;

        return true;

    }

    public boolean addPlayerSpectator(Player player){
        if(spectatorList.contains(player)) return false;
        spectatorList.add(player);
        playersSessionManager.getPlayerSession(player).setSpectator(true);
        if(timeLeft > 0) {
            player.teleport(mapList.get(mapList.size() - 1).getRandomSpawn());
        }
        player.getInventory().clear();
        player.getInventory().setItem(DropperItems.spectatorPlayerList.getSlot(), DropperItems.spectatorPlayerList.getItem());
        player.getInventory().setItem(DropperItems.playAgain.getSlot(), DropperItems.playAgain.getItem());
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
        final int[] timer = {DropperReloaded.getInstance().getConfig().getInt("games.timer_before_drop")};
        Sound sound = Sound.valueOf(MessageConfigUtils.get("games.timer_sound"));
        countdownStart = new BukkitRunnable() {
            @Override
            public void run() {
            if(timer[0] == 0) {
                playSoundToPlayers(sound, 1.2f, 1f);
                sendMessageToPlayers(MessageConfigUtils.get("games.go_message"));
                start();
                this.cancel();
            } else {
                sendMessageToPlayers(MessageConfigUtils.get("games.timer_message", "%timer%", String.valueOf(timer[0])));
                playSoundToPlayers(sound);
            }
            timer[0]--;
            }
        }.runTaskTimer(DropperReloaded.getInstance(), 0L, 20L);
    }

    public boolean hasStarted() {
        return gameStatus == GameStatus.PLAYING;
    }

    public String getTimeFormatted() {
        Date date = new Date(0);
        date.setTime(date.getTime() + timeLeft * 1000L);
        return new SimpleDateFormat(MessageConfigUtils.get("games.time_format")).format(date);
    }

    public int getId() {
        return id;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public List<Map> getMapList() {
        return mapList;
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
