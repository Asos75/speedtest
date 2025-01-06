package si.um.feri.speedii.screens.GameScreenComponents;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import si.um.feri.speedii.towerdefense.config.DIFFICULTY;

public class LoadMap {

    private TiledMap map;

    public void loadMap(DIFFICULTY difficulty) {
        String mapFile = getMapFile(difficulty);
        TmxMapLoader loader = new TmxMapLoader(new InternalFileHandleResolver());
        map = loader.load(mapFile);

        loadLayers();
        loadGameObjects();
    }

    private String getMapFile(DIFFICULTY difficulty) {
        switch (difficulty) {
            case VERY_EASY:
                return "Tiled/Maps/VeryEasyMap.tmx";
            case EASY:
                // Add other map files for different difficulties
            case MEDIUM:
                // Add other map files for different difficulties
            case HARD:
                // Add other map files for different difficulties
            case VERY_HARD:
                // Add other map files for different difficulties
            default:
                throw new IllegalArgumentException("Unknown difficulty: " + difficulty);
        }
    }

    private void loadLayers() {
        MapLayer backgroundLayer = map.getLayers().get("Background");
        MapLayer pathLayer = map.getLayers().get("Path");
        MapLayer borderLayer = map.getLayers().get("Border");
        MapLayer fieldLayer = map.getLayers().get("Field");
    }

    private void loadGameObjects() {
        MapLayer gameLogicLayer = map.getLayers().get("GameLogic");
        for (MapObject object : gameLogicLayer.getObjects()) {
            String objectName = object.getName();
            if ("SpawnPoint".equals(objectName)) {
                // Process spawn point
            } else if ("EndPoint".equals(objectName)) {
                // Process end point
            }
        }
    }

    // Each tile is 32 px
    public int getMapWidth()  {return map.getProperties().get("width", Integer.class) * 32; }
    public int getMapHeight() {return map.getProperties().get("height", Integer.class) * 32;}

    public TiledMap getMap() {
        return map;
    }
}
