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

    private Texture normalTexture;
    private Texture sableadaTexture;

    private boolean lethal = false;

    private float transformTimer = 0f;
    private float transformTime;

    private boolean justTransformed = false;

    // =========================
    // PARPADEO DE AVISO
    // =========================
    private boolean visible = true;
    private float blinkTimer = 0f;
    private int blinkToggleCount = 0;

    private static final int WARNING_BLINKS = 3;       // 3 parpadeos completos
    private static final float BLINK_INTERVAL = 0.10f; // velocidad del parpadeo


    // =========================
    // CONGELACIÓN DESPUÉS DE MUTAR
    // =========================
    private boolean frozen = false;
    private float freezeTimer = 0f;
    private static final float FREEZE_DURATION = 1f; // antes tenía 2f; 0.8 suele ser más jugable

    public YellowDrop(Texture normalTexture, Texture sableadaTexture, float x, float y) {
        super(normalTexture, x, y, 0.5f, 0.5f, -1.6f);

        this.normalTexture = normalTexture;
        this.sableadaTexture = sableadaTexture;

        if (MathUtils.random() < GameConfig.probabilidadSable) {   // % de mutar
            this.transformTime = MathUtils.random(0.6f, 2.5f);
        } else {
            this.transformTime = Float.MAX_VALUE; // nunca muta
        }
    }

    @Override
    public void update(float delta) {

        // =========================
        // 1) SI ESTÁ EN AVISO, PARPADEA
        // =========================
        if (state == State.WARNING) {
            super.update(delta); // sigue cayendo mientras avisa

            blinkTimer += delta;

            if (blinkTimer >= BLINK_INTERVAL) {
                blinkTimer -= BLINK_INTERVAL;
                visible = !visible;
                blinkToggleCount++;

                // 3 parpadeos completos = 6 cambios visible/no visible
                if (blinkToggleCount >= WARNING_BLINKS * 2) {
                    convertirEnLetal();
                }
            }

            return;
        }


        // =========================
        // 2) SI ESTÁ CONGELADA YA SIENDO LETAL
        // =========================
        if (frozen) {
            freezeTimer += delta;

            if (freezeTimer >= FREEZE_DURATION) {
                frozen = false;
            }

            return; // no se mueve mientras está congelada
        }

        // =========================
        // 3) MOVIMIENTO NORMAL
        // =========================
        super.update(delta);
        if (state == State.NORMAL) {
            updatePulse(delta);
        }

        // =========================
        // 4) COMPROBAR SI DEBE EMPEZAR EL AVISO
        // =========================
        if (state == State.NORMAL) {
            transformTimer += delta;

            if (transformTimer >= transformTime) {
                empezarWarning();
            }
        }
    }

    // el engorde...
    private void updatePulse(float delta) {
        animTime += delta;
        scaleFactor = 1f + 0.25f * ((MathUtils.sin(animTime * 8f) + 1f) / 2f);
        sprite.setOriginCenter();
        sprite.setScale(scaleFactor, scaleFactor);
    }

    private void empezarWarning() {
        state = State.WARNING;
        visible = true;
        blinkTimer = 0f;
        blinkToggleCount = 0;
    }

    private void convertirEnLetal() {
        state = State.LETHAL;
        lethal = true;

        sprite.setTexture(sableadaTexture);
        visible = true; // para que termine visible
        updateBounds();

        justTransformed = true;

        // pequeña pausa al transformarse para darle margen al jugador
        frozen = true;
        freezeTimer = 0f;
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
