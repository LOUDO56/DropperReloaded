package fr.loudo.dropperReloaded.utils;

import fr.loudo.dropperReloaded.DropperReloaded;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CheckVersion {

    private static String latestVersion;
    private static boolean newVersionAvailable = false;

    public static void verify() throws IOException, ParseException {

        DropperReloaded.getInstance().getLogger().info("Checking for new update...");

        URL url = new URL("https://api.github.com/repos/LOUDO56/DropperReloaded/releases/latest");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        int status = conn.getResponseCode();

        if(status != 200) {
            DropperReloaded.getInstance().getLogger().severe("Github API down, can't check new update.");
            return;
        }

        StringBuilder inline = new StringBuilder();
        Scanner scanner = new Scanner(url.openStream());

        while(scanner.hasNext()) {
            inline.append(scanner.nextLine());
        }

        JSONParser parse = new JSONParser();
        JSONObject dataObj = (JSONObject) parse.parse(inline.toString());
        latestVersion = ((String) dataObj.get("tag_name")).replace("v", "");

        if(!DropperReloaded.getInstance().getDescription().getVersion().equals(latestVersion)) {
            newVersionAvailable = true;
            DropperReloaded.getInstance().getLogger().info("New update available: " + latestVersion);
        } else {
            DropperReloaded.getInstance().getLogger().info("No update available.");
        }

    }

    public static void notifyPlayer(Player player) {
        if(newVersionAvailable) player.sendMessage("[DropperReloaded] §aDropperReloaded has a new update available: §e" + latestVersion + "§a. Download it here: §ehttps://github.com/LOUDO56/DropperReloaded/releases");
    }

}