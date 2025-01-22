package si.um.feri.speedii.towerdefense.gameobjects;

import com.badlogic.gdx.math.Vector2;

public abstract class GameObject {

    protected Vector2 position;

    public GameObject(Vector2 position) {
        this.position = position;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }
}
