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

    private final List<String> slots = DropperReloaded.getInstance().getConfig().getStringList("wait_lobby.scoreboard.slots");

    public WaitLobbyScoreboard(Game game) {
        this.game = game;
    }

    public void setup(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("test", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(MessageConfigUtils.get("wait_lobby.scoreboard.title"));

        String statusMessage = game.getGameStatus() == GameStatus.WAITING ? MessageConfigUtils.get("wait_lobby.scoreboard.game_state.waiting") : MessageConfigUtils.get("wait_lobby.scoreboard.game_state.starting");
        statusMessage = statusMessage.replace("%timer%", new SimpleDateFormat(MessageConfigUtils.get("wait_lobby.time_format")).format(game.getWaitLobby().getTimer()));

        String spaceString = " ";
        PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
        for (int i = 0; i < slots.size(); i++) {
            String slot = slots.get(i);
            if(slot.contains("{game_state}")) lineGameStatus = i;
            if(slot.contains("%current_player%")) linePlayerList = i;
            slot = slot.replace("%game_id%", String.valueOf(game.getId()));
            slot = slot.replace("%date%", new SimpleDateFormat("dd/MM/yy").format(new Date()));
            slot = slot.replace("%current_player%", String.valueOf(game.getPlayerList().size()));
            slot = slot.replace("%max_player%", String.valueOf(DropperReloaded.getWaitLobbyConfiguration().getMaxPlayer()));
            slot = slot.replace("{game_state}", statusMessage);
            slot = slot.replace("%player_map_vote_count%", String.valueOf(playerSession.getVoteCount()));
            slot = slot.replace("%website%", MessageConfigUtils.get("global.website"));
            slot = slot.replace("{space}", spaceString);
            Team team = scoreboard.registerNewTeam("line" + i);
            team.addEntry(ChatColor.values()[i].toString());
            team.setPrefix(slot);
            objective.getScore(ChatColor.values()[i].toString()).setScore(slots.size() - i);
            spaceString += " ";
        }
        player.setScoreboard(scoreboard);

    }

    public void updatePlayerList() {

        String playerListTemplate = slots.get(linePlayerList);
        playerListTemplate = playerListTemplate.replace("%current_player%", String.valueOf(game.getPlayerList().size()));
        playerListTemplate = playerListTemplate.replace("%max_player%", String.valueOf(DropperReloaded.getWaitLobbyConfiguration().getMaxPlayer()));

        for(Player player : game.getPlayerList()) {
            Team teamPlayerList = player.getScoreboard().getTeam("line" + linePlayerList);
            teamPlayerList.setPrefix(playerListTemplate);
        }
    }

    public void updateGameStatus() {

        String statusMessage = game.getGameStatus() == GameStatus.WAITING ? MessageConfigUtils.get("wait_lobby.scoreboard.game_state.waiting") : MessageConfigUtils.get("wait_lobby.scoreboard.game_state.starting");
        statusMessage = statusMessage.replace("%timer%", new SimpleDateFormat(MessageConfigUtils.get("wait_lobby.time_format")).format(game.getWaitLobby().getTimer()));

        for(Player player : game.getPlayerList()) {
            Team teamGameStatus = player.getScoreboard().getTeam("line" + lineGameStatus);
            teamGameStatus.setPrefix(statusMessage);
        }
    }
}
