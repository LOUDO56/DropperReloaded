package fr.loudo.dropperReloaded.items;

import fr.loudo.dropperReloaded.DropperReloaded;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class DropperItems {

    // Lobby
    public static DropperItem mapVote;
    public static DropperItem leaveBed;

    // In-Game
    public static DropperItem resetLocation;
    public static DropperItem playerVisibilityOn;
    public static DropperItem playerVisibilityOff;

    //Spectator
    public static DropperItem spectatorPlayerList;
    public static DropperItem playAgain;

    public static void registerItems() {
        mapVote = getItem("wait_lobby", "map_vote");
        leaveBed = getItem("wait_lobby", "leave");

        resetLocation = getItem("games", "reset_location");
        playerVisibilityOn = getItem("games", "player_visibility_on");
        playerVisibilityOff = getItem("games", "player_visibility_off");

        spectatorPlayerList = getItem("games", "spectator_player_list");
        playAgain = getItem("games", "play_again");
    }

    private static DropperItem getItem(String type, String section) {
        FileConfiguration configuration = DropperReloaded.getInstance().getConfig();
        return new DropperItem(
                configuration.getInt(type + ".items." + section + ".slot"),
                configuration.getString(type + ".items." + section + ".name"),
                configuration.getStringList(type + ".items." + section + ".description"),
                Material.valueOf(configuration.getString(type + ".items." + section + ".material"))
        );
    }



}
