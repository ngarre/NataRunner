package com.natalia.natarunner.model.entities.drops;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.natalia.natarunner.manager.AudioManager;
import com.natalia.natarunner.model.entities.projectile.RedDropProjectile;

public class RedDrop extends Drop {

    private float speedX = 0.35f;
    private Rectangle reducedBounds = new Rectangle();

    private float animTime = 0f;
    private float scaleFactor = 1f;

    private boolean shooting = false;
    private float shootCooldownTimer = 0f;
    private float nextShotDelay = MathUtils.random(2.5f, 4.5f);

    private float shootingTimer = 0f;
    private boolean projectileSpawned = false;

    public RedDrop(Texture texture, float x, float y) {
        super(texture, x, y, 0.8f, 0.8f, -1f);
        sprite.setOriginCenter();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        updatePulse(delta);
        updateBounds();

        if (!shooting) {
            shootCooldownTimer += delta;
        }
    }

    public void updateShooting(
        float delta,
        Array<RedDropProjectile> projectiles,
        Texture projectileTexture,
        Sound shootSound,
        AudioManager audio,
        boolean canStartShooting
    ) {
        if (!shooting) {
            if (canStartShooting && shootCooldownTimer >= nextShotDelay) {
                shooting = true;
                shootingTimer = 0f;
                projectileSpawned = false;
            }
            return;
        }

        shootingTimer += delta;

        if (!projectileSpawned && shootingTimer >= 0.35f) {
            float pWidth = 0.22f;
            float pHeight = 0.22f;

            float pX = sprite.getX() + sprite.getWidth() / 2f - pWidth / 2f;
            float pY = sprite.getY() + 0.05f;

            projectiles.add(new RedDropProjectile(
                projectileTexture,
                pX,
                pY,
                pWidth,
                pHeight,
                6.5f
            ));

            projectileSpawned = true;

            if (shootSound != null) {
                audio.playSound(shootSound);
            }
        }

        if (shootingTimer >= 0.75f) {
            shooting = false;
            shootingTimer = 0f;
            shootCooldownTimer = 0f;
            nextShotDelay = MathUtils.random(2.5f, 4.5f);
        }
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

    public boolean isShooting() {
        return shooting;
    }
}
