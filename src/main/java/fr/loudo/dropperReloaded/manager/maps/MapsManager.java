package fr.loudo.dropperReloaded.manager.maps;

import java.util.ArrayList;
import java.util.List;

public class MapsManager {

    private List<Map> mapList;

    public MapsManager() {
        this.mapList = new ArrayList<>();
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

    public boolean mapExists(String mapName) {
        for(Map map : mapList) {
            if(map.getName().equalsIgnoreCase(mapName)) {
                return true;
            }
        }
        return false;
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

}
