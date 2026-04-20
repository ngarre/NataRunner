package com.natalia.natarunner.model.entities.player;

import com.natalia.natarunner.manager.AudioManager;
import com.natalia.natarunner.manager.ResourceManager;
import com.natalia.natarunner.model.entities.projectile.BulletChicas;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PlayerBladecar {

    public static final int PENALTY = 25;

    private Rectangle rect;

    private Sound giroSound;
    private Sound aterrizajeSound;
    private Sound reciboImpacto;
    private Sound shootSound;

    private float speed = 10f;
    private boolean arrived = false;

    private boolean invulnerable = false;
    private float invulnerableTimer = 0f;
    private final float INVULNERABLE_TIME = 0.8f;

    private boolean hitFlash = false;
    private float hitFlashTimer = 0f;
    private float hitFlashDuration = 0.12f;
    private boolean impactSoundPlayed = false;

    private AudioManager audio;

    // Dirección horizontal
    private boolean facingLeft = true;

    // =========================
    // ANIMACIÓN NORMAL
    // =========================
    private Animation<TextureRegion> animation;
    private float animationTime = 0f;
    private float frameDuration = 0.5f;
    private TextureRegion idleFrame;

    // =========================
    // TEXTURA DE DISPARO
    // =========================
    private TextureRegion shootFrame;
    private boolean shooting = false;
    private float shootTimer = 0f;
    private float shootDuration = 0.10f; // configurable

    public PlayerBladecar(float viewportWidth, float viewportHeight, float desiredHeight,
                          AudioManager audio, ResourceManager resources) {

        this.audio = audio;

        this.giroSound = resources.giroSound;
        this.aterrizajeSound = resources.aterrizajeSound;
        this.reciboImpacto = resources.reciboImpacto;
        this.shootSound = resources.shootSound;

        TextureRegion frame1 = new TextureRegion(resources.cocheBladecar1);
        TextureRegion frame2 = new TextureRegion(resources.cocheBladecar2);
        TextureRegion frame3 = new TextureRegion(resources.cocheBladecar3);
        this.shootFrame = new TextureRegion(resources.cocheBladecarDispara);

        Array<TextureRegion> frames = new Array<>();
        frames.add(frame1);
        frames.add(frame2);
        frames.add(frame3);

        animation = new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
        idleFrame = frame1;

        int texWidthPx = resources.cocheBladecar1.getWidth();
        int texHeightPx = resources.cocheBladecar1.getHeight();

        float aspectRatio = (float) texWidthPx / texHeightPx;

        float height = desiredHeight;
        float width = desiredHeight * aspectRatio;

        float x = (viewportWidth - width) / 2f;
        float y = viewportHeight + height;

        rect = new Rectangle(x, y, width, height);
    }

    public void update(float delta) {

        if (!arrived) {
            float groundY = 0f;
            rect.y -= speed * 0.5f * delta;

            if (rect.y <= groundY) {
                rect.y = groundY;
                arrived = true;
                audio.playSound(aterrizajeSound);
            }
        }

        if (arrived) {
            animationTime += delta;
        }

        // Control de duración del sprite de disparo
        if (shooting) {
            shootTimer -= delta;
            if (shootTimer <= 0f) {
                shooting = false;
                shootTimer = 0f;
            }
        }

        if (hitFlash) {
            if (!impactSoundPlayed) {
                audio.playSound(reciboImpacto);
                impactSoundPlayed = true;
            }

            hitFlashTimer -= delta;
            if (hitFlashTimer <= 0f) {
                hitFlash = false;
            }
        }

        if (invulnerable) {
            invulnerableTimer -= delta;
            if (invulnerableTimer <= 0f) {
                invulnerable = false;
                invulnerableTimer = 0f;
            }
        }
    }

    public void handleMouseDrag(Vector2 mouseWorld) {
        rect.setPosition(
            mouseWorld.x - rect.width / 2f,
            mouseWorld.y - rect.height / 2f
        );
    }

    public void move(float delta, float worldWidth, float worldHeight, float hudHeight) {

        float moveSpeed = 10f;

        boolean ctrlPressed = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
            Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            rotate();
        }

        if (ctrlPressed) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) ||
                Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                rotate();
            }
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                rect.x -= moveSpeed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                rect.x += moveSpeed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                rect.y += moveSpeed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                rect.y -= moveSpeed * delta;
            }
        }

        if (rect.x < 0) rect.x = 0;
        if (rect.x + rect.width > worldWidth) rect.x = worldWidth - rect.width;

        float minY = 0f;
        float maxY = worldHeight - hudHeight - rect.height;
        if (rect.y < minY) rect.y = minY;
        if (rect.y > maxY) rect.y = maxY;
    }

    public void rotate() {
        flip();
        audio.playSound(giroSound);
    }

    public void flip() {
        facingLeft = !facingLeft;
    }

    public boolean isFacingLeft() {
        return facingLeft;
    }

    public void hit() {
        hitFlash = true;
        hitFlashTimer = hitFlashDuration;
        impactSoundPlayed = false;
    }

    private void startShootEffect() {
        shooting = true;
        shootTimer = shootDuration;
    }

    public void draw(SpriteBatch batch) {

        if (hitFlash) {
            if ((int)(hitFlashTimer * 100) % 2 == 0) {
                batch.setColor(1f, 1f, 1f, 0.3f);
            }
        }

        TextureRegion currentFrame;

        if (shooting) {
            currentFrame = shootFrame;
        } else {
            currentFrame = arrived ? animation.getKeyFrame(animationTime) : idleFrame;
        }

        if (facingLeft) {
            batch.draw(currentFrame, rect.x, rect.y, rect.width, rect.height);
        } else {
            batch.draw(
                currentFrame,
                rect.x + rect.width,
                rect.y,
                -rect.width,
                rect.height
            );
        }

        batch.setColor(1f, 1f, 1f, 1f);
    }

    public BulletChicas shoot(Texture bulletTexture) {

        Rectangle r = getRect();

        float bulletWidth = 0.3f;
        float bulletHeight = 0.15f;

        float bulletX;
        float bulletY = r.y + r.height * 0.45f - bulletHeight / 2f;
        bulletY -= 0.10f;

        boolean toRight = !isFacingLeft();

        if (toRight) {
            bulletX = r.x + r.width - 0.40f;
        } else {
            bulletX = r.x - bulletWidth + 0.40f;
        }

        // Activar sprite temporal de disparo
        startShootEffect();

        if (shootSound != null) {
            audio.playSound(shootSound);
        }

        return new BulletChicas(bulletTexture, bulletX, bulletY, bulletWidth, bulletHeight, toRight);
    }

    public Rectangle getRect() {
        return rect;
    }

    public boolean hasArrived() {
        return arrived;
    }



    public void setShootDuration(float shootDuration) {
        this.shootDuration = shootDuration;
    }


    public void startInvulnerability() {
        invulnerable = true;
        invulnerableTimer = INVULNERABLE_TIME;
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public void knockBackFrom(Rectangle sourceRect, float distance,
                              float worldWidth, float worldHeight, float hudHeight) {

        float playerCenterX = rect.x + rect.width / 2f;
        float playerCenterY = rect.y + rect.height / 2f;

        float sourceCenterX = sourceRect.x + sourceRect.width / 2f;
        float sourceCenterY = sourceRect.y + sourceRect.height / 2f;

        float dx = playerCenterX - sourceCenterX;
        float dy = playerCenterY - sourceCenterY;

        float len = (float) Math.sqrt(dx * dx + dy * dy);

        if (len == 0f) {
            dx = 1f;
            dy = 0f;
            len = 1f;
        }

        dx /= len;
        dy /= len;

        // cálculos del robote si toco al boss
        rect.x += dx * distance * 1.5f;
        rect.y += dy * distance * 0.8f;

        if (rect.x < 0f) rect.x = 0f;
        if (rect.x + rect.width > worldWidth) rect.x = worldWidth - rect.width;

        float minY = 0f;
        float maxY = worldHeight - hudHeight - rect.height;

        if (rect.y < minY) rect.y = minY;
        if (rect.y > maxY) rect.y = maxY;
    }

    public void dispose() {
        // Las texturas las libera ResourceManager
    }
}
