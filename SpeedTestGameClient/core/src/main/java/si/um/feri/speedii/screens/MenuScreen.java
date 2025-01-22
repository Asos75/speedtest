package si.um.feri.speedii.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.speedii.SpeediiApp;
import si.um.feri.speedii.assets.AssetDescriptors;
import si.um.feri.speedii.assets.RegionNames;
import si.um.feri.speedii.classes.SessionManager;
import si.um.feri.speedii.config.GameConfig;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;

import javax.swing.event.ChangeEvent;

import si.um.feri.speedii.towerdefense.config.DIFFICULTY;

public class MenuScreen extends ScreenAdapter {
    private final SpeediiApp app;
    private final AssetManager assetManager;
    private SessionManager sessionManager;
    private final Skin skin;

    private TextureRegion logoRegion;
    private Image logoImage;
    private TextureAtlas atlas;
    private Viewport viewport;
    private Stage stage;

    //private DIFFICULTY selectedDifficulty = DIFFICULTY.MEDIUM; // Default difficulty
    private DIFFICULTY selectedDifficulty = DIFFICULTY.VERY_EASY;

    public MenuScreen(SpeediiApp app, SessionManager sessionManager) {
        this.app = app;
        this.assetManager = app.getAssetManager();
        this.sessionManager = sessionManager;
        this.skin = app.getAssetManager().get(AssetDescriptors.UI_SKIN);
    }

    @Override
    public void show() {
        viewport = new ScreenViewport();
        stage = new Stage(viewport, app.getBatch());
        atlas = assetManager.get(AssetDescriptors.UI_IMAGES);

        stage.addActor(createBackground());
        stage.addActor(createUi());
        com.badlogic.gdx.Gdx.input.setInputProcessor(stage);

    }

    private Actor createBackground() {
        Table table = new Table();
        table.setFillParent(true);

        TextureRegion backgroundRegion = atlas.findRegion(RegionNames.BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));



        table.center();
        table.setFillParent(true);

        return table;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        float viewportWidth = stage.getViewport().getWorldWidth();
        float viewportHeight = stage.getViewport().getWorldHeight();


    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private Actor createUi() {
        Table table = new Table();
        table.defaults().pad(20);

        logoRegion = atlas.findRegion(RegionNames.LOGO);

        TextureRegionDrawable logoDrawable = new TextureRegionDrawable(logoRegion);
        logoImage = new Image(logoDrawable);
        logoImage.setSize(logoImage.getWidth() / 1.2f, logoImage.getHeight() / 4f);

        TextButton playButton = new TextButton("Play", skin);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.graphics.setWindowedMode((int)GameConfig.WORLD_WIDTH, (int)GameConfig.WORLD_HEIGHT);
                app.setScreen(new GameScreen(app, sessionManager, selectedDifficulty));
            }
        });

        SelectBox<DIFFICULTY> difficultySelectBox = new SelectBox<>(skin);
        difficultySelectBox.setItems(DIFFICULTY.values());
        difficultySelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedDifficulty = difficultySelectBox.getSelected();
            }
        });
        table.add(difficultySelectBox).width(250).pad(30).padBottom(15).expandX().row();

        TextButton leaderboardButton = new TextButton("Edit", skin);
        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                 app.setScreen(new InsertEditScreen(app, sessionManager, sessionManager.getUser()));
            }
        });


        TextButton mapButton = new TextButton("Map", skin);
        mapButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                 com.badlogic.gdx.Gdx.graphics.setWindowedMode(900, 900);
                 app.setScreen(new MapScreen(app, sessionManager, assetManager));
            }
        });

        TextButton quitButton = new TextButton("Quit", skin);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                com.badlogic.gdx.Gdx.app.exit();
                app.mqttClient.disconnectFromBroker();
            }
        });

        playButton.pad(10);
        leaderboardButton.pad(10);
        mapButton.pad(10);
        quitButton.pad(10);


        table.add(logoImage).row();
        table.add(playButton).width(250).padBottom(15).expandX().row();
        table.add(difficultySelectBox).width(250).padBottom(15).expandX().row();
        table.add(leaderboardButton).width(250).expandX().row();
        table.add(mapButton).width(250).expandX().row();
        table.add(quitButton).width(250);

        table.center();
        table.setFillParent(true);

        return table;
    }
}
