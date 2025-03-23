package fr.loudo.dropperReloaded.events;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.guis.mapVote.MapVoteListener;
import org.bukkit.plugin.PluginManager;

public class RegisterEvents {

    public static void register(PluginManager pluginManager, DropperReloaded instance) {
        pluginManager.registerEvents(new PlayerHurt(), instance);
        pluginManager.registerEvents(new PlayerKick(), instance);
        pluginManager.registerEvents(new FoodLevelChange(), instance);
        pluginManager.registerEvents(new BlockBreak(), instance);
        pluginManager.registerEvents(new BlockPlace(), instance);
        pluginManager.registerEvents(new InventoryClick(), instance);
        pluginManager.registerEvents(new DropItem(), instance);
        pluginManager.registerEvents(new PlayerInteract(), instance);
        pluginManager.registerEvents(new PlayerJoin(), instance);
        pluginManager.registerEvents(new PlayerLeave(), instance);
        pluginManager.registerEvents(new PortalEnter(), instance);
        pluginManager.registerEvents(new PortalPlayerEnter(), instance);
        pluginManager.registerEvents(new NPCHit(), instance);
        pluginManager.registerEvents(new NPCRightClick(), instance);

        //GUIS
        pluginManager.registerEvents(new MapVoteListener(), instance);
    }

}
