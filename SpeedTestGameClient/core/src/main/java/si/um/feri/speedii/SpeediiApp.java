package si.um.feri.speedii;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import org.bson.types.ObjectId;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import si.um.feri.speedii.classes.Location;
import si.um.feri.speedii.classes.Measurement;
import si.um.feri.speedii.classes.SessionManager;
import si.um.feri.speedii.classes.Type;
import si.um.feri.speedii.classes.User;
import si.um.feri.speedii.connection.MongoDBConnection;
import si.um.feri.speedii.dao.MeasurementCRUD;
import si.um.feri.speedii.dao.http.HttpMeasurement;
import si.um.feri.speedii.dao.http.HttpUser;
import si.um.feri.speedii.screens.GameScreen;
import si.um.feri.speedii.screens.MapScreen;
import si.um.feri.speedii.assets.AssetDescriptors;
import si.um.feri.speedii.screens.MenuScreen;

public class SpeediiApp extends Game {

    private AssetManager assetManager;
    private SpriteBatch batch;


    @Override
    public void create() {
        MongoDBConnection.connect();
        assetManager = new AssetManager();
        assetManager.setLoader(ParticleEffect.class, new ParticleEffectLoader(new InternalFileHandleResolver()));
        assetManager.load(AssetDescriptors.IMAGES);
        assetManager.load(AssetDescriptors.UI_SKIN);

        assetManager.finishLoading();

        batch = new SpriteBatch();

        //setScreen(new MapScreen());
        //setScreen(new GameScreen(this));
        //setScreen(new GameScreen());
        //new MenuScreen(this);

        SessionManager sessionManager = new SessionManager();
        HttpUser httpUser = new HttpUser(sessionManager);
        boolean successful = httpUser.authenticate("admin", "admin");
        System.out.println("Successful: " + successful);
        System.out.println("Session: " + sessionManager);

        ObjectId id = new ObjectId("67878b8fbc9ea613ee06ab50");


        /*

        Location location = new Location(Arrays.asList(1.0, 2.0));
        Measurement measurement = new Measurement(69420420, Type.wifi, "provider", location, LocalDateTime.now(), user);
        System.out.println(measurement.getId());

        HttpMeasurement httpMeasurement = new HttpMeasurement(sessionManager);
        try {
            boolean success = httpMeasurement.insert(measurement);
            System.out.println("Success: " + success);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

         */

        HttpMeasurement httpMeasurement = new HttpMeasurement(sessionManager);
        try {
            Measurement measurement = httpMeasurement.getById(id);

            httpMeasurement.delete(measurement);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
    public SpriteBatch getBatch() {
        return batch;
    }
}
