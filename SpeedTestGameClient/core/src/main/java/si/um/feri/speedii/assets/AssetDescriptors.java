package si.um.feri.speedii.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetDescriptors {
    public static final AssetDescriptor<TextureAtlas> IMAGES =
        new AssetDescriptor<TextureAtlas>(AssetPaths.IMAGES, TextureAtlas.class);
    public static final AssetDescriptor<TextureAtlas> UI_IMAGES =
        new AssetDescriptor<TextureAtlas>(AssetPaths.UI_IMAGES, TextureAtlas.class);

    public static final AssetDescriptor<Skin> UI_SKIN =
        new AssetDescriptor<Skin>(AssetPaths.UI_SKIN, Skin.class);
}
