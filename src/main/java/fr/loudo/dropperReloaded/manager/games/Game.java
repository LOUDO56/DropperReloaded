package fr.loudo.dropperReloaded.manager.games;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.manager.maps.Map;
import fr.loudo.dropperReloaded.manager.waitlobby.WaitLobby;
import fr.loudo.dropperReloaded.manager.waitlobby.WaitLobbyConfiguration;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final WaitLobbyConfiguration WAIT_LOBBY_CONFIGURATION = DropperReloaded.getWaitLobbyConfiguration();

    private int id;
    private List<Player> playerList;
    private List<Map> mapList;
    private GameStatus gameStatus;
    private WaitLobby waitLobby;

    public Game() {
        this.playerList = new ArrayList<>();
        this.mapList = new ArrayList<>();
        this.gameStatus = GameStatus.WAITING;
        this.waitLobby = new WaitLobby(this);
        this.id = DropperReloaded.getGamesManager().getGameList().size();
    }

    public boolean addPlayer(Player player) {
        if(playerList.contains(player) && playerList.size() >= WAIT_LOBBY_CONFIGURATION.getMaxPlayer()) return false;
        playerList.add(player);
        if(!hasStarted()) {
            player.teleport(WAIT_LOBBY_CONFIGURATION.getSpawn());
            waitLobby.getWaitLobbyScoreboard().setup(player);
            waitLobby.playerJoinedMessage(player.getDisplayName());
        }
        return true;
    }

    public boolean removePlayer(Player player) {
        if(!playerList.contains(player) && playerList.isEmpty()) return false;
        playerList.remove(player);
        if(!hasStarted()) {
            waitLobby.playerLeftMessage(player.getDisplayName());
        }
        return true;
    }

    public void start() {
        gameStatus = GameStatus.PLAYING;
        mapList = DropperReloaded.getMapsManager().getRandomMaps();
        sendMessageToPlayers("Map chosen: " + mapList);
    }

    public void stop() {
        gameStatus = GameStatus.ENDED;
    }

    public void sendMessageToPlayers(String message) {
        for(Player player : playerList) {
            player.sendMessage(message);
        }
    }

    public void playSoundToPlayers(Sound sound) {
        for(Player player : playerList) {
            player.playSound(player.getLocation(), sound, 1, 1);
        }
    }

    public void sendTitleToPlayers(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if(DropperReloaded.isIsProtocolLibPluginEnabled()) {
            PacketContainer titleTimesPacket;
            PacketContainer titlePacket;
            PacketContainer subtitlePacket;

            if(DropperReloaded.isNewerVersion()) {
                titlePacket = new PacketContainer(PacketType.Play.Server.SET_TITLE_TEXT);
                titlePacket.getChatComponents().write(0, WrappedChatComponent.fromText(title));

                subtitlePacket = new PacketContainer(PacketType.Play.Server.SET_SUBTITLE_TEXT);
                subtitlePacket.getChatComponents().write(0, WrappedChatComponent.fromText(subtitle));

                titleTimesPacket = new PacketContainer(PacketType.Play.Server.SET_TITLES_ANIMATION);
                titleTimesPacket.getIntegers()
                        .write(0, fadeIn)
                        .write(1, stay)
                        .write(2, fadeOut);
            } else {

                titleTimesPacket = new PacketContainer(PacketType.Play.Server.TITLE);
                titleTimesPacket.getTitleActions().write(0, EnumWrappers.TitleAction.TIMES);
                titleTimesPacket.getIntegers()
                        .write(0, fadeIn)
                        .write(1, stay)
                        .write(2, fadeOut);

                titlePacket = new PacketContainer(PacketType.Play.Server.TITLE);
                titlePacket.getTitleActions().write(0, EnumWrappers.TitleAction.TITLE);
                titlePacket.getChatComponents().write(0, WrappedChatComponent.fromText(title));

                subtitlePacket = new PacketContainer(PacketType.Play.Server.TITLE);
                subtitlePacket.getTitleActions().write(0, EnumWrappers.TitleAction.SUBTITLE);
                subtitlePacket.getChatComponents().write(0, WrappedChatComponent.fromText(subtitle));

            }
            for(Player player : playerList) {
                DropperReloaded.getProtocolManager().sendServerPacket(player, titleTimesPacket);
                DropperReloaded.getProtocolManager().sendServerPacket(player, titlePacket);
                DropperReloaded.getProtocolManager().sendServerPacket(player, subtitlePacket);
            }
        } else {
            for(Player player : playerList) {
                player.sendTitle(title, subtitle);
            }
        }
    }

    public boolean hasStarted() {
        return gameStatus == GameStatus.PLAYING;
    }

    public int getId() {
        return id;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public List<Map> getMapList() {
        return mapList;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public WaitLobby getWaitLobby() {
        return waitLobby;
    }
}
