package si.um.feri.speedii.screens.GameScreenComponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.Viewport;

public class InitializeGame {
    private final Skin skin;
    private final Table table;

    public Label locationLabel;
    public Label uploadSpeedLabel;
    public Label downloadSpeedLabel;
    public Label healthLabel;
    public Label waveLabel;
    public Label enemiesRemainingLabel;
    public TextButton pauseButton;
    public TextButton quitButton;

    public InitializeGame() {
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        this.table = new Table();
        initializeUI();
    }

    private void initializeUI() {
        table.setFillParent(true);

        locationLabel = new Label("Location: {name}", skin);
        uploadSpeedLabel = new Label("Upload speed: {something}", skin);
        downloadSpeedLabel = new Label("Download speed: {something}", skin);
        healthLabel = new Label("Health: {value}", skin);
        waveLabel = new Label("Wave {1 of 10}", skin);
        enemiesRemainingLabel = new Label("{64} enemies remaining", skin);
        pauseButton = new TextButton("Pause", skin);
        quitButton = new TextButton("Quit", skin);

        Table topTable = new Table();
        topTable.add(locationLabel).left().pad(10).expandX().fillX();
        topTable.add(uploadSpeedLabel).center().pad(10).expandX().fillX();
        topTable.add(downloadSpeedLabel).right().pad(10).expandX().fillX();

        Table leftTable = new Table();
        leftTable.add(healthLabel).left().pad(10).expandX().fillX();
        leftTable.row();
        leftTable.add(waveLabel).left().pad(10).expandX().fillX();
        leftTable.row();
        leftTable.add(enemiesRemainingLabel).left().pad(10).expandX().fillX();

        Table buttonTable = new Table();
        buttonTable.add(pauseButton).pad(10).expandX().fillX();
        buttonTable.add(quitButton).pad(10).expandX().fillX();

        table.top().left();
        table.add(topTable).expandX().fillX().colspan(2);
        table.row();
        table.add(leftTable).expand().fill();
        table.row();
        table.add(buttonTable).bottom().right().expand().fill();
    }

    public Table getTable() {
        return table;
    }
}
