package fr.loudo.dropperReloaded.maps;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {

    private String name;
    private List<Location> spawns;
    private List<Location> doorLocations;
    private MapDifficulty difficulty;
    private boolean isEnabled;

    public Map(String name) {
        this.name = name;
        this.spawns = new ArrayList<>();
        this.doorLocations = new ArrayList<>();
        this.difficulty = MapDifficulty.EASY;
        this.isEnabled = false;
    }

    public Map(String name, List<Location> spawns, MapDifficulty difficulty, boolean isEnabled) {
        this.name = name;
        this.spawns = spawns;
        this.difficulty = difficulty;
        this.isEnabled = isEnabled;
    }

    public boolean addSpawn(Location location) {
        if(spawns.contains(location)) return false;

        spawns.add(location);
        return true;
    }

    public boolean removeLastSpawn() {
        if(spawns.isEmpty()) return false;

        spawns.remove(spawns.size() - 1);
        return true;
    }

    public Location getRandomSpawn() {
        return spawns.get(new Random().nextInt(spawns.size()));
    }

    public MapDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(MapDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Location> getSpawns() {
        return spawns;
    }

    public List<Location> getDoorLocations() {
        return doorLocations;
    }

    public void setDoorLocations(List<Location> doorLocations) {
        this.doorLocations = doorLocations;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public String toString() {
        return "Map{" +
                "name='" + name + '\'' +
                ", spawns=" + spawns +
                ", difficulty=" + difficulty +
                '}';
    }
}
