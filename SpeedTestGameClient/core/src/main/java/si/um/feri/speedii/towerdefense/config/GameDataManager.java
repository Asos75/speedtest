package si.um.feri.speedii.towerdefense.config;

public class GameDataManager {
    private int enemiesRemaining;

    public GameDataManager() {
        this.enemiesRemaining = 0;
    }

    public int getEnemiesRemaining() {
        return enemiesRemaining;
    }

    public void setEnemiesRemaining(int enemiesRemaining) {
        this.enemiesRemaining = enemiesRemaining;
    }

    public void decreaseEnemiesRemaining() {
        if (enemiesRemaining > 0) {
            enemiesRemaining--;
        }
    }

    public void increaseEnemiesRemaining() {
        enemiesRemaining++;
    }
}
