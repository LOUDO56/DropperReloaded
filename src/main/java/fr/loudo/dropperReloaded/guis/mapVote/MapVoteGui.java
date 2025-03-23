package fr.loudo.dropperReloaded.guis.mapVote;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.games.Game;
import fr.loudo.dropperReloaded.maps.Map;
import fr.loudo.dropperReloaded.maps.MapDifficulty;
import fr.loudo.dropperReloaded.maps.MapDifficultyColorPrefix;
import fr.loudo.dropperReloaded.players.PlayerSession;
import fr.loudo.dropperReloaded.utils.Gui;
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
    private static Material EASY_ITEM = Material.LIME_TERRACOTTA;
    private static Material MEDIUM_ITEM = Material.YELLOW_TERRACOTTA;
    private static Material HARD_ITEM = Material.RED_TERRACOTTA;
    private static Material NEXT_PAGE_ITEM = Material.ARROW;
    private static Material PREVIOUS_PAGE_ITEM = Material.ARROW;
    private static int ITEMS_PER_PAGE = 24;

    private Game game;
    private PlayerSession playerSession;
    private HashMap<Integer, Map> slotAndMaps;
    private int page;

    public MapVoteGui(Player player, Game game) {
        super(player, 9 * 6, DropperReloaded.getInstance().getConfig().getString(CONFIG_STRING + "name"));
        this.game = game;
        this.page = 1;
        this.slotAndMaps = new HashMap<>();
        this.playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
        this.playerSession.setCurrentGui(this);
    }

    private void initItemCongig() {
        FileConfiguration config = DropperReloaded.getInstance().getConfig();
        EASY_ITEM = Material.valueOf(config.getString(CONFIG_STRING + "items.map.item.easy_map"));
        MEDIUM_ITEM = Material.valueOf(config.getString(CONFIG_STRING + "items.map.item.medium_map"));
        HARD_ITEM = Material.valueOf(config.getString(CONFIG_STRING + "items.map.item.hard_map"));
        HARD_ITEM = Material.valueOf(config.getString(CONFIG_STRING + "items.map.item.hard_map"));
        NEXT_PAGE_ITEM = Material.valueOf(config.getString(CONFIG_STRING + "items.next_page.item"));
        PREVIOUS_PAGE_ITEM = Material.valueOf(config.getString(CONFIG_STRING + "items.previous_page.item"));
    }

    public void showCurrentPage() {
        initItemCongig();
        getInventory().clear();
        slotAndMaps.clear();
        List<Map> maps = DropperReloaded.getMapsManager().getMapsSortedDifficulty();
        int slot = 10;
        int totalPages = (int) Math.ceil((double) maps.size() / ITEMS_PER_PAGE);
        FileConfiguration config = DropperReloaded.getInstance().getConfig();
        for(int i = (page - 1) * ITEMS_PER_PAGE; i < Math.min(maps.size(), page * ITEMS_PER_PAGE); i++) {
            Map map = maps.get(i);
            updateItemMap(slot, map);
            slotAndMaps.put(slot, map);
            slot++;
            if ((slot - 17) % 9 == 0) slot += 2;

        }
        if(page < totalPages) {
            addItem(NEXT_PAGE_ITEM, 53, config.getString(CONFIG_STRING + "items.next_page.name"));
        }

        if(page > 1) {
            addItem(PREVIOUS_PAGE_ITEM, 45, config.getString(CONFIG_STRING + "items.previous_page.name"));
        }
    }

    public void updateItemMap(int slot, Map map) {
        initItemCongig();
        FileConfiguration config = DropperReloaded.getInstance().getConfig();
        ItemStack item;
        String name = MapDifficultyColorPrefix.get(map.getDifficulty()) + map.getName();
        List<String> description = config.getStringList(CONFIG_STRING + "items.map.description");
        description.replaceAll(s -> s.replace("%map_difficulty%", MapDifficultyColorPrefix.get(map.getDifficulty()) + map.getDifficulty().name()));

        if(map.getDifficulty() == MapDifficulty.EASY) {
            item = new ItemStack(EASY_ITEM);
        } else if(map.getDifficulty() == MapDifficulty.MEDIUM) {
            item = new ItemStack(MEDIUM_ITEM);
        } else if(map.getDifficulty() == MapDifficulty.HARD) {
            item = new ItemStack(HARD_ITEM);
        } else {
            item = new ItemStack(EASY_ITEM);
        }

        List<Map> votedMaps = playerSession.getVotedMaps();
        if(!votedMaps.contains(map) && playerSession.getVotedMaps().size() >= config.getInt("wait_lobby.map_vote_count")) {
            description.addAll(config.getStringList(CONFIG_STRING + "items.map.extra.no_remaining_vote"));
        } else if(votedMaps.contains(map)) {
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

    public void nextPage() {
        page++;
        showCurrentPage();
    }

    public void previousPage() {
        page--;
        showCurrentPage();
    }

    public HashMap<Integer, Map> getSlotAndMaps() {
        return slotAndMaps;
    }
}
