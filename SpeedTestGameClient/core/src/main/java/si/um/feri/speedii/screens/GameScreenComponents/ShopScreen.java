package si.um.feri.speedii.screens.GameScreenComponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import si.um.feri.speedii.assets.AssetDescriptors;
import si.um.feri.speedii.assets.RegionNames;
import si.um.feri.speedii.config.GameConfig;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ShopScreen {
    private final Stage shopStage;
    private final Viewport sideViewport;
    private ShapeRenderer shapeRenderer;

    public ShopScreen(AssetManager assetManager) {
        sideViewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        shopStage = new Stage(sideViewport);
        setupShopStage(assetManager);
    }

    private void setupShopStage(AssetManager assetManager) {
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        Table table = new Table();
        table.top();
        table.setFillParent(true);

        TextureAtlas atlas = assetManager.get(AssetDescriptors.IMAGES);

        Image routerIcon = new Image(atlas.findRegion(RegionNames.ROUTER));
        Image cellTowerIcon = new Image(atlas.findRegion(RegionNames.CELL_TOWER));
        Image switchIcon = new Image(atlas.findRegion(RegionNames.SWITCH));

        float iconSize = GameConfig.SHOP_ICON_SIZE;

        routerIcon.setSize(iconSize, iconSize);
        cellTowerIcon.setSize(iconSize, iconSize);
        switchIcon.setSize(iconSize, iconSize);

        table.add(routerIcon).size(iconSize).pad(10).row();
        table.add(cellTowerIcon).size(iconSize).pad(10).row();
        table.add(switchIcon).size(iconSize).pad(10).row();

        shopStage.addActor(table);
    }

    private void drawBorder() {
        float borderX = GameConfig.WORLD_WIDTH * 0.75f;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.line(borderX, 0, borderX, GameConfig.WORLD_HEIGHT);
        shapeRenderer.end();
    }

    public Stage getShopStage() {
        return shopStage;
    }

    public void resize(int width, int height) {
        sideViewport.update(width, height, true);
    }

    public void dispose() {
        shopStage.dispose();
    }
}
