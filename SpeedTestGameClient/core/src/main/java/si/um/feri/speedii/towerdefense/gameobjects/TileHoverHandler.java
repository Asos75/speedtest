package si.um.feri.speedii.towerdefense.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import si.um.feri.speedii.screens.GameScreenComponents.InitializeGame;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TileHoverHandler extends InputListener {
    private TiledMapTileLayer fieldLayer;
    private ShapeRenderer shapeRenderer;
    private Rectangle hoveredTile;
    private OrthographicCamera camera;
    private InitializeGame initializeGame;
    private Set<Rectangle> occupiedTiles;

    public TileHoverHandler(TiledMapTileLayer fieldLayer, OrthographicCamera camera, InitializeGame initializeGame) {
        this.fieldLayer = fieldLayer;
        this.shapeRenderer = new ShapeRenderer();
        this.camera = camera;
        this.initializeGame = initializeGame;
        this.occupiedTiles = new HashSet<>();
        //Gdx.app.log("TileHoverHandler", "Initialized with field layer: " + fieldLayer.getName());
    }

    public void render() {
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        hoveredTile = null;

        float tileWidth = fieldLayer.getTileWidth();
        float tileHeight = fieldLayer.getTileHeight();

        // Calculate the tile coordinates
        int tileX = (int) (mousePos.x / tileWidth);
        int tileY = (int) (mousePos.y / tileHeight);

        TiledMapTileLayer.Cell cell = fieldLayer.getCell(tileX, tileY);
        if (cell != null) {
            hoveredTile = new Rectangle(tileX * tileWidth, tileY * tileHeight, tileWidth, tileHeight);
        }

        if (hoveredTile != null) {
            if (initializeGame.getSelectedTower() != null) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            } else {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            }
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.rect(hoveredTile.x, hoveredTile.y, hoveredTile.width, hoveredTile.height);
            shapeRenderer.end();
        }

        handleTileHoverAndClick(mousePos, tileX, tileY);
    }

    private void handleTileHoverAndClick(Vector3 mousePos, int tileX, int tileY) {
        Container<Table> selectedTowerContainer = initializeGame.getSelectedTower();
        if (selectedTowerContainer != null && hoveredTile != null && !occupiedTiles.contains(hoveredTile)) {
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                //Gdx.app.log("TileHoverHandler", "Placing tower at: (" + hoveredTile.x + ", " + hoveredTile.y + ")");
                float tileWidth = fieldLayer.getTileWidth();
                float tileHeight = fieldLayer.getTileHeight();
                String regionName = selectedTowerContainer.getName(); // Assuming the container has a name set to the regionName
                initializeGame.placeTower(regionName, hoveredTile.x, hoveredTile.y, tileWidth, tileHeight);
                occupiedTiles.add(new Rectangle(hoveredTile.x, hoveredTile.y, hoveredTile.width, hoveredTile.height));
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            } else {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }
        } else {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }
    }

    public void removeOccupiedTile(Vector2 position) {
        for (Iterator<Rectangle> iterator = occupiedTiles.iterator(); iterator.hasNext();) {
            Rectangle tile = iterator.next();
            if (tile.contains(position)) {
                iterator.remove();
                break;
            }
        }
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
