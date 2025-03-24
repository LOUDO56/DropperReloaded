package fr.loudo.dropperReloaded.maps;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.players.PlayerSession;
import fr.loudo.dropperReloaded.utils.LocationDeserializer;
import fr.loudo.dropperReloaded.utils.LocationSerializer;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class DropperMapsManager {

    private final File DROPPER_MAPS_FILE = new File(DropperReloaded.getInstance().getDataFolder(), "dropper_maps.json");

    private List<DropperMap> dropperMapList;

    public DropperMapsManager() {
        this.dropperMapList = new ArrayList<>();
        try {
            deserialize();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't load dropper maps: " + e);
        }
    }

    public boolean addMap(DropperMap dropperMap) {
        if(dropperMapList.contains(dropperMap) || mapExists(dropperMap.getName())) return false;

        dropperMapList.add(dropperMap);
        return true;
    }

    public boolean removeMap(DropperMap dropperMap) {
        if(!dropperMapList.contains(dropperMap)) return false;

        dropperMapList.remove(dropperMap);
        return true;
    }

    public boolean removeMap(String mapName) {
        for(DropperMap dropperMap : dropperMapList) {
            if(dropperMap.getName().equalsIgnoreCase(mapName)) {
                dropperMapList.remove(dropperMap);
                return true;
            }
        }
        return false;
    }

    public boolean mapExists(String mapName) {
        for(DropperMap dropperMap : dropperMapList) {
            if(dropperMap.getName().equalsIgnoreCase(mapName)) {
                return true;
            }
        }
        return false;
    }

    public DropperMap getFromName(String mapName) {
        for(DropperMap dropperMap : dropperMapList) {
            if(dropperMap.getName().equalsIgnoreCase(mapName)) {
                return dropperMap;
            }
        }
        return null;
    }

    public List<String> getListMapName() {
        List<String> mapsName = new ArrayList<>();
        for(DropperMap dropperMap : dropperMapList) {
            mapsName.add(dropperMap.getName());
        }
        return mapsName;
    }

    public List<DropperMap> getMapList() {
        return dropperMapList;
    }

    public List<DropperMap> getRandomMaps() {

        List<DropperMap> randomDropperMaps = new ArrayList<>();

        List<DropperMap> easyDropperMaps = getMapsFromDifficulty(DropperMapDifficulty.EASY);
        List<DropperMap> mediumDropperMaps = getMapsFromDifficulty(DropperMapDifficulty.MEDIUM);
        List<DropperMap> hardDropperMaps = getMapsFromDifficulty(DropperMapDifficulty.HARD);

        Collections.shuffle(easyDropperMaps);
        Collections.shuffle(mediumDropperMaps);
        Collections.shuffle(hardDropperMaps);

        FileConfiguration config = DropperReloaded.getInstance().getConfig();
        int easyMapCount = config.getInt("games.easy_map_count");
        int mediumMapCount = config.getInt("games.medium_map_count");
        int hardMapCount = config.getInt("games.hard_map_count");

        for(int i = 0; i < easyMapCount; i++) {
            randomDropperMaps.add(easyDropperMaps.get(i));
        }
        for(int i = 0; i < mediumMapCount; i++) {
            randomDropperMaps.add(mediumDropperMaps.get(i));
        }
        for(int i = 0; i < hardMapCount; i++) {
            randomDropperMaps.add(hardDropperMaps.get(i));
        }

        return randomDropperMaps;

    }

    public List<DropperMap> getMapsFromPlayersVote(List<Player> playerList) {

        List<DropperMap> finalDropperMapList = new ArrayList<>();

        List<DropperMap> easyDropperMaps = new ArrayList<>();
        List<DropperMap> medDropperMaps = new ArrayList<>();
        List<DropperMap> hardDropperMaps = new ArrayList<>();

        FileConfiguration config = DropperReloaded.getInstance().getConfig();
        int easyMapCount = config.getInt("games.easy_map_count");
        int mediumMapCount = config.getInt("games.medium_map_count");
        int hardMapCount = config.getInt("games.hard_map_count");

        for(Player player : playerList) {
            PlayerSession playerSession = DropperReloaded.getPlayersSessionManager().getPlayerSession(player);
            for (DropperMap dropperMap : playerSession.getVotedMaps()) {
                if(dropperMap.isEnabled()) {
                   if(dropperMap.getDifficulty() == DropperMapDifficulty.EASY) {
                       easyDropperMaps.add(dropperMap);
                   } else if(dropperMap.getDifficulty() == DropperMapDifficulty.MEDIUM) {
                       medDropperMaps.add(dropperMap);
                   } else if(dropperMap.getDifficulty() == DropperMapDifficulty.HARD) {
                       hardDropperMaps.add(dropperMap);
                   }
                }
            }
        }

        easyDropperMaps = getMostVotedMap(easyDropperMaps, DropperMapDifficulty.EASY, easyMapCount);
        medDropperMaps = getMostVotedMap(medDropperMaps, DropperMapDifficulty.MEDIUM, mediumMapCount);
        hardDropperMaps = getMostVotedMap(hardDropperMaps, DropperMapDifficulty.HARD, hardMapCount);

        finalDropperMapList.addAll(easyDropperMaps);
        finalDropperMapList.addAll(medDropperMaps);
        finalDropperMapList.addAll(hardDropperMaps);

        return finalDropperMapList;

    }

    private List<DropperMap> getMostVotedMap(List<DropperMap> dropperMapList, DropperMapDifficulty dropperMapDifficulty, int limit) {
        HashMap<DropperMap, Integer> mapVotes = new HashMap<>();
        for (DropperMap dropperMap : dropperMapList) {
            mapVotes.put(dropperMap, mapVotes.getOrDefault(dropperMap, 0) + 1);
        }

        int maxVotes = mapVotes.values().stream().max(Integer::compareTo).orElse(0);

        List<DropperMap> mostVotedMaps = mapVotes.entrySet().stream()
                .filter(entry -> entry.getValue() == maxVotes)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (mostVotedMaps.size() > limit) {
            mostVotedMaps = mostVotedMaps.subList(0, limit);
        }

        if (mostVotedMaps.size() < limit) {
            List<DropperMap> dropperMapsDifficulty = getMapsFromDifficulty(dropperMapDifficulty);
            dropperMapsDifficulty.removeAll(mostVotedMaps); // Enlever celles déjà sélectionnées

            Random random = new Random();
            while (mostVotedMaps.size() < limit && !dropperMapsDifficulty.isEmpty()) {
                mostVotedMaps.add(dropperMapsDifficulty.remove(random.nextInt(dropperMapsDifficulty.size())));
            }
        }

        return mostVotedMaps;
    }

    public List<DropperMap> getMapsFromDifficulty(DropperMapDifficulty dropperMapDifficulty) {
        return dropperMapList.stream()
                .filter(map -> map.getDifficulty() == dropperMapDifficulty)
                .collect(Collectors.toList());
    }

    public List<DropperMap> getMapsSortedDifficulty() {
        List<DropperMap> finalDropperMapList = new ArrayList<>();
        finalDropperMapList.addAll(dropperMapList.stream()
                .filter(map -> map.getDifficulty() == DropperMapDifficulty.EASY)
                .collect(Collectors.toList()));
        finalDropperMapList.addAll(dropperMapList.stream()
                .filter(map -> map.getDifficulty() == DropperMapDifficulty.MEDIUM)
                .collect(Collectors.toList()));
        finalDropperMapList.addAll(dropperMapList.stream()
                .filter(map -> map.getDifficulty() == DropperMapDifficulty.HARD)
                .collect(Collectors.toList()));

        return finalDropperMapList;
    }

    public boolean enoughMapsToPlay() {
        FileConfiguration config = DropperReloaded.getInstance().getConfig();

        int easyMapCount = config.getInt("games.easy_map_count");
        int mediumMapCount = config.getInt("games.medium_map_count");
        int hardMapCount = config.getInt("games.hard_map_count");

        long finalCountEasy = getMapsFromDifficulty(DropperMapDifficulty.EASY)
                .stream()
                .filter(DropperMap::isEnabled)
                .limit(easyMapCount)
                .count();

        long finalCountMed = getMapsFromDifficulty(DropperMapDifficulty.MEDIUM)
                .stream()
                .filter(DropperMap::isEnabled)
                .limit(mediumMapCount)
                .count();

        long finalCountHard = getMapsFromDifficulty(DropperMapDifficulty.HARD)
                .stream()
                .filter(DropperMap::isEnabled)
                .limit(hardMapCount)
                .count();

        return finalCountEasy == easyMapCount &&
                finalCountMed == mediumMapCount &&
                finalCountHard == hardMapCount;
    }

    public void serialize() {
        fileInit();
        Gson gson;
        if(DropperReloaded.getInstance().getConfig().getBoolean("global.pretty_json")) {
            gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(Location.class, new LocationSerializer())
                    .create();
        } else {
            gson = new GsonBuilder()
                    .registerTypeAdapter(Location.class, new LocationSerializer())
                    .create();
        }
        try {
            try(Writer writer = new BufferedWriter(new FileWriter(DROPPER_MAPS_FILE))) {
                gson.toJson(dropperMapList, writer);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not serialize dropper maps: " + e);
        }
    }

    public void deserialize() throws IOException {
        fileInit();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Location.class, new LocationDeserializer())
                .create();
        try(Reader reader = new BufferedReader(new FileReader(DROPPER_MAPS_FILE))) {
            Type listType = new TypeToken<List<DropperMap>>() {}.getType();
            List<DropperMap> dropperMaps = gson.fromJson(reader, listType);
            if (dropperMaps != null) {
                this.dropperMapList.addAll(dropperMaps);
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
