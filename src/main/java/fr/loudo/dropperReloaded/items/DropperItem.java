package fr.loudo.dropperReloaded.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class DropperItem {

    private int slot;
    private ItemStack item;

    public DropperItem(int slot, String name, List<String> description, Material material) {
        this.slot = slot;
        this.item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(description);
        item.setItemMeta(itemMeta);
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItem() {
        return item;
    }
}
