package fr.loudo.dropperReloaded.events;

import fr.loudo.dropperReloaded.DropperReloaded;
import org.bukkit.plugin.PluginManager;

public class RegisterEvents {

    public static void register(PluginManager pluginManager, DropperReloaded instance) {
        pluginManager.registerEvents(new PlayerHurt(), instance);
        pluginManager.registerEvents(new FoodLevelChange(), instance);
        pluginManager.registerEvents(new BlockBreak(), instance);
        pluginManager.registerEvents(new BlockPlace(), instance);
    }

}
