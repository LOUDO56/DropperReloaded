package fr.loudo.dropperReloaded.players;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayersSessionManager {

    private List<PlayerSession> playerSessionList;

    public PlayersSessionManager() {
        this.playerSessionList = new ArrayList<>();
    }

    public PlayerSession getPlayerSession(Player player) {
        for(PlayerSession playerSession : playerSessionList) {
            if(playerSession.getPlayer().getDisplayName().equals(player.getDisplayName())) {
                return playerSession;
            }
        }
        return null;
    }

    public boolean isPlaying(Player player) {
        for(PlayerSession playerSession : playerSessionList) {
            if(playerSession.getPlayer().getDisplayName().equals(player.getDisplayName())) {
                return true;
            }
        }
        return false;
    }

    public List<PlayerSession> getPlayerSessionList() {
        return playerSessionList;
    }
}
