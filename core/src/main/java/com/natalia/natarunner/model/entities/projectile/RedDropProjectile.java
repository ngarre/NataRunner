package com.natalia.natarunner.model.entities.projectile;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class RedDropProjectile {

    private final Sprite sprite;
    private final Rectangle bounds = new Rectangle();
    private final Vector2 velocity = new Vector2();

    public RedDropProjectile(Texture texture, float x, float y, float angleDeg) {
        sprite = new Sprite(texture);

        // Tamaño pequeño para que parezca una gotita disparada
        sprite.setSize(0.18f, 0.18f);
        sprite.setPosition(x, y);

        float speed = 6.0f;
        velocity.set(1f, 0f).setAngleDeg(angleDeg).scl(speed);

        updateBounds();
    }

    public void update(float delta) {
        sprite.translate(velocity.x * delta, velocity.y * delta);
        updateBounds();
    }

    public void draw(SpriteBatch spriteBatch) {
        sprite.draw(spriteBatch);
    }

    public boolean isOutOfWorld(float worldWidth, float worldHeight) {
        return sprite.getX() + sprite.getWidth() < 0f
            || sprite.getX() > worldWidth
            || sprite.getY() + sprite.getHeight() < 0f
            || sprite.getY() > worldHeight;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getX() {
        return sprite.getX();
    }

    public float getY() {
        return sprite.getY();
    }

    private void updateBounds() {
        bounds.set(
            sprite.getX(),
            sprite.getY(),
            sprite.getWidth(),
            sprite.getHeight()
        );
    }
}
