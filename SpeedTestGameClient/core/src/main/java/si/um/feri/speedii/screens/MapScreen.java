package si.um.feri.speedii.screens;

import static si.um.feri.speedii.utils.Constants.ZOOM;
import static si.um.feri.speedii.utils.MapRasterTiles.TILE_SIZE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.io.IOException;
import java.util.List;

import si.um.feri.speedii.SpeediiApp;
import si.um.feri.speedii.assets.AssetDescriptors;
import si.um.feri.speedii.assets.RegionNames;
import si.um.feri.speedii.classes.Measurement;
import si.um.feri.speedii.classes.MobileTower;
import si.um.feri.speedii.classes.Pair;
import si.um.feri.speedii.classes.SessionManager;
import si.um.feri.speedii.dao.http.HttpMeasurement;
import si.um.feri.speedii.dao.http.HttpMobileTower;
import si.um.feri.speedii.screens.mapcomponents.ScrollWheelInputProcessor;
import si.um.feri.speedii.utils.Constants;
import si.um.feri.speedii.utils.Geolocation;
import si.um.feri.speedii.utils.MapRasterTiles;
import si.um.feri.speedii.utils.ZoomXY;
import si.um.feri.speedii.screens.mapcomponents.MapOverlay;

public class MapScreen implements Screen, GestureDetector.GestureListener {

    boolean debug = false;
    private final SpeediiApp app;

    private ShapeRenderer shapeRenderer;
    private Vector3 touchPosition;


    private TiledMap tiledMap;
    int height = Gdx.graphics.getHeight();
    private TiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;

    private final AssetManager assetManager;
    private Texture[] mapTiles;
    private ZoomXY beginTile;   // top left tile

    // center geolocation
    private final Geolocation CENTER_GEOLOCATION = new Geolocation(46.557314, 15.637771);

    // test marker
    private final Geolocation MARKER_GEOLOCATION = new Geolocation(46.559070, 15.638100);

    // MapOverlay instance
    private MapOverlay mapOverlay;
    private SessionManager sessionManager;

    private BitmapFont font;
    private SpriteBatch spriteBatch;
    private String speedInfo;
    private Vector2 speedInfoPosition;

    TextureAtlas atlas;
    private TextureRegion mobileTowerTexture;

    private List<Pair<Vector2, MobileTower>> mobileTowerPositions;

    private Stage stage;
    private Window speedInfoWindow;
    private Label speedInfoLabel;
    private Label difficultyLabel;
    private Window towerInfoWindow;
    private Label providerLabel;
    private Label typeLabel;
    private Label confirmedLabel;

    private Skin skin;

    private Vector2 drawnTower;
    private int towerRadius = 600;

    private boolean drawGrid = true;
    private float gridOpacity = Constants.OVERLAY_ALPHA;
    private boolean drawMobileTowers = true;
    private int minSpeed = 0;
    private int maxSpeed = 0;
    private int selectedDifficulty = 0;



    public MapScreen(SpeediiApp app, SessionManager sessionManager, AssetManager assetManager) {
        Gdx.graphics.setWindowedMode(Constants.HUD_WIDTH, Constants.HUD_HEIGHT);
        this.app = app;
        this.sessionManager = sessionManager;
        this.font = new BitmapFont();
        this.spriteBatch = new SpriteBatch();
        this.speedInfo = "";
        this.assetManager = assetManager;
        this.speedInfoPosition = new Vector2();

        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("skin/flat-earth-ui.json"));

        createActors();
    }

    private void createActors(){
        //SPEED INFO WINDOW
        speedInfoWindow = new Window("Average speed", skin);
        speedInfoLabel = new Label("", skin);
        difficultyLabel = new Label("", skin);
        TextButton playButton = new TextButton("Play", skin);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO - launch game with difficulty selected

                System.out.println("Button clicked");
            }
        });

        speedInfoWindow.add(speedInfoLabel).pad(10);
        speedInfoWindow.row();
        speedInfoWindow.add(difficultyLabel).pad(10);
        speedInfoWindow.row();
        speedInfoWindow.add(playButton).pad(10);
        speedInfoWindow.pack();
        speedInfoWindow.setVisible(false);

        towerInfoWindow = new Window("Tower Info", skin);
        providerLabel = new Label("", skin);
        typeLabel = new Label("", skin);
        confirmedLabel = new Label("", skin);

        towerInfoWindow.add(providerLabel).left().pad(10);
        towerInfoWindow.row();
        towerInfoWindow.add(typeLabel).left().pad(10);
        towerInfoWindow.row();
        towerInfoWindow.add(confirmedLabel).left().pad(10);
        towerInfoWindow.pack();
        towerInfoWindow.setVisible(false);



        TextButton toggleGridButton = new TextButton("Toggle Grid", skin);
        TextButton toggleTowersButton = new TextButton("Toggle Towers", skin);
        Slider gridOpacitySlider = new Slider(0.0f, 1.0f, 0.01f, false, skin);

        // Set button positions
        toggleTowersButton.setPosition(10, Gdx.graphics.getHeight() - 50);
        toggleGridButton.setPosition(10, Gdx.graphics.getHeight() - 100);
        gridOpacitySlider.setPosition(10, Gdx.graphics.getHeight() - 150);

        // Add listeners to buttons
        toggleGridButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                drawGrid = !drawGrid;
                gridOpacitySlider.setVisible(drawGrid);
            }
        });

        toggleTowersButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                drawMobileTowers = !drawMobileTowers;
                if(drawnTower != null) {
                    drawnTower = null;
                }
            }
        });

        gridOpacitySlider.setValue(gridOpacity);
        gridOpacitySlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gridOpacity = gridOpacitySlider.getValue();
            }
        });

        stage.addActor(gridOpacitySlider);
        stage.addActor(toggleGridButton);
        stage.addActor(toggleTowersButton);
        stage.addActor(speedInfoWindow);
        stage.addActor(towerInfoWindow);
    }


    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        camera.position.set(Constants.MAP_WIDTH / 2f, Constants.MAP_HEIGHT / 2f, 0);
        camera.viewportWidth = Constants.MAP_WIDTH / 2f;
        camera.viewportHeight = Constants.MAP_HEIGHT / 2f;
        camera.zoom = 2f;
        camera.update();

        touchPosition = new Vector3();

        try {
            //in most cases, geolocation won't be in the center of the tile because tile borders are predetermined (geolocation can be at the corner of a tile)
            ZoomXY centerTile = MapRasterTiles.getTileNumber(CENTER_GEOLOCATION.lat, CENTER_GEOLOCATION.lng, ZOOM);
            mapTiles = MapRasterTiles.getRasterTileZone(centerTile, Constants.NUM_TILES);
            //you need the beginning tile (tile on the top left corner) to convert geolocation to a location in pixels.
            beginTile = new ZoomXY(ZOOM, centerTile.x - ((Constants.NUM_TILES - 1) / 2), centerTile.y - ((Constants.NUM_TILES - 1) / 2));
        } catch (IOException e) {
            e.printStackTrace();
        }

        atlas = assetManager.get(AssetDescriptors.IMAGES);
        mobileTowerTexture =  (atlas.findRegion(RegionNames.CELL_TOWER));
        tiledMap = new TiledMap();
        MapLayers layers = tiledMap.getLayers();

        TiledMapTileLayer layer = new TiledMapTileLayer(Constants.NUM_TILES, Constants.NUM_TILES, TILE_SIZE, TILE_SIZE);
        int index = 0;
        for (int j = Constants.NUM_TILES - 1; j >= 0; j--) {
            for (int i = 0; i < Constants.NUM_TILES; i++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new StaticTiledMapTile(new TextureRegion(mapTiles[index], TILE_SIZE, TILE_SIZE)));
                layer.setCell(i, j, cell);
                index++;
            }
        }
        layers.add(layer);

        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        GestureDetector gestureDetector = new GestureDetector(this);
        ScrollWheelInputProcessor scrollWheelInputProcessor = new ScrollWheelInputProcessor(camera);
        InputMultiplexer inputMultiplexer = new InputMultiplexer(stage, gestureDetector, scrollWheelInputProcessor);
        Gdx.input.setInputProcessor(inputMultiplexer);
        mapOverlay = new MapOverlay();

        List<Measurement> measurements = getMeasurements();
        List<MobileTower> mobileTowers = getMobileTowers();

        mapOverlay.calculateAverageSpeeds(measurements, beginTile);
        minSpeed = mapOverlay.getMinSpeed();
        maxSpeed = mapOverlay.getMaxSpeed();

        mobileTowerPositions =  mapOverlay.turnMobileTowerCoordinatesToPixels(mobileTowers,beginTile);
        speedInfoWindow.setSize(300, 200); // Set the desired width and height
        speedInfoWindow.pack();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        handleInput();
        camera.update();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        if(drawGrid) {
            mapOverlay.drawGrid(shapeRenderer, camera, gridOpacity);
        }

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        if(drawMobileTowers) {
            float textureWidth = mobileTowerTexture.getRegionWidth() * 0.3f;
            float textureHeight = mobileTowerTexture.getRegionHeight() * 0.3f;
            for (Pair<Vector2, MobileTower> position : mobileTowerPositions) {
                spriteBatch.draw(mobileTowerTexture, position.getFirst().x - textureWidth / 2, position.getFirst().y - textureHeight / 2, textureWidth, textureHeight);

            }

        }

        spriteBatch.end();

        if (!speedInfo.isEmpty()) {
            float scaleFactor = Math.min(Gdx.graphics.getWidth() / 100f, Gdx.graphics.getHeight() / 100f);
            font.getData().setScale(scaleFactor);

            speedInfoLabel.setText(speedInfo);
            speedInfoWindow.pack();

            // Convert touch position to screen coordinates
            Vector3 screenPosition = camera.project(new Vector3(speedInfoPosition.x, speedInfoPosition.y, 0));

            // Set the position of the popup on the screen
            float popupX = screenPosition.x;
            float popupY = screenPosition.y - speedInfoWindow.getHeight();
            speedInfoWindow.setPosition(popupX, popupY);

        }

        if(drawnTower != null) {
            towerInfoWindow.pack();
            towerInfoWindow.setPosition(Gdx.graphics.getWidth() - towerInfoWindow.getWidth(), Gdx.graphics.getHeight() - towerInfoWindow.getHeight());

        }

        stage.act(delta);
        stage.draw();


        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if(drawnTower != null) {
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.circle(drawnTower.x, drawnTower.y, towerRadius);
        }
        shapeRenderer.end();


        if(debug) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.RED);
            for (Pair<Vector2, MobileTower> position : mobileTowerPositions) {
                shapeRenderer.circle(position.getFirst().x, position.getFirst().y, 10);
            }
            shapeRenderer.end();
        }
    }


    private void drawMarkers() {
        Vector2 marker = MapRasterTiles.getPixelPosition(MARKER_GEOLOCATION.lat, MARKER_GEOLOCATION.lng, beginTile.x, beginTile.y);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(marker.x, marker.y, 10);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        if (speedInfoWindow.isVisible()) {
            Vector3 screenPosition = camera.project(new Vector3(speedInfoPosition.x, speedInfoPosition.y, 0));

            float popupX = screenPosition.x;
            float popupY = screenPosition.y - speedInfoWindow.getHeight();
            speedInfoWindow.setPosition(popupX, popupY);
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
        spriteBatch.dispose();
        stage.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        touchPosition.set(x, y, 0);
        camera.unproject(touchPosition);

        // Convert touch position to stage coordinates
        Vector2 stageCoords = stage.screenToStageCoordinates(new Vector2(x, y));

        if (speedInfoWindow.isVisible() && speedInfoWindow.hit(stageCoords.x, stageCoords.y, true) != null) {
            return true;
        }

        speedInfo = "";
        speedInfoWindow.setVisible(false);



        if(drawMobileTowers) {
            float textureWidth = mobileTowerTexture.getRegionWidth() * 0.3f;
            for (Pair<Vector2, MobileTower> position : mobileTowerPositions) {
                if (position.getFirst().dst(touchPosition.x, touchPosition.y) < textureWidth / 2) {
                    drawnTower = position.getFirst();

                    providerLabel.setText("Provider: " + position.getSecond().getProvider());
                    typeLabel.setText("Type: " + position.getSecond().getType());
                    confirmedLabel.setText("Confirmed: " + position.getSecond().isConfirmed());
                    towerInfoWindow.setVisible(true);
                    return false;
                }
            }
            drawnTower = null;
            towerInfoWindow.setVisible(false);
        }

        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        touchPosition.set(x, y, 0);
        camera.unproject(touchPosition);

        if(drawGrid) {
            int speed = mapOverlay.getSpeed((int) touchPosition.x, (int) touchPosition.y, beginTile);
            speedInfo = "Speed: " + String.format("%.2f", speed / 1_000_000.0) + " Mbps";
            selectedDifficulty = calculateDifficulty(speed);
            difficultyLabel.setText("Difficulty: " + difficultyToString(selectedDifficulty));
            if (speed == -1) {
                speedInfo = "";
                speedInfoWindow.setVisible(false);
                return false;
            }
            speedInfoWindow.setVisible(true);
            speedInfoPosition.set(touchPosition.x, touchPosition.y);
        }
        return false;
    }


    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        camera.translate(-deltaX, deltaY);
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        if (initialDistance >= distance)
            camera.zoom += 0.02;
        else
            camera.zoom -= 0.02;
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }


    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.zoom += 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom -= 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0, -3, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0, 3, 0);
        }


        camera.zoom = MathUtils.clamp(camera.zoom, 0.5f, 2f);

        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f, Constants.MAP_WIDTH - effectiveViewportWidth / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f, Constants.MAP_HEIGHT - effectiveViewportHeight / 2f);
    }

    private List<Measurement> getMeasurements() {
        HttpMeasurement httpMeasurement = new HttpMeasurement(sessionManager);
        List<Measurement> measurements = null;
        try {
            measurements = httpMeasurement.getAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(measurements.size());
        return measurements;

    }


    private List<MobileTower> getMobileTowers() {
        HttpMobileTower httpMobileTower = new HttpMobileTower(sessionManager);
        List<MobileTower> mobileTowers;
        mobileTowers = httpMobileTower.getAll();
        return mobileTowers;

    }

    private int calculateDifficulty(int speed) {
        if (speed < minSpeed + (maxSpeed - minSpeed) / 5) {
            return 1;
        } else if (speed < minSpeed + (maxSpeed - minSpeed) / 5 * 2) {
            return 2;
        } else if (speed < minSpeed + (maxSpeed - minSpeed) / 5 * 3) {
            return 3;
        } else if (speed < minSpeed + (maxSpeed - minSpeed) / 5 * 4) {
            return 4;
        } else {
            return 5;
        }
    }

    private String difficultyToString(int difficulty) {
        switch (difficulty) {
            case 1:
                return "Very Easy";
            case 2:
                return "Easy";
            case 3:
                return "Medium";
            case 4:
                return "Hard";
            case 5:
                return "Very Hard";
            default:
                return "Unknown";
        }
    }





}
