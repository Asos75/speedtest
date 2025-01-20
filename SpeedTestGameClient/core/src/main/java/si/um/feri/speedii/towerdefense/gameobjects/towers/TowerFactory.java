package si.um.feri.speedii.towerdefense.gameobjects.towers;

import com.badlogic.gdx.math.Vector2;
import java.util.HashMap;
import java.util.Map;

import si.um.feri.speedii.assets.RegionNames;
import si.um.feri.speedii.assets.RegionPrices;

public class TowerFactory {
    private static final Map<String, Tower> towerTypes = new HashMap<>();

    static {
        towerTypes.put(RegionNames.FAST_ACCESS, new ConcreteTower(new Vector2(0, 0), RegionPrices.FAST_ACCESS.getPrice(), 20, 100.0f, 0.3f));
        towerTypes.put(RegionNames.CABINETS, new ConcreteTower(new Vector2(0, 0), RegionPrices.CABINETS.getPrice(), 30, 120.0f, 0.4f));
        towerTypes.put(RegionNames.SERVER_CONTROL, new ConcreteTower(new Vector2(0, 0), RegionPrices.SERVER_CONTROL.getPrice(), 40, 140.0f, 0.55f));
        towerTypes.put(RegionNames.HUB, new ConcreteTower(new Vector2(0, 0), RegionPrices.HUB.getPrice(), 50, 160.0f, 0.6f));
        towerTypes.put(RegionNames.SWITCH, new ConcreteTower(new Vector2(0, 0), RegionPrices.SWITCH.getPrice(), 60, 180.0f, 0.8f));
        towerTypes.put(RegionNames.CELL_TOWER, new ConcreteTower(new Vector2(0, 0), RegionPrices.CELL_TOWER.getPrice(), 70, 200.0f, 1f));
        towerTypes.put(RegionNames.SATELLITE_TOWER, new ConcreteTower(new Vector2(0, 0), RegionPrices.SATELLITE_TOWER.getPrice(), 80, 220.0f, 1.2f));
        towerTypes.put(RegionNames.SIGNAL, new ConcreteTower(new Vector2(0, 0), RegionPrices.SIGNAL.getPrice(), 90, 240.0f, 1.4f));
    }

    public static Tower createTower(String type, Vector2 position) {
        Tower prototype = towerTypes.get(type);
        if (prototype != null) {
            return prototype.clone(position);
        }
        return null;
    }
}
