package com.natalia.natarunner.model.entities.npc;

import com.natalia.natarunner.manager.AudioManager;
import com.natalia.natarunner.model.entities.player.PlayerBladecar;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class NpcChicas {

    private Rectangle rect;

    private float speedX;          // horizontal
    private float amplitudeY;      // altura máxima del zigzag
    private float frequencyY;      // frecuencia del zigzag
    private float initialY;        // posición inicial Y
    private boolean movingRight;   // dirección de salida
    private int points = 15;       // puntos al destruir
    private float factorVelocidad = 2;
    private float worldHeight;
    private float hudHeight;

    private float preparingBaseX = 0f;
    private float preparingBaseY = 0f;

    // Animación normal
    private Animation<TextureRegion> animation;
    private float stateTime = 0f;
    private float frameDuration = 0.20f;

    // Guardamos frame1 para usarlo en parada y embestida
    private TextureRegion frame1Region;

    // Textura al recibir impacto
    private TextureRegion hitFrame;

    // Estado de impacto / desaparición
    private boolean dying = false;
    private float hitEffectTimer = 0f;
    private int blinkCount = 0;

    private final float BLINK_INTERVAL = 0.10f; // tiempo entre cambios visible/no visible
    private final int TOTAL_BLINKS = 3;         // 3 parpadeos completos

    private float rotation = 0f;

    private boolean visibleDuringBlink = true;
    private boolean readyToRespawn = false;

    // =====================================================
    // NUEVA LÓGICA DE PARADA + EMBESTIDA AL PLAYER
    // =====================================================
    private Sound attackSound;

    private enum AttackState {
        NORMAL,
        PREPARING,
        DASHING
    }

    private AttackState attackState = AttackState.NORMAL;

    private boolean willDash = false;
    private float attackDecisionProbability = 0.45f; // <--- probabilidad de lanzarse

    private float aliveTimer = 0f;
    private float dashTriggerTime = 0f;

    private float preparingTimer = 0f;
    private final float PREPARING_TIME = 1f;

    private float dashSpeed = 12f;    // <--- Velocidad al lanzarse
    private float dashVelX = 0f;
    private float dashVelY = 0f;

    public NpcChicas(Texture frame1, Texture frame2, Texture frame3, Texture frame4, Texture hitTexture,
                     Sound attackSound,
                     float worldWidth, float worldHeight) {

        // Decidir lado de aparición aleatorio
        movingRight = MathUtils.randomBoolean();

        // Tamaño de la nave
        float width = 1f;
        float height = 1f;

        rect = new Rectangle();
        rect.width = width;
        rect.height = height;

        // Crear animación de 4 frames
        Array<TextureRegion> frames = new Array<>();

        frame1Region = new TextureRegion(frame1);
        frames.add(frame1Region);
        frames.add(new TextureRegion(frame2));
        frames.add(new TextureRegion(frame3));
        frames.add(new TextureRegion(frame4));

        animation = new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);

        // Frame de impacto
        hitFrame = new TextureRegion(hitTexture);

        this.attackSound = attackSound;

        resetPosition(worldWidth, worldHeight, hudHeight);

        // Zigzag aleatorio
        amplitudeY = MathUtils.random(0.5f, 1.5f);
        frequencyY = MathUtils.random(1f, 3f);
    }

    // Resetear posición cuando aparezca o sea destruida
    public void resetPosition(float worldWidth, float worldHeight, float hudHeight) {

        this.worldHeight = worldHeight;
        this.hudHeight = hudHeight;

        rect.y = MathUtils.random(1f, worldHeight - hudHeight - rect.height);
        initialY = rect.y;

        // Elegir lado aleatorio cada vez que reaparece
        movingRight = MathUtils.randomBoolean();

        if (movingRight) {
            rect.x = -rect.width; // empieza fuera por la izquierda
            speedX = MathUtils.random(3f, 5f) / factorVelocidad;
        } else {
            rect.x = worldWidth;   // empieza fuera por la derecha
            speedX = -MathUtils.random(3f, 5f) / factorVelocidad;
        }

        // Zigzag aleatorio cada vez que reaparece
        amplitudeY = MathUtils.random(0.5f, 1.5f);
        frequencyY = MathUtils.random(1f, 3f);

        // Reiniciar animación
        stateTime = 0f;

        // Reiniciar estado visual
        dying = false;
        hitEffectTimer = 0f;
        blinkCount = 0;
        visibleDuringBlink = true;
        readyToRespawn = false;

        // Reiniciar estado de ataque
        resetAttackState();
    }

    private void resetAttackState() {
        attackState = AttackState.NORMAL;
        aliveTimer = 0f;
        preparingTimer = 0f;
        dashVelX = 0f;
        dashVelY = 0f;
        rotation = 0f;

        willDash = MathUtils.randomBoolean(attackDecisionProbability);
        dashTriggerTime = MathUtils.random(0.8f, 2.2f);
    }

    // Activar estado de impacto antes de desaparecer
    public void takeHit() {
        if (dying) return;

        dying = true;
        hitEffectTimer = 0f;
        blinkCount = 0;
        visibleDuringBlink = true;
        readyToRespawn = false;

        // Si estaba preparando o embistiendo, se cancela
        attackState = AttackState.NORMAL;
        dashVelX = 0f;
        dashVelY = 0f;
    }

    // Actualizar posición y movimiento zigzag / embestida
    public void update(float delta, PlayerBladecar player, AudioManager audio) {

        // Si está en fase de destrucción, no sigue moviéndose normal
        if (dying) {
            hitEffectTimer += delta;

            if (hitEffectTimer >= BLINK_INTERVAL) {
                hitEffectTimer = 0f;
                visibleDuringBlink = !visibleDuringBlink;
                blinkCount++;

                // 3 parpadeos completos = 6 cambios de visibilidad
                if (blinkCount >= TOTAL_BLINKS * 2) {
                    readyToRespawn = true;
                    dying = false;
                }
            }

            return;
        }

        aliveTimer += delta;

        switch (attackState) {

            case NORMAL:
                // Movimiento original: horizontal + zigzag vertical
                rect.x += speedX * delta;

                // Avance de la animación
                stateTime += delta;

                // Zigzag vertical original
                float y = initialY + amplitudeY * MathUtils.sin(frequencyY * rect.x);
                rect.y = MathUtils.clamp(y, 0.5f, worldHeight - hudHeight - rect.height);

                // Decisión de parar y lanzarse
                if (willDash && aliveTimer >= dashTriggerTime) {
                    attackState = AttackState.PREPARING;
                    preparingTimer = PREPARING_TIME;

                    preparingBaseX = rect.x;
                    preparingBaseY = rect.y;
                    rotation = 0f;
                    if (attackSound != null) {
                        audio.playSound(attackSound, 1f);
                    }
                }
                break;

            case PREPARING:
                preparingTimer -= delta;

                // Temblor visual en el sitio antes de lanzarse
                float shakeX = MathUtils.sin(preparingTimer * 80f) * 0.05f;
                float shakeY = MathUtils.cos(preparingTimer * 95f) * 0.03f;

                rect.x = preparingBaseX + shakeX;
                rect.y = preparingBaseY + shakeY;

                if (preparingTimer <= 0f) {
                    // Antes de calcular la dirección, devolvemos el npc a su posición real base
                    rect.x = preparingBaseX;
                    rect.y = preparingBaseY;

                    launchTowardsPlayer(player);
                    attackState = AttackState.DASHING;
                }
                break;

            case DASHING:
                rect.x += dashVelX * delta;
                rect.y += dashVelY * delta;
                break;
        }
    }

    private void launchTowardsPlayer(PlayerBladecar player) {
        Vector2 npcCenter = new Vector2(
            rect.x + rect.width / 2f,
            rect.y + rect.height / 2f
        );

        Vector2 playerCenter = new Vector2(
            player.getRect().x + player.getRect().width / 2f,
            player.getRect().y + player.getRect().height / 2f
        );

        Vector2 direction = playerCenter.cpy().sub(npcCenter).nor();

        dashVelX = direction.x * dashSpeed;
        dashVelY = direction.y * dashSpeed;

        float angle = MathUtils.atan2(direction.y, direction.x) * MathUtils.radiansToDegrees;

        // Como el sprite está dibujado "mirando hacia arriba",
        // ajustamos el ángulo para que su parte superior apunte al player.
        rotation = angle - 90f;
    }

    public void draw(SpriteBatch batch) {

        // Si está parpadeando y toca fase invisible, no se dibuja
        if (dying && !visibleDuringBlink) {
            return;
        }

        if (dying) {
            batch.draw(hitFrame, rect.x, rect.y, rect.width, rect.height);
        } else if (attackState == AttackState.PREPARING) {

            // Mientras vibra, no rota todavía
            batch.draw(frame1Region, rect.x, rect.y, rect.width, rect.height);

        } else if (attackState == AttackState.DASHING) {

            float originX = rect.width / 2f;
            float originY = rect.height / 2f;

            batch.draw(
                frame1Region,
                rect.x,
                rect.y,
                originX,
                originY,
                rect.width,
                rect.height,
                1f,
                1f,
                rotation
            );

        } else {
            TextureRegion currentFrame = animation.getKeyFrame(stateTime);
            batch.draw(currentFrame, rect.x, rect.y, rect.width, rect.height);
        }
    }

    public Rectangle getRect() {
        return rect;
    }

    public int getPoints() {
        return points;
    }

    public float getX() {
        return rect.x;
    }

    public float getY() {
        return rect.y;
    }

    public boolean isDying() {
        return dying;
    }

    public boolean isReadyToRespawn() {
        return readyToRespawn;
    }

    public void clearRespawnFlag() {
        readyToRespawn = false;
    }

    // Las texturas las libera ResourceManager, no esta clase
    public void dispose() {
    }

    // Verificar si se salió de la pantalla
    public boolean isOutOfWorld(float worldWidth) {
        return !dying && (
            (speedX > 0 && rect.x > worldWidth) ||
                (speedX < 0 && rect.x + rect.width < 0)
        );
    }

    // Sobrecarga útil para la embestida diagonal
    public boolean isOutOfWorld(float worldWidth, float worldHeight) {
        return !dying && (
            rect.x > worldWidth ||
                rect.x + rect.width < 0 ||
                rect.y > worldHeight ||
                rect.y + rect.height < 0
        );
    }
}
