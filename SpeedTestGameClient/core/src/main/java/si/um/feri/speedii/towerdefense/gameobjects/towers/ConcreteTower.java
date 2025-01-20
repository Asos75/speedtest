package si.um.feri.speedii.towerdefense.gameobjects.towers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import si.um.feri.speedii.towerdefense.gameobjects.enemies.Bullet;
import si.um.feri.speedii.towerdefense.gameobjects.enemies.Enemy;

import java.util.Random;

public class ConcreteTower extends Tower {
    private List<Bullet> bullets;
    private Texture bulletTexture;

    public ConcreteTower(Vector2 position, int price, int damage, float range, float cooldown) {
        super(position, price, damage, range, cooldown);
        bullets = new ArrayList<>();
        bulletTexture = new Texture("assets/images/black_pixel.png");
    }

    @Override
    public void attack() {
        // Not used
    }

    @Override
    public void attack(Enemy enemy) {
        if (enemy != null) {
            Gdx.app.log("ConcreteTower", "Attacking enemy at position: " + enemy.getPosition());
            Bullet bullet = new Bullet(new Vector2(position), enemy, damage, bulletTexture);
            bullets.add(bullet);
            Gdx.app.log("ConcreteTower", "Bullet created at position: " + bullet.getPosition());
        }
    }

    // In ConcreteTower.java
    public void update(float delta, List<Enemy> enemies, ShapeRenderer shapeRenderer) {
        //Gdx.app.log("ConcreteTower", "update called");
        lastShot += delta;
        setEnemiesInRange(0); // Reset the count

        // Debug log for circle drawing
        //Gdx.app.log("ConcreteTower", "Drawing range circle at position: (" + (position.x + 16) + ", " + (position.y + 16) + ") with range: " + range);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(position.x + 16, position.y + 16, range); // Adjusted to center the circle
        shapeRenderer.end();

        for (Enemy enemy : enemies) {
            if (position.dst(enemy.getPosition()) <= range) {
                setEnemiesInRange(getEnemiesInRange() + 1);
            }
        }

        if (lastShot >= cooldown) {
            Enemy target = findTarget(enemies);
            if (target != null) {
                shoot(target);
                lastShot = 0;
            }
        }

        // Update bullets
        for (Iterator<Bullet> iterator = bullets.iterator(); iterator.hasNext();) {
            Bullet bullet = iterator.next();
            bullet.update(delta);
            if (bullet.getTarget() == null) {
                iterator.remove();
            }
        }
    }

    // Method to find a target enemy within range
    private Enemy findTarget(List<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (enemy.getPosition().dst(getPosition()) <= range) {
                return enemy;
            }
        }
        return null;
    }

    private void shoot(Enemy target) {
        Bullet bullet = new Bullet(getPosition(), target, damage, bulletTexture);
        bullets.add(bullet);
    }

    public void draw(SpriteBatch spriteBatch) {
        for (Bullet bullet : bullets) {
            bullet.draw(spriteBatch);
            //Gdx.app.log("ConcreteTower", "Bullet drawn at position: " + bullet.getPosition());
        }
    }

    @Override
    public Tower clone(Vector2 position) {
        return new ConcreteTower(position, price, damage, range, cooldown);
    }
}
