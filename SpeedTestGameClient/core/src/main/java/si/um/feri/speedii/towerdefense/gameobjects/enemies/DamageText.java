package si.um.feri.speedii.towerdefense.gameobjects.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class DamageText {
    private String text;
    private Vector2 position;
    private float duration;
    private BitmapFont font;

    public DamageText(String text, Vector2 position, float duration, BitmapFont font) {
        this.text = text;
        this.position = position;
        this.duration = duration;
        this.font = font;
        this.font.setColor(Color.RED);
    }

    public void update(float delta) {
        duration -= delta;
        position.y += delta * 20; // Move text upwards
    }

    public void render(SpriteBatch batch) {
        font.draw(batch, text, position.x, position.y);
    }

    public boolean isFinished() {
        return duration <= 0;
    }
}
