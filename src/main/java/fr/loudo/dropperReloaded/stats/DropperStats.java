package fr.loudo.dropperReloaded.stats;

import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DropperStats {
    private String uuid;
    private String username;
    private int bestTime;
    private int totalFails;
    private int totalMapCompleted;
    private int totalWins;
    private int totalLost;

    public DropperStats(String uuid, String username, int bestTime, int totalFails, int totalMapCompleted, int totalWins, int totalLost) {
        this.uuid = uuid;
        this.username = username;
        this.bestTime = bestTime;
        this.totalFails = totalFails;
        this.totalMapCompleted = totalMapCompleted;
        this.totalWins = totalWins;
        this.totalLost = totalLost;
    }

    public int getBestTime() {
        return bestTime;
    }

    public int getTotalFails() {
        return totalFails;
    }

    public void setTotalFails(int totalFails) {
        this.totalFails = totalFails;
    }

    public int getTotalMapCompleted() {
        return totalMapCompleted;
    }

    public void setTotalMapCompleted(int totalMapCompleted) {
        this.totalMapCompleted = totalMapCompleted;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(int totalWins) {
        this.totalWins = totalWins;
    }

    public int getTotalLost() {
        return totalLost;
    }

    public void setTotalLost(int totalLost) {
        this.totalLost = totalLost;
    }

    @Override
    public String toString() {
        String bestTimeString = ChatColor.GREEN + "Best Time: " + ChatColor.YELLOW + new SimpleDateFormat("mm:ss:SSS").format(new Date(bestTime)) + "\n";
        String totalFailsString = ChatColor.GREEN + "Total Fails: " + ChatColor.YELLOW + totalFails + "\n";
        String totalMapCompletedString = ChatColor.GREEN + "Total Map Completed: " + ChatColor.YELLOW + totalMapCompleted + "\n";
        String totalGamesString = ChatColor.GREEN + "Total Games: " + ChatColor.YELLOW + (totalLost + totalWins) + "\n";
        String totalWinsString = ChatColor.GREEN + "Total Wins: " + ChatColor.YELLOW + totalWins + "\n";
        String totalLostString = ChatColor.GREEN + "Total Lost: " + ChatColor.YELLOW + totalLost + "\n";

        return bestTimeString + totalFailsString + totalMapCompletedString + totalGamesString + totalWinsString + totalLostString;
    }
}
