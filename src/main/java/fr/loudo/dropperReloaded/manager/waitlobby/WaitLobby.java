package fr.loudo.dropperReloaded.manager.waitlobby;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.manager.games.Game;
import fr.loudo.dropperReloaded.manager.games.GameStatus;
import fr.loudo.dropperReloaded.scoreboards.WaitLobbyScoreboard;
import fr.loudo.dropperReloaded.utils.MessageConfigUtils;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class WaitLobby {

    private final int maxPlayer = DropperReloaded.getWaitLobbyConfiguration().getMaxPlayer();
    private final int minPlayer = DropperReloaded.getWaitLobbyConfiguration().getMinPlayer();

    private int timer;
    private boolean isOnCountdown;
    private BukkitTask countdownTask;
    private Game game;
    private WaitLobbyScoreboard waitLobbyScoreboard;

    public WaitLobby(Game game) {
        this.game = game;
        this.isOnCountdown = false;
        this.waitLobbyScoreboard = new WaitLobbyScoreboard(game);
        waitLobbyScoreboard.setup();
    }

    public void playerJoinedMessage(String username) {
        String joinMessage = MessageConfigUtils.get("wait_lobby.join_message");
        joinMessage = joinMessage.replace("%player%", username);
        joinMessage = joinMessage.replace("%current_player%", String.valueOf(game.getPlayerList().size()));
        joinMessage = joinMessage.replace("%max_player%", String.valueOf(maxPlayer));
        game.sendMessageToPlayers(joinMessage);
        waitLobbyScoreboard.update();
        if(game.getPlayerList().size() == minPlayer) {
            startCountdown();
        }
    }

    public void playerLeftMessage(String username) {
        String leftMessage = MessageConfigUtils.get("wait_lobby.left_message");
        leftMessage = leftMessage.replace("%player%", username);
        leftMessage = leftMessage.replace("%current_player%", String.valueOf(game.getPlayerList().size()));
        leftMessage = leftMessage.replace("%max_player%", String.valueOf(maxPlayer));
        game.sendMessageToPlayers(leftMessage);
        waitLobbyScoreboard.update();
        if(game.getPlayerList().size() < minPlayer) {
            stopCountdown();
        }
    }

    public boolean startCountdown() {
        if(isOnCountdown) return false;
        isOnCountdown = true;
        game.setGameStatus(GameStatus.STARTING);
        timer = Integer.parseInt(MessageConfigUtils.get("wait_lobby.timer_start_seconds"));
        waitLobbyScoreboard.update();

        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(timer % 15 == 0) {
                    String startingMessage = MessageConfigUtils.get("wait_lobby.timer_message");
                    startingMessage = startingMessage.replace("%timer_seconds%", String.valueOf(timer));
                    game.sendMessageToPlayers(startingMessage);
                } else if (timer == 0) {
                    stopCountdown();
                }
                waitLobbyScoreboard.update();
                timer--;
            }
        }.runTaskTimer(DropperReloaded.getInstance(), 0L, 20L);

        return true;
    }

    public boolean stopCountdown() {
        if(!isOnCountdown) return false;
        isOnCountdown = false;

        countdownTask.cancel();
        countdownTask = null;

        return true;
    }

    public void startGame() {

    }

    public int getTimer() {
        return timer;
    }
}
