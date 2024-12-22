package si.um.feri.speedii.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AssetDescriptors {
    public static final AssetDescriptor<TextureAtlas> IMAGES =
        new AssetDescriptor<TextureAtlas>(AssetPaths.IMAGES, TextureAtlas.class);
}
