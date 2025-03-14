package fr.loudo.dropperReloaded.manager.maps;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.utils.LocationDeserializer;
import fr.loudo.dropperReloaded.utils.LocationSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MapsManager {

    private final File DROPPER_MAPS_FILE = new File(DropperReloaded.getInstance().getDataFolder(), "dropper_maps.json");

    private List<Map> mapList;

    public MapsManager() {
        this.mapList = new ArrayList<>();
        try {
            deserialize();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't load dropper maps: " + e);
        }
    }

    public boolean addMap(Map map) {
        if(mapList.contains(map) || mapExists(map.getName())) return false;

        mapList.add(map);
        return true;
    }

    public boolean removeMap(Map map) {
        if(!mapList.contains(map)) return false;

        mapList.remove(map);
        return true;
    }

    public boolean removeMap(String mapName) {
        for(Map map : mapList) {
            if(map.getName().equalsIgnoreCase(mapName)) {
                mapList.remove(map);
                return true;
            }
        }
        return false;
    }

    public boolean mapExists(String mapName) {
        for(Map map : mapList) {
            if(map.getName().equalsIgnoreCase(mapName)) {
                return true;
            }
        }
        return false;
    }

    public Map getFromName(String mapName) {
        for(Map map : mapList) {
            if(map.getName().equalsIgnoreCase(mapName)) {
                return map;
            }
        }
        return null;
    }

    public List<String> getListMapName() {
        List<String> mapsName = new ArrayList<>();
        for(Map map : mapList) {
            mapsName.add(map.getName());
        }
        return mapsName;
    }

    public List<Map> getMapList() {
        return mapList;
    }

    //TODO: get map from players votes
    public List<Map> getRandomMaps() {

        List<Map> randomMaps = new ArrayList<>();

        List<Map> easyMaps = getMapsFromDifficulty(MapDifficulty.EASY);
        List<Map> mediumMaps = getMapsFromDifficulty(MapDifficulty.MEDIUM);
        List<Map> hardMaps = getMapsFromDifficulty(MapDifficulty.HARD);

        Collections.shuffle(easyMaps);
        Collections.shuffle(mediumMaps);
        Collections.shuffle(hardMaps);

        FileConfiguration config = DropperReloaded.getInstance().getConfig();
        int mapCount = config.getInt("games.map_per_games");
        int easyMapCount = config.getInt("games.easy_map_count");
        int mediumMapCount = config.getInt("games.medium_map_count");
        int hardMapCount = config.getInt("games.hard_map_count");

        int diffTotalCount = easyMapCount + mediumMapCount + hardMapCount;
        if(diffTotalCount != mapCount) {
            Bukkit.getLogger().severe("[DropperReloaded] Your config argument is wrong. You set the map count to " + mapCount + " but it doesn't match the total of easy, medium and hard count. Got " + diffTotalCount);
            return null;
        }

        for(int i = 0; i < easyMapCount; i++) {
            randomMaps.add(easyMaps.get(i));
        }
        for(int i = 0; i < mediumMapCount; i++) {
            randomMaps.add(mediumMaps.get(i));
        }
        for(int i = 0; i < hardMapCount; i++) {
            randomMaps.add(hardMaps.get(i));
        }

        return randomMaps;

    }

    public List<Map> getMapsFromDifficulty(MapDifficulty mapDifficulty) {
        return mapList.stream()
                .filter(map -> map.getDifficulty() == mapDifficulty)
                .collect(Collectors.toList());
    }

    public void serialize() {
        fileInit();
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Location.class, new LocationSerializer())
                .create();
        try {
            try(Writer writer = new BufferedWriter(new FileWriter(DROPPER_MAPS_FILE))) {
                gson.toJson(mapList, writer);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not serialize dropper maps: " + e);
        }
    }

    public void deserialize() throws IOException {
        fileInit();
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Location.class, new LocationDeserializer())
                .create();
        try(Reader reader = new BufferedReader(new FileReader(DROPPER_MAPS_FILE))) {
            Type listType = new TypeToken<List<Map>>() {}.getType();
            List<Map> maps = gson.fromJson(reader, listType);
            if (maps != null) {
                this.mapList.addAll(maps);
            }
        }
    }

    private void fileInit() {
        try {
            File fileMaps = new File(DropperReloaded.getInstance().getDataFolder(), "dropper_maps.json");
            if(!fileMaps.exists()) {
                if(!fileMaps.createNewFile()) {
                    DropperReloaded.getInstance().getLogger().info("Dropper maps file json couldn't be created.");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
