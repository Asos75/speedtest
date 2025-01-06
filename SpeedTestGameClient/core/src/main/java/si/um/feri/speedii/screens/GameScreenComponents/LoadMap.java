package si.um.feri.speedii.screens.GameScreenComponents;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;

import si.um.feri.speedii.towerdefense.config.DIFFICULTY;

public class LoadMap {
    private TiledMap map;
    // Path
    private Vector2 startPoint;
    private Vector2 endPoint;

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

    public MapLayer loadLayer(String name) {
        return map.getLayers().get(name);
    }

    private void loadGameObjects() {
        MapLayer gameLogicLayer = map.getLayers().get("GameLogic");
        if (gameLogicLayer != null) {
            for (MapObject object : gameLogicLayer.getObjects()) {
                if (object.getName().equals("SpawnPoint")) {
                    float x = object.getProperties().get("x", Float.class);
                    float y = object.getProperties().get("y", Float.class);
                    startPoint = new Vector2(x, y);
                } else if (object.getName().equals("EndPoint")) {
                    float x = object.getProperties().get("x", Float.class);
                    float y = object.getProperties().get("y", Float.class);
                    endPoint = new Vector2(x, y);
                }
            }
        }
    }

    public Vector2 getStartPoint() { return startPoint;}

    public Vector2 getEndPoint() { return endPoint;}

    // Each tile is 32 px
    public int getMapWidth()  {return map.getProperties().get("width", Integer.class) * 32; }
    public int getMapHeight() {return map.getProperties().get("height", Integer.class) * 32;}

    public TiledMap getMap() {
        return map;
    }
}
