package si.um.feri.speedii.screens;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import org.bson.types.ObjectId;

import si.um.feri.speedii.classes.MobileTower;
import si.um.feri.speedii.classes.SessionManager;
import si.um.feri.speedii.classes.User;
import si.um.feri.speedii.dao.http.HttpMeasurement;
import si.um.feri.speedii.classes.Measurement;
import si.um.feri.speedii.dao.http.HttpMobileTower;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


public class InsertEditScreen implements Screen {

    private final SessionManager sessionManager;
    private final User user;
    private HttpMeasurement httpMeasurement;
    private HttpMobileTower httpMobileTower;

    private SpriteBatch spriteBatch;
    private BitmapFont font;

    private List<Measurement> measurements;
    private List<MobileTower> mobileTowers;

    private Stage stage;
    private Skin skin;

    private Table table;

    public InsertEditScreen(SessionManager sessionManager, User user) {
        this.sessionManager = sessionManager;
        this.user = user;

        this.spriteBatch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.getData().setScale(1.5f);

        try {
            this.httpMeasurement = new HttpMeasurement(sessionManager);
            this.httpMobileTower = new HttpMobileTower(sessionManager);
            this.measurements = httpMeasurement.getByUser(user);
            this.mobileTowers = httpMobileTower.getByLocator(user);
        } catch (IOException e) {
            e.printStackTrace();
            this.measurements = null;
            this.mobileTowers = null;
        }

        font = new BitmapFont();

        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        table = new Table();
        table.top();
        table.setFillParent(true);
        stage.addActor(table);

        table.add(new Label("Measurements", skin)).pad(10).center().row();
        table.add(new Label("Speed", skin)).pad(10);
        table.add(new Label("Provider", skin)).pad(10);
        table.add(new Label("Date", skin)).pad(10);
        table.row();


        for (Measurement measurement : measurements) {
            TextField speedField = new TextField(String.valueOf(measurement.getSpeed()) , skin);
            TextField providerField = new TextField(measurement.getProvider() , skin);
            TextField timeField = new TextField(measurement.getTime().toString() , skin);
            TextButton updateButton = new TextButton("Update", skin);
            table.add(speedField).width(200).pad(10);
            table.add(providerField).width(200).pad(10);
            table.add(timeField).width(200).pad(10);
            table.add(updateButton).width(200).pad(10).row();

            updateButton.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                   // measurement.setSpeed(Float.parseFloat(speedField.getText()));
                    //measurement.setProvider(providerField.getText());
                   // measurement.setTime(LocalDateTime.parse(timeField.getText()));
                    long newSpeed = Long.parseLong(speedField.getText());
                    String newProvider = providerField.getText();
                    LocalDateTime newDate = LocalDateTime.parse(timeField.getText());
                    Measurement newMeasurement = new Measurement(newSpeed, measurement.getType(), newProvider, measurement.getLocation(), newDate,measurement.getUser(), measurement.getId());
                    HttpMeasurement newHttpMeasurement = new HttpMeasurement(sessionManager);
                    try {
                       System.out.println(newHttpMeasurement.update(newMeasurement));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(newMeasurement);
                }

        });
        Gdx.input.setInputProcessor(stage);

     }
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        stage.act();
        stage.draw();
    }




    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {

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
        spriteBatch.dispose();
        font.dispose();
    }
    }
