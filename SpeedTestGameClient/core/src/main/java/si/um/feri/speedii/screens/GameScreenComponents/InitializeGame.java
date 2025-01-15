package si.um.feri.speedii.screens.GameScreenComponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.speedii.assets.AssetDescriptors;
import si.um.feri.speedii.assets.RegionNames;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.graphics.g2d.NinePatch;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import si.um.feri.speedii.towerdefense.config.GameDataManager;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class InitializeGame {
    private final Skin skin;
    private final Table table;
    private final GameDataManager gameDataManager;
    private Stage stage;

    public Label locationLabel;
    public Label uploadSpeedLabel;
    public Label downloadSpeedLabel;
    public Label healthLabel;
    public Label waveLabel;
    public Label enemiesRemainingLabel;
    public TextButton pauseButton;
    public TextButton quitButton;

    Drawable whiteBackground;
    Drawable tableBorder;

    TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("images/spediiIcons.atlas"));

    public InitializeGame(GameDataManager gameDataManager) {
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        this.table = new Table();
        this.gameDataManager = gameDataManager;
        this.stage = new Stage(new ScreenViewport());
        initializeUI();
    }

    private void initializeUI() {
        whiteBackground = createWhiteBackground();
        tableBorder = createTableBorder();
        table.setFillParent(true);

        initializeLabels();
        initializeButtons();
        initializeTables();

        stage.addActor(table); // Add the table to the stage

        // Set the stage as the input processor
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void initializeLabels() {
        locationLabel = new Label("Location: {name}", skin);
        uploadSpeedLabel = new Label("Upload speed: {something}", skin);
        downloadSpeedLabel = new Label("Download speed: {something}", skin);
        healthLabel = new Label("Health: {value}", skin);
        waveLabel = new Label("Wave {1 of 10}", skin);
        enemiesRemainingLabel = new Label(gameDataManager.getEnemiesRemaining() + " enemies remaining", skin);
    }

    private void initializeButtons() {
        pauseButton = new TextButton("Pause", skin);
        quitButton = new TextButton("Quit", skin);
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
        buttonTable.add(pauseButton).expandX().fillX().pad(10);
        buttonTable.row();
        buttonTable.add(quitButton).expandX().fillX().pad(10);

        Table mainTable = new Table();
        mainTable.add(towersTable).expand().fill().left();
        mainTable.add(buttonTable).expand().fill().right();

        table.top();
        table.add(topTable).expandX().fill();
        table.row();
        table.add(leftTable).expand().fill().row();
        table.add(mainTable).expand().fill().row();
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

        for (String regionName : regionNames) {
            Image icon = createIcon(regionName, iconSize);
            Container<Table> iconContainer = createIconWithPriceContainer(icon, "100");
            iconContainer.setTouchable(Touchable.enabled); // Ensure the container is touchable
            addInputListeners(iconContainer); // Add input listeners to the container
            towersTable.add(iconContainer).size(iconSize, iconSize * 1.2f).pad(10);
        }

        // Ensure the table layout is updated
        towersTable.pack();
        towersTable.layout();
    }

    private void addInputListeners(Container<Table> container) {
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

        container.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                container.setBackground(createGrayBackground());
                container.setDebug(true, true); // Enable border for debugging
            }
        });
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

        Drawable grayBackground = createGrayBackground();
        Drawable border = createTableBorder();

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

        container.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                container.setBackground(grayBackground);
                container.setDebug(true, true); // Enable border for debugging
            }
        });

        return container;
    }

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
        pixmap.setColor(0, 0, 0, 1);
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
