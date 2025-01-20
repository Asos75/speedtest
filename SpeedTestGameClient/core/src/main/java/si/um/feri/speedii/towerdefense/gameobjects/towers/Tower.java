package si.um.feri.speedii.towerdefense.gameobjects.towers;

import com.badlogic.gdx.math.Vector2;
import si.um.feri.speedii.towerdefense.gameobjects.GameObject;
import si.um.feri.speedii.towerdefense.gameobjects.enemies.Enemy;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public abstract class Tower extends GameObject {
    protected int price;
    protected int damage;
    protected float range;
    protected float cooldown;
    protected float lastShot;
    protected float level;
    private Drawable background;
    private String name;
    private int enemiesInRange;

    public Tower(Vector2 position, int price, int damage, float range, float cooldown) {
        super(position);
        this.price = price;
        this.damage = damage;
        this.range = range;
        this.cooldown = cooldown;
        this.lastShot = 0;
        this.level = 1;
        this.enemiesInRange = 0;
    }

    public int getEnemiesInRange() { return enemiesInRange; }

    public void setEnemiesInRange(int enemiesInRange) { this.enemiesInRange = enemiesInRange; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getPrice() { return price; }

    public int getDamage() { return damage; }

    public float getRange() { return range; }

    public float getCooldown() { return cooldown; }

    public float getWidth() { return background.getMinWidth(); }

    public float getLevel() { return level; }

    public void upgrade() {
        level++;
        damage += 10; // Example upgrade logic
        range += 1.0f;
        cooldown -= 0.1f;
    }

    public void setBackground(Drawable background) { this.background = background; }

    public abstract void attack();

    public abstract void attack(Enemy enemy);

    public abstract Tower clone(Vector2 position);
}
