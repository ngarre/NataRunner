package com.natalia.natarunner.model.entities.drops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class RedDrop extends Drop {

    private float speedX = 0.35f;
    private Rectangle reducedBounds = new Rectangle();

    private float animTime = 0f;
    private float scaleFactor = 1f;

    public RedDrop(Texture texture, float x, float y) {
        super(texture, x, y, 0.8f, 0.8f, -1f);
        sprite.setOriginCenter();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        updatePulse(delta);
        updateBounds();
    }

    public void chase(float targetCenterX, float delta) {
        float myCenterX = sprite.getX() + sprite.getWidth() / 2f;

        if (myCenterX < targetCenterX) {
            sprite.translateX(speedX * delta);
        } else {
            sprite.translateX(-speedX * delta);
        }

        updateBounds();
    }

    public Rectangle getReducedBounds() {
        float scaledWidth = sprite.getWidth() * scaleFactor;
        float scaledHeight = sprite.getHeight() * scaleFactor;

        float centerX = sprite.getX() + sprite.getWidth() / 2f;
        float centerY = sprite.getY() + sprite.getHeight() / 2f;

        float visualX = centerX - scaledWidth / 2f;
        float visualY = centerY - scaledHeight / 2f;

        float margin = 0.12f;

        reducedBounds.set(
            visualX + margin,
            visualY + margin,
            scaledWidth - margin * 2f,
            scaledHeight - margin * 2f
        );

        return reducedBounds;
    }

    private void updatePulse(float delta) {
        animTime += delta;
        scaleFactor = 1f + 0.25f * ((MathUtils.sin(animTime * 8f) + 1f) / 2f);
        sprite.setScale(scaleFactor, scaleFactor);
    }
}
