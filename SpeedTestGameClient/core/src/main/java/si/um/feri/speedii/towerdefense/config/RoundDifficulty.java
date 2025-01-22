package si.um.feri.speedii.towerdefense.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import java.util.ArrayList;
import java.util.List;

public class RoundDifficulty {
    private final DIFFICULTY difficulty;
    private final List<Wave> waves;

    public RoundDifficulty(DIFFICULTY difficulty, GameDataManager gameDataManager) {
        this.difficulty = difficulty;
        this.waves = new ArrayList<>();
        loadWavesFromJson(gameDataManager);
    }

    private void loadWavesFromJson(GameDataManager gameDataManager) {
        String fileName = getFileNameForDifficulty();
        FileHandle fileHandle = Gdx.files.internal(fileName);
        Json json = new Json();
        WaveConfig waveConfig = null;
        try {
            waveConfig = json.fromJson(WaveConfig.class, fileHandle);
        } catch (Exception e) {
            Gdx.app.error("RoundDifficulty", "Error reading JSON file: " + fileName, e);
        }
        if (waveConfig != null) {
            waves.addAll(waveConfig.getWaves());
            gameDataManager.setTotalWaves(waves.size());
        } else {
            Gdx.app.error("RoundDifficulty", "WaveConfig is null for file: " + fileName);
        }
    }

    private String getFileNameForDifficulty() {
        switch (difficulty) {
            case VERY_EASY:
                return "assets/waves/VeryEasyWaves.json";
            case EASY:
                return "assets/waves/EasyWaves.json";
            case MEDIUM:
                return "assets/waves/MediumWaves.json";
            case HARD:
                return "assets/waves/HardWaves.json";
            case VERY_HARD:
                return "assets/waves/VeryHardWaves.json";
            default:
                throw new IllegalArgumentException("Unknown difficulty: " + difficulty);
        }
    }

    public List<Wave> getWaves() {
        return waves;
    }

    public static class Wave {
        private int waveNumber;
        private List<EnemyConfig> enemies;

        public int getWaveNumber() { return waveNumber; }

        public List<EnemyConfig> getEnemies() { return enemies; }
    }

    public static class EnemyConfig {
        private String type;
        private float delay;
        private int health;
        private float speed;
        private int count;

        public String getType() { return type; }

        public float getDelay() { return delay; }

        public int getHealth() { return health; }

        public float getSpeed() { return speed; }

        public int getCount() { return count; }
    }

    public static class WaveConfig {
        private List<Wave> waves;

        public List<Wave> getWaves() { return waves; }

        public void setWaves(List<Wave> waves) { this.waves = waves; }
    }
}
