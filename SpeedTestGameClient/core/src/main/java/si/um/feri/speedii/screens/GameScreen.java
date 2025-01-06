package si.um.feri.speedii.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.speedii.SpeediiApp;
import si.um.feri.speedii.screens.GameScreenComponents.InitializeGame;

// Map
import si.um.feri.speedii.screens.GameScreenComponents.LoadMap;
import si.um.feri.speedii.towerdefense.config.DIFFICULTY;
import si.um.feri.speedii.towerdefense.gameobjects.enemies.Enemy;
import si.um.feri.speedii.towerdefense.gameobjects.enemies.EnemySpawner;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;

// Path
import com.badlogic.gdx.math.Vector2;

// Enemy
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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
    private Enemy enemy;

    public GameScreen(/*final SpeediiApp app*/) {
        //this.app = app;
        //this.assetManager = app.getAssetManager();
    }

    @Override
    public void show() {
        // Start game
        gameViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer = new ShapeRenderer();
        stage = new Stage(gameViewport);

        // Load map
        loadMap = new LoadMap();
        loadMap.loadMap(DIFFICULTY.VERY_EASY); // Example usage with VERY_EASY difficulty

        // Initialize map renderer
        // This value is hard coded scaling so it's like a plan B if transform matrix doesn't work (1/0.66f full screen)
        mapRenderer = new OrthogonalTiledMapRenderer(loadMap.getMap(), 1/0.66f);
        //mapRenderer = new OrthogonalTiledMapRenderer(loadMap.getMap(), 1f);
        camera = new OrthographicCamera();
        //camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, loadMap.getMapWidth(), loadMap.getMapHeight());

        // Scale the map renderer to fit the screen
        /*float scaleX = (float) Gdx.graphics.getWidth() / loadMap.getMapWidth();
        float scaleY = (float) Gdx.graphics.getHeight() / loadMap.getMapHeight();
        mapRenderer.getBatch().getTransformMatrix().scale(scaleX, scaleY, 1);*/

        // Initialize game
        initializeGame = new InitializeGame();
        initializeGame.getTable().setFillParent(true);
        stage.addActor(initializeGame.getTable());

        // Initialize sprite batch
        spriteBatch = new SpriteBatch();

        // Initialize enemy spawner
        enemySpawner = new EnemySpawner(loadMap);
        enemy = enemySpawner.spawnEnemy();
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

        if (enemy != null) {
            enemy.update(delta);
            enemy.draw(spriteBatch, enemy.getX(), enemy.getY());
        }

        // End the sprite batch
        spriteBatch.end();

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
        camera.setToOrtho(false, width, height);

        // Scale the map renderer to fit the new screen size
        /*float scaleX = (float) width / loadMap.getMapWidth();
        float scaleY = (float) height / loadMap.getMapHeight();
        mapRenderer.getBatch().getTransformMatrix().scale(scaleX, scaleY, 1);*/
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
    }
}
