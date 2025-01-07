package si.um.feri.speedii.towerdefense.gameobjects.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

// Handle sprite sheet animations
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import si.um.feri.speedii.towerdefense.logic.GameLogic;

public class Enemy {
    public enum Type {
        BASIC, REGENATIVE, DEFENSE, BOSS
    }

    private int health;
    private float speed;
    private Type type;
    private Animation<TextureRegion> idleAnimation;
    private float stateTime;
    private Vector2 position;
    private Vector2 direction;
    private GameLogic gameLogic;

    public Enemy(int health, float speed, Type type, String texturePath, GameLogic gameLogic) {
        this.health = health;
        this.speed = speed;
        this.type = type;
        this.stateTime = 0f;
        this.gameLogic = gameLogic;
        this.position = new Vector2();
        this.direction = new Vector2(1, 0); // Start moving right

        // Load the sprite sheet as a texture
        Texture texture = new Texture(texturePath);

        // Split the texture into individual frames
        TextureRegion[][] tmp = TextureRegion.split(texture, 32, 32);

        // Convert the 2D array to a 1D array (first three rows * 4 columns)
        // Hardcoded for the current sprite sheet
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < tmp.length-1; i++) {
            for (int j = 0; j < 4; j++) {
                frames.add(tmp[i][j]);
            }
        }

        // Duration of the animation
        idleAnimation = new Animation<>(0.15f, frames, Animation.PlayMode.LOOP);
    }

    public int getHealth() { return health; }

    public void setHealth(int health) { this.health = health; }

    public float getSpeed() { return speed; }

    public void setSpeed(float speed) { this.speed = speed; }

    public Type getType() { return type; }

    public void setType(Type type) { this.type = type; }

    public void update(float delta) {
        stateTime += delta;

        boolean directionChanged = false;

        for (Vector2 point : gameLogic.goUpPoints) {
            if (position.epsilonEquals(point, 5.0f)) { // Increased tolerance value
                direction.set(0, 1); // Move up
                directionChanged = true;
                System.out.println("Moving up at point: " + point);
                break;
            }
        }
        if (!directionChanged) {
            for (Vector2 point : gameLogic.goRightPoints) {
                if (position.epsilonEquals(point, 5.0f)) { // Increased tolerance value
                    direction.set(1, 0); // Move right
                    directionChanged = true;
                    System.out.println("Moving right at point: " + point);
                    break;
                }
            }
        }
        if (!directionChanged) {
            for (Vector2 point : gameLogic.goDownPoints) {
                if (position.epsilonEquals(point, 5.0f)) { // Increased tolerance value
                    direction.set(0, -1); // Move down
                    directionChanged = true;
                    System.out.println("Moving down at point: " + point);
                    break;
                }
            }
        }
        if (position.epsilonEquals(gameLogic.endPoint, 5.0f)) { // Increased tolerance value
            direction.set(0, 0); // Stop moving
            System.out.println("Reached end point: " + gameLogic.endPoint);
        }

        position.mulAdd(direction, speed * delta);
    }

    public void draw(SpriteBatch spriteBatch, float x, float y) {
        TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime);
        float scale = 2.0f; // Adjust the scale factor as needed
        spriteBatch.draw(currentFrame, x, y, currentFrame.getRegionWidth() * scale, currentFrame.getRegionHeight() * scale);
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }
}
