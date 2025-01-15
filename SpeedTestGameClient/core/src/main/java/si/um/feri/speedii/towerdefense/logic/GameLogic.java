package si.um.feri.speedii.towerdefense.logic;

import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class GameLogic {
    public Vector2 spawnPoint;
    public Vector2 endPoint;

    public List<Vector2> goUpPoints;
    public List<Vector2> goRightPoints;
    public List<Vector2> goDownPoints;

    // MEDIUM MAP
    public Vector2 intersection;
    public Vector2[] teleportEnterDown;
    public Vector2[] teleportEnterUp;

    // HARD MAP
    public Vector2 teleportInterSectionEnterGoRight;
    public List<Vector2> teleportIntersectionGoRight;
    public List<Vector2> teleportIntersectionEnter;
    public Vector2 teleportIntersectionLeave;

    // VERY HARD MAP
    public Vector2 teleportIntersectionEnterGoUpGoDown;
    public Vector2 teleportIntersectionGoDown;
    public Vector2 teleportIntersectionGoUp;

    public GameLogic(Vector2 spawnPoint, List<Vector2> goUpPoints, List<Vector2> goRightPoints, List<Vector2> goDownPoints, Vector2 intersection, Vector2[] teleportEnterDown , Vector2[] teleportEnterUp, Vector2 endPoint
            , Vector2 teleportInterSectionEnterGoRight, List<Vector2> teleportIntersectionGoRight, List<Vector2> teleportIntersectionEnter, Vector2 teleportIntersectionLeave
            , Vector2 teleportIntersectionEnterGoUpGoDown, Vector2 teleportIntersectionGoDown, Vector2 teleportIntersectionGoUp) {
        this.spawnPoint = spawnPoint;
        this.endPoint = endPoint;

        this.goUpPoints = goUpPoints;
        this.goRightPoints = goRightPoints;
        this.goDownPoints = goDownPoints;

        // MEDIUM
        this.intersection = intersection;
        this.teleportEnterDown = teleportEnterDown;
        this.teleportEnterUp = teleportEnterUp;

        // HARD
        this.teleportInterSectionEnterGoRight = teleportInterSectionEnterGoRight;
        this.teleportIntersectionGoRight = teleportIntersectionGoRight;
        this.teleportIntersectionEnter = teleportIntersectionEnter;
        this.teleportIntersectionLeave = teleportIntersectionLeave;

        // VERY HARD
        this.teleportIntersectionEnterGoUpGoDown = teleportIntersectionEnterGoUpGoDown;
        this.teleportIntersectionGoDown = teleportIntersectionGoDown;
        this.teleportIntersectionGoUp = teleportIntersectionGoUp;
    }
}
