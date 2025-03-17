package fr.loudo.dropperReloaded.waitlobby;

import fr.loudo.dropperReloaded.DropperReloaded;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class WaitLobbyConfiguration {

    //TODO: fix reload config
    private int minPlayer;
    private int maxPlayer;
    private Location spawn;

    public WaitLobbyConfiguration() {
        FileConfiguration configuration = DropperReloaded.getInstance().getConfig();
        this.minPlayer = configuration.getInt("wait_lobby.min_player");
        this.maxPlayer = configuration.getInt("wait_lobby.max_player");
        this.spawn = (Location) configuration.get("wait_lobby.spawn");
    }

    public void reload() {
        FileConfiguration configuration = DropperReloaded.getInstance().getConfig();
        this.minPlayer = configuration.getInt("wait_lobby.min_player");
        this.maxPlayer = configuration.getInt("wait_lobby.max_player");
        this.spawn = (Location) configuration.get("wait_lobby.spawn");
    }

    public int getMinPlayer() {
        return minPlayer;
    }

    public void setMinPlayer(int minPlayer) {
        this.minPlayer = minPlayer;
        FileConfiguration configuration = DropperReloaded.getInstance().getConfig();
        configuration.set("wait_lobby.min_player", minPlayer);
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
        FileConfiguration configuration = DropperReloaded.getInstance().getConfig();
        configuration.set("wait_lobby.max_player", maxPlayer);
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
        FileConfiguration configuration = DropperReloaded.getInstance().getConfig();
        configuration.set("wait_lobby.spawn", spawn);
    }

}
