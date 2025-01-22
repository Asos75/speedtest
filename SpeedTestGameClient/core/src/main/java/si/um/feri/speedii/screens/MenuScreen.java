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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.speedii.SpeediiApp;
import si.um.feri.speedii.assets.AssetDescriptors;
import si.um.feri.speedii.config.GameConfig;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;

import javax.swing.event.ChangeEvent;

import si.um.feri.speedii.towerdefense.config.DIFFICULTY;

public class MenuScreen extends ScreenAdapter {
    private final SpeediiApp app;
    private final AssetManager assetManager;
    private final Skin skin;
    private Viewport viewport;
    private Stage stage;

    //private DIFFICULTY selectedDifficulty = DIFFICULTY.MEDIUM; // Default difficulty
    private DIFFICULTY selectedDifficulty = DIFFICULTY.VERY_EASY;

    public MenuScreen(SpeediiApp app) {
        this.app = app;
        this.assetManager = app.getAssetManager();
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
                app.setScreen(new GameScreen(app, selectedDifficulty));
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

        TextButton leaderboardButton = new TextButton("Leaderboard", skin);
        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // game.setScreen(new LeaderboardScreen(game));
            }
        });


        TextButton settingsButton = new TextButton("Settings", skin);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
             //    game.setScreen(new SettingsScreen(game));
            }
        });

        TextButton quitButton = new TextButton("Quit", skin);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                com.badlogic.gdx.Gdx.app.exit();
            }
        });

        table.add(playButton).width(250).padBottom(15).expandX().row();
        table.add(difficultySelectBox).width(250).padBottom(15).expandX().row();
        table.add(leaderboardButton).width(250).expandX().row();
        table.add(settingsButton).width(250).expandX().row();
        table.add(quitButton).width(250);

        table.center();
        table.setFillParent(true);

        return table;
    }
}
