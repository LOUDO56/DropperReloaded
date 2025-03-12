package fr.loudo.dropperReloaded.scoreboards;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.manager.games.Game;
import fr.loudo.dropperReloaded.manager.games.GameStatus;
import fr.loudo.dropperReloaded.manager.players.PlayerSession;
import fr.loudo.dropperReloaded.utils.MessageConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class WaitLobbyScoreboard {

    private Game game;
    private Scoreboard scoreboard;
    private Objective objective;

    public WaitLobbyScoreboard(Game game) {
        this.game = game;
    }

    public void update() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("wait_lobby", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(MessageConfigUtils.get("wait_lobby.scoreboard.title"));
        for(Player player : game.getPlayerList()) {
            String spaceString = " ";
            List<String> slots = DropperReloaded.getInstance().getConfig().getStringList("wait_lobby.scoreboard.slots");
            Collections.reverse(slots);
            PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
            for(int i = 0; i < slots.size(); i++) {
                String slot = slots.get(i);
                slot = slot.replace("%date%", new SimpleDateFormat("dd/MM/yy").format(new Date()));
                slot = slot.replace("%current_player%", String.valueOf(game.getPlayerList().size()));
                slot = slot.replace("%max_player%", String.valueOf(DropperReloaded.getWaitLobbyConfiguration().getMaxPlayer()));
                slot = slot.replace("{game_state}", game.getGameStatus() == GameStatus.WAITING ? MessageConfigUtils.get("wait_lobby.scoreboard.game_state.waiting") : MessageConfigUtils.get("wait_lobby.scoreboard.game_state.starting"));
                slot = slot.replace("%player_map_vote_count%", String.valueOf(playerSession.getVoteCount()));
                slot = slot.replace("%website%", MessageConfigUtils.get("global.website"));
                slot = slot.replace("{space}", spaceString);
                Score score = objective.getScore(slot);
                score.setScore(i);
                spaceString += " ";
                System.out.println(slot + " " + i);
            }
            player.setScoreboard(scoreboard);
        }
    }


}
