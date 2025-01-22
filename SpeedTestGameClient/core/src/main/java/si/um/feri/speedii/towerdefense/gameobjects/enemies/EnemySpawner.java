package si.um.feri.speedii.towerdefense.gameobjects.enemies;

import com.badlogic.gdx.math.Vector2;
import si.um.feri.speedii.screens.GameScreenComponents.LoadMap;
import si.um.feri.speedii.towerdefense.logic.GameLogic;

public class EnemySpawner {
    private LoadMap loadMap;
    private GameLogic gameLogic;

    public EnemySpawner(LoadMap loadMap, GameLogic gameLogic) {
        this.loadMap = loadMap;
        this.gameLogic = gameLogic;
    }

    public Enemy spawnEnemyByType(String type, int health, float speed) {
        switch (type) {
            case "BasicEnemy":
                return spawnBasicEnemy(health, speed);
            case "DefenseEnemy":
                return spawnDefenseEnemy(health, speed);
            case "SpeedEnemy":
                return spawnSpeedEnemy(health, speed);
            case "DifficultEnemy":
                return spawnDifficultEnemy(health, speed);
            default:
                throw new IllegalArgumentException("Unknown enemy type: " + type);
        }
    }

    public Enemy spawnBasicEnemy(int health, float speed) {
        Vector2 spawnPoint = loadMap.getStartPoint();
        if (spawnPoint != null) {
            Enemy enemy = new Enemy(health, speed, Enemy.Type.BASIC, "images/Bugs/Dragonfly Sprite Sheet.png", gameLogic);
            enemy.setPosition(spawnPoint.x, spawnPoint.y);
            return enemy;
        } else {
            System.err.println("Spawn point not found!");
            return null;
        }
    }

    public Enemy spawnDefenseEnemy(int health, float speed) {
        Vector2 spawnPoint = loadMap.getStartPoint();
        if (spawnPoint != null) {
            Enemy enemy = new Enemy(health, speed, Enemy.Type.DEFENSE, "images/Bugs/MaggotWalk.png", gameLogic);
            enemy.setPosition(spawnPoint.x, spawnPoint.y);
            return enemy;
        } else {
            System.err.println("Spawn point not found!");
            return null;
        }
    }

    public Enemy spawnSpeedEnemy(int health, float speed) {
        Vector2 spawnPoint = loadMap.getStartPoint();
        if (spawnPoint != null) {
            Enemy enemy = new Enemy(health, speed, Enemy.Type.FAST, "images/Bugs/BeetleMove.png", gameLogic);
            enemy.setPosition(spawnPoint.x, spawnPoint.y);
            return enemy;
        } else {
            System.err.println("Spawn point not found!");
            return null;
        }
    }

    public Enemy spawnDifficultEnemy(int health, float speed) {
        Vector2 spawnPoint = loadMap.getStartPoint();
        if (spawnPoint != null) {
            Enemy enemy = new Enemy(health, speed, Enemy.Type.BOSS, "images/Bugs/MantisMove.png", gameLogic);
            enemy.setPosition(spawnPoint.x, spawnPoint.y);
            return enemy;
        } else {
            System.err.println("Spawn point not found!");
            return null;
        }
    }
}
