package si.um.feri.speedii.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import si.um.feri.speedii.SpeediiApp;
import si.um.feri.speedii.assets.AssetDescriptors;
import si.um.feri.speedii.classes.SessionManager;
import si.um.feri.speedii.towerdefense.config.GameDataManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class GameOverScreen implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final GameDataManager gameDataManager;
    private final SpeediiApp app;
    private final String message;
    private final SessionManager sessionManager;

    public GameOverScreen(SpeediiApp app, String message, GameDataManager gameDataManager, SessionManager sessionManager) {
        this.app = app;
        this.message = message;
        this.gameDataManager = gameDataManager;
        this.skin = app.getAssetManager().get(AssetDescriptors.UI_SKIN);
        this.stage = new Stage(new ScreenViewport());
        this.sessionManager = sessionManager;
        Gdx.input.setInputProcessor(stage);
        initializeUI();
    }

    private void initializeUI() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont(); // Use default font

        // Update label styles
        Label.LabelStyle messageLabelStyle = new Label.LabelStyle();
        messageLabelStyle.font = new BitmapFont();
        messageLabelStyle.font.getData().setScale(4); // 4x bigger

        Label.LabelStyle statsLabelStyle = new Label.LabelStyle();
        statsLabelStyle.font = new BitmapFont();
        statsLabelStyle.font.getData().setScale(2); // 2x bigger

        Label messageLabel = new Label(message, messageLabelStyle);
        table.add(messageLabel).colspan(2).align(Align.center).padBottom(10).row();

        // Create statistics table
        Table statisticsTable = new Table();
        statisticsTable.add(new Label("Statistics", statsLabelStyle)).colspan(2).align(Align.center).padBottom(10).row();
        Label enemiesKilledLabel = new Label("Enemies Killed: " + gameDataManager.getEnemiesKilled(), statsLabelStyle);
        statisticsTable.add(enemiesKilledLabel).align(Align.left).pad(5).row();
        Label towersPlacedLabel = new Label("Towers Placed: " + gameDataManager.getTowersPlaced(), statsLabelStyle);
        statisticsTable.add(towersPlacedLabel).align(Align.left).pad(5).row();
        Label moneyGotLabel = new Label("Money Earned: " + gameDataManager.getmoneyGot(), statsLabelStyle);
        statisticsTable.add(moneyGotLabel).align(Align.left).pad(5).row();

        table.add(statisticsTable).expand().fill().row();

        TextButton mainMenuButton = new TextButton("Main Menu", skin);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(new MenuScreen(app, sessionManager));
            }
        });
        table.add(mainMenuButton).colspan(2).align(Align.center).padTop(10);
    }

    @Override
    public void show() {
        //Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
