package com.natalia.natarunner.model.entities.npc;

import com.natalia.natarunner.manager.AudioManager;
import com.natalia.natarunner.manager.ResourceManager;
import com.natalia.natarunner.model.entities.player.PlayerBladecar;
import com.natalia.natarunner.model.entities.projectile.BulletBoss;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class NpcBoss {

    private Texture fireBeamTexture;

    // Parpadeo del beam
    private boolean showFireBeam = true;
    private float fireBeamTimer = 0f;
    private final float FIRE_BEAM_BLINK_INTERVAL = 0.12f;


    // Control de ráfagas
    private int shotsInBurst = 0;
    private final int BURST_SIZE = 4;

    private float shootTimer = 0f;
    private float pauseTimer = 0f;

    private boolean inBurst = false;

    private final float TIME_BETWEEN_SHOTS = 0.25f;
    private final float BURST_PAUSE = 1f;

    Texture texture;
    private Texture normalTexture;
    private Texture smilingTexture;

    private Texture hitTexture;        // textura cuando recibe disparo

    private boolean showingHit = false;  // indica si estamos mostrando la textura de impacto
    private float hitTimer = 0f;         // tiempo que llevamos mostrando la textura de impacto
    private final float HIT_DISPLAY_TIME = 0.1f; // tiempo en segundos que se muestra la textura de impacto

    // Sonido de impacto con intervalo para no repetirlo demasiado
    private Sound hurtSound;
    private float hurtSoundTimer = 0f;
    private final float HURT_SOUND_INTERVAL = 0.5f;

    private Rectangle rect;

    private boolean visible = false;
    private boolean atLeft = true;

    private float worldWidth;
    private float worldHeight;

    private int hits = 0;
    private int points = 10;       // puntos al destruir

    private float shootInterval = 0.2f;

    private float targetX;      // posición final X
    private boolean movingToCenter = false;

    // Movimiento vertical
    private float verticalSpeed = 1.5f; // unidades por segundo
    private boolean movingUp = true;     // dirección inicial hacia arriba

    // cambio de lado en pantalla
    private float sideTimer = 0f;
    private final float SIDE_INTERVAL = 8f;

    private Sound roarSound;
    AudioManager audio;

    public NpcBoss(float worldWidth, float worldHeight, AudioManager audio, ResourceManager resources) {
        this.normalTexture = resources.naveRoy;
        this.smilingTexture = resources.naveRoySonriendo;
        this.hitTexture = resources.naveRoyTocada;
        this.fireBeamTexture = resources.fireBeam;
        this.hurtSound = resources.hurtSound;
        roarSound = resources.roarSound;

        this.texture = normalTexture;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.audio = audio;

        rect = new Rectangle();

        rect.width = 2f;
        rect.height = 2f;

        hide();
    }

    public void hide() {
        visible = false;
        rect.x = -10;
        rect.y = -10;
    }

    public void appearLeft() {
        rect.x = -rect.width;                       // empieza fuera a la izquierda
        targetX = 0.2f;   // posición final centrada
        rect.y = (worldHeight - rect.height) / 2f;  // centrado vertical
        movingToCenter = true;                       // activa movimiento hacia el centro
        visible = true;                              // hacer visible
        hits = 0;                                    // reiniciar golpes

        atLeft = true;

        audio.playSound(roarSound);
        sideTimer = 0f;
    }

    public void appearRight(float hudHeight) {

        rect.x = worldWidth - rect.width - 0.2f;

        rect.y = MathUtils.random(
            1f,
            worldHeight - hudHeight - rect.height
        );

        visible = true;
        atLeft = false;
        hits = 0;

        audio.playSound(roarSound);
        sideTimer = 0f;

    }

    public void update(float delta) {
        if (!visible) return;

        // esta controla el tiempo para cambiar de lado
        sideTimer += delta;

        if (sideTimer >= SIDE_INTERVAL) {

            sideTimer = 0f;

            if (atLeft) {
                appearRight(1f);   // pasar al lado derecho
            } else {
                appearLeft();      // volver al lado izquierdo
            }
        }

        // Movimiento hacia el centro
        if (movingToCenter) {
            float speed = 3f; // unidades por segundo
            rect.x += speed * delta;

            if (rect.x >= targetX) {
                rect.x = targetX;
                movingToCenter = false; // ya llegó
            }
        }

        if (!movingToCenter) {
            // Movimiento vertical oscilante
            if (movingUp) {
                rect.y += verticalSpeed * delta;

                // Llegó al tope superior (sin tocar HUD)
                if (rect.y + rect.height >= worldHeight - 1f) { // 1f es margen para no tocar HUD
                    rect.y = worldHeight - 1f - rect.height;   // ajustar posición exacta
                    movingUp = false;                           // cambiar dirección
                }
            } else {
                rect.y -= verticalSpeed * delta;

                // Llegó al suelo
                if (rect.y <= 0f) {
                    rect.y = 0f;
                    movingUp = true;                            // cambiar dirección
                }
            }
        }


        // Parpadeo del fire beam solo al subir
        if (movingUp && !movingToCenter) {
            fireBeamTimer += delta;

            if (fireBeamTimer >= FIRE_BEAM_BLINK_INTERVAL) {
                fireBeamTimer = 0f;
                showFireBeam = !showFireBeam;
            }
        } else {
            // cuando no sube, no se muestra y reiniciamos el estado
            showFireBeam = false;
            fireBeamTimer = 0f;
        }


        // Control del flash de impacto
        if (showingHit) {
            hitTimer += delta;
            if (hitTimer >= HIT_DISPLAY_TIME) {
                showingHit = false;

                if (inBurst) {
                    texture = smilingTexture;
                } else {
                    texture = normalTexture; // volver a la normal
                }
            }
        } else {
            if (inBurst) {
                texture = smilingTexture;
            } else {
                texture = normalTexture;
            }
        }

        // Control del intervalo del sonido de impacto
        if (hurtSoundTimer > 0f) {
            hurtSoundTimer -= delta;
        }

        shootTimer += delta;
    }

    public boolean canShoot(float delta) {
        if (inBurst) {

            shootTimer += delta;

            if (shootTimer >= TIME_BETWEEN_SHOTS) {
                shootTimer = 0f;
                shotsInBurst++;

                if (shotsInBurst >= BURST_SIZE) {
                    inBurst = false;
                    pauseTimer = 0f;
                }

                return true;
            }

        } else {

            pauseTimer += delta;

            if (pauseTimer >= BURST_PAUSE) {
                inBurst = true;
                shotsInBurst = 0;
                shootTimer = 0f;
            }
        }

        return false;
    }

    public BulletBoss shoot(PlayerBladecar player, Texture bulletTexture) {

        float startX = rect.x + rect.width / 2f;
        float startY = rect.y + rect.height / 2f;

        float playerX = player.getRect().x + player.getRect().width / 2f;
        float playerY = player.getRect().y + player.getRect().height / 2f;

        float dirX = playerX - startX;
        float dirY = playerY - startY;

        float len = (float)Math.sqrt(dirX * dirX + dirY * dirY);

        dirX /= len;
        dirY /= len;

        float speed = 6f;

        return new BulletBoss(
            bulletTexture,
            startX,
            startY,
            0.4f,
            0.4f,
            dirX * speed,
            dirY * speed
        );
    }

    public void draw(SpriteBatch batch) {
        if (visible) {

            // Dibujar el fire beam solo cuando sube y toca mostrarlo
            if (movingUp && !movingToCenter && showFireBeam) {

                float beamWidth = rect.width * 0.15f;
                float beamHeight = rect.height * 0.55f;

                float beamX = rect.x + (rect.width - beamWidth) / 2f;
                float beamY = rect.y - beamHeight + 0.20f; // <--- esta línea mete el beam debajo de la nave.

                batch.draw(fireBeamTexture, beamX, beamY, beamWidth, beamHeight);
            }

            batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
        }
    }

    // Aquí es donde se acumulan los disparos que recibe el boss
    public void takeHit() {
        hits++;

        // Activar la textura de impacto
        showingHit = true;
        hitTimer = 0f;
        texture = hitTexture;

        // Reproducir el "ouch" solo si ha pasado un pequeño intervalo
        if (hurtSoundTimer <= 0f) {
            audio.playSound(hurtSound, 1f);
            hurtSoundTimer = HURT_SOUND_INTERVAL;
        }
    }

    public boolean phaseFinished() {
        return hits >= 10;
    }

    public Rectangle getRect() {
        return rect;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isAtLeft() {
        return atLeft;
    }

    public int getHits() {
        return hits;
    }

    public int getPoints() {
        return points;
    }

    public void dispose() {
        // Los recursos compartidos se liberan en ResourceManager
    }
}
