package si.um.feri.speedii.utils;

import com.badlogic.gdx.Gdx;

public class Constants {
    public static final String TILE_FOLDER_PATH = "assets/maptiles/";
    public static final int NUM_TILES = 5;
    public static final int ZOOM = 15;
    public static final int MAP_WIDTH = MapRasterTiles.TILE_SIZE * NUM_TILES;
    public static final int MAP_HEIGHT = MapRasterTiles.TILE_SIZE * NUM_TILES;
    public static final int HUD_WIDTH = Gdx.graphics.getWidth();
    public static final int HUD_HEIGHT = Gdx.graphics.getHeight();

    public static final int GRID_SIZE = 75;
    //TODO this should be updated when speeds are fixed
    public static final int SPEED_LOW = 50000000;
    public static final int SPEED_MEDIUM = 60000000;
    public static final int SPEED_HIGH = 75000000;
    public static final int MAX_SPEED = 100000000;
    public static final float OVERLAY_ALPHA = 0.4f;
    public static final int SPEED_INFO_WIDTH = 600;
    public static final int SPEED_INFO_HEIGHT = 400;

}
