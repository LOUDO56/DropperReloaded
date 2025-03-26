package fr.loudo.dropperReloaded.scoreboards;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.games.Game;
import fr.loudo.dropperReloaded.maps.DropperMap;
import fr.loudo.dropperReloaded.maps.DropperMapDifficultyColorPrefix;
import fr.loudo.dropperReloaded.players.PlayerSession;
import fr.loudo.dropperReloaded.utils.MessageConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class InGameScoreboard {

    private final Game game;

    private String mapNotCompletedSymbol;
    private String mapCompletedSymbol;
    private String inThisMapSymbol;
    private List<String> lines;
    private HashMap<UUID, Integer> lineTimeLeftPlayer;
    private int lineTotalFails;
    private int configTotalFailsLineIndex;
    private int configTimeLeftLineIndex;
    private int currentMapLine;
    private int configCurrentMapLineIndex;

    public InGameScoreboard(Game game) {
        this.game = game;
        this.lineTimeLeftPlayer = new HashMap<>();
    }

    public void setup(Player player) {

        this.mapNotCompletedSymbol = MessageConfigUtils.get("games.scoreboard.map_not_completed_symbol");
        this.mapCompletedSymbol = MessageConfigUtils.get("games.scoreboard.map_completed_symbol");
        this.inThisMapSymbol = MessageConfigUtils.get("games.scoreboard.in_this_map_symbol");
        this.lines = DropperReloaded.getInstance().getConfig().getStringList("games.scoreboard.lines");
        
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("dropperReloaded", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(MessageConfigUtils.get("global.scoreboard_title"));

        String spaceString = " ";
        player.setScoreboard(scoreboard);

        PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);

        int lineIndex;
        if(playerSession.hasFinishedGame()) {
            lineIndex = lines.size() - 1 + game.getMapList().size();
        } else {
            lineIndex = lines.size() - 2 + game.getMapList().size();
        }
        for(int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains("%time_left%")) {
                lineTimeLeftPlayer.put(player.getUniqueId(), lineIndex);
                configTimeLeftLineIndex = i;
            }
            if (line.contains("%total_fails%")) {
                lineTotalFails = lineIndex;
                configTotalFailsLineIndex = i;
                line = line.replace("%total_fails%", String.valueOf(playerSession.getTotalFails()));
            }
            if (line.contains("%current_map_count%")) {
                currentMapLine = lineIndex;
                configCurrentMapLineIndex = i;
                setupCurrentMapLine(player);
            } else if (line.contains("%map_completed_symbol%")) {
                setupMapLines(player);
                updateCurrentMapPlayer(player);
                lineIndex -= game.getMapList().size();
                lineIndex++;
            } else if (line.contains("%total_time%") && !playerSession.hasFinishedGame()) {
                // Don't add line
                lineIndex++;
            } else {
                line = line.replace("{space}", spaceString);
                line = line.replace("%total_time%", playerSession.getFinalTimeStopwatchFormatted());
                Team team = scoreboard.registerNewTeam("line_" + lineIndex);
                team.addEntry(ChatColor.values()[lineIndex].toString());
                objective.getScore(ChatColor.values()[lineIndex].toString()).setScore(lineIndex);
                team.setPrefix(formatLine(line));
            }
            spaceString += " ";
            lineIndex--;
        }
    }

    private void setupCurrentMapLine(Player player) {
        PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
        String currentMapString = lines.get(configCurrentMapLineIndex);
        currentMapString = currentMapString
                .replace("%current_map_count%", String.valueOf(playerSession.getCurrentMapCount()))
                .replace("%total_map_count%", String.valueOf(game.getMapList().size()));
        Team team = player.getScoreboard().registerNewTeam("currentMap");
        team.addEntry(ChatColor.values()[currentMapLine].toString());
        team.setPrefix(currentMapString);
        player.getScoreboard().getObjective("dropperReloaded").getScore(ChatColor.values()[currentMapLine].toString()).setScore(currentMapLine);
    }

    private void setupMapLines(Player player) {
        List<DropperMap> dropperMapList = game.getMapList();
        for (int i = 0; i < dropperMapList.size(); i++) {
            Team team = player.getScoreboard().registerNewTeam("map_" + i);
            team.addEntry(ChatColor.values()[currentMapLine - i - 1].toString());
            player.getScoreboard().getObjective("dropperReloaded").getScore(ChatColor.values()[currentMapLine - i - 1].toString()).setScore(currentMapLine - i - 1);
        }
    }

    private String formatLine(String line) {
        return line.replace("%game_id%", String.valueOf(game.getId()))
                .replace("%date%", new SimpleDateFormat("dd/MM/yy").format(new Date()))
                .replace("%time_left%", game.getTimeFormatted())
                .replace("%website%", MessageConfigUtils.get("global.website"));
    }

    public void updateCurrentMapPlayer(Player player) {
        PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);

        String currentMapString = lines.get(configCurrentMapLineIndex);
        int currentMapCount = Math.min(playerSession.getCurrentMapCount(), playerSession.getPlayerGame().getMapList().size());
        currentMapString = currentMapString
                .replace("%current_map_count%", String.valueOf(currentMapCount))
                .replace("%total_map_count%", String.valueOf(game.getMapList().size()));
        player.getScoreboard().getTeam("currentMap").setPrefix(currentMapString);

        List<DropperMap> dropperMapList = game.getMapList();
        int currentMapIndex = playerSession.getCurrentMapCount();

        for (int i = 0; i < dropperMapList.size(); i++) {
            Team mapTeam = player.getScoreboard().getTeam("map_" + i);
            DropperMap dropperMap = dropperMapList.get(i);
            String line = lines.get(configCurrentMapLineIndex + 1);

            line = line.replace("%map_name%", DropperMapDifficultyColorPrefix.get(dropperMap.getDifficulty()) + dropperMap.getDisplayName())
                    .replace("%map_completed_symbol%", i + 1 < currentMapIndex ? mapCompletedSymbol : mapNotCompletedSymbol)
                    .replace("%in_this_map_symbol%", i + 1 == currentMapIndex ? inThisMapSymbol : "");

            mapTeam.setPrefix(line);
        }
    }

    public void updateTotalFails(Player player) {
        String line = lines.get(configTotalFailsLineIndex);
        PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
        line = line.replace("%total_fails%", String.valueOf(playerSession.getTotalFails()));
        player.getScoreboard().getTeam("line_" + lineTotalFails).setPrefix(formatLine(line));
    }

    public void updateTimeLeft() {
        String line = lines.get(configTimeLeftLineIndex);
        for(Player player : game.getPlayerList()) {
            player.getScoreboard().getTeam("line_" + lineTimeLeftPlayer.get(player.getUniqueId())).setPrefix(formatLine(line));
        }
    }
}