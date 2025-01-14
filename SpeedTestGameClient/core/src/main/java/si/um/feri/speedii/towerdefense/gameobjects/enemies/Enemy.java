package si.um.feri.speedii.towerdefense.gameobjects.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.List;

import si.um.feri.speedii.towerdefense.logic.GameLogic;

public class Enemy {
    public enum Type {
        BASIC, REGENATIVE, DEFENSE, BOSS
    }

    // Enemy attributes
    private int health;
    private float speed;
    private Type type;
    private Animation<TextureRegion> idleAnimation;
    private float stateTime;
    private Vector2 position;
    private Vector2 direction;
    private GameLogic gameLogic;

    // Constructor to initialize enemy attributes
    public Enemy(int health, float speed, Type type, String texturePath, GameLogic gameLogic) {
        this.health = health;
        this.speed = speed;
        this.type = type;
        this.stateTime = 0f;
        this.gameLogic = gameLogic;
        this.position = new Vector2();
        this.direction = new Vector2(1, 0); // Start moving right

        loadAnimation(texturePath);
    }

    // Load animation frames from texture
    private void loadAnimation(String texturePath) {
        Texture texture = new Texture(texturePath);
        TextureRegion[][] tmp = TextureRegion.split(texture, 32, 32);
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < tmp.length - 1; i++) {
            for (int j = 0; j < 4; j++) {
                frames.add(tmp[i][j]);
            }
        }
        idleAnimation = new Animation<>(0.15f, frames, Animation.PlayMode.LOOP);
    }

    // Getters and setters for enemy attributes
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    // Update enemy state
    public void update(float delta) {
        stateTime += delta;
        updateDirection();
        move(delta);
    }

    // Update enemy direction (only up, right and down points)
    private void updateDirection() {
        boolean directionChanged = false;

        directionChanged = checkPoints(gameLogic.goUpPoints, new Vector2(0, 1)) || directionChanged;
        directionChanged = checkPoints(gameLogic.goRightPoints, new Vector2(1, 0)) || directionChanged;
        directionChanged = checkPoints(gameLogic.goDownPoints, new Vector2(0, -1)) || directionChanged;

        if (!directionChanged && position.epsilonEquals(gameLogic.intersection, 5.0f)) {
            direction.set(0, Math.random() < 0.5 ? 1 : -1);
            directionChanged = true;
        }

        if (position.epsilonEquals(gameLogic.endPoint, 5.0f)) {
            direction.set(0, 0);
        }

        if (!directionChanged) {
            checkTeleportPoints();
        }
    }

    // Check if enemy is at specific points and update direction
    private boolean checkPoints(List<Vector2> points, Vector2 newDirection) {
        for (Vector2 point : points) {
            if (position.epsilonEquals(point, 5.0f)) {
                direction.set(newDirection);
                return true;
            }
        }
        return false;
    }

    // Check if enemy is at teleport points and update position and direction
    private void checkTeleportPoints() {
        if (position.epsilonEquals(gameLogic.teleportEnterDown[0], 5.0f)) {
            position.set(gameLogic.teleportEnterDown[1]);
            direction.set(0, -1);
        } else if (position.epsilonEquals(gameLogic.teleportEnterUp[0], 5.0f)) {
            position.set(gameLogic.teleportEnterUp[1]);
            direction.set(0, 1);
        } else if (position.epsilonEquals(gameLogic.teleportInterSectionEnterGoRight, 5.0f)) {
            position.set(gameLogic.teleportIntersectionGoRight.get(Math.random() < 0.5 ? 0 : 1));
            direction.set(1, 0);
        } else if (position.epsilonEquals(gameLogic.teleportIntersectionEnterGoUpGoDown, 5.0f)) {
            if (Math.random() < 0.5) {
                position.set(gameLogic.teleportIntersectionGoDown);
                direction.set(0, -1);
            } else {
                position.set(gameLogic.teleportIntersectionGoUp);
                direction.set(0, 1);
            }
        } else {
            for (Vector2 point : gameLogic.teleportIntersectionGoRight) {
                if (position.epsilonEquals(point, 5.0f)) {
                    direction.set(1, 0);
                    break;
                }
            }
        }

        for (Vector2 enterPoint : gameLogic.teleportIntersectionEnter) {
            if (position.epsilonEquals(enterPoint, 5.0f)) {
                position.set(gameLogic.teleportIntersectionLeave);
                break;
            }
        }
    }

    // Move enemy based on direction and speed
    private void move(float delta) {
        Vector2 movement = new Vector2(direction).scl(speed * delta);
        position.add(movement);
    }

    // Draw enemy on the screen
    public void draw(SpriteBatch spriteBatch, float x, float y) {
        TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime);
        float scale = 2.0f; // Adjust the scale factor as needed
        spriteBatch.draw(currentFrame, x, y, currentFrame.getRegionWidth() * scale, currentFrame.getRegionHeight() * scale);
    }

    public void setPosition(float x, float y) { this.position.set(x, y); }
    public float getX() { return position.x; }
    public float getY() { return position.y; }
}
