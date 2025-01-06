package si.um.feri.speedii.towerdefense.gameobjects.enemies;

import com.badlogic.gdx.math.Vector2;
import si.um.feri.speedii.screens.GameScreenComponents.LoadMap;

public class EnemySpawner {
    private LoadMap loadMap;

    public EnemySpawner(LoadMap loadMap) {
        this.loadMap = loadMap;
    }

    public Enemy spawnEnemy() {
        Vector2 spawnPoint = loadMap.getStartPoint();
        if (spawnPoint != null) {
            // Create a new enemy with basic type and sprite
            Enemy enemy = new Enemy(100, 1.0f, Enemy.Type.BASIC, "images/Bugs/Dragonfly Sprite Sheet.png");
            // Set the enemy's position to the spawn point
            enemy.setPosition(spawnPoint.x * 1.5f, spawnPoint.y * 1.5f);
            System.out.println("Enemy spawned at: " + spawnPoint);
            return enemy;
        } else {
            System.err.println("Spawn point not found!");
            return null;
        }
    }
}
