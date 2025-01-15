package si.um.feri.speedii.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.speedii.SpeediiApp;
import si.um.feri.speedii.screens.GameScreenComponents.InitializeGame;

// Map
import si.um.feri.speedii.screens.GameScreenComponents.LoadMap;
import si.um.feri.speedii.towerdefense.config.DIFFICULTY;
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
import java.util.Arrays;
import java.util.List;
import si.um.feri.speedii.towerdefense.logic.GameLogic;

public class GameScreen implements Screen {
    //private final SpeediiApp app;
    //private final AssetManager assetManager;
    private LoadMap loadMap;

    private Viewport gameViewport;
    private ShapeRenderer shapeRenderer;

    private Stage stage;
    private InitializeGame initializeGame;

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

    public GameScreen() {}

    @Override
    public void show() {
        // Start game
        gameViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer = new ShapeRenderer();
        stage = new Stage(gameViewport);

        // Load map
        loadMap = new LoadMap();
        //loadMap.loadMap(DIFFICULTY.VERY_EASY);
        //loadMap.loadMap(DIFFICULTY.EASY);
        //loadMap.loadMap(DIFFICULTY.MEDIUM);
        //loadMap.loadMap(DIFFICULTY.HARD);
        loadMap.loadMap(DIFFICULTY.VERY_HARD);

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

        // Log the results of gameLogic
        if (false) {
            System.out.println("Spawn point: " + gameLogic.spawnPoint);
            System.out.println("End point: " + gameLogic.endPoint);
            System.out.println("Go right points: " + gameLogic.goRightPoints);
            System.out.println("Go up points: " + gameLogic.goUpPoints);
            System.out.println("Go down points: " + gameLogic.goDownPoints);
            System.out.println("Intersection: " + gameLogic.intersection);
            System.out.println("Teleport enter down: " + Arrays.toString(gameLogic.teleportEnterDown));
            System.out.println("Teleport enter up: " + Arrays.toString(gameLogic.teleportEnterUp));
        }

        // Initialize enemy spawner
        enemySpawner = new EnemySpawner(loadMap, gameLogic);
        spawnEnemies();

        // Initialize TileHoverHandler
        MapLayer fieldLayer = loadMap.getFieldLayer();
        if (fieldLayer instanceof TiledMapTileLayer) {
            tileHoverHandler = new TileHoverHandler((TiledMapTileLayer) fieldLayer, camera);
        } else {
            Gdx.app.log("GameScreen", "Field layer not found or is not a TiledMapTileLayer");
        }

        // Initialize map renderer
        mapRenderer = new OrthogonalTiledMapRenderer(loadMap.getMap());
        camera = new OrthographicCamera();
        camera.setToOrtho(false, CAMERA_VIEWPORT_WIDTH, CAMERA_VIEWPORT_HEIGHT);
        camera.update();

        // Initialize game
        initializeGame = new InitializeGame();
        initializeGame.getTable().setFillParent(true);
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
        }, 0f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Enemy enemy = enemySpawner.spawnEnemy();
                if (enemy != null) {
                    enemies.add(enemy);
                }
            }
        }, 0.4f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Enemy enemy = enemySpawner.spawnEnemy();
                if (enemy != null) {
                    enemies.add(enemy);
                }
            }
        }, 0.6f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Enemy enemy = enemySpawner.spawnEnemy();
                if (enemy != null) {
                    enemies.add(enemy);
                }
            }
        }, 1.1f);
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

        // Show path
        //showPath();

        // Begin the sprite batch
        spriteBatch.begin();

        // Update and draw enemies
        for (Enemy enemy : enemies) {
            if (isRoundActive) {
                enemy.update(delta);
            }
            enemy.draw(spriteBatch, enemy.getX(), enemy.getY());
            enemy.renderHealthBar(spriteBatch);
        }

        // End the sprite batch
        spriteBatch.end();

        if (tileHoverHandler != null) {
            tileHoverHandler.render();
        }

        stage.act(delta);
        stage.draw();
    }

    private void showPath() {
        Vector2 startPoint = loadMap.getStartPoint();
        Vector2 endPoint = loadMap.getEndPoint();

        if (startPoint != null && endPoint != null) {
            // Calculate the middle points of the SpawnPoint and EndPoint
            Vector2 startMiddle = new Vector2(startPoint.x * 1.6f, startPoint.y * 1.7f);
            Vector2 endMiddle = new Vector2(endPoint.x * 1.55f, endPoint.y * 1.7f);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0, 1, 0, 1); // Green color

            // Draw dashed line
            float dashLength = 10f;
            float totalLength = startMiddle.dst(endMiddle);
            int numDashes = (int) (totalLength / dashLength);
            Vector2 dashVector = new Vector2(endMiddle).sub(startMiddle).nor().scl(dashLength);

            Gdx.gl.glLineWidth(3);

            for (int i = 0; i < numDashes; i++) {
                if (i % 2 == 0) {
                    Vector2 dashStart = new Vector2(startMiddle).add(dashVector.x * i, dashVector.y * i);
                    Vector2 dashEnd = new Vector2(startMiddle).add(dashVector.x * (i + 1), dashVector.y * (i + 1));
                    shapeRenderer.line(dashStart, dashEnd);
                }
            }

            // Draw arrowhead
            float arrowHeadSize = 20f;
            Vector2 arrowDir = new Vector2(endMiddle).sub(startMiddle).nor();
            Vector2 arrowLeft = new Vector2(arrowDir).rotateDeg(135).scl(arrowHeadSize);
            Vector2 arrowRight = new Vector2(arrowDir).rotateDeg(-135).scl(arrowHeadSize);

            shapeRenderer.line(endMiddle, new Vector2(endMiddle).add(arrowLeft));
            shapeRenderer.line(endMiddle, new Vector2(endMiddle).add(arrowRight));

            shapeRenderer.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
        //camera.setToOrtho(false, width, height);
        camera.setToOrtho(false, CAMERA_VIEWPORT_WIDTH, CAMERA_VIEWPORT_HEIGHT);
        camera.update();
    }

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
