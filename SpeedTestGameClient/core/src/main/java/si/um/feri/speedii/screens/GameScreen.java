package si.um.feri.speedii.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.speedii.SpeediiApp;
import si.um.feri.speedii.classes.SessionManager;
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

import si.um.feri.speedii.towerdefense.config.RoundDifficulty;

public class GameScreen implements Screen {
    //private final SpeediiApp app;
    //private final AssetManager assetManager;
    private LoadMap loadMap;

    private Viewport gameViewport;
    private ShapeRenderer shapeRenderer;

    private Stage stage;
    private InitializeGame initializeGame;
    private GameDataManager gameDataManager;

    private String location;
    private float downloadSpeed;

    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;

    private SpriteBatch spriteBatch;

    private EnemySpawner enemySpawner;
    private List<Enemy> enemies = new ArrayList<>();
    //private float spawnTimer = 0f;
    //private int enemyCount = 0;
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

    private final Skin skin;

    private SessionManager sessionManager;


    public GameScreen(SpeediiApp app, SessionManager sessionManager, DIFFICULTY selectedDifficulty, String location, String downloadSpeed) {
        this.app = app;
        this.assetManager = app.getAssetManager();
        this.selectedDifficulty = selectedDifficulty;
        this.skin = app.getAssetManager().get(AssetDescriptors.UI_SKIN);
        this.sessionManager = sessionManager;
        this.location = location;
        this.downloadSpeed = downloadSpeed != null ? Float.parseFloat(downloadSpeed) : 0f;
        gameDataManager = new GameDataManager();
        gameDataManager.setLocation(location);
        gameDataManager.setDownloadSpeed(this.downloadSpeed);
    }

    @Override
    public void show() {
        // Start game
        gameViewport = new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer = new ShapeRenderer();

        stage = new Stage(gameViewport);
        //stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        pauseContainer = new PauseContainer(app, skin, gameDataManager);
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

        // Initialize enemy spawner
        enemySpawner = new EnemySpawner(loadMap, gameLogic);
        //spawnEnemies();

        // Initialize map renderer
        mapRenderer = new OrthogonalTiledMapRenderer(loadMap.getMap());
        camera = new OrthographicCamera();
        camera.setToOrtho(false, CAMERA_VIEWPORT_WIDTH, CAMERA_VIEWPORT_HEIGHT);
        camera.update();

        // Initialize game
        initializeGame = new InitializeGame(gameDataManager, skin, this);
        initializeGame.getTable().setFillParent(true);
        stage.addActor(initializeGame.getTable());
        //initializeGame.initializeUI();

        // Initialize TileHoverHandler
        MapLayer fieldLayer = loadMap.getFieldLayer();
        if (fieldLayer instanceof TiledMapTileLayer) {
            tileHoverHandler = new TileHoverHandler((TiledMapTileLayer) fieldLayer, camera, initializeGame, gameDataManager);
        } else {
            Gdx.app.log("GameScreen", "Field layer not found or is not a TiledMapTileLayer");
        }

        stage.addActor(initializeGame.getTable());
    }

    private void spawnEnemies() {
        RoundDifficulty roundDifficulty = new RoundDifficulty(selectedDifficulty, gameDataManager);
        List<RoundDifficulty.Wave> waves = roundDifficulty.getWaves();

        int totalEnemies = 0;
        for (RoundDifficulty.Wave wave : waves) {
            for (RoundDifficulty.EnemyConfig enemyConfig : wave.getEnemies()) {
                totalEnemies += enemyConfig.getCount();
                for (int i = 0; i < enemyConfig.getCount(); i++) {
                    float spawnDelay = enemyConfig.getDelay() + (i * 0.3f);
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            Enemy enemy = enemySpawner.spawnEnemyByType(enemyConfig.getType(), enemyConfig.getHealth(), enemyConfig.getSpeed(), gameDataManager);
                            if (enemy != null) {
                                enemies.add(enemy);
                            }
                        }
                    }, spawnDelay);
                }
            }
        }
        gameDataManager.setEnemiesRemaining(totalEnemies);
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

        initializeGame.drawTowers(spriteBatch);

        // Update and draw enemies
        for (Iterator<Enemy> iterator = enemies.iterator(); iterator.hasNext();) {
            Enemy enemy = iterator.next();
            if (enemy.isDead()) {
                // Add money
                gameDataManager.addMoney(enemy.getMoneyReward());
                gameDataManager.addmoneyGot(enemy.getMoneyReward());
                iterator.remove();
                gameDataManager.decrementEnemiesRemaining();
                gameDataManager.incrementEnemiesKilled();
                initializeGame.updateEnemiesRemainingLabel();
                // Money logic
                initializeGame.updateLabels();
                continue;
            }

            if (enemy.isAtEnd()) {
                iterator.remove();
                gameDataManager.decrementEnemiesRemaining();
                initializeGame.updateEnemiesRemainingLabel();
                initializeGame.updateLabels();
                // Decrease 5 health for each enemy
                gameDataManager.setHealth(gameDataManager.getHealth() - 5);
                continue;
            }

            if (isRoundActive && !isPaused) {
                enemy.update(delta);
            }
            enemy.draw(spriteBatch, enemy.getX(), enemy.getY());
            enemy.renderHealthBar(spriteBatch);
        }

        // Check if all enemies are defeated
        checkAndStartNextRound();

        // Check if health is 0 or less
        if (gameDataManager.getHealth() <= 0) {
            app.setScreen(new GameOverScreen(app, "You Lose!", gameDataManager, sessionManager));
        }

        // Draw the tower range circle
        //drawTowerRangeCircle();

        // Update and draw towers
        if (!isPaused) { initializeGame.updateTowers(delta, enemies, shapeRenderer); }

        // End the sprite batch
        spriteBatch.end();

        if (tileHoverHandler != null) {
            tileHoverHandler.render();
        }

        if (isPaused) {
            pauseContainer.setVisible(true);
            pauseContainer.toFront();
            pauseContainer.updateStatistics();
        } else {
            pauseContainer.setVisible(false);
        }

        stage.act(delta);
        stage.draw();
    }

    private void checkAndStartNextRound() {
        if (enemies.isEmpty()) {
            if (gameDataManager.getCurrentWave() >= gameDataManager.getTotalWaves()) {
                app.setScreen(new GameOverScreen(app, "You Win!", gameDataManager, sessionManager));
            } else {
                gameDataManager.incrementWave();
                initializeGame.updateLabels();
                spawnEnemies();
            }
        }
    }

    private void drawTowerRangeCircle() {
        if (initializeGame.drawCircle) {
            Drawable circleDrawable = initializeGame.createCircleDrawable(initializeGame.towerRange, skin.getColor("color"));
            circleDrawable.draw(spriteBatch, initializeGame.towerPosition.x - initializeGame.towerRange, initializeGame.towerPosition.y - initializeGame.towerRange, initializeGame.towerRange * 2, initializeGame.towerRange * 2);
        }
    }

    public TileHoverHandler getTileHoverHandler() { return tileHoverHandler; }

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

    public void quitGame() {
        app.setScreen(new MenuScreen(app, sessionManager));
    }

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
