package fr.loudo.dropperReloaded.items;

import fr.loudo.dropperReloaded.DropperReloaded;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class DropperItems {

    //Admin
    public static DropperItem stickWand;

    // Lobby
    public static DropperItem mapVote;
    public static DropperItem leaveBed;

    // In-Game
    public static DropperItem resetLocation;
    public static DropperItem playerVisibilityOn;
    public static DropperItem playerVisibilityOff;

    //Spectator
    public static DropperItem spectatorPlayerList;
    public static DropperItem viewMap;
    public static DropperItem playAgain;

    public static void registerItems() {
        stickWand = new DropperItem(
                -1,
                ChatColor.GREEN
                        + "DropperReloaded WAND - "
                        + ChatColor.WHITE + "Pos 1 LEFT"
                        + ChatColor.GREEN + " - "
                        + ChatColor.WHITE + "Pos 2 RIGHT",
                new ArrayList<>(),
                Material.STICK
        );

        mapVote = getItem("wait_lobby", "map_vote");
        leaveBed = getItem("wait_lobby", "leave");

        resetLocation = getItem("games", "reset_location");
        playerVisibilityOn = getItem("games", "player_visibility_on");
        playerVisibilityOff = getItem("games", "player_visibility_off");

        spectatorPlayerList = getItem("games", "spectator_player_list");
        viewMap = getItem("games", "view_map");
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
