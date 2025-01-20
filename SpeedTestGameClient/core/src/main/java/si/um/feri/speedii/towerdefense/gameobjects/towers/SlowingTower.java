package si.um.feri.speedii.towerdefense.gameobjects.towers;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

import si.um.feri.speedii.towerdefense.gameobjects.enemies.Enemy;

public class SlowingTower extends Tower {
    private static final float SLOW_PERCENTAGE = 50.0f;

    public SlowingTower(Vector2 position, int price, float range){
        super(position, price, 0, range, 0);
    }

    public void update(float delta, List<Enemy> enemies, ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.circle(position.x + 16, position.y + 16, range);
        shapeRenderer.end();


        for (Enemy enemy : enemies) {
            if (position.dst(enemy.getPosition()) <= range) {
                enemy.setSpeedMultiplier(1 - SLOW_PERCENTAGE / 100);
            } else {
                enemy.setSpeedMultiplier(1.0f);
            }
        }
    }



    @Override
    public void attack() {

    }

    @Override
    public void attack(Enemy enemy) {

    }

    @Override
    public Tower clone(Vector2 position) {
        return new SlowingTower(position, price, range);
    }
}
