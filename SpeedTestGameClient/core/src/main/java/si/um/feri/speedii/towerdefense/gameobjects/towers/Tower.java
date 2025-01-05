package si.um.feri.speedii.towerdefense.gameobjects.towers;

import com.badlogic.gdx.math.Vector2;

import si.um.feri.speedii.towerdefense.gameobjects.GameObject;

public abstract class Tower extends GameObject {

    protected int price;
    protected int damage;
    protected float range;
    protected float cooldown;
    protected float lastShot;


    public Tower(Vector2 position) {
        super(position);
    }
}
