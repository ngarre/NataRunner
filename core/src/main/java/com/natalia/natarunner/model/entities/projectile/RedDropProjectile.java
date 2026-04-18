package com.natalia.natarunner.model.entities.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class RedDropProjectile {

    private final Texture texture;
    private final Rectangle bounds;
    private final float speed;

    public RedDropProjectile(Texture texture, float x, float y, float width, float height, float speed) {
        this.texture = texture;
        this.bounds = new Rectangle(x, y, width, height);
        this.speed = speed;
    }

    public void update(float delta) {
        bounds.y -= speed * delta;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public boolean isOutOfWorld(float worldWidth, float worldHeight) {
        return bounds.x + bounds.width < 0
            || bounds.x > worldWidth
            || bounds.y + bounds.height < 0
            || bounds.y > worldHeight;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getX() {
        return bounds.x;
    }

    public float getY() {
        return bounds.y;
    }
}
