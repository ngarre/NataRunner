package com.natalia.natarunner.model.entities.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PlayerTears {

    private final Sprite sprite;
    private final Rectangle bounds = new Rectangle();

    private final Texture manosTexture;
    private final Texture manosCerradasTexture;

    private float speed = 15f;

    private float animationTimer = 0f;
    private final float frameDuration = 0.5f;
    private int currentFrame = 0;

    public PlayerTears(Texture manosTexture, Texture manosCerradasTexture, float x, float y) {
        this.manosTexture = manosTexture;
        this.manosCerradasTexture = manosCerradasTexture;

        sprite = new Sprite(manosTexture);
        sprite.setSize(1f, 1f);
        sprite.setPosition(x, y);
        updateBounds();
    }

    public void handleKeyboard(float delta) {
        float moveX = 0f;
        float moveY = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) moveX += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) moveX -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) moveY += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) moveY -= 1f;

        if (moveX != 0f && moveY != 0f) {
            float inv = 0.7071f;
            moveX *= inv;
            moveY *= inv;
        }

        float dx = moveX * speed * delta;
        float dy = moveY * speed * delta;

        sprite.translate(dx, dy);
    }

    public void handleMouseDrag(Vector2 mouseWorld) {
        float targetX = mouseWorld.x - sprite.getWidth() / 2f;
        float targetY = mouseWorld.y - sprite.getHeight() / 2f;
        sprite.setPosition(targetX, targetY);
    }

    public void update(float delta, float worldWidth, float worldHeight, float hudHeight) {
        float clampedX = MathUtils.clamp(
            sprite.getX(),
            0,
            worldWidth - sprite.getWidth()
        );

        float clampedY = MathUtils.clamp(
            sprite.getY(),
            0,
            worldHeight - hudHeight - sprite.getHeight()
        );

        sprite.setPosition(clampedX, clampedY);

        updateAnimation(delta);
        updateBounds();
    }

    private void updateAnimation(float delta) {
        animationTimer += delta;

        if (animationTimer >= frameDuration) {
            animationTimer -= frameDuration;
            currentFrame = (currentFrame + 1) % 2;
        }

        if (currentFrame == 0) {
            sprite.setTexture(manosTexture);
        } else {
            sprite.setTexture(manosCerradasTexture);
        }
    }

    public float getCenterX() {
        return sprite.getX() + sprite.getWidth() / 2f;
    }

    public void updateBounds() {
        bounds.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
