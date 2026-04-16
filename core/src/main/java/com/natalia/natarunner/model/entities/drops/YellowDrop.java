package com.natalia.natarunner.model.entities.drops;

import com.natalia.natarunner.config.GameConfig;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class YellowDrop extends Drop {

    public static final int POINTS = 15;
    public static final int PENALTY = 25;

    private float animTime = 0f;
    private float scaleFactor = 1f;

    private enum State {
        NORMAL,
        WARNING,
        LETHAL
    }

    private State state = State.NORMAL;

    private final Texture normalTexture;
    private final Texture sableadaTexture;

    private boolean lethal = false;

    private float transformTimer = 0f;
    private float transformTime;

    private boolean justTransformed = false;

    private boolean visible = true;
    private float blinkTimer = 0f;
    private int blinkToggleCount = 0;

    private static final int WARNING_BLINKS = 3;
    private static final float BLINK_INTERVAL = 0.10f;

    public YellowDrop(Texture normalTexture, Texture sableadaTexture, float x, float y) {
        super(normalTexture, x, y, 0.5f, 0.5f, -1.6f);

        this.normalTexture = normalTexture;
        this.sableadaTexture = sableadaTexture;

        sprite.setOriginCenter();

        if (MathUtils.random() < GameConfig.probabilidadSable) {
            this.transformTime = MathUtils.random(0.6f, 2.5f);
        } else {
            this.transformTime = Float.MAX_VALUE;
        }
    }

    @Override
    public void update(float delta) {
        if (state == State.WARNING) {
            super.update(delta);

            blinkTimer += delta;

            if (blinkTimer >= BLINK_INTERVAL) {
                blinkTimer -= BLINK_INTERVAL;
                visible = !visible;
                blinkToggleCount++;

                if (blinkToggleCount >= WARNING_BLINKS * 2) {
                    convertToLethal();
                }
            }

            return;
        }

        super.update(delta);

        if (state == State.NORMAL) {
            updatePulse(delta);

            transformTimer += delta;
            if (transformTimer >= transformTime) {
                startWarning();
            }
        }
    }

    private void updatePulse(float delta) {
        animTime += delta;
        scaleFactor = 1f + 0.25f * ((MathUtils.sin(animTime * 8f) + 1f) / 2f);
        sprite.setOriginCenter();
        sprite.setScale(scaleFactor, scaleFactor);
    }

    private void startWarning() {
        state = State.WARNING;
        visible = true;
        blinkTimer = 0f;
        blinkToggleCount = 0;
    }

    private void convertToLethal() {
        state = State.LETHAL;
        lethal = true;

        sprite.setTexture(sableadaTexture);
        sprite.setScale(1f, 1f);
        visible = true;
        updateBounds();

        justTransformed = true;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (visible) {
            super.draw(batch);
        }
    }

    public boolean consumeTransformEvent() {
        if (justTransformed) {
            justTransformed = false;
            return true;
        }
        return false;
    }

    public boolean isLethal() {
        return lethal;
    }

    public boolean isWarning() {
        return state == State.WARNING;
    }
}
