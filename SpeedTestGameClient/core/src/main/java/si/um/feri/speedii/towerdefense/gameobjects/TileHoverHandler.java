package si.um.feri.speedii.towerdefense.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class TileHoverHandler {
    private TiledMapTileLayer fieldLayer;
    private ShapeRenderer shapeRenderer;
    private Rectangle hoveredTile;

    public TileHoverHandler(TiledMapTileLayer fieldLayer, OrthographicCamera camera) {
        this.fieldLayer = fieldLayer;
        this.shapeRenderer = new ShapeRenderer();
        //Gdx.app.log("TileHoverHandler", "Initialized with field layer: " + fieldLayer.getName());
    }

    public void render() {
        Vector2 mousePos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        hoveredTile = null;

        int tileX = (int) (mousePos.x / fieldLayer.getTileWidth());
        int tileY = (int) ((Gdx.graphics.getHeight() - mousePos.y) / fieldLayer.getTileHeight());

        TiledMapTileLayer.Cell cell = fieldLayer.getCell(tileX, tileY);
        if (cell != null) {
            hoveredTile = new Rectangle(tileX * fieldLayer.getTileWidth(), tileY * fieldLayer.getTileHeight(), fieldLayer.getTileWidth(), fieldLayer.getTileHeight());
        }

        if (hoveredTile != null) {
            // Whatever you prefer to draw the hovered tile
            //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.rect(hoveredTile.x, hoveredTile.y, hoveredTile.width, hoveredTile.height);
            shapeRenderer.end();
        }
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
