package com.natalia.natarunner.model.entities.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PlayerBladecar {

    private final Animation<TextureRegion> animation;
    private final Rectangle bounds = new Rectangle();

    private float x;
    private float y;

    private float width;
    private float height;

    private float speed = 5.5f;
    private float stateTime = 0f;

    public PlayerBladecar(Texture tex1, Texture tex2, Texture tex3, float x, float y) {
        TextureRegion[] frames = new TextureRegion[3];
        frames[0] = new TextureRegion(tex1);
        frames[1] = new TextureRegion(tex2);
        frames[2] = new TextureRegion(tex3);

        animation = new Animation<>(0.12f, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);

        this.x = x;
        this.y = y;

        // Mantener proporción real del sprite
        this.height = 1.1f;
        float aspectRatio = (float) tex1.getWidth() / tex1.getHeight();
        this.width = height * aspectRatio;

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

        x += moveX * speed * delta;
        y += moveY * speed * delta;
    }

    public void handleMouseDrag(Vector2 mouseWorld) {
        x = mouseWorld.x - width / 2f;
        y = mouseWorld.y - height / 2f;
    }

    public void update(float delta, float worldWidth, float worldHeight, float hudHeight) {
        stateTime += delta;

        x = MathUtils.clamp(x, 0f, worldWidth - width);
        y = MathUtils.clamp(y, 0f, worldHeight - hudHeight - height);

        updateBounds();
    }

    private void updateBounds() {
        bounds.set(x, y, width, height);
    }

    public void draw(SpriteBatch batch) {
        TextureRegion frame = animation.getKeyFrame(stateTime, true);
        batch.draw(frame, x, y, width, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getCenterX() {
        return x + width / 2f;
    }

    public float getCenterY() {
        return y + height / 2f;
    }
}
