package si.um.feri.speedii.screens.mapcomponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import si.um.feri.speedii.classes.Measurement;
import si.um.feri.speedii.classes.MobileTower;
import si.um.feri.speedii.utils.Constants;
import si.um.feri.speedii.utils.MapRasterTiles;
import si.um.feri.speedii.utils.ZoomXY;

import java.util.ArrayList;
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

    public List<Vector2> turnMobileTowerCoordinatesToPixels(List<MobileTower> mobileTowers, ZoomXY beginTile) {
        List<Vector2> positions = new ArrayList<>();

        for (MobileTower mobileTower : mobileTowers) {
            System.out.println(mobileTower.getLocation().coordinates.get(1) + ", " +
                mobileTower.getLocation().coordinates.get(0));

            Vector2 position = MapRasterTiles.getPixelPosition(
                mobileTower.getLocation().coordinates.get(1),
                mobileTower.getLocation().coordinates.get(0),
                beginTile.x,
                beginTile.y
            );

            positions.add(position);
        }

        return positions;
    }


    public void drawGrid(ShapeRenderer shapeRenderer, OrthographicCamera camera, float gridAlpha) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float cellWidth = (float) Constants.MAP_WIDTH / Constants.GRID_SIZE;
        float cellHeight = (float) Constants.MAP_HEIGHT / Constants.GRID_SIZE;

        for (int x = 0; x < Constants.GRID_SIZE; x++) {
            for (int y = 0; y < Constants.GRID_SIZE; y++) {
                float avgSpeed = 0;
                if (speedCounts[x][y] > 0) {
                    avgSpeed = speedSums[x][y] / speedCounts[x][y];

                }
                if(speedCounts[x][y] == 0) {
                    avgSpeed = getAverageOfNeighbors(x, y);
                }
                Color color = getColorForSpeed(avgSpeed, gridAlpha);

                shapeRenderer.setColor(color);
                shapeRenderer.rect(
                    x * cellWidth,
                    y * cellHeight,
                    cellWidth,
                    cellHeight
                );
            }
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public float getAverageOfNeighbors(int x, int y) {
        int rows = speedSums.length;
        int cols = speedSums[0].length;

        float sum = 0;
        float count = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                int neighborX = x + i;
                int neighborY = y + j;

                if (neighborX >= 0 && neighborX < rows && neighborY >= 0 && neighborY < cols) {
                    if(speedCounts[neighborX][neighborY] == 0) continue;
                    sum += speedSums[neighborX][neighborY] / speedCounts[neighborX][neighborY];
                    count++;
                }
            }
        }

        return count > 0 ? (float) sum / count : 0;
    }

    public int getSpeed(int touchPosX, int touchPosY, ZoomXY beginTile) {
        int gridX = touchPosX / (Constants.MAP_WIDTH / Constants.GRID_SIZE);
        int gridY = touchPosY / (Constants.MAP_HEIGHT / Constants.GRID_SIZE);
        if(speedCounts[gridX][gridY] == 0) return -1;
        return (int) speedSums[gridX][gridY] / speedCounts[gridX][gridY];
    }

    private Color getColorForSpeed(float speed, float gridAlpha) {
        speed = Math.max(20_000_000, Math.min(100_000_000, speed));

        float t = (speed - 20_000_000) / 90_000_000f;

        float red = 1 - t;
        float green = t;

        return new Color(red, green, 0, gridAlpha);
    }
}

