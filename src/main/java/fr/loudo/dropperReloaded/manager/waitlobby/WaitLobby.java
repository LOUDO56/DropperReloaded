package fr.loudo.dropperReloaded.manager.waitlobby;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.manager.games.Game;
import fr.loudo.dropperReloaded.scoreboards.WaitLobbyScoreboard;
import fr.loudo.dropperReloaded.utils.MessageConfigUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class WaitLobby {

    private int timer;
    private boolean isOnCountdown;
    private BukkitTask countdownTask;
    private Game game;
    private WaitLobbyScoreboard waitLobbyScoreboard;

    public WaitLobby(Game game) {
        this.game = game;
        this.isOnCountdown = false;
        this.waitLobbyScoreboard = new WaitLobbyScoreboard(game);
    }

    public void updateScoreboard() {

    }

    public void playerJoinedMessage(String username) {
        String joinMessage = MessageConfigUtils.get("wait_lobby.join_message");
        joinMessage = joinMessage.replace("%player%", username);
        joinMessage = joinMessage.replace("%current_player%", String.valueOf(game.getPlayerList().size()));
        joinMessage = joinMessage.replace("%max_player%", String.valueOf(DropperReloaded.getWaitLobbyConfiguration().getMaxPlayer()));
        game.sendMessageToPlayers(joinMessage);
        waitLobbyScoreboard.update();
    }

    public void playerLeftMessage(String username) {
        String leftMessage = MessageConfigUtils.get("wait_lobby.left_message");
        leftMessage = leftMessage.replace("%player%", username);
        leftMessage = leftMessage.replace("%current_player%", String.valueOf(game.getPlayerList().size()));
        leftMessage = leftMessage.replace("%max_player%", String.valueOf(DropperReloaded.getWaitLobbyConfiguration().getMaxPlayer()));
        game.sendMessageToPlayers(leftMessage);
        waitLobbyScoreboard.update();
    }

    public boolean startCountdown() {
        if(isOnCountdown) return false;
        isOnCountdown = true;

        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {

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

}
