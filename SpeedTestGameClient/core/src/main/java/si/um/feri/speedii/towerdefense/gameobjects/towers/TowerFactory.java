package si.um.feri.speedii.towerdefense.gameobjects.towers;

import com.badlogic.gdx.math.Vector2;
import java.util.HashMap;
import java.util.Map;

import si.um.feri.speedii.assets.RegionNames;
import si.um.feri.speedii.assets.RegionPrices;

public class TowerFactory {
    private static final Map<String, Tower> towerTypes = new HashMap<>();

    static {
        towerTypes.put(RegionNames.FAST_ACCESS, new ConcreteTower(new Vector2(0, 0), RegionPrices.FAST_ACCESS.getPrice(), 20, 50.0f, 1.0f));
        towerTypes.put(RegionNames.CABINETS, new ConcreteTower(new Vector2(0, 0), RegionPrices.CABINETS.getPrice(), 30, 60.0f, 1.2f));
        towerTypes.put(RegionNames.SERVER_CONTROL, new ConcreteTower(new Vector2(0, 0), RegionPrices.SERVER_CONTROL.getPrice(), 40, 70.0f, 1.5f));
        towerTypes.put(RegionNames.HUB, new ConcreteTower(new Vector2(0, 0), RegionPrices.HUB.getPrice(), 50, 80.0f, 1.8f));
        towerTypes.put(RegionNames.SWITCH, new ConcreteTower(new Vector2(0, 0), RegionPrices.SWITCH.getPrice(), 60, 90.0f, 2.0f));
        towerTypes.put(RegionNames.CELL_TOWER, new ConcreteTower(new Vector2(0, 0), RegionPrices.CELL_TOWER.getPrice(), 70, 100.0f, 2.2f));
        towerTypes.put(RegionNames.SATELLITE_TOWER, new ConcreteTower(new Vector2(0, 0), RegionPrices.SATELLITE_TOWER.getPrice(), 80, 110.0f, 2.5f));
        towerTypes.put(RegionNames.SIGNAL, new ConcreteTower(new Vector2(0, 0), RegionPrices.SIGNAL.getPrice(), 90, 120.0f, 2.8f));
    }

    public static Tower createTower(String type, Vector2 position) {
        Tower prototype = towerTypes.get(type);
        if (prototype != null) {
            return prototype.clone(position);
        }
        return null;
    }
}
