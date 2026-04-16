package com.natalia.natarunner.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LogoIntroAnimation {

    private final Texture logoTexture;

    private float alpha = 0f;
    private float fadeSpeed = 0.3f;

    private float timer = 0f;
    private float y;
    private float targetY;
    private boolean movingUp = false;
    private float moveSpeed = 1f;

    private float width;
    private float height;

    private boolean finished = false;

    public boolean isFinished() {
        return finished;
    }

    public LogoIntroAnimation(Texture logoTexture, Viewport viewport) {
        this.logoTexture = logoTexture;

        float ratio = (float) logoTexture.getWidth() / (float) logoTexture.getHeight();
        height = 4.0f;
        width = height * ratio;

        y = viewport.getWorldHeight() - height - 0.5f;
        targetY = viewport.getWorldHeight() * 0.8f - height / 2f;
    }

    public void update(float delta) {
        if (alpha < 1f) {
            alpha += fadeSpeed * delta;
            if (alpha > 1f) alpha = 1f;
        }

        timer += delta;

        if (timer > 2f && !movingUp) {
            movingUp = true;
        }

        if (movingUp) {
            y += moveSpeed * delta;

            if (y > targetY) {
                y = targetY;
                movingUp = false;
            }
        }

        if (alpha >= 1f && !movingUp && y == targetY) {
            finished = true;
        }
    }

    public void draw(SpriteBatch batch, Viewport viewport) {
        float x = viewport.getWorldWidth() / 2f - width / 2f;

        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(logoTexture, x, y, width, height);
        batch.setColor(1f, 1f, 1f, 1f);
    }
}
