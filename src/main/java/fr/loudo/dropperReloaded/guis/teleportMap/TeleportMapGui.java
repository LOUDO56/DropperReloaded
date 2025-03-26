package fr.loudo.dropperReloaded.guis.teleportMap;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.guis.Gui;
import fr.loudo.dropperReloaded.maps.DropperMap;
import fr.loudo.dropperReloaded.players.PlayerSession;
import fr.loudo.dropperReloaded.utils.MessageConfigUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.List;

public class TeleportMapGui extends Gui {

    private static final String CONFIG_STRING = "games.guis.teleporter_map.";
    private PlayerSession playerSession;

    public TeleportMapGui(Player player) {
        super(player, 9 * 6, DropperReloaded.getInstance().getConfig().getString(CONFIG_STRING + "name"));
        this.slotItems = new HashMap<>();
        this.playerSession = DropperReloaded.getInstance().getPlayersSessionManager().getPlayerSession(player);
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
        List<DropperMap> mapList = playerSession.getPlayerGame().getMapList();
        int slot = 10;
        int totalPages = (int) Math.ceil((double) mapList.size() / itemsPerPage);
        FileConfiguration config = DropperReloaded.getInstance().getConfig();

        for (int i = (page - 1) * itemsPerPage; i < Math.min(mapList.size(), page * itemsPerPage); i++) {
            DropperMap dropperMap = mapList.get(i);
            Material material = Material.valueOf(config.getString(CONFIG_STRING + "items.dropper_map.item"));
            String name = config.getString(CONFIG_STRING + ".items.dropper_map.name").replace("%map_name%", dropperMap.getColoredName());
            List<String> description = config.getStringList(CONFIG_STRING + ".items.dropper_map.description");
            addItem(material, slot, name, description);
            slotItems.put(slot, dropperMap);
            slot++;
            if ((slot - 17) % 9 == 0) slot += 2;
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

