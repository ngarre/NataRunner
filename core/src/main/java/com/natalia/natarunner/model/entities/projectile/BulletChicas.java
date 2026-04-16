package com.natalia.natarunner.model.entities.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class BulletChicas {

    private Texture texture;
    private Rectangle rect;
    private float speed;
    private boolean toRight;

    public BulletChicas(Texture texture, float x, float y, float w, float h, boolean toRight) {
        this.texture = texture;
        this.toRight = toRight;

        rect = new Rectangle(x, y, w, h);

        speed = toRight ? 20f : -20f;
    }

    public void update(float delta) {
        rect.x += speed * delta;
    }

    public boolean isOutOfWorld(float worldWidth) {
        return rect.x > worldWidth || rect.x + rect.width < 0;
    }

    public void draw(SpriteBatch batch) {
        if (toRight) {
            batch.draw(texture,
                rect.x + rect.width,
                rect.y,
                -rect.width,
                rect.height);
        } else {
            batch.draw(texture,
                rect.x,
                rect.y,
                rect.width,
                rect.height);
        }
    }

    public Rectangle getRect() {
        return rect;
    }
}
