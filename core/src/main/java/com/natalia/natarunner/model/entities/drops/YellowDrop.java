package com.natalia.natarunner.model.entities.drops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

public class YellowDrop extends Drop {

    public static final int POINTS = 15;
    public static final int PENALTY = 25;

    private float animTime = 0f;
    private float scaleFactor = 1f;

    public YellowDrop(Texture texture, float x, float y) {
        super(texture, x, y, 0.5f, 0.5f, -1.6f);
        sprite.setOriginCenter();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        updatePulse(delta);
    }

    private void updatePulse(float delta) {
        animTime += delta;
        scaleFactor = 1f + 0.25f * ((MathUtils.sin(animTime * 8f) + 1f) / 2f);
        sprite.setScale(scaleFactor, scaleFactor);
    }
}
