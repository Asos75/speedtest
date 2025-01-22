package si.um.feri.speedii.towerdefense.gameobjects.towers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import si.um.feri.speedii.towerdefense.gameobjects.enemies.Enemy;

public class SupportTower extends Tower {
    private static final float FIRE_RATE_BOOST = 0.10f; // 10% boost to fire rate
    private static final float DAMAGE_BOOST = 0.10f; // 10% boost to damage
    private static final float RANGE_BOOST = 0.10f; // 10% boost to range
    private Set<Tower> buffedTowers; // Keeps track of towers already buffed

    public SupportTower(Vector2 position, int price, float range) {
        super(position, price, 0, range, 0);
        buffedTowers = new HashSet<>();
    }

    @Override
    public void attack() {
    }

    @Override
    public void attack(Enemy enemy) {
    }

    public void update(float delta, List<Tower> towers, ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.circle(position.x + 16, position.y + 16, range);
        shapeRenderer.end();

        Set<Tower> towersInRange = new HashSet<>();

        for (Tower tower : towers) {
            if (tower != this && position.dst(tower.getPosition()) <= range) {
                towersInRange.add(tower);
                if (!buffedTowers.contains(tower)) {
                    applyBuff(tower);
                    buffedTowers.add(tower);
                }
            }
        }

        buffedTowers.removeIf(tower -> {
            if (!towersInRange.contains(tower)) {
                revertBuff(tower);
                return true;
            }
            return false;
        });
    }

    private void applyBuff(Tower tower) {
        float boostedCooldown = tower.getCooldown() * (1 - FIRE_RATE_BOOST);
        tower.setCooldown(boostedCooldown);

        int boostedRange = (int) (tower.getRange() * (1 + RANGE_BOOST));
        tower.setRange(boostedRange);
    }

    private void revertBuff(Tower tower) {
        float originalCooldown = tower.getCooldown() / (1 - FIRE_RATE_BOOST);
        tower.setCooldown(originalCooldown);

        int originalRange = (int) (tower.getRange() / (1 + RANGE_BOOST));
        tower.setRange(originalRange);
    }

    @Override
    public Tower clone(Vector2 position) {
        return new SupportTower(position, price, range);
    }
}
