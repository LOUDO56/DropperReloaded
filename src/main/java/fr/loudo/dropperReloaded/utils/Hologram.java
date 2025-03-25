package fr.loudo.dropperReloaded.utils;

import fr.loudo.dropperReloaded.DropperReloaded;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hologram {

    private List<String> stringLines;
    private List<ArmorStand> armorStandList;
    private Location location;
    private double offsetY;
    private double lineGap;

    public Hologram(List<String> stringLines, Location location) {
        this.stringLines = stringLines;
        this.location = location;
        this.offsetY = DropperReloaded.getInstance().getConfig().getDouble("main_lobby.npc.hologram.offset_y");
        this.lineGap = DropperReloaded.getInstance().getConfig().getDouble("main_lobby.npc.hologram.line_gap");
        this.armorStandList = new ArrayList<>();
    }

    public void spawn() {
        List<String> reversedLines = new ArrayList<>(stringLines);
        Collections.reverse(reversedLines);
        double currentY = location.getY() - 0.2 + offsetY;
        for(String line : reversedLines) {
            instantiateArmorStand(currentY, line);
            currentY += lineGap;
        }

    }

    public void remove() {
        for(ArmorStand armorStand : armorStandList) {
            armorStand.remove();
        }
        armorStandList = new ArrayList<>();
        stringLines = new ArrayList<>();
    }

    public void update(List<String> newLines) {
        stringLines = newLines;
        List<String> reversedLines = new ArrayList<>(newLines);
        Collections.reverse(reversedLines);
        double currentY = location.getY() - 0.2 + offsetY;
        if(newLines.size() < armorStandList.size()) {
            remove();
        }
        for(int i = 0; i < newLines.size(); i++) {
            if(i < armorStandList.size()) {
                ArmorStand armorStand = armorStandList.get(i);
                if(armorStand.isDead()) {
                    instantiateArmorStand(currentY, reversedLines.get(i));
                } else {
                    update(reversedLines.get(i), i);
                    armorStand.teleport(new Location(location.getWorld(), location.getX(), currentY, location.getZ()));
                }
            } else {
                instantiateArmorStand(currentY, reversedLines.get(i));
            }
            currentY += lineGap;
        }
    }

    public void update(String newLine, int line) {
        ArmorStand armorStand = armorStandList.get(line);
        if(armorStand == null) return;
        armorStand.setCustomName(newLine);
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public void setLineGap(double lineGap) {
        this.lineGap = lineGap;
    }

    private void instantiateArmorStand(double y, String line) {
        Location armorStandLoc = new Location(location.getWorld(), location.getX(), y, location.getZ());
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(armorStandLoc, EntityType.ARMOR_STAND);
        armorStand.setInvisible(true);
        armorStand.setInvulnerable(true);
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(false);
        armorStand.setBasePlate(false);
        armorStand.setCustomName(line);
        armorStandList.add(armorStand);
    }

}
