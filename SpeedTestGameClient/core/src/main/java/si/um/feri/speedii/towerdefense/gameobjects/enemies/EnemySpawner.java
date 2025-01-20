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

    public Enemy spawnEnemy() {
        Vector2 spawnPoint = loadMap.getStartPoint();
        if (spawnPoint != null) {
            // Create a new enemy with basic type and sprite, and pass the game logic
            Enemy enemy = new Enemy(100, 70.0f, Enemy.Type.BASIC, "images/Bugs/Dragonfly Sprite Sheet.png", gameLogic);
            // Set the enemy's position to the spawn point
            enemy.setPosition(spawnPoint.x, spawnPoint.y);
            //System.out.println("Enemy spawned at: " + spawnPoint);
            return enemy;
        } else {
            System.err.println("Spawn point not found!");
            return null;
        }
    }
}
