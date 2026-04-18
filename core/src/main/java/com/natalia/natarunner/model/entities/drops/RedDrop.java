package com.natalia.natarunner.model.entities.drops;

import com.natalia.natarunner.config.GameConfig;
import com.natalia.natarunner.manager.AudioManager;
import com.natalia.natarunner.model.entities.projectile.RedDropProjectile;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class RedDrop extends Drop {

    private float speedX = 0.35f;
    private Rectangle reducedBounds = new Rectangle();

    // Animación de engorde
    private float animTime = 0f;
    private float scaleFactor = 1f;

    // =========================
    // DISPARO
    // =========================
    private enum State {
        FALLING,
        SHOOTING
    }

    private State state = State.FALLING;

    private boolean hasDecidedToShoot = false;
    private boolean willShoot = false;
    private boolean finishedShooting = false;

    // private float shootTriggerTime;   <--- disparo por tiempo aleatorio
    private float shootTriggerY;

    // Ángulos: 0, 30, 60 ... 330
    private int currentAngleIndex = 0;
    private static final int ANGLE_STEP = 30;

    // Disparo secuencial: 4 proyectiles uno detrás de otro
    private int bulletsShotInCurrentBurst = 0;
    private static final int BULLETS_PER_BURST = 4;

    private float shotTimer = 0f;
    private float shotInterval = 0.06f;

    private float burstPauseTimer = 0f;
    private float burstPauseDuration = 0.20f;
    private boolean waitingNextBurst = false;

    // Temblor mientras escupe
    private float shakeTime = 0f;
    private float baseX;
    private boolean shakeBaseCaptured = false;

    public RedDrop(Texture texture, float x, float y) {
        super(texture, x, y, 0.8f, 0.8f, -1f);
        sprite.setOriginCenter();
        //shootTriggerTime = MathUtils.random(1.0f, 5.0f);  <--- ajuste disparo por tiempo aleatorio
        shootTriggerY = MathUtils.random(1.5f, 5.5f);  // <--- lo hago por altura aleatoria, no tiempo
    }

    @Override
    public void update(float delta) {
        // Solo cae verticalmente si no está disparando
        if (state == State.FALLING) {
            super.update(delta);
        }

        updatePulse(delta);
        updateBounds();
    }

    public void chase(float targetCenterX, float delta) {
        if (state != State.FALLING) return;

        float myCenterX = sprite.getX() + sprite.getWidth() / 2f;

        if (myCenterX < targetCenterX) {
            sprite.translateX(speedX * delta);
        } else {
            sprite.translateX(-speedX * delta);
        }

        updateBounds();
    }

    public void updateShooting(float delta,
                               Array<RedDropProjectile> projectiles,
                               Texture projectileTexture,
                               Sound shootSound,
                               AudioManager audio,
                               boolean canStartShooting) {

        if (finishedShooting) return;


        // Decide una sola vez si va a disparar,
        // pero solo cuando tenga permiso para iniciar.

        //if (!hasDecidedToShoot && shootDecisionTime >= shootTriggerTime) {   <--- la comento. es por tiempo aleatorio
        //   cuando dispara

        if (!hasDecidedToShoot && sprite.getY() <= shootTriggerY) {

            // Si otra gota ya está disparando, esta espera su turno
            if (!canStartShooting) {
                return;
            }

            hasDecidedToShoot = true;
            willShoot = MathUtils.randomBoolean(GameConfig.probabilidadRojaDispara);

            if (willShoot) {
                state = State.SHOOTING;
            } else {
                finishedShooting = true;
            }
        }

        if (state != State.SHOOTING) return;

        if (!shakeBaseCaptured) {
            baseX = sprite.getX();
            shakeBaseCaptured = true;
        }

        // Temblor horizontal mientras dispara
        shakeTime += delta;
        float offsetX = MathUtils.sin(shakeTime * 65f) * 0.05f;
        sprite.setX(baseX + offsetX);

        int angle = currentAngleIndex * ANGLE_STEP;

        // 360 no se dispara
        if (angle >= 360) {
            state = State.FALLING;
            finishedShooting = true;
            sprite.setX(baseX);
            updateBounds();
            return;
        }

        // Pausa antes de pasar a la siguiente dirección
        if (waitingNextBurst) {
            burstPauseTimer += delta;

            if (burstPauseTimer >= burstPauseDuration) {
                burstPauseTimer = 0f;
                waitingNextBurst = false;
                bulletsShotInCurrentBurst = 0;
                currentAngleIndex++;
                shotTimer = 0f;
            }

            updateBounds();
            return;
        }

        // Disparo secuencial: una bala detrás de otra
        shotTimer += delta;

        if (shotTimer >= shotInterval) {
            shotTimer = 0f;

            fireSingleShot(projectiles, projectileTexture, angle, shootSound, audio);
            bulletsShotInCurrentBurst++;

            // Cuando lanza 4, espera antes de cambiar de ángulo
            if (bulletsShotInCurrentBurst >= BULLETS_PER_BURST) {
                waitingNextBurst = true;
            }
        }

        updateBounds();
    }

    private void fireSingleShot(Array<RedDropProjectile> projectiles,
                                Texture projectileTexture,
                                float angleDeg,
                                Sound shootSound,
                                AudioManager audio) {

        float centerX = sprite.getX() + sprite.getWidth() / 2f;
        float centerY = sprite.getY() + sprite.getHeight() / 2f;

        projectiles.add(new RedDropProjectile(
            projectileTexture,
            centerX - 0.09f,
            centerY - 0.09f,
            angleDeg
        ));

        // Sonido por bala
        audio.playSound(shootSound, 0.6f);
    }

    public boolean isShooting() {
        return state == State.SHOOTING;
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

        // Oscila entre 1.00 y 1.25
        scaleFactor = 1f + 0.25f * ((MathUtils.sin(animTime * 8f) + 1f) / 2f);
        sprite.setScale(scaleFactor, scaleFactor);
    }
}
