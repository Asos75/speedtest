package si.um.feri.speedii.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.speedii.SpeediiApp;
import si.um.feri.speedii.screens.GameScreenComponents.InitializeGame;

// Map
import si.um.feri.speedii.screens.GameScreenComponents.LoadMap;
import si.um.feri.speedii.towerdefense.config.DIFFICULTY;
import si.um.feri.speedii.towerdefense.config.GameDataManager;
import si.um.feri.speedii.towerdefense.gameobjects.TileHoverHandler;
import si.um.feri.speedii.towerdefense.gameobjects.enemies.Enemy;
import si.um.feri.speedii.towerdefense.gameobjects.enemies.EnemySpawner;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;

// Path
import com.badlogic.gdx.math.Vector2;

// Enemy
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import si.um.feri.speedii.towerdefense.logic.GameLogic;

import si.um.feri.speedii.assets.AssetDescriptors;

import si.um.feri.speedii.screens.GameScreenComponents.PauseContainer;


public class GameScreen implements Screen {
    //private final SpeediiApp app;
    //private final AssetManager assetManager;
    private LoadMap loadMap;

    private Viewport gameViewport;
    private ShapeRenderer shapeRenderer;

    private Stage stage;
    private InitializeGame initializeGame;
    private GameDataManager gameDataManager;

    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;

    private SpriteBatch spriteBatch;

    private EnemySpawner enemySpawner;
    private List<Enemy> enemies = new ArrayList<>();
    private float spawnTimer = 0f;
    private int enemyCount = 0;
    private GameLogic gameLogic;

    private static final float CAMERA_VIEWPORT_WIDTH = 35 * 32;
    private static final float CAMERA_VIEWPORT_HEIGHT = 20 * 32;

    private boolean isRoundActive = true;

    private TileHoverHandler tileHoverHandler;

    private final SpeediiApp app;
    private final AssetManager assetManager;
    private DIFFICULTY selectedDifficulty;

    private PauseContainer pauseContainer;
    private boolean isPaused = false;

    private Skin skin;

    public GameScreen(SpeediiApp app, DIFFICULTY selectedDifficulty) {
        this.app = app;
        this.assetManager = app.getAssetManager();
        this.selectedDifficulty = selectedDifficulty;
        this.skin = app.getAssetManager().get(AssetDescriptors.UI_SKIN);
    }

    @Override
    public void show() {
        // Start game
        gameViewport = new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer = new ShapeRenderer();

        stage = new Stage(gameViewport);
        //stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        pauseContainer = new PauseContainer(app, skin);
        pauseContainer.setFillParent(true);
        pauseContainer.setVisible(false);
        stage.addActor(pauseContainer);

        // Log to verify PauseContainer is added

        // Load map
        loadMap = new LoadMap();
        loadMap.loadMap(selectedDifficulty);

        // Initialize sprite batch
        spriteBatch = new SpriteBatch();

        // Initialize game logic
        Vector2 spawnPoint = loadMap.getStartPoint();
        Vector2 endPoint = loadMap.getEndPoint();

        List<Vector2> goRightPoints = loadMap.getGoRightPoints();
        List<Vector2> goUpPoints = loadMap.getGoUpPoints();
        List<Vector2> goDownPoints = loadMap.getGoDownPoints();

        // MEDIUM MAP
        Vector2 intersection = loadMap.getIntersection();
        Vector2[] teleportEnterDown = loadMap.getTeleportEnterDown();
        Vector2[] teleportEnterUp = loadMap.getTeleportEnterUp();

        // HARD MAP
        Vector2 teleportIntersectionEnterGoRight = loadMap.getTeleportIntersectionEnterGoRight();
        List<Vector2> teleportIntersectionGoRight = loadMap.getTeleportIntersectionGoRight();
        List<Vector2> teleportIntersectionEnter = loadMap.getTeleportIntersectionEnter();
        Vector2 teleportIntersectionLeave = loadMap.getTeleportIntersectionLeave();

        // VERY HARD MAP
        Vector2 teleportIntersectionGoDown = loadMap.getTeleportIntersectionGoDown();
        Vector2 teleportIntersectionGoUp = loadMap.getTeleportIntersectionGoUp();
        Vector2 teleportIntersectionEnterGoUpGoDown = loadMap.getTeleportIntersectionEnterGoUpGoDown();

        gameLogic = new GameLogic(spawnPoint, goUpPoints, goRightPoints, goDownPoints, intersection, teleportEnterDown, teleportEnterUp, endPoint,
                teleportIntersectionEnterGoRight, teleportIntersectionGoRight, teleportIntersectionEnter, teleportIntersectionLeave,
                teleportIntersectionEnterGoUpGoDown, teleportIntersectionGoDown, teleportIntersectionGoUp);

        // Initialize game data manager
        gameDataManager = new GameDataManager();

        // Initialize enemy spawner
        enemySpawner = new EnemySpawner(loadMap, gameLogic);
        spawnEnemies();

        // Initialize map renderer
        mapRenderer = new OrthogonalTiledMapRenderer(loadMap.getMap());
        camera = new OrthographicCamera();
        camera.setToOrtho(false, CAMERA_VIEWPORT_WIDTH, CAMERA_VIEWPORT_HEIGHT);
        camera.update();

        // Initialize game
        initializeGame = new InitializeGame(gameDataManager, skin, this);
        initializeGame.getTable().setFillParent(true);
        //initializeGame.initializeUI();

        // Initialize TileHoverHandler
        MapLayer fieldLayer = loadMap.getFieldLayer();
        if (fieldLayer instanceof TiledMapTileLayer) {
            tileHoverHandler = new TileHoverHandler((TiledMapTileLayer) fieldLayer, camera, initializeGame);
        } else {
            Gdx.app.log("GameScreen", "Field layer not found or is not a TiledMapTileLayer");
        }

        stage.addActor(initializeGame.getTable());
    }

    private void spawnEnemies() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Enemy enemy = enemySpawner.spawnEnemy();
                if (enemy != null) {
                    enemies.add(enemy);
                }
            }
        }, 4f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Enemy enemy = enemySpawner.spawnEnemy();
                if (enemy != null) {
                    enemies.add(enemy);
                }
            }
        }, 4.4f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Enemy enemy = enemySpawner.spawnEnemy();
                if (enemy != null) {
                    enemies.add(enemy);
                }
            }
        }, 4.6f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Enemy enemy = enemySpawner.spawnEnemy();
                if (enemy != null) {
                    enemies.add(enemy);
                }
            }
        }, 5.1f);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        // Update camera
        camera.update();
        mapRenderer.setView(camera);

        // Render map
        mapRenderer.render();

        // Set the projection matrix for the shape renderer
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Begin the sprite batch
        spriteBatch.begin();

        // Update and draw enemies
        for (Iterator<Enemy> iterator = enemies.iterator(); iterator.hasNext();) {
            Enemy enemy = iterator.next();
            if (enemy.isDead()) {
                iterator.remove();
                continue;
            }
            if (isRoundActive && !isPaused) {
                enemy.update(delta);
            }
            enemy.draw(spriteBatch, enemy.getX(), enemy.getY());
            enemy.renderHealthBar(spriteBatch);
        }

        // Draw the tower range circle
        //drawTowerRangeCircle();

        // Update and draw towers
        if (!isPaused) { initializeGame.updateTowers(delta, enemies, shapeRenderer); }
        initializeGame.drawTowers(spriteBatch);

        // End the sprite batch
        spriteBatch.end();

        if (tileHoverHandler != null) {
            tileHoverHandler.render();
        }

        if (isPaused) {
            pauseContainer.setVisible(true);
            pauseContainer.toFront();
        } else {
            pauseContainer.setVisible(false);
        }

        stage.act(delta);
        stage.draw();
    }

    private void drawTowerRangeCircle() {
        if (initializeGame.drawCircle) {
            Drawable circleDrawable = initializeGame.createCircleDrawable(initializeGame.towerRange, skin.getColor("color"));
            circleDrawable.draw(spriteBatch, initializeGame.towerPosition.x - initializeGame.towerRange, initializeGame.towerPosition.y - initializeGame.towerRange, initializeGame.towerRange * 2, initializeGame.towerRange * 2);
        }
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
        camera.setToOrtho(false, CAMERA_VIEWPORT_WIDTH, CAMERA_VIEWPORT_HEIGHT);
        camera.update();
    }

    public boolean getPausable() { return isPaused; }

    public void pauseGame() { isPaused = true; }

    public void resumeGame() { isPaused = false; }

    public void quitGame() { Gdx.app.exit(); }

    public Stage getStage() { return stage; }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        stage.dispose();
        if (tileHoverHandler != null) {
            tileHoverHandler.dispose();
        }
    }
}
