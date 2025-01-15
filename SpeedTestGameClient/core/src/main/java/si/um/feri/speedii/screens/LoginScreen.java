package si.um.feri.speedii.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import org.bson.types.ObjectId;

import si.um.feri.speedii.SpeediiApp;
import si.um.feri.speedii.classes.MobileTower;
import si.um.feri.speedii.classes.User;
import si.um.feri.speedii.dao.http.HttpMobileTower;
import si.um.feri.speedii.dao.http.HttpUser;
import java.util.List;

public class LoginScreen implements Screen {

    private final SpeediiApp app;
    private final HttpUser httpUser;
    private final HttpMobileTower httpMobileTower;
    private Stage stage;
    private Skin skin;

    public LoginScreen(SpeediiApp app, HttpUser httpUser, HttpMobileTower httpMobileTower) {
        this.app = app;
        this.httpUser = httpUser;
        this.httpMobileTower = httpMobileTower;

        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label usernameLabel = new Label("Username:", skin);
        TextField usernameField = new TextField("", skin);

        Label passwordLabel = new Label("Password:", skin);
        TextField passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        TextButton loginButton = new TextButton("Login", skin);

        table.add(usernameLabel).pad(10);
        table.add(usernameField).width(200).pad(10).row();
        table.add(passwordLabel).pad(10);
        table.add(passwordField).width(200).pad(10).row();
        table.add(loginButton).colspan(2).center().pad(10);

        loginButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                String username = usernameField.getText();
                String password = passwordField.getText();

                boolean success = httpUser.authenticate(username, password);
                if (success) {
                    System.out.println("Login successful!");
                    ObjectId objectId = new ObjectId("664751010ed2fc39038d7913");
                    User user =  httpUser.getById(objectId);
                    System.out.println(user);
                    List<MobileTower> mobileTowers = httpMobileTower.getByLocator(user);
                    if (mobileTowers.isEmpty()) {
                        System.out.println("No mobile towers found.");
                    } else {
                        System.out.println("Mobile Towers:");
                        for (MobileTower tower : mobileTowers) {
                            System.out.println(tower);
                        }
                    }
                    app.setScreen(new MenuScreen(app));
                } else {
                    System.out.println("Login failed!");
                }
            }
            return true;
        });

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
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
