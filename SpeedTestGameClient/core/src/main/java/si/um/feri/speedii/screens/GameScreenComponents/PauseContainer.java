package si.um.feri.speedii.screens.GameScreenComponents;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import si.um.feri.speedii.SpeediiApp;
import si.um.feri.speedii.towerdefense.config.GameDataManager;
import si.um.feri.speedii.towerdefense.gameobjects.towers.TowerDescription;
import si.um.feri.speedii.towerdefense.gameobjects.towers.TowerDescriptionLoader;
import si.um.feri.speedii.assets.RegionNames;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class PauseContainer extends Table {
    private final Skin skin;
    private final Map<String, TowerDescription> towerDescriptions = new HashMap<>();
    private final TextureAtlas atlas;

    private Label enemiesKilledLabel;
    private Label towersPlacedLabel;
    private Label moneySpentLabel;

    private final GameDataManager gameDataManager;

    public PauseContainer(SpeediiApp app, Skin skin, GameDataManager gameDataManager) {
        this.skin = skin;
        this.atlas = new TextureAtlas(Gdx.files.internal("images/spediiIcons.atlas"));
        this.gameDataManager = gameDataManager;
        initializeUI(app);
    }

    private void initializeUI(SpeediiApp app) {
        // Set a background color for visibility
        Drawable background = skin.newDrawable("white", 0.8f, 0.8f, 0.8f, 0.7f);
        this.setBackground(background);

        // Load tower descriptions
        List<TowerDescription> descriptions = TowerDescriptionLoader.loadDescriptions();
        for (TowerDescription description : descriptions) {
            towerDescriptions.put(description.getName(), description);
        }

        // Create main table
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // Create tower info table
        Table towerTable = new Table();
        //Label headerLabel = new Label("Tower Info", skin);
        //headerLabel.setFontScale(2.0f);
        //towerTable.add(headerLabel).colspan(2).align(Align.center).padBottom(10).row();
        addTowerInfo(towerTable, RegionNames.FAST_ACCESS);
        addTowerInfo(towerTable, RegionNames.CABINETS);
        addTowerInfo(towerTable, RegionNames.SERVER_CONTROL);
        addTowerInfo(towerTable, RegionNames.HUB);
        addTowerInfo(towerTable, RegionNames.SWITCH);
        addTowerInfo(towerTable, RegionNames.CELL_TOWER);
        addTowerInfo(towerTable, RegionNames.SATELLITE_TOWER);
        addTowerInfo(towerTable, RegionNames.SIGNAL);

        // Create statistics table
        Table statisticsTable = new Table();
        statisticsTable.add(new Label("Statistics", skin)).colspan(2).align(Align.center).padBottom(10).row();
        enemiesKilledLabel = new Label("Enemies Killed: " + gameDataManager.getEnemiesKilled(), skin);
        statisticsTable.add(enemiesKilledLabel).align(Align.left).pad(5).row();
        towersPlacedLabel = new Label("Towers Placed: " + gameDataManager.getTowersPlaced(), skin);
        statisticsTable.add(towersPlacedLabel).align(Align.left).pad(5).row();
        moneySpentLabel = new Label("Money Spent: " + gameDataManager.getMoneySpent(), skin);
        statisticsTable.add(moneySpentLabel).align(Align.left).pad(5).row();

        // Add towerTable and statisticsTable to mainTable
        mainTable.add(towerTable).expand().fill().left().pad(10);
        mainTable.add(statisticsTable).expand().fill().right().pad(10);

        this.add(mainTable).expand().fill();
    }

    public void updateStatistics() {
        enemiesKilledLabel.setText("Enemies Killed: " + gameDataManager.getEnemiesKilled());
        towersPlacedLabel.setText("Towers Placed: " + gameDataManager.getTowersPlaced());
        moneySpentLabel.setText("Money Spent: " + gameDataManager.getMoneySpent());
    }

    private void addTowerInfo(Table towerTable, String towerName) {
        TowerDescription description = towerDescriptions.get(towerName);
        if (description == null) {
            return;
        }

        String towerDescription = description.getDescription();
        if (towerDescription == null || towerDescription.trim().isEmpty()) {
            towerDescription = "No description available.";
        }

        TextureRegion towerImage = atlas.findRegion(towerName);
        // Create the image
        Image image = new Image(towerImage);
        image.setOrigin(Align.center);

        // Create a white background with 15-degree angles
        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixmap.setColor(Color.CLEAR);
        pixmap.fillTriangle(0, 0, 15, 0, 0, 15);
        pixmap.fillTriangle(64, 0, 49, 0, 64, 15);
        pixmap.fillTriangle(0, 64, 0, 49, 15, 64);
        pixmap.fillTriangle(64, 64, 49, 64, 64, 49);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        Drawable background = new TextureRegionDrawable(new TextureRegion(texture));

        // Wrap the image in a container and set the background
        Container<Image> imageContainer = new Container<>(image);
        imageContainer.setBackground(background);

        // Create the tower info table
        Table towerInfoTable = new Table();
        towerInfoTable.add(imageContainer).size(64).padRight(10);
        towerInfoTable.add(new Label(towerName + ": " + towerDescription, skin)).align(Align.left).expandX().fillX();
        towerTable.add(towerInfoTable).expandX().fillX().pad(5).row();
    }
}
