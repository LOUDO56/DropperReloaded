package fr.loudo.dropperReloaded.manager.maps;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Map {

    private String name;
    private List<Location> spawns;
    private MapDifficulty difficulty;

    public Map(String name) {
        this.name = name;
        this.spawns = new ArrayList<>();
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
}
