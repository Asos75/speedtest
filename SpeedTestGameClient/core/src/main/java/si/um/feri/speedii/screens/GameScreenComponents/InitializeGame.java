package si.um.feri.speedii.screens.GameScreenComponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import si.um.feri.speedii.assets.RegionNames;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import si.um.feri.speedii.screens.GameScreen;
import si.um.feri.speedii.towerdefense.config.GameDataManager;
import si.um.feri.speedii.towerdefense.gameobjects.enemies.Enemy;
import si.um.feri.speedii.towerdefense.gameobjects.enemies.Bullet;
import si.um.feri.speedii.towerdefense.gameobjects.towers.ConcreteTower;
import si.um.feri.speedii.towerdefense.gameobjects.towers.Tower;
import si.um.feri.speedii.towerdefense.gameobjects.towers.TowerFactory;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.graphics.Cursor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;



import java.util.ArrayList;
import java.util.List;

import si.um.feri.speedii.assets.RegionPrices;

public class InitializeGame {
    private final Skin skin;
    private final Table table;
    private final GameDataManager gameDataManager;
    //private Stage stage;

    public Label locationLabel;
    public Label uploadSpeedLabel;
    public Label downloadSpeedLabel;
    public Label healthLabel;
    public Label waveLabel;
    public Label enemiesRemainingLabel;
    public TextButton pauseButton;

    Drawable whiteBackground;
    Drawable tableBorder;

    private TextButton quitButton;
    private GameScreen gameScreen;
    private Container<Table> selectedTower;
    private String selectedTowerType;

    private ShapeRenderer shapeRenderer;

    public boolean drawCircle = false;
    public Vector2 towerPosition;
    public float towerRange;

    private List<Tower> towers;

    private Stage stage;

    TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("images/spediiIcons.atlas"));

    public InitializeGame(GameDataManager gameDataManager, Skin skin, GameScreen gameScreen) {
        //this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        this.skin = skin;
        this.table = new Table();
        this.gameDataManager = gameDataManager;
        this.stage = new Stage(new ScreenViewport());
        this.shapeRenderer = new ShapeRenderer();
        this.gameScreen = gameScreen;
        this.towers = new ArrayList<>();
        initializeUI();
    }

    private void initializeUI() {
        whiteBackground = createWhiteBackground();
        tableBorder = createTableBorder();
        table.setFillParent(true);
       //stage.addActor(table);

        initializeLabels();
        initializeButtons();
        initializeTables();
    }

    private void initializeLabels() {
        locationLabel = new Label("Location: {name}", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        locationLabel.setFontScale(1.2f);
        locationLabel.getStyle().font.getData().markupEnabled = true;
        locationLabel.setText("[#FFFFFF]Location: {name}");

        uploadSpeedLabel = new Label("Upload speed: {something}", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        uploadSpeedLabel.setFontScale(1.2f);
        uploadSpeedLabel.getStyle().font.getData().markupEnabled = true;
        uploadSpeedLabel.setText("[#FFFFFF]Upload speed: {something}");

        downloadSpeedLabel = new Label("Download speed: {something}", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        downloadSpeedLabel.setFontScale(1.2f);
        downloadSpeedLabel.getStyle().font.getData().markupEnabled = true;
        downloadSpeedLabel.setText("[#FFFFFF]Download speed: {something}");

        healthLabel = new Label("Health: {value}", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        healthLabel.setFontScale(1.2f);
        healthLabel.getStyle().font.getData().markupEnabled = true;
        healthLabel.setText("[#FFFFFF]Health: {value}");

        waveLabel = new Label("Wave {1 of 10}", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        waveLabel.setFontScale(1.2f);
        waveLabel.getStyle().font.getData().markupEnabled = true;
        waveLabel.setText("[#FFFFFF]Wave {1 of 10}");

        enemiesRemainingLabel = new Label(gameDataManager.getEnemiesRemaining() + " enemies remaining", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        enemiesRemainingLabel.setFontScale(1.2f);
        enemiesRemainingLabel.getStyle().font.getData().markupEnabled = true;
        enemiesRemainingLabel.setText("[#FFFFFF]" + gameDataManager.getEnemiesRemaining() + " enemies remaining");
    }

    private void initializeButtons() {
        pauseButton = new TextButton("Pause", skin);
        quitButton = new TextButton("Quit", skin);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("QuitButton", "Quit button clicked");
                gameScreen.quitGame();
            }
        });
    }

    private void initializeTables() {
        Table topTable = new Table();
        topTable.add(locationLabel).left().pad(10).expandX();
        topTable.add(uploadSpeedLabel).center().pad(10).expandX();
        topTable.add(downloadSpeedLabel).right().pad(10).expandX();

        Table leftTable = new Table();
        leftTable.add(healthLabel).left().pad(10).expandX().fillX();
        leftTable.row();
        leftTable.add(waveLabel).left().pad(10).expandX().fillX();
        leftTable.row();
        leftTable.add(enemiesRemainingLabel).left().pad(10).expandX().fillX().padBottom(370);

        Table towersTable = new Table();
        initializeTowers(towersTable);

        Table buttonTable = new Table();
        buttonTable.add(pauseButton).expandX().fillX().pad(5);
        buttonTable.row();
        buttonTable.add(quitButton).expandX().fillX().pad(5);


        Table mainTable = new Table();
        mainTable.add(towersTable).expand().fill().left();
        mainTable.add(buttonTable).expand().fill().right();

        table.top();
        table.add(topTable).expandX().fill();
        table.row();
        table.add(leftTable).expand().fill().row();
        table.add(mainTable).expand().fill().row();
    }

    public void addTower(Tower tower) {
        towers.add(tower);
        Gdx.app.log("InitializeGame", "Tower added at position: " + tower.getPosition());
    }

    public void placeTower(String towerType, float x, float y, float tileWidth, float tileHeight) {
        //Gdx.app.log("InitializeGame", "Attempting to place tower of type: " + selectedTowerType + " at: (" + x + ", " + y + ")");
        if (selectedTowerType == null) {
            Gdx.app.log("InitializeGame", "Tower type is null, cannot place tower.");
            return;
        }
        Tower tower = TowerFactory.createTower(selectedTowerType, new Vector2(x, y));
        if (tower != null) {
            addTower(tower);
            //Gdx.app.log("InitializeGame", "Tower created successfully: " + selectedTowerType);
            Table towerContent = new Table();
            Image towerImage = new Image(atlas.findRegion(selectedTowerType));
            towerContent.add(towerImage).expand().fill().row();
            Container<Table> newTower = new Container<>(towerContent);
            newTower.setSize(tileWidth, tileHeight);
            newTower.setPosition(x, y);
            newTower.setTransform(true);
            newTower.setBackground(whiteBackground); // Set the background of the new tower
            newTower.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showTowerStats(tower);
                }
            });
            gameScreen.getStage().addActor(newTower);
            //Gdx.app.log("InitializeGame", "New tower added at: (" + x + ", " + y + ") with size: (" + tileWidth + ", " + tileHeight + ")");
            if (selectedTower != null) {
                selectedTower.setBackground(whiteBackground);
                //Gdx.app.log("InitializeGame", "Deselecting tower: " + selectedTower);
            }
            selectedTower = null;
            selectedTowerType = null;
        } else {
            Gdx.app.log("InitializeGame", "Failed to create tower of type: " + selectedTowerType);
        }
    }

    public void updateTowers(float delta, List<Enemy> enemies, ShapeRenderer shapeRenderer) {
        for (Tower tower : towers) {
            if (tower instanceof ConcreteTower) {
                //Gdx.app.log("InitializeGame", "Updating ConcreteTower at position: " + tower.getPosition());
                ((ConcreteTower) tower).update(delta, enemies, shapeRenderer);
            }
        }
    }

    public void drawTowers(SpriteBatch spriteBatch) {
        for (Tower tower : towers) {
            if (tower instanceof ConcreteTower) {
                ((ConcreteTower) tower).draw(spriteBatch);
            }
        }
    }

    private void showTowerStats(Tower tower) {
        Dialog statsDialog = new Dialog("Tower Stats", skin) {
            @Override
            protected void result(Object object) {
                drawCircle = false; // When the dialog is closed
            }
        };

        Label.LabelStyle labelStyle = new Label.LabelStyle(skin.getFont("font"), skin.getColor("white"));

        statsDialog.text(new Label("Damage: " + tower.getDamage(), labelStyle));
        statsDialog.text(new Label("Range: " + tower.getRange(), labelStyle));
        statsDialog.text(new Label("Cooldown: " + tower.getCooldown(), labelStyle));
        statsDialog.text(new Label("Enemies in Range: " + tower.getEnemiesInRange(), labelStyle));
        statsDialog.button(new TextButton("OK", skin));
        statsDialog.show(gameScreen.getStage());

        // Set the flag and store the tower's position and range
        drawCircle = true;
        // Adjust the position to be centered (tower -> 32x32 pixels)
        towerPosition = new Vector2(tower.getPosition().x + 16, tower.getPosition().y + 16);
        towerRange = tower.getRange();
    }

    public Drawable createCircleDrawable(float radius, Color color) {
        int diameter = (int) (radius * 2);
        Pixmap pixmap = new Pixmap(diameter, diameter, Pixmap.Format.RGBA8888);
        color.a = 0.4f; // Opacity
        pixmap.setColor(color);
        pixmap.fillCircle(diameter / 2, diameter / 2, (int) radius);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(texture);
    }

    private void initializeTowers(Table towersTable) {
        String[] regionNames = {
            RegionNames.FAST_ACCESS,
            RegionNames.CABINETS,
            RegionNames.SERVER_CONTROL,
            RegionNames.HUB,
            RegionNames.SWITCH,
            RegionNames.CELL_TOWER,
            RegionNames.SATELLITE_TOWER,
            RegionNames.SIGNAL
        };

        float iconSize = 64 * Gdx.graphics.getDensity(); // Scale icon size based on screen density

        towersTable.setBackground(tableBorder);
        towersTable.setColor(1, 1, 1, 0.3f);

        // Hovered / not hovered table logic
        towersTable.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                towersTable.setColor(1, 1, 1, 1.0f);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                towersTable.setColor(1, 1, 1, 0.2f);
            }
        });

        for (String regionName : regionNames) {
            Image icon = createIcon(regionName, iconSize);
            String enumName = regionName.toUpperCase().replace("-", "_");
            int price = RegionPrices.valueOf(enumName).getPrice();
            Container<Table> iconContainer = createIconWithPriceContainer(icon, String.valueOf(price));
            iconContainer.setTouchable(Touchable.enabled); // Ensure the container is touchable
            addInputListeners(iconContainer, regionName); // Add input listeners to the container
            towersTable.add(iconContainer).size(iconSize, iconSize * 1.2f).pad(10);
        }

        // Ensure the table layout is updated
        towersTable.pack();
        towersTable.layout();
    }

    private void addInputListeners(Container<Table> container, String towerType) {
        container.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedTower == container) {
                    container.setBackground(whiteBackground);
                    selectedTower = null;
                    selectedTowerType = null;
                    //Gdx.app.log("InitializeGame", "Deselected tower: " + container);
                } else {
                    if (selectedTower != null) {
                        selectedTower.setBackground(whiteBackground);
                        //Gdx.app.log("InitializeGame", "Deselected previous tower: " + selectedTower);
                    }
                    container.setBackground(createGrayBackground());
                    selectedTower = container;
                    selectedTowerType = towerType;
                    //Gdx.app.log("InitializeGame", "Selected tower: " + container);
                }
            }
        });
    }

    public void setSelectedTower(Container<Table> selectedTower) {
        this.selectedTower = selectedTower;
        if (selectedTower != null) {
            Gdx.app.log("InitializeGame", "Selected tower set to: " + selectedTower);
        } else {
            Gdx.app.log("InitializeGame", "Deselected tower");
        }
    }

    private Image createIcon(String regionName, float size) {
        Image icon = new Image(atlas.findRegion(regionName));
        icon.setSize(size, size);
        return icon;
    }

    private Drawable createGrayBackground() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.5f, 0.5f, 0.5f, 1);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(texture);
    }

    private Container<Table> createIconWithPriceContainer(Image icon, String price) {
        Table iconTable = new Table();
        iconTable.add(icon).row();
        Label priceLabel = new Label(price, skin);
        iconTable.add(priceLabel).center();

        Container<Table> container = new Container<>(iconTable);
        container.setBackground(whiteBackground);
        container.setTouchable(Touchable.enabled);

        //Drawable grayBackground = createGrayBackground();
        //Drawable border = createTableBorder();

        container.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
        });

        return container;
    }

    public Container<Table> getSelectedTower() { return selectedTower;}

    private Drawable createWhiteBackground() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(texture);
    }

    private Drawable createTableBorder() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        Color greenColor = skin.getColor("color");
        pixmap.setColor(greenColor);
        pixmap.drawRectangle(0, 0, 1, 1);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(texture);
    }

    public void updateEnemiesRemainingLabel() {
        enemiesRemainingLabel.setText(gameDataManager.getEnemiesRemaining() + " enemies remaining");
    }

    public Table getTable() {
        return table;
    }
}
