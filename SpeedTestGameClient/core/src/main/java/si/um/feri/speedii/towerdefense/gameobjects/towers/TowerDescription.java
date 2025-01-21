package si.um.feri.speedii.towerdefense.gameobjects.towers;

public class TowerDescription {
    private String name;
    private String description;

    public TowerDescription(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
