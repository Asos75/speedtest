package si.um.feri.speedii.towerdefense.gameobjects.towers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;
import java.util.List;

import si.um.feri.speedii.config.GameConfig;
import si.um.feri.speedii.towerdefense.gameobjects.enemies.Enemy;

public class AoETower extends Tower {
    private boolean isAnimating; // Flag to track if the animation is active
    private float animationRadius; // Current radius of the animation circle
    private float maxAnimationRadius; // Maximum radius the animation should reach
    private float animationSpeed; // Speed at which the animation expands

    public AoETower(Vector2 position, int price, int damage, float range, float cooldown) {
        super(position, price, damage, range, cooldown);
        this.isAnimating = false;
        this.animationRadius = 0;
        this.maxAnimationRadius = range;
        this.animationSpeed = 200;
    }

    @Override
    public void attack() {
        // Not used
    }

    @Override
    public void attack(Enemy enemy) {
        // Not used; this tower attacks all enemies in range
    }

    public void update(float delta, List<Enemy> enemies, ShapeRenderer shapeRenderer) {
        lastShot += delta;
        setEnemiesInRange(0);

        if(GameConfig.DEBUG_MODE) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.circle(position.x + 16, position.y + 16, range);
            shapeRenderer.end();
        }

        for (Enemy enemy : enemies) {
            if (position.dst(enemy.getPosition()) <= range) {
                setEnemiesInRange(getEnemiesInRange() + 1);
            }
        }

        if (lastShot >= cooldown) {
            dealAoEDamage(enemies);
            lastShot = 0;
            startAnimation();
        }

        if (isAnimating) {
            updateAnimation(delta, shapeRenderer);
        }
    }

    private void dealAoEDamage(List<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (position.dst(enemy.getPosition()) <= range) {
                enemy.takeDamage(damage);
            }
        }
    }

    private void startAnimation() {
        isAnimating = true;
        animationRadius = 0; // Reset the radius to start the animation
    }

    private void updateAnimation(float delta, ShapeRenderer shapeRenderer) {
        if (!isAnimating) return;

        animationRadius += animationSpeed * delta; // Expand the radius over time
        if (animationRadius > maxAnimationRadius) {
            isAnimating = false; // Stop the animation once it exceeds max radius
            return;
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(1, 0, 0, 1 - animationRadius / maxAnimationRadius));
        shapeRenderer.circle(position.x + 16, position.y + 16, animationRadius);
        shapeRenderer.end();
    }

    @Override
    public Tower clone(Vector2 position) {
        return new AoETower(position, price, damage, range, cooldown);
    }
}
