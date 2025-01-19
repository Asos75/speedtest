package si.um.feri.speedii.towerdefense.gameobjects.towers;

import com.badlogic.gdx.math.Vector2;

public class ConcreteTower extends Tower {

    public ConcreteTower(Vector2 position, int price, int damage, float range, float cooldown) {
        super(position, price, damage, range, cooldown);
    }

    @Override
    public void attack() {
        // TODO Implement attack logic
    }

    @Override
    public Tower clone(Vector2 position) {
        return new ConcreteTower(position, this.price, this.damage, this.range, this.cooldown);
    }
}
