package si.um.feri.speedii.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import org.bson.types.ObjectId;

import java.util.Objects;

import si.um.feri.speedii.SpeediiApp;
import si.um.feri.speedii.assets.AssetDescriptors;
import si.um.feri.speedii.assets.RegionNames;
import si.um.feri.speedii.classes.SessionManager;
import si.um.feri.speedii.classes.User;
import si.um.feri.speedii.dao.http.HttpUser;

public class LoginScreen implements Screen {

    private final SpeediiApp app;
    private final HttpUser httpUser;
    private final SessionManager sessionManager;

    private AssetManager assetManager;
    private Stage stage;
    private Skin skin;

    private TextureAtlas atlas;
    private TextureRegion logoRegion;
    private Image logoImage;
    private Table table;

    public LoginScreen(SpeediiApp app, HttpUser httpUser, SessionManager sessionManager, AssetManager assetManager) {
        this.app = app;
        this.httpUser = httpUser;
        this.sessionManager = sessionManager;
        this.assetManager = assetManager;

        // Inicializacija Scene2D
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Nalaganje tekstur in logotipa
        atlas = assetManager.get(AssetDescriptors.IMAGES);
        logoRegion = atlas.findRegion(RegionNames.LOGO);

        TextureRegionDrawable logoDrawable = new TextureRegionDrawable(logoRegion);
        logoImage = new Image(logoDrawable);
        logoImage.setSize(logoImage.getWidth() / 1.2f, logoImage.getHeight() / 1.2f);
        stage.addActor(createBackground());
        stage.addActor(logoImage);

        // Postavitev tabele za uporabniški vmesnik
        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label usernameLabel = new Label("Username:", skin);
        usernameLabel.setColor(0, 0, 0, 1);
        TextField usernameField = new TextField("", skin);
        usernameField.setColor(0.9f, 0.9f, 0.9f, 1);
        Label passwordLabel = new Label("Password:", skin);
        passwordLabel.setColor(0, 0, 0, 1);
        TextField passwordField = new TextField("", skin);
        passwordField.setColor(0.9f, 0.9f, 0.9f, 1);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        TextButton loginButton = new TextButton("Login", skin);
        loginButton.pad(5);

        table.add(usernameLabel).pad(10);
        table.add(usernameField).width(200).pad(10).row();
        table.add(passwordLabel).pad(10);
        table.add(passwordField).width(200).pad(10).row();
        table.add(loginButton).colspan(2).center().pad(20);

        loginButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                String username = usernameField.getText();
                String password = passwordField.getText();

                boolean success = httpUser.authenticate(username, password);
                if (success) {
                    System.out.println("Login successful!");
                    ObjectId objectId = sessionManager.getUser().getId();
                    User user = httpUser.getById(objectId);
                    System.out.println(user);
                    app.setScreen(new MenuScreen(app, sessionManager));
                } else {
                    System.out.println("Login failed!");
                }
            }
            return true;
        });

        Gdx.input.setInputProcessor(stage);
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
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        float viewportWidth = stage.getViewport().getWorldWidth();
        float viewportHeight = stage.getViewport().getWorldHeight();

        logoImage.setPosition(
            viewportWidth / 2 - logoImage.getWidth() / 2,
            viewportHeight - logoImage.getHeight() - 50
        );
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
