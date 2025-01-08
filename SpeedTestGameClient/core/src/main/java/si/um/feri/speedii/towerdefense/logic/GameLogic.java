package si.um.feri.speedii.towerdefense.logic;

import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class GameLogic {
    public Vector2 spawnPoint;
    public Vector2 endPoint;

    public List<Vector2> goUpPoints;
    public List<Vector2> goRightPoints;
    public List<Vector2> goDownPoints;

    public Vector2 intersection;

    public Vector2[] teleportEnterDown;
    public Vector2[] teleportEnterUp;

    public GameLogic(Vector2 spawnPoint, List<Vector2> goUpPoints, List<Vector2> goRightPoints, List<Vector2> goDownPoints, Vector2 intersection, Vector2[] teleportEnterDown , Vector2[] teleportEnterUp, Vector2 endPoint) {
        this.spawnPoint = spawnPoint;
        this.endPoint = endPoint;

        this.goUpPoints = goUpPoints;
        this.goRightPoints = goRightPoints;
        this.goDownPoints = goDownPoints;

        this.intersection = intersection;

        this.teleportEnterDown = teleportEnterDown;
        this.teleportEnterUp = teleportEnterUp;
    }
}
