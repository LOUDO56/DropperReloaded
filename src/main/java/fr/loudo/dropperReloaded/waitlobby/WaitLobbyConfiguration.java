package fr.loudo.dropperReloaded.waitlobby;

import fr.loudo.dropperReloaded.DropperReloaded;
import org.bukkit.Location;

public class WaitLobbyConfiguration {

    public int getMinPlayer() {
        return DropperReloaded.getInstance().getConfig().getInt("wait_lobby.min_player");
    }

    public void setMinPlayer(int minPlayer) {
        DropperReloaded.getInstance().getConfig().set("wait_lobby.min_player", minPlayer);
        DropperReloaded.getInstance().saveConfig();
    }

    public int getMaxPlayer() {
        return DropperReloaded.getInstance().getConfig().getInt("wait_lobby.max_player");
    }

    public void setMaxPlayer(int maxPlayer) {
        DropperReloaded.getInstance().getConfig().set("wait_lobby.max_player", maxPlayer);
        DropperReloaded.getInstance().saveConfig();
    }

    public Location getSpawn() {
        return (Location) DropperReloaded.getInstance().getConfig().get("wait_lobby.spawn");
    }

    public void setSpawn(Location spawn) {
        DropperReloaded.getInstance().getConfig().set("wait_lobby.spawn", spawn);
        DropperReloaded.getInstance().saveConfig();
    }

}
