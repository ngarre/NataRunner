package com.natalia.natarunner.model.entities.drops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Drop {

    protected Sprite sprite;
    protected Rectangle bounds = new Rectangle();
    protected float speedY;

    public Drop(Texture texture, float x, float y, float width, float height, float speedY) {
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        sprite.setPosition(x, y);
        this.speedY = speedY;
        updateBounds();
    }

    public void update(float delta) {
        sprite.translateY(speedY * delta);
        updateBounds();
    }

    public void updateBounds() {
        bounds.set(
            sprite.getX(),
            sprite.getY(),
            sprite.getWidth(),
            sprite.getHeight()
        );
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public boolean isOutOfScreen() {
        return sprite.getY() < -sprite.getHeight();
    }
}
