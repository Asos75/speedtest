package si.um.feri.speedii.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.speedii.SpeediiApp;
import si.um.feri.speedii.assets.AssetDescriptors;
import si.um.feri.speedii.classes.SessionManager;
import si.um.feri.speedii.config.GameConfig;

public class MenuScreen extends ScreenAdapter {
    private final SpeediiApp app;
    private final AssetManager assetManager;
    private SessionManager sessionManager;
    private final Skin skin;
    private Viewport viewport;
    private Stage stage;

    public MenuScreen(SpeediiApp app, SessionManager sessionManager) {
        this.app = app;
        this.assetManager = app.getAssetManager();
        this.sessionManager = sessionManager;
        this.skin = app.getAssetManager().get(AssetDescriptors.UI_SKIN);
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, app.getBatch());

        stage.addActor(createUi());

        com.badlogic.gdx.Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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

        //TextureRegion menuBackground = gameplayAtlas.findRegion(RegionNames.BACKGROUND);
        //table.setBackground(new TextureRegionDrawable(menuBackground));

        TextButton playButton = new TextButton("Play", skin);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
              //  game.setScreen(new PlayScreen(game));
            }
        });

        TextButton leaderboardButton = new TextButton("Edit", skin);
        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                 app.setScreen(new InsertEditScreen(app, sessionManager, sessionManager.getUser()));
            }
        });


        TextButton settingsButton = new TextButton("Map", skin);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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

        table.add(playButton).width(250).padBottom(15).expandX().row();
        table.add(leaderboardButton).width(250).expandX().row();
        table.add(settingsButton).width(250).expandX().row();
        table.add(quitButton).width(250);

        table.center();
        table.setFillParent(true);

        return table;
    }
}
