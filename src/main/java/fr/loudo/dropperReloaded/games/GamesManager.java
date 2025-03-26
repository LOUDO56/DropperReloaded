package fr.loudo.dropperReloaded.games;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.players.PlayerSession;
import fr.loudo.dropperReloaded.players.PlayersSessionManager;
import fr.loudo.dropperReloaded.utils.MessageConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GamesManager {

    private final PlayersSessionManager playersSessionManager = DropperReloaded.getInstance().getPlayersSessionManager();

    private List<Game> gameList;

    public GamesManager() {
        this.gameList = new ArrayList<>();
    }

    public void joinGame(Player player) {
        if(!DropperReloaded.getInstance().getMapsManager().enoughMapsToPlay()) {
            if(player.hasPermission("dropper-reloaded.admin")) {
                player.sendMessage(ChatColor.RED + "There aren't enough maps to play with, respect the minimum number of enabled maps for each difficulty.");
            } else {
                player.sendMessage(ChatColor.RED + "Not enough maps, you can't play dropper for now.");
            }
            return;
        }
        PlayerSession playerSession = playersSessionManager.getPlayerSession(player);
        if(playerSession == null) {
            playerSession = new PlayerSession(player);
        } else {
            leaveGame(player);
        }
        playersSessionManager.getPlayerSessionList().add(playerSession);
        Game game = getAvalaibleGame();
        game.addPlayer(player);
        DropperReloaded.getInstance().getJoinGameNPCManager().updateNPCHologram();
    }

    public Game getAvalaibleGame() {
        Game game = gameList.stream()
                .filter(currentGame -> currentGame.getGameStatus() == GameStatus.WAITING || currentGame.getGameStatus() == GameStatus.STARTING)
                .max(Comparator.comparingInt((Game currentGame) -> currentGame.getPlayerList().size())
                        .thenComparing(currentGame -> currentGame.getGameStatus() == GameStatus.STARTING ? 0 : 1))
                .orElseGet(Game::new);

        if (!gameList.contains(game)) gameList.add(game);

        return game;


    }

    public boolean leaveGame(Player player) {
        PlayerSession playerSession = playersSessionManager.getPlayerSession(player);
        if(!playersSessionManager.getPlayerSessionList().contains(playerSession)) return false;

        playerSession.setInvincible(true);
        if(playerSession.getPlayerGame().hasStarted()) {
            playerSession.getDropperStats().setTotalLosses(playerSession.getDropperStats().getTotalLosses() + 1);
        }
        Location mainLobbySpawn = (Location) DropperReloaded.getInstance().getConfig().get("main_lobby.spawn");
        if(mainLobbySpawn != null) {
            player.teleport(mainLobbySpawn);
        } else {
            player.teleport(playerSession.getLastPlayerPos());
        }
        DropperReloaded.getInstance().getDatabase().updatePlayerStats(player, playerSession.getDropperStats());
        playerSession.getPlayerGame().removePlayer(player);
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        player.sendMessage(MessageConfigUtils.get("player.left_game"));
        player.getInventory().clear();
        if(DropperReloaded.getInstance().getConfig().getBoolean("global.restore_items_on_leave")) {
            playerSession.restoreOldItems();
        }
        player.closeInventory();
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        playerSession.reset();
        new BukkitRunnable() {
            @Override
            public void run() {
                playersSessionManager.getPlayerSessionList().remove(playerSession);
                DropperReloaded.getInstance().getJoinGameNPCManager().updateNPCHologram();
            }
        }.runTaskLater(DropperReloaded.getInstance(), 5L);

        return true;
    }



    public List<Game> getGameList() {
        return gameList;
    }

}
