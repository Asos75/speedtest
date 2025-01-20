package si.um.feri.speedii.towerdefense.gameobjects.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    private Vector2 position;
    private Enemy target;
    private int damage;
    private Texture texture;
    private float speed = 300f;

    public Bullet(Vector2 position, Enemy target, int damage, Texture texture) {
        this.position = new Vector2(position);
        this.target = target;
        this.damage = damage;
        this.texture = texture;
    }

    public void update(float delta) {
        if (target == null) return;

        Vector2 direction = new Vector2(target.getPosition()).sub(position).nor();
        position.mulAdd(direction, speed * delta);

        if (position.dst(target.getPosition()) < speed * delta) {
            hitTarget();
        }
    }


    private void hitTarget() {
        if (target != null) {
            target.setHealth(target.getHealth() - damage);
            target = null; // Bullet is no longer active
        }
    }

    public Enemy getTarget() {
        return target;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void draw(SpriteBatch spriteBatch) {
        if (target != null) {
            spriteBatch.draw(texture, position.x, position.y, 10, 10);
        }
    }
}
