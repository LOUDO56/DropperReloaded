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
    private static Material NEXT_PAGE_ITEM = Material.ARROW;
    private static Material PREVIOUS_PAGE_ITEM = Material.ARROW;
    private static int ITEMS_PER_PAGE = 24;

    private PlayerSession playerSession;
    private HashMap<Integer, DropperMap> slotAndDropperMaps;
    private int page;

    public TeleportMapGui(Player player) {
        super(player, 9 * 6, DropperReloaded.getInstance().getConfig().getString(CONFIG_STRING + "name"));
        this.page = 1;
        this.slotAndDropperMaps = new HashMap<>();
        this.playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
        this.playerSession.setCurrentGui(this);
    }

    private void initItemCongig() {
        FileConfiguration config = DropperReloaded.getInstance().getConfig();
        NEXT_PAGE_ITEM = Material.valueOf(config.getString("global.guis.items.next_page.item"));
        PREVIOUS_PAGE_ITEM = Material.valueOf(config.getString("global.guis.items.previous_page.item"));
    }

    public void showCurrentPage() {
        initItemCongig();
        getInventory().clear();
        slotAndDropperMaps.clear();
        List<DropperMap> mapList = playerSession.getPlayerGame().getMapList();
        int slot = 10;
        int totalPages = (int) Math.ceil((double) mapList.size() / ITEMS_PER_PAGE);
        FileConfiguration config = DropperReloaded.getInstance().getConfig();
        for(int i = (page - 1) * ITEMS_PER_PAGE; i < Math.min(mapList.size(), page * ITEMS_PER_PAGE); i++) {
            DropperMap dropperMap = mapList.get(i);
            Material material = Material.valueOf(config.getString(CONFIG_STRING + "items.dropper_map.item"));
            String name = config.getString(CONFIG_STRING + ".items.dropper_map.name");
            name = name.replace("%map_name%", dropperMap.getColoredName());
            List<String> description = config.getStringList(CONFIG_STRING + ".items.dropper_map.description");
            addItem(material, slot, name, description);
            slotAndDropperMaps.put(slot, dropperMap);
            slot++;
            if ((slot - 17) % 9 == 0) slot += 2;

        }
        Material barrier = Material.valueOf(config.getString("global.guis.items.close.item"));
        addItem(barrier, 49, config.getString("global.guis.items.close.name"));
        if(page < totalPages) {
            addItem(NEXT_PAGE_ITEM, 53, config.getString("global.guis.items.next_page.name"));
        }

        if(page > 1) {
            addItem(PREVIOUS_PAGE_ITEM, 45, config.getString("global.guis.items.previous_page.name"));
        }
    }

    public void nextPage() {
        page++;
        showCurrentPage();
    }

    public void previousPage() {
        page--;
        showCurrentPage();
    }

    public HashMap<Integer, DropperMap> getSlotAndDropperMaps() {
        return slotAndDropperMaps;
    }
}
