package fr.loudo.dropperReloaded.scoreboards;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.games.Game;
import fr.loudo.dropperReloaded.maps.Map;
import fr.loudo.dropperReloaded.maps.MapDifficultyColorPrefix;
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
import java.util.List;

public class InGameScoreboard {

    private final String mapNotCompletedSymbol;
    private final String mapCompletedSymbol;
    private final String inThisMapSymbol;
    private final List<String> lines;
    private final Game game;
    private int lineTotalFails;
    private int configTotalFailsLineIndex;
    private int lineTimeLeft;
    private int configTimeLeftLineIndex;
    private int currentMapLine;
    private int configCurrentMapLineIndex;

    public InGameScoreboard(Game game) {
        this.game = game;
        DropperReloaded plugin = DropperReloaded.getInstance();
        this.mapNotCompletedSymbol = plugin.getConfig().getString("games.scoreboard.map_not_completed_symbol");
        this.mapCompletedSymbol = plugin.getConfig().getString("games.scoreboard.map_completed_symbol");
        this.inThisMapSymbol = plugin.getConfig().getString("games.scoreboard.in_this_map_symbol");
        this.lines = plugin.getConfig().getStringList("games.scoreboard.lines");
    }

    public void setup(Player player) {

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("dropperReloaded", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(MessageConfigUtils.get("global.scoreboard_title"));

        String spaceString = " ";
        player.setScoreboard(scoreboard);

        PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);

        int lineIndex = lines.size() + game.getMapList().size();
        for(int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains("%time_left%")) {
                lineTimeLeft = lineIndex;
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
                lineIndex -= game.getMapList().size() + 1;
            } else if (line.contains("%total_time%") && !playerSession.hasFinishedGame()) {
                // Don't add line
                lineIndex++;
            } else {
                line = line.replace("{space}", spaceString);
                line = line.replace("%total_time%", playerSession.getTotalTimeFormatted());
                Team team = scoreboard.registerNewTeam("dropperReloaded_game_line_" + lineIndex);
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
        Team team = player.getScoreboard().registerNewTeam("dropperReloaded_game_currentMap");
        team.addEntry(ChatColor.values()[currentMapLine].toString());
        team.setPrefix(currentMapString);
        player.getScoreboard().getObjective("dropperReloaded").getScore(ChatColor.values()[currentMapLine].toString()).setScore(currentMapLine);
    }

    private void setupMapLines(Player player) {
        List<Map> mapList = game.getMapList();
        for (int i = 0; i < mapList.size(); i++) {
            Team team = player.getScoreboard().registerNewTeam("dropperReloaded_game_map_" + i);
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
        player.getScoreboard().getTeam("dropperReloaded_game_currentMap").setPrefix(currentMapString);

        List<Map> mapList = game.getMapList();
        int currentMapIndex = playerSession.getCurrentMapCount();

        for (int i = 0; i < mapList.size(); i++) {
            Team mapTeam = player.getScoreboard().getTeam("dropperReloaded_game_map_" + i);
            Map map = mapList.get(i);
            String line = lines.get(configCurrentMapLineIndex + 1);

            line = line.replace("%map_name%", MapDifficultyColorPrefix.get(map.getDifficulty()) + map.getName())
                    .replace("%map_completed_symbol%", i + 1 < currentMapIndex ? mapCompletedSymbol : mapNotCompletedSymbol)
                    .replace("%in_this_map_symbol%", i + 1 == currentMapIndex ? inThisMapSymbol : "");

            mapTeam.setPrefix(line);
        }
    }

    public void updateTotalFails(Player player) {
        String line = lines.get(configTotalFailsLineIndex);
        PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
        line = line.replace("%total_fails%", String.valueOf(playerSession.getTotalFails()));
        player.getScoreboard().getTeam("dropperReloaded_game_line_" + lineTotalFails).setPrefix(formatLine(line));
    }

    public void updateTimeLeft() {
        String line = lines.get(configTimeLeftLineIndex);
        for(Player player : game.getPlayerList()) {
            player.getScoreboard().getTeam("dropperReloaded_game_line_" + lineTimeLeft).setPrefix(formatLine(line));
        }
    }
}