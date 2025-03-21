package fr.loudo.dropperReloaded.utils;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hologram {

    private final double defaultOffsetY = 0;
    private final double defaultlineGap = 0.3;

    private List<String> stringLines;
    private List<ArmorStand> armorStandList;
    private Location location;
    private double offsetY;
    private double lineGap;

    public Hologram(List<String> stringLines, Location location, double offsetY, double lineGap) {
        this.stringLines = stringLines;
        this.location = location;
        this.offsetY = offsetY;
        this.lineGap = lineGap;
        this.armorStandList = new ArrayList<>();
    }

    public Hologram(List<String> stringLines, Location location, double offsetY) {
        this.stringLines = stringLines;
        this.location = location;
        this.offsetY = offsetY;
        this.lineGap = defaultlineGap;
        this.armorStandList = new ArrayList<>();
    }

    public Hologram(List<String> stringLines, Location location) {
        this.stringLines = stringLines;
        this.location = location;
        this.offsetY = defaultOffsetY;
        this.lineGap = defaultlineGap;
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
    }

    public void update(List<String> newLines) {
        stringLines = newLines;
        List<String> reversedLines = new ArrayList<>(newLines);
        Collections.reverse(reversedLines);
        for(int i = 0; i < armorStandList.size(); i++) { //TODO: Change with newLines size bc if new line then new armorstand
            armorStandList.get(i).setCustomName(reversedLines.get(i));
        }
    }

    public boolean update(String newLine, int line) {
        ArmorStand armorStand = armorStandList.get(line);
        if(armorStand == null) return false;
        armorStand.setCustomName(newLine);
        return true;
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
