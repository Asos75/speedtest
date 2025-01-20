package si.um.feri.speedii.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import si.um.feri.speedii.SpeediiApp;

public class PauseScreen {
    private final Stage stage;
    private final Skin skin;
    private final SpeediiApp app;

    public PauseScreen(SpeediiApp app, Skin skin) {
        this.app = app;
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        initializeUI();
    }

    private void initializeUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.setColor(0, 0, 0, 0.7f); // Semi-transparent background

        Label pauseLabel = new Label("Game Paused", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        pauseLabel.setFontScale(2);

        TextButton resumeButton = new TextButton("Resume", skin);
        // TODO TOMORROW, TONIGHT WE DO RRI
        resumeButton.addListener(new ClickListener() {
            //@Override
            /*public void clicked(InputEvent event, float x, float y) {
                app.getGameScreen().resumeGame();
            }*/
        });

        TextButton quitButton = new TextButton("Quit", skin);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.add(pauseLabel).padBottom(20).row();
        table.add(resumeButton).width(200).padBottom(10).row();
        table.add(quitButton).width(200);

        stage.addActor(table);
    }

    public Stage getStage() {
        return stage;
    }
}
