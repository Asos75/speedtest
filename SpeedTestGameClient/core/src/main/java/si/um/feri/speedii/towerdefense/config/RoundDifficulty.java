// File: core/src/main/java/si/um/feri/speedii/towerdefense/config/RoundDifficulty.java
package si.um.feri.speedii.towerdefense.config;

import java.util.ArrayList;
import java.util.List;

public class RoundDifficulty {
    private final DIFFICULTY difficulty;
    private final List<Wave> waves;

    public RoundDifficulty(DIFFICULTY difficulty) {
        this.difficulty = difficulty;
        this.waves = new ArrayList<>();
        initializeWaves();
    }

    private void initializeWaves() {
        switch (difficulty) {
            case VERY_EASY:
                createWaves(5, "BasicEnemy");
                break;
            case EASY:
                createWaves(5, "BasicEnemy", "FastEnemy");
                break;
            case MEDIUM:
                createWaves(7, "BasicEnemy", "FastEnemy", "StrongEnemy");
                break;
            case HARD:
                createWaves(8, "BasicEnemy", "FastEnemy", "StrongEnemy", "BossEnemy");
                break;
            case VERY_HARD:
                createWaves(10, "BasicEnemy", "FastEnemy", "StrongEnemy", "BossEnemy", "EliteEnemy");
                break;
        }
    }

    private void createWaves(int numberOfWaves, String... enemyTypes) {
        for (int i = 1; i <= numberOfWaves; i++) {
            Wave wave = new Wave(i, enemyTypes);
            waves.add(wave);
        }
    }

    public List<Wave> getWaves() {
        return waves;
    }

    public static class Wave {
        private final int waveNumber;
        private final List<String> enemies;

        public Wave(int waveNumber, String... enemyTypes) {
            this.waveNumber = waveNumber;
            this.enemies = new ArrayList<>();
            for (String enemyType : enemyTypes) {
                enemies.add(enemyType);
            }
        }

        public int getWaveNumber() {
            return waveNumber;
        }

        public List<String> getEnemies() {
            return enemies;
        }
    }
}
