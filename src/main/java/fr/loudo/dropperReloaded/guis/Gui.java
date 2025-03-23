package fr.loudo.dropperReloaded.guis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Gui {

    private Inventory inventory;
    private Player player;

    public Gui(Player player, int size, String name) {
        this.inventory = Bukkit.createInventory(player, size, name);
        this.player = player;
    }

    public void open() {
        player.openInventory(inventory);
    }

    public void close() {
        player.closeInventory();
    }

    public void addItem(Material material, int slot, String name, List<String> description) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(description);
        item.setItemMeta(itemMeta);
        inventory.setItem(slot, item);
    }

    public void addItem(Material material, int slot, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        inventory.setItem(slot, item);
    }

    public void addItem(ItemStack item, int slot, String name, List<String> description) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(description);
        item.setItemMeta(itemMeta);
        inventory.setItem(slot, item);
    }

    public void addItem(ItemStack item, int slot, String name) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        inventory.setItem(slot, item);
    }



    public ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }

    public Inventory getInventory() {
        return inventory;
    }
}
