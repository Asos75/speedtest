package si.um.feri.speedii.towerdefense.config;

public class GameDataManager {
    private String location;
    private float uploadSpeed;
    private float downloadSpeed;
    private int health;
    private int currentWave;
    private int totalWaves;
    private int enemiesRemaining;

    public GameDataManager() {
        this.location = "Unknown";
        this.uploadSpeed = 0.0f;
        this.downloadSpeed = 0.0f;
        this.health = 100;
        this.currentWave = 0;
        this.totalWaves = 10;
        this.enemiesRemaining = 0;
    }

    public GameDataManager(String location, float uploadSpeed, float downloadSpeed, int health, int currentWave, int totalWaves, int enemiesRemaining) {
        this.location = location;
        this.uploadSpeed = uploadSpeed;
        this.downloadSpeed = downloadSpeed;
        this.health = health;
        this.currentWave = currentWave;
        this.totalWaves = totalWaves;
        this.enemiesRemaining = enemiesRemaining;
    }

    public void incrementWave() {
        currentWave++;
        if (currentWave > totalWaves) {
            currentWave = 1; // Reset to the first wave or handle end of game
        }
    }

    public void decrementEnemiesRemaining() {
        if (enemiesRemaining > 0) { enemiesRemaining--; }
    }

    public String getLocation() { return location; }

    public void setLocation(String location) { this.location = location; }

    public float getUploadSpeed() { return uploadSpeed; }

    public void setUploadSpeed(float uploadSpeed) { this.uploadSpeed = uploadSpeed; }

    public float getDownloadSpeed() { return downloadSpeed; }

    public void setDownloadSpeed(float downloadSpeed) { this.downloadSpeed = downloadSpeed; }

    public int getHealth() { return health; }

    public void setHealth(int health) { this.health = health; }

    public int getCurrentWave() { return currentWave; }

    public void setCurrentWave(int currentWave) { this.currentWave = currentWave; }

    public int getTotalWaves() { return totalWaves; }

    public void setTotalWaves(int totalWaves) { this.totalWaves = totalWaves; }

    public int getEnemiesRemaining() { return enemiesRemaining; }

    public void setEnemiesRemaining(int enemiesRemaining) { this.enemiesRemaining = enemiesRemaining; }

    public void decreaseEnemiesRemaining() {
        if (enemiesRemaining > 0) { enemiesRemaining--; }
    }

    public void increaseEnemiesRemaining() { enemiesRemaining++; }
}
