package si.um.feri.speedii.screens.GameScreenComponents;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

import si.um.feri.speedii.towerdefense.config.DIFFICULTY;

public class LoadMap {
    private TiledMap map;
    // Path
    private Vector2 startPoint;
    private Vector2 endPoint;
    // Path angles
    private List<Vector2> goRightPoints = new ArrayList<>();
    private List<Vector2> goUpPoints = new ArrayList<>();
    private List<Vector2> goDownPoints = new ArrayList<>();

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
                return "Tiled/Maps/EasyMap.tmx";
            case MEDIUM:
                return "Tiled/Maps/MediumMap.tmx";
            case HARD:
                return "Tiled/Maps/HardMap.tmx";
            case VERY_HARD:
                return "Tiled/Maps/VeryHardMap.tmx";
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
                float x = object.getProperties().get("x", Float.class);
                float y = object.getProperties().get("y", Float.class);
                Vector2 point = new Vector2(x, y);

                switch (object.getName()) {
                    case "SpawnPoint":
                        startPoint = point;
                        break;
                    case "EndPoint":
                        endPoint = point;
                        break;
                    case "GoRight":
                        goRightPoints.add(point);
                        break;
                    case "GoUp":
                        goUpPoints.add(point);
                        break;
                    case "GoDown":
                        goDownPoints.add(point);
                        break;
                }
            }
        }
    }

    public Vector2 getStartPoint() { return startPoint;}

    public Vector2 getEndPoint() { return endPoint;}

    public List<Vector2> getGoRightPoints() { return goRightPoints; }

    public List<Vector2> getGoUpPoints() { return goUpPoints; }

    public List<Vector2> getGoDownPoints() { return goDownPoints; }

    public TiledMap getMap() {
        return map;
    }
}
