package si.um.feri.speedii;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Map;

import si.um.feri.speedii.screens.GameScreen;
import si.um.feri.speedii.screens.MapScreen;
import si.um.feri.speedii.assets.AssetDescriptors;
public class SpeediiApp extends Game {

    private AssetManager assetManager;
    private SpriteBatch batch;


    @Override
    public void create() {
        assetManager = new AssetManager();
        assetManager.setLoader(ParticleEffect.class, new ParticleEffectLoader(new InternalFileHandleResolver()));

        assetManager.load(AssetDescriptors.IMAGES);

        assetManager.finishLoading();

        batch = new SpriteBatch();

        setScreen(new GameScreen(this));
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
    public SpriteBatch getBatch() {
        return batch;
    }
}
