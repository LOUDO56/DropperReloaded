package fr.loudo.dropperReloaded.guis.mapVote;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.games.Game;
import fr.loudo.dropperReloaded.maps.DropperMap;
import fr.loudo.dropperReloaded.maps.DropperMapDifficulty;
import fr.loudo.dropperReloaded.maps.DropperMapDifficultyColorPrefix;
import fr.loudo.dropperReloaded.players.PlayerSession;
import fr.loudo.dropperReloaded.guis.Gui;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class MapVoteGui extends Gui {

    private static final String CONFIG_STRING = "wait_lobby.guis.map_voting.";
    private Material easyItem, mediumItem, hardItem;
    private PlayerSession playerSession;

    public MapVoteGui(Player player) {
        super(player, 9 * 6, DropperReloaded.getInstance().getConfig().getString(CONFIG_STRING + "name"));
        this.slotItems = new HashMap<>();
        this.playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
        this.playerSession.setCurrentGui(this);
        loadConfigItems();
    }

    private void loadConfigItems() {
        FileConfiguration config = DropperReloaded.getInstance().getConfig();
        easyItem = Material.valueOf(config.getString(CONFIG_STRING + "items.map.item.easy_map"));
        mediumItem = Material.valueOf(config.getString(CONFIG_STRING + "items.map.item.medium_map"));
        hardItem = Material.valueOf(config.getString(CONFIG_STRING + "items.map.item.hard_map"));
        nextPageItem = Material.valueOf(config.getString("global.guis.items.next_page.item"));
        previousPageItem = Material.valueOf(config.getString("global.guis.items.previous_page.item"));
    }

    @Override
    public void showCurrentPage() {
        getInventory().clear();
        slotItems.clear();
        List<DropperMap> dropperMaps = DropperReloaded.getMapsManager().getMapsSortedDifficulty();
        int slot = 10;
        int totalPages = (int) Math.ceil((double) dropperMaps.size() / itemsPerPage);
        FileConfiguration config = DropperReloaded.getInstance().getConfig();

        for (int i = (page - 1) * itemsPerPage; i < Math.min(dropperMaps.size(), page * itemsPerPage); i++) {
            DropperMap dropperMap = dropperMaps.get(i);
            if(dropperMap.isEnabled()) {
                updateItemMap(slot, dropperMap);
                slotItems.put(slot, dropperMap);
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

    public void updateItemMap(int slot, DropperMap dropperMap) {
        FileConfiguration config = DropperReloaded.getInstance().getConfig();
        String name = DropperMapDifficultyColorPrefix.get(dropperMap.getDifficulty()) + dropperMap.getDisplayName();
        List<String> description = config.getStringList(CONFIG_STRING + "items.map.description");
        description.replaceAll(s -> s.replace("%map_difficulty%", DropperMapDifficultyColorPrefix.get(dropperMap.getDifficulty()) + dropperMap.getDifficulty().name()));

        Material itemMaterial;
        switch (dropperMap.getDifficulty()) {
            case EASY:
                itemMaterial = easyItem;
                break;
            case MEDIUM:
                itemMaterial = mediumItem;
                break;
            case HARD:
                itemMaterial = hardItem;
                break;
            default:
                itemMaterial = easyItem;
                break;
        }


        ItemStack item = new ItemStack(itemMaterial);
        List<DropperMap> votedDropperMaps = playerSession.getVotedMaps();

        if (!votedDropperMaps.contains(dropperMap) && playerSession.getVotedMaps().size() >= config.getInt("wait_lobby.map_vote_count")) {
            description.addAll(config.getStringList(CONFIG_STRING + "items.map.extra.no_remaining_vote"));
        } else if (votedDropperMaps.contains(dropperMap)) {
            description.addAll(config.getStringList(CONFIG_STRING + "items.map.extra.voted"));
            item.addUnsafeEnchantment(Enchantment.DURABILITY, 2);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(itemMeta);
        } else {
            description.addAll(config.getStringList(CONFIG_STRING + "items.map.extra.vote"));
        }

        addItem(item, slot, name, description);
    }
}


