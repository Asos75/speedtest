package si.um.feri.speedii.towerdefense.gameobjects.enemies;

import com.badlogic.gdx.math.Vector2;
import si.um.feri.speedii.screens.GameScreenComponents.LoadMap;
import si.um.feri.speedii.towerdefense.config.GameDataManager;
import si.um.feri.speedii.towerdefense.logic.GameLogic;

public class EnemySpawner {
    private LoadMap loadMap;
    private GameLogic gameLogic;

    public EnemySpawner(LoadMap loadMap, GameLogic gameLogic) {
        this.loadMap = loadMap;
        this.gameLogic = gameLogic;
    }

    public Enemy spawnEnemyByType(String type, int health, float speed, GameDataManager gameDataManager) {
        switch (type) {
            case "BasicEnemy":
                return spawnBasicEnemy(health, speed, gameDataManager);
            case "DefenseEnemy":
                return spawnDefenseEnemy(health, speed, gameDataManager);
            case "FastEnemy":
                return spawnFastEnemy(health, speed, gameDataManager);
            case "BossEnemy":
                return spawnBossEnemy(health, speed, gameDataManager);
            default:
                throw new IllegalArgumentException("Unknown enemy type: " + type);
        }
    }

    public Enemy spawnBasicEnemy(int health, float speed, GameDataManager gameDataManager) {
        Vector2 spawnPoint = loadMap.getStartPoint();
        if (spawnPoint != null) {
            Enemy enemy = new Enemy(health, speed, Enemy.Type.BASIC, "images/Bugs/Dragonfly Sprite Sheet.png", gameLogic, gameDataManager);
            enemy.setPosition(spawnPoint.x, spawnPoint.y);
            return enemy;
        } else {
            System.err.println("Spawn point not found!");
            return null;
        }
    }

    public Enemy spawnDefenseEnemy(int health, float speed, GameDataManager gameDataManager) {
        Vector2 spawnPoint = loadMap.getStartPoint();
        if (spawnPoint != null) {
            Enemy enemy = new Enemy(health, speed, Enemy.Type.DEFENSE, "images/Bugs/MaggotWalk.png", gameLogic, gameDataManager);
            enemy.setPosition(spawnPoint.x, spawnPoint.y);
            return enemy;
        } else {
            System.err.println("Spawn point not found!");
            return null;
        }
    }

    public Enemy spawnFastEnemy(int health, float speed, GameDataManager gameDataManager) {
        Vector2 spawnPoint = loadMap.getStartPoint();
        if (spawnPoint != null) {
            Enemy enemy = new Enemy(health, speed, Enemy.Type.FAST, "images/Bugs/BeetleMove.png", gameLogic, gameDataManager);
            enemy.setPosition(spawnPoint.x, spawnPoint.y);
            return enemy;
        } else {
            System.err.println("Spawn point not found!");
            return null;
        }
    }

    public Enemy spawnBossEnemy(int health, float speed, GameDataManager gameDataManager) {
        Vector2 spawnPoint = loadMap.getStartPoint();
        if (spawnPoint != null) {
            Enemy enemy = new Enemy(health, speed, Enemy.Type.BOSS, "images/Bugs/MantisMove.png", gameLogic, gameDataManager);
            enemy.setPosition(spawnPoint.x, spawnPoint.y);
            return enemy;
        } else {
            System.err.println("Spawn point not found!");
            return null;
        }
    }
}
