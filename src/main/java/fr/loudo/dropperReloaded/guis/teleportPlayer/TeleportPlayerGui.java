package fr.loudo.dropperReloaded.guis.teleportPlayer;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.guis.Gui;
import fr.loudo.dropperReloaded.maps.DropperMap;
import fr.loudo.dropperReloaded.players.PlayerSession;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.List;

public class TeleportPlayerGui extends Gui {

    private static final String CONFIG_STRING = "games.guis.teleporter_player.";
    private PlayerSession playerSession;

    public TeleportPlayerGui(Player player) {
        super(player, 9 * 6, DropperReloaded.getInstance().getConfig().getString(CONFIG_STRING + "name"));
        this.playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
        this.playerSession.setCurrentGui(this);
        loadConfigItems();
    }

    private void loadConfigItems() {
        FileConfiguration config = DropperReloaded.getInstance().getConfig();
        nextPageItem = Material.valueOf(config.getString("global.guis.items.next_page.item"));
        previousPageItem = Material.valueOf(config.getString("global.guis.items.previous_page.item"));
    }

    @Override
    public void showCurrentPage() {
        getInventory().clear();
        slotItems.clear();
        List<Player> playerList = playerSession.getPlayerGame().getPlayerList();
        int slot = 10;
        int totalPages = (int) Math.ceil((double) playerList.size() / itemsPerPage);
        FileConfiguration config = DropperReloaded.getInstance().getConfig();

        for(int i = (page - 1) * itemsPerPage; i < Math.min(playerList.size(), page * itemsPerPage); i++) {
            Player player = playerList.get(i);
            PlayerSession playerHeadSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
            if (!playerHeadSession.isSpectator() && !player.getDisplayName().equals(playerSession.getPlayer().getDisplayName())) {
                ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta itemMeta = (SkullMeta) playerHead.getItemMeta();
                itemMeta.setOwnerProfile(player.getPlayerProfile());
                playerHead.setItemMeta(itemMeta);
                List<String> description = config.getStringList(CONFIG_STRING + "items.player_head.description");
                description.replaceAll(s -> s.replace("%map_name%", playerHeadSession.getCurrentMap().getColoredName()));
                addItem(playerHead, slot, ChatColor.WHITE + player.getDisplayName(), description);
                slotItems.put(slot, player);
                slot++;
                if ((slot - 17) % 9 == 0) slot += 2;
            }
        }

        Material barrier = Material.valueOf(config.getString("global.guis.items.close.item"));
        addItem(barrier, 49, config.getString("global.guis.items.close.name"));
        if (page < totalPages) {
            addItem(nextPageItem, 53, config.getString("global.guis.items.next_page.name"));
        }
        if (page > 1) {
            addItem(previousPageItem, 45, config.getString("global.guis.items.previous_page.name"));
        }
    }
}

