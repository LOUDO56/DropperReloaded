package fr.loudo.dropperReloaded.scoreboards;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.games.Game;
import fr.loudo.dropperReloaded.games.GameStatus;
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

public class WaitLobbyScoreboard {

    private Game game;

    private int lineGameStatus;
    private int linePlayerList;
    private int lineMapCount;

    private List<String> lines;

    public WaitLobbyScoreboard(Game game) {
        this.game = game;
    }

    public void setup(Player player) {
        lines = DropperReloaded.getInstance().getConfig().getStringList("wait_lobby.scoreboard.lines");
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("dropperReloaded", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(MessageConfigUtils.get("global.scoreboard_title"));

        // To get the correct time shown on the scoreboard of all players
        Date date = new Date(0);
        date.setTime(date.getTime() + game.getWaitLobby().getTimer() * 1000L + 1000L);
        String currentTimerWaitLobby = new SimpleDateFormat(MessageConfigUtils.get("wait_lobby.time_format")).format(date.getTime());

        String statusMessage = game.getGameStatus() == GameStatus.WAITING ? MessageConfigUtils.get("wait_lobby.scoreboard.game_state.waiting") : MessageConfigUtils.get("wait_lobby.scoreboard.game_state.starting");
        statusMessage = statusMessage.replace("%timer%", currentTimerWaitLobby);

        String spaceString = " ";
        PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
        for (int i = 0; i < lines.size(); i++) {
            String slot = lines.get(i);
            if(slot.contains("{game_state}")) lineGameStatus = i;
            if(slot.contains("%current_player%")) linePlayerList = i;
            if(slot.contains("%player_map_vote_count%")) lineMapCount = i;
            slot = slot.replace("%game_id%", String.valueOf(game.getId()));
            slot = slot.replace("%date%", new SimpleDateFormat("dd/MM/yy").format(new Date()));
            slot = slot.replace("%current_player%", String.valueOf(game.getPlayerList().size()));
            slot = slot.replace("%max_player%", String.valueOf(DropperReloaded.getWaitLobbyConfiguration().getMaxPlayer()));
            slot = slot.replace("{game_state}", statusMessage);
            slot = slot.replace("%player_map_vote_count%", String.valueOf(playerSession.getVoteCount()));
            slot = slot.replace("%website%", MessageConfigUtils.get("global.website"));
            slot = slot.replace("{space}", spaceString);
            Team team = scoreboard.registerNewTeam("line_" + i);
            team.addEntry(ChatColor.values()[i].toString());
            team.setPrefix(slot);
            objective.getScore(ChatColor.values()[i].toString()).setScore(lines.size() - i);
            spaceString += " ";
        }
        player.setScoreboard(scoreboard);

    }

    public void updatePlayerList() {

        String playerListTemplate = lines.get(linePlayerList);
        playerListTemplate = playerListTemplate.replace("%current_player%", String.valueOf(game.getPlayerList().size()));
        playerListTemplate = playerListTemplate.replace("%max_player%", String.valueOf(DropperReloaded.getWaitLobbyConfiguration().getMaxPlayer()));

        for(Player player : game.getPlayerList()) {
            Team teamPlayerList = player.getScoreboard().getTeam("line_" + linePlayerList);
            teamPlayerList.setPrefix(playerListTemplate);
        }
    }

    public void updateGameStatus() {

        String statusMessage = game.getGameStatus() == GameStatus.WAITING ? MessageConfigUtils.get("wait_lobby.scoreboard.game_state.waiting") : MessageConfigUtils.get("wait_lobby.scoreboard.game_state.starting");
        statusMessage = statusMessage.replace("%timer%", game.getWaitLobby().getTimeFormatted());

        for(Player player : game.getPlayerList()) {
            if(game.getGameStatus() != GameStatus.PLAYING) {
                Team teamGameStatus = player.getScoreboard().getTeam("line_" + lineGameStatus);
                teamGameStatus.setPrefix(statusMessage);
            }
        }
    }

    public void updateMapCount(Player player) {

        PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
        int mapVoteCount = DropperReloaded.getInstance().getConfig().getInt("wait_lobby.map_vote_count") - playerSession.getVotedMaps().size();
        String mapCountSting = lines.get(lineMapCount).replace("%player_map_vote_count%", String.valueOf(mapVoteCount));

        player.getScoreboard().getTeam("line_" + lineMapCount).setPrefix(mapCountSting);

    }
}
