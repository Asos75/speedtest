package si.um.feri.speedii.screens;

import static si.um.feri.speedii.utils.Constants.ZOOM;
import static si.um.feri.speedii.utils.MapRasterTiles.TILE_SIZE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import si.um.feri.speedii.SpeediiApp;
import si.um.feri.speedii.assets.AssetDescriptors;
import si.um.feri.speedii.assets.RegionNames;
import si.um.feri.speedii.classes.ExtremeEvent;
import si.um.feri.speedii.classes.Measurement;
import si.um.feri.speedii.classes.MobileTower;
import si.um.feri.speedii.classes.Pair;
import si.um.feri.speedii.classes.SessionManager;
import si.um.feri.speedii.config.GameConfig;
import si.um.feri.speedii.dao.http.HttpMeasurement;
import si.um.feri.speedii.dao.http.HttpMobileTower;
import si.um.feri.speedii.screens.mapcomponents.ScrollWheelInputProcessor;
import si.um.feri.speedii.towerdefense.config.DIFFICULTY;
import si.um.feri.speedii.utils.Constants;
import si.um.feri.speedii.utils.Geolocation;
import si.um.feri.speedii.utils.MapRasterTiles;
import si.um.feri.speedii.utils.ZoomXY;
import si.um.feri.speedii.screens.mapcomponents.MapOverlay;

public class MapScreen implements Screen, GestureDetector.GestureListener {

    private Viewport viewport;

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
    private Vector2 extremeEventPosition;

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
    private Window extremeEventWindow;
    private Label extremeEventTypeLabel;
    private Label extremeEventTimeLabel;
    private Label extremeEventDateLabel;
    private Label extremeUserLocationLabel;

    // ASUS FIX
    private String location = "Sample Location";
    private float lastSelectedSpeed = 0;

    private Skin skin;

    private Vector2 drawnTower;
    private float towerRadius = 1.2f;
    private float pxPerKm;

    private boolean drawGrid = true;
    private float gridOpacity = Constants.OVERLAY_ALPHA;
    private boolean drawMobileTowers = true;
    private int minSpeed = 0;
    private int maxSpeed = 0;
    private DIFFICULTY selectedDifficulty = DIFFICULTY.VERY_EASY;

    private boolean drawExtremeEvents = true;



    public MapScreen(SpeediiApp app, SessionManager sessionManager, AssetManager assetManager) {
        Gdx.graphics.setWindowedMode(Constants.HUD_WIDTH, Constants.HUD_HEIGHT);
        this.app = app;
        this.sessionManager = sessionManager;
        this.font = new BitmapFont();
        this.spriteBatch = new SpriteBatch();
        this.speedInfo = "";
        this.assetManager = assetManager;
        this.speedInfoPosition = new Vector2();
        this.extremeEventPosition = new Vector2();
        this.camera = new OrthographicCamera();
        this.viewport = new ScreenViewport(camera);


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
                Gdx.graphics.setWindowedMode((int)GameConfig.WORLD_WIDTH, (int)GameConfig.WORLD_HEIGHT);
                String formattedSpeed = String.format("%.2f", lastSelectedSpeed);
                app.setScreen(new GameScreen(app, sessionManager, selectedDifficulty, location, formattedSpeed));
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

        //EXTREME EVENT WINDOW
        extremeEventWindow = new Window("Extreme Event                          ", skin);
        extremeEventTypeLabel = new Label("", skin);
        extremeEventTimeLabel = new Label("", skin);
        extremeEventDateLabel = new Label("", skin);
        extremeUserLocationLabel = new Label("", skin);

        extremeEventWindow.add(extremeEventTypeLabel).left().pad(10);
        extremeEventWindow.row();
        extremeEventWindow.add(extremeEventTimeLabel).left().pad(10);
        extremeEventWindow.row();
        extremeEventWindow.add(extremeEventDateLabel).left().pad(10);
        extremeEventWindow.row();
        extremeEventWindow.add(extremeUserLocationLabel).left().pad(10);
        extremeEventWindow.pack();

        extremeEventWindow.setVisible(false);

        TextButton backButton = new TextButton("Back", skin);
        backButton.setPosition(10, Gdx.graphics.getHeight() - 50); // Adjust position as needed
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.graphics.setWindowedMode((int)GameConfig.WORLD_WIDTH, (int)GameConfig.WORLD_HEIGHT);
                app.setScreen(new MenuScreen(app, sessionManager));
            }
        });

        stage.addActor(backButton);

        TextButton toggleGridButton = new TextButton("Toggle Grid", skin);
        TextButton toggleExtremeEventsButton = new TextButton("Toggle Extreme Events", skin);
        TextButton toggleTowersButton = new TextButton("Toggle Towers", skin);
        Slider gridOpacitySlider = new Slider(0.0f, 1.0f, 0.01f, false, skin);

        toggleTowersButton.setPosition(10, Gdx.graphics.getHeight() - 100);
        toggleExtremeEventsButton.setPosition(10, Gdx.graphics.getHeight() - 150);
        toggleGridButton.setPosition(10, Gdx.graphics.getHeight() - 200);
        gridOpacitySlider.setPosition(10, Gdx.graphics.getHeight() - 250);

        toggleGridButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                drawGrid = !drawGrid;
                gridOpacitySlider.setVisible(drawGrid);
            }
        });

        toggleExtremeEventsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                drawExtremeEvents = !drawExtremeEvents;
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
        stage.addActor(toggleExtremeEventsButton);
        stage.addActor(toggleGridButton);
        stage.addActor(toggleTowersButton);
        stage.addActor(speedInfoWindow);
        stage.addActor(towerInfoWindow);
        stage.addActor(extremeEventWindow);
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

        atlas = assetManager.get(AssetDescriptors.UI_IMAGES);
        mobileTowerTexture =  (atlas.findRegion(RegionNames.CELLTOWER));
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

        pxPerKm = (float) calculatePixelsPerKm(CENTER_GEOLOCATION.lat, ZOOM);
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

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(Color.RED);
        if(drawExtremeEvents) {
            for(ExtremeEvent e : app.extremeEvents) {
                drawMarker(e.location.coordinates.get(1), e.location.coordinates.get(0));
            }
        }

        shapeRenderer.end();


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

        if(extremeEventWindow.isVisible()) {
            Vector3 screenPosition = camera.project(new Vector3(extremeEventPosition.x, extremeEventPosition.y, 0));

            float popupX = screenPosition.x;
            float popupY = screenPosition.y - extremeEventWindow.getHeight();
            extremeEventWindow.setPosition(popupX, popupY);
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
            if (typeLabel.getText().toString().toLowerCase().contains("5g".toLowerCase())) {
                float pixelRadius = (Constants.RANGE_5G * pxPerKm);
                shapeRenderer.circle(drawnTower.x, drawnTower.y, pixelRadius);
            } else if (typeLabel.getText().toString().toLowerCase().contains("4g".toLowerCase())) {
                float pixelRadius = (Constants.RANGE_4G * pxPerKm);
                shapeRenderer.circle(drawnTower.x, drawnTower.y, pixelRadius);
            } else if (typeLabel.getText().toString().toLowerCase().contains("3g".toLowerCase())) {
                float pixelRadius = (Constants.RANGE_3G * pxPerKm);
                shapeRenderer.circle(drawnTower.x, drawnTower.y, pixelRadius);
            }
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

    private void drawMarker(double x, double y) {
        Vector2 marker = MapRasterTiles.getPixelPosition(x, y, beginTile.x, beginTile.y);
        shapeRenderer.circle(marker.x, marker.y, 10);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);

        if (speedInfoWindow.isVisible()) {
            Vector3 screenPosition = camera.project(new Vector3(speedInfoPosition.x, speedInfoPosition.y, 0));
            float popupX = screenPosition.x;
            float popupY = screenPosition.y - speedInfoWindow.getHeight();
            speedInfoWindow.setPosition(popupX, popupY);
        }

        if (extremeEventWindow.isVisible()) {
            Vector3 screenPosition = camera.project(new Vector3(extremeEventPosition.x, extremeEventPosition.y, 0));
            float popupX = screenPosition.x;
            float popupY = screenPosition.y - extremeEventWindow.getHeight();
            extremeEventWindow.setPosition(popupX, popupY);
        }

        if (drawnTower != null) {
            towerInfoWindow.pack();
            towerInfoWindow.setPosition(Gdx.graphics.getWidth() - towerInfoWindow.getWidth(), Gdx.graphics.getHeight() - towerInfoWindow.getHeight());
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
        if (drawExtremeEvents){
            for(ExtremeEvent e : app.extremeEvents) {
                Vector2 marker = MapRasterTiles.getPixelPosition(e.location.coordinates.get(1), e.location.coordinates.get(0), beginTile.x, beginTile.y);
                if (marker.dst(touchPosition.x, touchPosition.y) < 30) {
                    System.out.println("Extreme event clicked");
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

                    extremeEventTypeLabel.setText("Type: " + e.type);
                    extremeEventDateLabel.setText("Date: " + e.time.format(dateFormatter));
                    extremeEventTimeLabel.setText("Time: " + e.time.format(timeFormatter));
                    if (e.user != null) extremeUserLocationLabel.setText("Location: " + e.user);
                    extremeEventWindow.setVisible(true);

                    extremeEventPosition.set(touchPosition.x, touchPosition.y);

                    return false;
                }
                extremeEventWindow.setVisible(false);
            }
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

            lastSelectedSpeed = speed;

            speedInfo = "Speed: " + String.format("%.2f", speed / 1_000_000.0) + " Mbps";
            selectedDifficulty = calculateDifficulty(speed);
            difficultyLabel.setText("Difficulty: " + selectedDifficulty.toString());
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            app.setScreen(new MenuScreen(app, sessionManager));
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

    private DIFFICULTY calculateDifficulty(int speed) {
        if (speed < minSpeed + (maxSpeed - minSpeed) / 5) {
            return DIFFICULTY.VERY_EASY;
        } else if (speed < minSpeed + (maxSpeed - minSpeed) / 5 * 2) {
            return DIFFICULTY.EASY;
        } else if (speed < minSpeed + (maxSpeed - minSpeed) / 5 * 3) {
            return DIFFICULTY.MEDIUM;
        } else if (speed < minSpeed + (maxSpeed - minSpeed) / 5 * 4) {
            return DIFFICULTY.HARD;
        } else {
            return DIFFICULTY.VERY_HARD;
        }
    }

    private double calculatePixelsPerKm(double latitude, int zoom) {
        final double EARTH_CIRCUMFERENCE = 40075016.686;
        final int TILE_SIZE = MapRasterTiles.TILE_SIZE;

        double resolution = EARTH_CIRCUMFERENCE * Math.cos(Math.toRadians(latitude)) / Math.pow(2, zoom + 8);

        double kmPerPixel = resolution / 1000.0;

        return 1.0 / kmPerPixel;
    }



}
