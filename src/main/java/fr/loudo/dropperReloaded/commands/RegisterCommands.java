package fr.loudo.dropperReloaded.commands;

import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.commands.dropper.DropperCommand;
import fr.loudo.dropperReloaded.commands.dropperadmin.DropperAdminCommand;

public class RegisterCommands {

    public static void register(DropperReloaded instance) {
        instance.getCommand("dropperadmin").setExecutor(new DropperAdminCommand());
        instance.getCommand("dropper").setExecutor(new DropperCommand());
    }

}
