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

    private static final String CONFIG_STRING = "games.guis.teleporter.";
    private static Material NEXT_PAGE_ITEM = Material.ARROW;
    private static Material PREVIOUS_PAGE_ITEM = Material.ARROW;
    private static int ITEMS_PER_PAGE = 24;

    private PlayerSession playerSession;
    private HashMap<Integer, Player> slotAndPlayers;
    private int page;

    public TeleportPlayerGui(Player player) {
        super(player, 9 * 6, DropperReloaded.getInstance().getConfig().getString(CONFIG_STRING + "name"));
        this.page = 1;
        this.slotAndPlayers = new HashMap<>();
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
        slotAndPlayers.clear();
        List<Player> playerList = playerSession.getPlayerGame().getPlayerList();
        int slot = 10;
        int totalPages = (int) Math.ceil((double) playerList.size() / ITEMS_PER_PAGE);
        FileConfiguration config = DropperReloaded.getInstance().getConfig();
        for(int i = (page - 1) * ITEMS_PER_PAGE; i < Math.min(playerList.size(), page * ITEMS_PER_PAGE); i++) {
            Player player = playerList.get(i);
            PlayerSession playerHeadSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
            if(!playerHeadSession.isSpectator() && !player.getDisplayName().equals(playerSession.getPlayer().getDisplayName())) {
                ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta itemMeta = (SkullMeta) playerHead.getItemMeta();
                itemMeta.setOwnerProfile(player.getPlayerProfile());
                playerHead.setItemMeta(itemMeta);
                List<String> description = DropperReloaded.getInstance().getConfig().getStringList(CONFIG_STRING + "items.player_head.description");
                description.replaceAll(s -> s.replace("%map_name%", playerHeadSession.getCurrentMap().getColoredName()));
                addItem(playerHead, slot, ChatColor.WHITE + player.getDisplayName(), description);
                slotAndPlayers.put(slot, player);
                slot++;
                if ((slot - 17) % 9 == 0) slot += 2;
            }

        }
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

    public HashMap<Integer, Player> getSlotAndPlayers() {
        return slotAndPlayers;
    }
}
