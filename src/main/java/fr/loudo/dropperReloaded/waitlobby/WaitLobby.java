package fr.loudo.dropperReloaded.waitlobby;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.games.Game;
import fr.loudo.dropperReloaded.games.GameStatus;
import fr.loudo.dropperReloaded.guis.mapVote.MapVoteGui;
import fr.loudo.dropperReloaded.scoreboards.WaitLobbyScoreboard;
import fr.loudo.dropperReloaded.utils.MessageConfigUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WaitLobby {

    private final Sound timerSound = Sound.valueOf(DropperReloaded.getInstance().getConfig().getString("wait_lobby.timer_sound"));

    private int timer;
    private boolean isOnCountdown;
    private BukkitTask countdownTask;
    private Game game;
    private WaitLobbyScoreboard waitLobbyScoreboard;
    private MapVoteGui mapVoteGui;

    public WaitLobby(Game game) {
        this.game = game;
        this.isOnCountdown = false;
        this.waitLobbyScoreboard = new WaitLobbyScoreboard(game);
    }

    public void playerJoinedMessage(String username) {
        String joinMessage = MessageConfigUtils.get("wait_lobby.join_message");
        joinMessage = joinMessage.replace("%player%", username);
        joinMessage = joinMessage.replace("%current_player%", String.valueOf(game.getPlayerList().size()));
        joinMessage = joinMessage.replace("%max_player%", String.valueOf(DropperReloaded.getInstance().getWaitLobbyConfiguration().getMaxPlayer()));
        game.sendMessageToPlayers(joinMessage);
        waitLobbyScoreboard.updatePlayerList();
        if(game.getPlayerList().size() == DropperReloaded.getInstance().getWaitLobbyConfiguration().getMinPlayer()) {
            startCountdown();
        }
    }

    public void playerLeftMessage(String username) {
        String leftMessage = MessageConfigUtils.get("wait_lobby.left_message");
        leftMessage = leftMessage.replace("%player%", username);
        leftMessage = leftMessage.replace("%current_player%", String.valueOf(game.getPlayerList().size()));
        leftMessage = leftMessage.replace("%max_player%", String.valueOf(DropperReloaded.getInstance().getWaitLobbyConfiguration().getMaxPlayer()));
        game.sendMessageToPlayers(leftMessage);
        waitLobbyScoreboard.updatePlayerList();
        if(game.getPlayerList().size() < DropperReloaded.getInstance().getWaitLobbyConfiguration().getMinPlayer()) {
            stopCountdown();
        }
    }

    public boolean startCountdown() {
        if(isOnCountdown) return false;
        isOnCountdown = true;
        game.setGameStatus(GameStatus.STARTING);
        timer = Integer.parseInt(MessageConfigUtils.get("wait_lobby.timer_start_seconds"));
        waitLobbyScoreboard.updateGameStatus();
        sendStartMessage();
        final boolean[] dontSendSendStartTwice = {true};
        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (timer == 0) {
                    startGame();
                } else if(timer % 10 == 0 && timer <= 30 || timer % 60 == 0 || timer >= 1 && timer <= 5) {
                    if(!dontSendSendStartTwice[0]) {
                        sendStartMessage();
                    }
                    if(timer >= 1 && timer <= 5) {
                        game.sendTitle(ChatColor.RED + String.valueOf(timer), "", 0, 30, 0);
                    }
                }
                if(timer >= 1) {
                    waitLobbyScoreboard.updateGameStatus();
                }
                if(dontSendSendStartTwice[0]) {
                    dontSendSendStartTwice[0] = false;
                }
                timer--;
            }
        }.runTaskTimer(DropperReloaded.getInstance(), 0L, 20L);

        return true;
    }

    private void sendStartMessage() {
        String startingMessage = MessageConfigUtils.get("wait_lobby.timer_message");
        startingMessage = startingMessage.replace("%timer_seconds%", String.valueOf(timer));
        game.sendMessageToPlayers(startingMessage);
        game.playSoundToPlayers(timerSound);
    }

    public boolean stopCountdown() {
        if(!isOnCountdown) return false;
        isOnCountdown = false;

        countdownTask.cancel();
        countdownTask = null;

        game.setGameStatus(GameStatus.WAITING);
        game.sendTitle(MessageConfigUtils.get("wait_lobby.cancelled_title"), MessageConfigUtils.get("wait_lobby.cancelled_subtitle"), 0, 40 ,20);
        waitLobbyScoreboard.updateGameStatus();

        return true;
    }

    public String getTimeFormatted() {
        Date date = new Date(0);
        date.setTime(date.getTime() + timer * 1000L);
        return new SimpleDateFormat(MessageConfigUtils.get("wait_lobby.time_format")).format(date.getTime());
    }

    public void startGame() {
        stopCountdown();
        game.setup();
    }

    public int getTimer() {
        return timer;
    }

    public WaitLobbyScoreboard getWaitLobbyScoreboard() {
        return waitLobbyScoreboard;
    }
}
