package si.um.feri.speedii.towerdefense.gameobjects.towers;

import com.badlogic.gdx.math.Vector2;
import java.util.HashMap;
import java.util.Map;

import si.um.feri.speedii.assets.RegionNames;
import si.um.feri.speedii.assets.RegionPrices;

public class TowerFactory {
    private static final Map<String, Tower> towerTypes = new HashMap<>();

    static {
        towerTypes.put(RegionNames.FAST_ACCESS, new ConcreteTower(new Vector2(0, 0), RegionPrices.FAST_ACCESS.getPrice(), 50, 120.0f, 0.3f));
        towerTypes.put(RegionNames.CABINETS, new ConcreteTower(new Vector2(0, 0), RegionPrices.CABINETS.getPrice(), 60, 140.0f, 0.4f));
        towerTypes.put(RegionNames.SERVER_CONTROL, new ConcreteTower(new Vector2(0, 0), RegionPrices.SERVER_CONTROL.getPrice(), 50, 155.0f, 0.4f));
        towerTypes.put(RegionNames.HUB, new SlowingTower(new Vector2(0, 0), RegionPrices.HUB.getPrice(), 150.0f));
        towerTypes.put(RegionNames.SWITCH, new SupportTower(new Vector2(0, 0), RegionPrices.SWITCH.getPrice(), 70.0f));
        towerTypes.put(RegionNames.CELL_TOWER, new AoETower(new Vector2(0, 0), RegionPrices.CELL_TOWER.getPrice(), 50, 200.0f, 1.0f));
        towerTypes.put(RegionNames.SATELLITE_TOWER, new ConcreteTower(new Vector2(0, 0), RegionPrices.SATELLITE_TOWER.getPrice(), 70, 220.0f, 1.2f));
        towerTypes.put(RegionNames.SIGNAL, new ConcreteTower(new Vector2(0, 0), RegionPrices.SIGNAL.getPrice(), 150, 240.0f, 1.4f));

        //TowerDescriptionLoader.loadDescriptions(towerTypes);
    }

    public static Tower createTower(String type, Vector2 position) {
        Tower prototype = towerTypes.get(type);
        if (prototype != null) {
            return prototype.clone(position);
        }
        return null;
    }

    public static void removeTower(Tower tower) {
        towerTypes.values().removeIf(existingTower -> existingTower.equals(tower));
    }
}
