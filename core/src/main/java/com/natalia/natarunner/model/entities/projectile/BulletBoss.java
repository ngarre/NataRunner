package com.natalia.natarunner.model.entities.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class BulletBoss {

    private Texture texture;
    private Rectangle rect;

    private float velX;
    private float velY;

    public BulletBoss(Texture texture, float x, float y, float w, float h, float velX, float velY) {

        this.texture = texture;
        this.velX = velX;
        this.velY = velY;

        rect = new Rectangle(x, y, w, h);
    }

    public void update(float delta) {
        rect.x += velX * delta;
        rect.y += velY * delta;
    }

    public void draw(SpriteBatch batch) {
        if (velX > 0) {
            // bala hacia la derecha → flip horizontal
            batch.draw(texture,
                rect.x + rect.width,
                rect.y,
                -rect.width,
                rect.height);
        } else {
            // bala hacia la izquierda → normal
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

    public boolean isOutOfWorld(float worldWidth, float worldHeight) {
        return rect.x > worldWidth ||
            rect.x + rect.width < 0 ||
            rect.y > worldHeight ||
            rect.y + rect.height < 0;
    }
}
