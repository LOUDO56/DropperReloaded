package fr.loudo.dropperReloaded.waitlobby;

import fr.loudo.dropperReloaded.DropperReloaded;
import org.bukkit.Location;

public class WaitLobbyConfiguration {

    private DropperReloaded instance;
    private int minPlayer;
    private int maxPlayer;
    private Location spawn;

    public WaitLobbyConfiguration(DropperReloaded instance) {
        this.instance = instance;
        this.minPlayer = instance.getConfig().getInt("wait_lobby.min_player");
        this.maxPlayer = instance.getConfig().getInt("wait_lobby.max_player");
        this.spawn = (Location) instance.getConfig().get("wait_lobby.spawn");
    }

    public int getMinPlayer() {
        return minPlayer;
    }

    public void setMinPlayer(int minPlayer) {
        this.minPlayer = minPlayer;
        instance.getConfig().set("wait_lobby.min_player", minPlayer);
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
        instance.getConfig().set("wait_lobby.max_player", maxPlayer);
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
        instance.getConfig().set("wait_lobby.spawn", spawn);
    }

}
