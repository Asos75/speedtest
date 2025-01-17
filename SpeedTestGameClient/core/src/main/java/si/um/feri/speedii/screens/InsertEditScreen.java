package si.um.feri.speedii.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import org.bson.types.ObjectId;

import si.um.feri.speedii.classes.MobileTower;
import si.um.feri.speedii.classes.SessionManager;
import si.um.feri.speedii.classes.User;
import si.um.feri.speedii.dao.http.HttpMeasurement;
import si.um.feri.speedii.classes.Measurement;
import si.um.feri.speedii.dao.http.HttpMobileTower;

import java.io.IOException;
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

    public InsertEditScreen(SessionManager sessionManager, User user) {
        this.sessionManager = sessionManager;
        this.user = user;

        // Inicializacija SpriteBatch in BitmapFont za risanje besedila
        this.spriteBatch = new SpriteBatch();
        this.font = new BitmapFont(); // Privzeta pisava
        this.font.getData().setScale(1.5f); // Povečava pisave za boljšo berljivost

        // Pridobivanje podatkov
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

    }

    @Override
    public void render(float delta) {
        // Počisti zaslon
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1); // Temno siva barva ozadja
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();

        if (measurements == null || mobileTowers == null) {
            font.draw(spriteBatch, "Error loading data.", 50, Gdx.graphics.getHeight() - 50);
        } else {
            // Prikaz naslovov z obrobo
            drawBoxedText("Measurements", 50, 720);


            // Prikaz meritev v stolpcih
            font.draw(spriteBatch, "Speed", 50, 680);
            font.draw(spriteBatch, "Provider:", 200, 680);
            font.draw(spriteBatch, "Date", 450, 680);


            int yOffset = 30;
            for (Measurement measurement : measurements) {
                font.draw(spriteBatch, String.valueOf(measurement.getSpeed()) + " mbps", 50, 650 - yOffset);
                font.draw(spriteBatch, String.valueOf(measurement.getProvider()), 200, 650 - yOffset);
                font.draw(spriteBatch, measurement.getTime().toString(), 450, 650 - yOffset);
                yOffset += 30;
            }


            drawBoxedText("Mobile Towers", 50, 400);
            // Prikaz mobilnih stolpov v stolpcih
            font.draw(spriteBatch, "Provider", 50, 350);
            font.draw(spriteBatch, "Location", 200, 350);
            font.draw(spriteBatch, "Type", 650, 350);
            yOffset = 30;

            for (MobileTower tower : mobileTowers) {
                font.draw(spriteBatch, tower.getProvider(), 50, 320 - yOffset);
                font.draw(spriteBatch, tower.getLocation().toString(), 200, 320 - yOffset);
                font.draw(spriteBatch, String.valueOf(tower.getType()), 650, 320 - yOffset);
                yOffset += 30;
            }
        }

        spriteBatch.end();
    }

    private void drawBoxedText(String text, float x, float y) {
        float padding = 10f;
        float width = font.getRegion().getRegionWidth() + padding * 2;
        float height = font.getRegion().getRegionHeight() + padding * 2;

        // Nariši obrobo (pravokotnik)
        spriteBatch.setColor(0.2f, 0.2f, 0.2f, 1); // Temno siva obroba
        // spriteBatch.draw(new Texture("images/white_pixel.png"), x - padding, y - height + padding, width, height);

        // Prikaz besedila
        spriteBatch.setColor(1, 1, 1, 1); // Bela barva besedila
        font.draw(spriteBatch, text, x, y);
    }


    @Override
    public void resize(int width, int height) {
        // Če uporabljate Viewport, ga posodobite tukaj
    }

    @Override
    public void show() {
        // Inicializacija ali predpriprava virov
    }

    @Override
    public void pause() {
        // Uporabno za mobilne igre (shrani stanje)
    }

    @Override
    public void resume() {
        // Obnovi stanje po pavzi
    }

    @Override
    public void hide() {
        // Čiščenje, če se zaslon skrije
    }

    @Override
    public void dispose() {
        // Sprostite vire
        spriteBatch.dispose();
        font.dispose();
    }
}
