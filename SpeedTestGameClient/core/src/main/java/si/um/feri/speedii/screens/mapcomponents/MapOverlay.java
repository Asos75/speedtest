package si.um.feri.speedii.screens.mapcomponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import si.um.feri.speedii.classes.Measurement;
import si.um.feri.speedii.utils.Constants;
import si.um.feri.speedii.utils.MapRasterTiles;
import si.um.feri.speedii.utils.ZoomXY;

import java.util.List;

public class MapOverlay {
    private final float[][] speedSums;
    private final int[][] speedCounts;

    public MapOverlay() {
        this.speedSums = new float[Constants.GRID_SIZE][Constants.GRID_SIZE];
        this.speedCounts = new int[Constants.GRID_SIZE][Constants.GRID_SIZE];
    }

    public void calculateAverageSpeeds(List<Measurement> measurements, ZoomXY beginTile) {
        for (Measurement measurement : measurements) {
            Vector2 position = MapRasterTiles.getPixelPosition(
                measurement.getLocation().coordinates.get(1), measurement.getLocation().coordinates.get(0),
                beginTile.x, beginTile.y
            );
            int gridX = (int) (position.x / ((float) Constants.MAP_WIDTH / Constants.GRID_SIZE));
            int gridY = (int) (position.y / ((float) Constants.MAP_HEIGHT / Constants.GRID_SIZE));

            if (gridX >= 0 && gridX < Constants.GRID_SIZE && gridY >= 0 && gridY < Constants.GRID_SIZE) {
                speedSums[gridX][gridY] += measurement.getSpeed();
                speedCounts[gridX][gridY]++;
            }
        }
    }

    public void drawGrid(ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float cellWidth = (float) Constants.MAP_WIDTH / Constants.GRID_SIZE;
        float cellHeight = (float) Constants.MAP_HEIGHT / Constants.GRID_SIZE;

        for (int x = 0; x < Constants.GRID_SIZE; x++) {
            for (int y = 0; y < Constants.GRID_SIZE; y++) {
                if (speedCounts[x][y] > 0) {
                    float avgSpeed = speedSums[x][y] / speedCounts[x][y];
                    Color color = getColorForSpeed(avgSpeed);

                    shapeRenderer.setColor(color);
                    shapeRenderer.rect(
                        x * cellWidth,
                        y * cellHeight,
                        cellWidth,
                        cellHeight
                    );
                }
            }
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public int getSpeed(int touchPosX, int touchPosY, ZoomXY beginTile) {
        int gridX = touchPosX / (Constants.MAP_WIDTH / Constants.GRID_SIZE);
        int gridY = touchPosY / (Constants.MAP_HEIGHT / Constants.GRID_SIZE);
        if(speedCounts[gridX][gridY] == 0) return -1;
        return (int) speedSums[gridX][gridY] / speedCounts[gridX][gridY];
    }

    private Color getColorForSpeed(float speed) {
        if (speed < Constants.SPEED_LOW) return new Color(1, 0, 0, Constants.OVERLAY_ALPHA);
        if (speed < Constants.SPEED_MEDIUM) return new Color(1, 0.5f, 0, Constants.OVERLAY_ALPHA);
        if (speed < Constants.SPEED_HIGH) return new Color(1, 1, 0, Constants.OVERLAY_ALPHA);
        return new Color(0, 1, 0, Constants.OVERLAY_ALPHA);
    }
}

