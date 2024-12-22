package si.um.feri.speedii.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.speedii.SpeediiApp;
import si.um.feri.speedii.assets.AssetDescriptors;
import si.um.feri.speedii.assets.RegionNames;
import si.um.feri.speedii.config.GameConfig;

public class GameScreen implements Screen {

    private final SpeediiApp app;
    private final AssetManager assetManager;

    private Viewport gameViewport;
    private Viewport sideViewport;

    private Stage gameStage;
    private Stage shopStage;

    private ShapeRenderer shapeRenderer;

    public GameScreen(final SpeediiApp app) {
        this.app = app;
        this.assetManager = app.getAssetManager();
    }

    @Override
    public void show() {
        gameViewport = new FitViewport(GameConfig.WORLD_WIDTH * 0.75f, GameConfig.WORLD_HEIGHT);
        sideViewport = new FitViewport(GameConfig.WORLD_WIDTH * 0.25f, GameConfig.WORLD_HEIGHT);

        gameStage = new Stage(gameViewport, app.getBatch());
        shopStage = new Stage(sideViewport, app.getBatch());

        shapeRenderer = new ShapeRenderer();

        Gdx.input.setInputProcessor(gameStage);

        setupShopStage();
    }

    private void setupShopStage() {
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

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.5f, 0, 1);

        gameStage.act(delta);
        shopStage.act(delta);

        gameStage.draw();
        shopStage.draw();

        drawBorder();
    }

    private void drawBorder() {
        float borderX = GameConfig.WORLD_WIDTH * 0.75f;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.line(borderX, 0, borderX, GameConfig.WORLD_HEIGHT);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        sideViewport.update(width, height, true);
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
        gameStage.dispose();
        shopStage.dispose();
        shapeRenderer.dispose();
    }
}
