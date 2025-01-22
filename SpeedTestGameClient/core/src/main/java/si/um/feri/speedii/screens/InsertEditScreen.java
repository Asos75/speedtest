package si.um.feri.speedii.screens;






import static si.um.feri.speedii.config.GameConfig.WORLD_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;


import org.bson.types.ObjectId;


import si.um.feri.speedii.SpeediiApp;
import si.um.feri.speedii.classes.Location;
import si.um.feri.speedii.classes.MobileTower;
import si.um.feri.speedii.classes.SessionManager;
import si.um.feri.speedii.classes.Type;
import si.um.feri.speedii.classes.User;
import si.um.feri.speedii.config.GameConfig;
import si.um.feri.speedii.dao.http.HttpMeasurement;
import si.um.feri.speedii.classes.Measurement;
import si.um.feri.speedii.dao.http.HttpMobileTower;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;




public class InsertEditScreen implements Screen {


    private final SessionManager sessionManager;
    private final User user;
    private HttpMeasurement httpMeasurement;
    private HttpMobileTower httpMobileTower;


    private SpriteBatch spriteBatch;
    private BitmapFont font;

   private SpeediiApp app;
    private List<Measurement> measurements;
    private List<MobileTower> mobileTowers;


    private Stage stage;
    private Skin skin;


    private Table table;


    public InsertEditScreen(SpeediiApp app, SessionManager sessionManager, User user) {
        this.app = app;
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


        stage = new Stage(new StretchViewport(WORLD_WIDTH, GameConfig.WORLD_HEIGHT));


        skin = new Skin(Gdx.files.internal("uiskin.json"));


        boolean showMeasurements = true;


        run(showMeasurements);



    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            app.setScreen(new MenuScreen(app, sessionManager));
        }

    }

    public void run(Boolean showMeasurements) {

        if (showMeasurements) {
            table = new Table();
            table.top();
            table.padTop(20);
            table.padBottom(20);
            table.defaults().pad(10);

            ScrollPane scrollPane = new ScrollPane(table, skin);
            scrollPane.setScrollingDisabled(true, false);
            scrollPane.setFadeScrollBars(false);

            scrollPane.setFillParent(true);
            stage.addActor(scrollPane);

// Gumb na sredini
            TextButton showBtn = new TextButton("Show Towers", skin);
            showBtn.setPosition(WORLD_WIDTH / 2, showBtn.getY());
            table.add(showBtn).width(200).padBottom(30).row();

            showBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("clicked");
                    table.clear();
                    run(false);
                }
            });

// Dodaj "Measurements" naslov pod gumbom, centrirano
            table.add(new Label("Add measurement", skin)).pad(10).center().colspan(2).row();

// Prva vrstica z Longitude in Latitude
           // table.add(new Label("Longitude", skin)).pad(10).right();
            TextField locationLongitudeAdd = new TextField("", skin);
            locationLongitudeAdd.setMessageText("Longitude");
            table.add(locationLongitudeAdd).width(200).pad(10);

            //table.add(new Label("Latitude", skin)).pad(10).right();
            TextField locationLatitudeAdd = new TextField("", skin);
            locationLatitudeAdd.setMessageText("Latitude");
            table.add(locationLatitudeAdd).width(200).pad(10).row();

// Druga vrstica z Provider in Speed
          //  table.add(new Label("Provider", skin)).pad(10).right();
            TextField providerFieldAdd = new TextField("", skin);
            providerFieldAdd.setMessageText("Provider");
            table.add(providerFieldAdd).width(200).pad(10);

           // table.add(new Label("Speed", skin)).pad(10).right();
            TextField speedAdd = new TextField("", skin);
            speedAdd.setMessageText("Speed");
            table.add(speedAdd).width(200).pad(10).row();

// Dodaj "Add" gumb centrirano in z razmikom pod besedilnimi polji
            TextButton addButton = new TextButton("Add", skin);
            table.add(addButton).width(200).colspan(2).padTop(30).row();

// Tabela za prikaz podatkov
            table.add(new Label("Speed", skin)).pad(10).center();
            table.add(new Label("Provider", skin)).pad(10).center();
            table.add(new Label("Date", skin)).pad(10).center();
            table.row();

            Gdx.input.setInputProcessor(stage);


            addButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Location newLocationAdd = new Location(Arrays.asList(
                        Double.parseDouble(locationLongitudeAdd.getText()),
                        Double.parseDouble(locationLatitudeAdd.getText())
                    ));
                    String newProviderAdd = providerFieldAdd.getText();
                    long newSpeedAdd = Long.parseLong(speedAdd.getText());
                    Measurement newMeasurement = new Measurement(
                        newSpeedAdd,
                        Type.wifi,
                        newProviderAdd,
                        newLocationAdd,
                        LocalDateTime.now(),
                        sessionManager.getUser(),
                        new ObjectId()
                    );
                    HttpMeasurement newHttpMeasurement = new HttpMeasurement(sessionManager);
                    try {
                        System.out.println(newHttpMeasurement.insert(newMeasurement));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            for (Measurement measurement : measurements) {
                TextField speedField = new TextField(String.valueOf(measurement.getSpeed()), skin);
                TextField providerField = new TextField(measurement.getProvider(), skin);
                TextField timeField = new TextField(measurement.getTime().toString(), skin);
                TextButton updateButton = new TextButton("Update", skin);
                table.add(speedField).width(200).pad(10);
                table.add(providerField).width(200).pad(10);
                table.add(timeField).width(200).pad(10);
                table.add(updateButton).width(200).pad(10).row();


                updateButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        long newSpeed = Long.parseLong(speedField.getText());
                        String newProvider = providerField.getText();
                        LocalDateTime newDate = LocalDateTime.parse(timeField.getText());
                        Measurement newMeasurement = new Measurement(
                            newSpeed,
                            measurement.getType(),
                            newProvider,
                            measurement.getLocation(),
                            newDate,
                            measurement.getUser(),
                            measurement.getId()
                        );
                        HttpMeasurement newHttpMeasurement = new HttpMeasurement(sessionManager);
                        try {
                            System.out.println(newHttpMeasurement.update(newMeasurement));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println(newMeasurement);
                    }
                });


            }
        } else {
            table = new Table();
            table.top();
            table.padTop(20);
            table.padBottom(20);
            table.defaults().pad(10);

            ScrollPane scrollPane = new ScrollPane(table, skin);
            scrollPane.setScrollingDisabled(true, false);
            scrollPane.setFadeScrollBars(false);


            scrollPane.setFillParent(true);
            stage.addActor(scrollPane);
            Gdx.input.setInputProcessor(stage);

            TextButton showBtn = new TextButton("Show Measurements", skin);
            table.add(showBtn).width(200).pad(10).row();


            showBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("clicked");
                    table.clear();
                    run(true);
                }
            });


            table.add(new Label("Add Mobile Tower", skin)).pad(10).center().row();

            TextField locationLongitudeAdd = new TextField("", skin);
            locationLongitudeAdd.setMessageText("Longitude");
            table.add(locationLongitudeAdd).width(200).pad(10);

            TextField locationLatitudeAdd = new TextField("", skin);
            locationLatitudeAdd.setMessageText("Latitude");
            table.add(locationLatitudeAdd).width(200).pad(10).row();


            TextField providerFieldAdd = new TextField("", skin);
            providerFieldAdd.setMessageText("Provider");
            table.add(providerFieldAdd).width(200).pad(10);

            TextField typeFieldAdd = new TextField("", skin);
            typeFieldAdd.setMessageText("Type");
            table.add(typeFieldAdd).width(200).pad(10).row();

            TextButton addButton = new TextButton("Add", skin);
            table.add(addButton).width(200).pad(10).colspan(2).row();

            table.add(new Label("Confirmed", skin)).pad(10);
            table.add(new Label("Provider", skin)).pad(10);
            table.add(new Label("Type", skin)).pad(10);
            table.row();


            addButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Location newLocationAdd = new Location(Arrays.asList(
                        Double.parseDouble(locationLongitudeAdd.getText()),
                        Double.parseDouble(locationLatitudeAdd.getText())
                    ));
                    String newProviderAdd = providerFieldAdd.getText();
                    String newTypeAdd = typeFieldAdd.getText();
                    MobileTower newMobileTower = new MobileTower(
                        newLocationAdd,
                        newProviderAdd,
                        newTypeAdd,
                        false,
                        sessionManager.getUser(),
                        new ObjectId()
                    );
                    HttpMobileTower newHttpMobileTower = new HttpMobileTower(sessionManager);
                    System.out.println(newHttpMobileTower.insert(newMobileTower));
                }
            });


            for (MobileTower mobileTower : mobileTowers) {
                System.out.println(mobileTower.getLocation());
                TextField confirmedField = new TextField(String.valueOf(mobileTower.isConfirmed()), skin);
                TextField providerField = new TextField(mobileTower.getProvider(), skin);
                TextField typeField = new TextField(mobileTower.getType(), skin);
                TextButton updateButton = new TextButton("Update", skin);
                table.add(confirmedField).width(200).pad(10);
                table.add(providerField).width(200).pad(10);
                table.add(typeField).width(200).pad(10);
                table.add(updateButton).width(200).pad(10).row();


                updateButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Boolean newConfirmed = Boolean.valueOf(confirmedField.getText());
                        String newProvider = providerField.getText();
                        MobileTower newMobileTower = new MobileTower(
                            mobileTower.getLocation(),
                            newProvider,
                            mobileTower.getType(),
                            newConfirmed,
                            mobileTower.getLocator(),
                            mobileTower.getId()
                        );
                        HttpMobileTower newHttpMobileTower = new HttpMobileTower(sessionManager);
                        System.out.println(newHttpMobileTower.update(newMobileTower));

                    }
                });
            }
        }

    }


    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
