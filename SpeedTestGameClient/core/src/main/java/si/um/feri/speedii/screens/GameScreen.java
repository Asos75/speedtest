package si.um.feri.speedii.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.speedii.SpeediiApp;
import si.um.feri.speedii.screens.GameScreenComponents.InitializeGame;

// Map
import si.um.feri.speedii.screens.GameScreenComponents.LoadMap;
import si.um.feri.speedii.towerdefense.config.DIFFICULTY;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;

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
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        // Update camera
        camera.update();
        mapRenderer.setView(camera);

        // Render map
        mapRenderer.render();

        stage.act(delta);
        stage.draw();
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
