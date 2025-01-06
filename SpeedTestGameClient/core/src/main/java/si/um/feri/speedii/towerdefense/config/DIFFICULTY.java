package si.um.feri.speedii.towerdefense.config;

public enum DIFFICULTY {
    VERY_EASY("Very Easy"),
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard"),
    VERY_HARD("Very Hard");

    private final String name;

    DIFFICULTY(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
