package fr.loudo.dropperReloaded.manager.waitlobby;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.manager.games.Game;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class WaitLobby {

    private List<Player> playerList;
    private int timer;
    private boolean isOnCountdown;
    private BukkitTask countdownTask;

    public WaitLobby() {
        this.playerList = new ArrayList<>();
        this.isOnCountdown = false;
    }

    public boolean addPlayer(Player player) {
        if(playerList.contains(player)) return false;
        playerList.add(player);
        return true;
    }

    public boolean removePlayer(Player player) {
        if(!playerList.contains(player)) return false;
        playerList.remove(player);
        return true;
    }

    public int getPlayerListSize() {
        return playerList.size();
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

    private void startGame() {

    }

}
