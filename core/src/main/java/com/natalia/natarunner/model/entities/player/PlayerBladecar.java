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

    // Penalización de puntos cuando el jugador recibe daño en el nivel 2.
    public static final int PENALTY = 25;

    // Rectángulo que representa la posición, tamaño e hitbox del jugador.
    // En esta clase no se usa Sprite, sino Rectangle + TextureRegion.
    // Esto es porque el sprite de Bladecar es más complejo (animación + disparo) y es más fácil manejarlo con
    // Rectangle para la lógica de colisiones y posición, y TextureRegion para el dibujo.
    private Rectangle rect;

    // Sonidos asociados al jugador.
    private Sound giroSound;
    private Sound aterrizajeSound;
    private Sound reciboImpacto;
    private Sound shootSound;

    // Velocidad base del jugador.
    private float speed = 10f;
    // Indica si el jugador ya ha terminado su entrada inicial en pantalla.
    private boolean arrived = false;

    // Invulnerabilidad temporal, usada tras chocar con el boss.
    private boolean invulnerable = false;
    private float invulnerableTimer = 0f;
    private final float INVULNERABLE_TIME = 0.8f;

    // Efecto visual al recibir impacto.
    private boolean hitFlash = false;
    private float hitFlashTimer = 0f;
    private float hitFlashDuration = 0.12f;

    // Evita que el sonido de impacto se repita muchas veces durante el mismo flash.
    private boolean impactSoundPlayed = false;

    // Manager de audio para reproducir sonidos respetando la configuración del juego.
    private AudioManager audio;

    // Dirección horizontal del jugador.
    // true = mira a la izquierda, false = mira a la derecha.
    private boolean facingLeft = true;

    // =========================
    // ANIMACIÓN NORMAL
    // =========================

    // Animación normal del jugador, formada por varios frames.
    private Animation<TextureRegion> animation;

    // Tiempo acumulado para avanzar la animación.
    private float animationTime = 0f;

    // Duración de cada frame de la animación.
    private float frameDuration = 0.5f;

    // Frame fijo usado antes de que el jugador haya aterrizado.
    private TextureRegion idleFrame;

    // =========================
    // TEXTURA DE DISPARO
    // =========================

    // Imagen temporal que se muestra cuando el jugador dispara.
    private TextureRegion shootFrame;

    // Indica si ahora mismo se está mostrando la textura de disparo.
    private boolean shooting = false;

    // Temporizador que controla cuánto dura visualmente el disparo.
    private float shootTimer = 0f;

    // Duración de la textura de disparo.
    private float shootDuration = 0.10f; // configurable


    public PlayerBladecar(float viewportWidth, float viewportHeight, float desiredHeight,
                          AudioManager audio, ResourceManager resources) {

        // Guarda el AudioManager recibido para reproducir sonidos desde esta clase.
        this.audio = audio;

        // Asigna sonidos desde ResourceManager.
        this.giroSound = resources.giroSound;
        this.aterrizajeSound = resources.aterrizajeSound;
        this.reciboImpacto = resources.reciboImpacto;
        this.shootSound = resources.shootSound;

        // Crea los frames de animación del jugador a partir de las texturas cargadas.
        TextureRegion frame1 = new TextureRegion(resources.cocheBladecar1);
        TextureRegion frame2 = new TextureRegion(resources.cocheBladecar2);
        TextureRegion frame3 = new TextureRegion(resources.cocheBladecar3);
        // Textura especial que se mostrará brevemente al disparar.
        this.shootFrame = new TextureRegion(resources.cocheBladecarDispara);

        // Lista de frames para la animación normal.
        Array<TextureRegion> frames = new Array<>();
        frames.add(frame1);
        frames.add(frame2);
        frames.add(frame3);

        // Crea una animación en bucle con los frames anteriores.
        animation = new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
        // Frame inicial usado mientras el jugador todavía no ha aterrizado.
        idleFrame = frame1;

        // Calcula el tamaño del jugador manteniendo la proporción de la imagen original.
        int texWidthPx = resources.cocheBladecar1.getWidth();
        int texHeightPx = resources.cocheBladecar1.getHeight();

        float aspectRatio = (float) texWidthPx / texHeightPx;

        float height = desiredHeight;
        float width = desiredHeight * aspectRatio;

        // Coloca al jugador centrado en X.
        float x = (viewportWidth - width) / 2f;

        // Lo coloca por encima de la pantalla para que haga una entrada descendente.
        float y = viewportHeight + height;

        // Crea el rectángulo del jugador con posición y tamaño.
        rect = new Rectangle(x, y, width, height);
    }

    public void update(float delta) {

        // Entrada inicial del jugador:
        // mientras no ha llegado al suelo, baja desde fuera de la pantalla.
        if (!arrived) {
            float groundY = 0f;
            rect.y -= speed * 0.5f * delta;

            // Cuando llega al suelo, se fija en Y = 0,
            // se marca como "arrived" y reproduce sonido de aterrizaje.
            if (rect.y <= groundY) {
                rect.y = groundY;
                arrived = true;
                audio.playSound(aterrizajeSound);
            }
        }

        // La animación normal solo avanza cuando el jugador ya ha llegado.
        if (arrived) {
            animationTime += delta;
        }

        // Controla la duración de la textura especial de disparo.
        if (shooting) {
            shootTimer -= delta;
            if (shootTimer <= 0f) {
                shooting = false;
                shootTimer = 0f;
            }
        }

        // Controla el efecto de impacto.
        if (hitFlash) {
            // Reproduce el sonido de impacto una sola vez por golpe.
            if (!impactSoundPlayed) {
                audio.playSound(reciboImpacto);
                impactSoundPlayed = true;
            }

            // Reduce el tiempo restante del flash.
            hitFlashTimer -= delta;
            if (hitFlashTimer <= 0f) {
                hitFlash = false;
            }
        }

        // Controla la invulnerabilidad temporal.
        if (invulnerable) {
            invulnerableTimer -= delta;

            // Cuando se acaba el tiempo, el jugador vuelve a poder recibir daño normal.
            if (invulnerableTimer <= 0f) {
                invulnerable = false;
                invulnerableTimer = 0f;
            }
        }
    }

    public void handleMouseDrag(Vector2 mouseWorld) {
        // Coloca el jugador centrado en la posición del ratón.
        rect.setPosition(
            mouseWorld.x - rect.width / 2f,
            mouseWorld.y - rect.height / 2f
        );
    }

    public void move(float delta, float worldWidth, float worldHeight, float hudHeight) {

        // Velocidad de movimiento durante la partida.
        float moveSpeed = 10f;

        // Comprueba si está pulsado CTRL izquierdo o derecho.
        boolean ctrlPressed = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
            Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);

        // Tecla G: gira el jugador.
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            rotate();
        }

        // Si CTRL está pulsado, izquierda/derecha no mueven:
        // sirven para girar el coche/nave.
        if (ctrlPressed) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) ||
                Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                rotate();
            }

        // Si CTRL no está pulsado, las flechas mueven al jugador.
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

        // Límites horizontales: evita que el jugador salga por izquierda o derecha.
        if (rect.x < 0) rect.x = 0;
        if (rect.x + rect.width > worldWidth) rect.x = worldWidth - rect.width;

        // Límites verticales: evita que salga por abajo o invada el HUD.
        float minY = 0f;
        float maxY = worldHeight - hudHeight - rect.height;
        if (rect.y < minY) rect.y = minY;
        if (rect.y > maxY) rect.y = maxY;
    }

    public void rotate() {
        // Cambia la dirección horizontal y reproduce sonido de giro.
        flip();
        audio.playSound(giroSound);
    }

    public void flip() {
        // Invierte la dirección del jugador.
        facingLeft = !facingLeft;
    }

    public boolean isFacingLeft() {
        // Devuelve hacia dónde está mirando el jugador.
        return facingLeft;
    }

    public void hit() {
        // Activa el efecto visual y sonoro de impacto.
        hitFlash = true;
        hitFlashTimer = hitFlashDuration;
        impactSoundPlayed = false;
    }

    private void startShootEffect() {
        // Activa la textura temporal de disparo.
        shooting = true;
        shootTimer = shootDuration;
    }

    public void draw(SpriteBatch batch) {
        // Si el jugador está en efecto de impacto, cambia temporalmente la transparencia.
        // Esto crea un pequeño parpadeo visual.
        if (hitFlash) {
            if ((int)(hitFlashTimer * 100) % 2 == 0) {
                batch.setColor(1f, 1f, 1f, 0.3f);
            }
        }

        TextureRegion currentFrame;

        // Si está disparando, se muestra la textura especial de disparo.
        if (shooting) {
            currentFrame = shootFrame;
        } else {
            // Si no está disparando, se muestra animación normal si ya aterrizó,
            // o frame fijo si todavía está entrando en pantalla.
            currentFrame = arrived ? animation.getKeyFrame(animationTime) : idleFrame;
        }

        // Si mira a la izquierda, se dibuja normal.
        if (facingLeft) {
            batch.draw(currentFrame, rect.x, rect.y, rect.width, rect.height);
            // Si mira a la derecha, se dibuja con ancho negativo para voltear la imagen.
        } else {
            batch.draw(
                currentFrame,
                rect.x + rect.width,
                rect.y,
                -rect.width,
                rect.height
            );
        }

        // Restaura el color normal del batch.  Después de aplicar el efecto de transparencia del jugador
        // restauro el color del SpriteBatch para que los siguientes elementos se dibujen con normalidad.
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public BulletChicas shoot(Texture bulletTexture) {

        // Obtiene el rectángulo actual del jugador.
        Rectangle r = getRect();

        // Tamaño de la bala.
        float bulletWidth = 0.3f;
        float bulletHeight = 0.15f;

        float bulletX;

        // Altura desde la que sale la bala, ajustada visualmente al sprite.
        float bulletY = r.y + r.height * 0.45f - bulletHeight / 2f;
        bulletY -= 0.10f;

        // Si no mira a la izquierda, entonces dispara hacia la derecha.
        boolean toRight = !isFacingLeft();

        // Posición inicial de la bala según la dirección del jugador.
        if (toRight) {
            bulletX = r.x + r.width - 0.40f;
        } else {
            bulletX = r.x - bulletWidth + 0.40f;
        }

        // Activa durante un instante la textura especial de disparo.
        startShootEffect();

        // Reproduce sonido de disparo.
        if (shootSound != null) {
            audio.playSound(shootSound);
        }

        // Crea y devuelve la bala del jugador.
        // Aunque la clase se llama BulletChicas, aquí representa los disparos del jugador.
        return new BulletChicas(bulletTexture, bulletX, bulletY, bulletWidth, bulletHeight, toRight);
    }

    public Rectangle getRect() {
        // Devuelve posición, tamaño e hitbox del jugador.
        return rect;
    }

    public boolean hasArrived() {
        // Indica si el jugador ya terminó su entrada inicial.
        return arrived;
    }



    public void setShootDuration(float shootDuration) {
        // Permite modificar desde fuera cuánto dura visualmente el disparo.
        this.shootDuration = shootDuration;
    }


    public void startInvulnerability() {
        // Activa la invulnerabilidad temporal.
        invulnerable = true;
        invulnerableTimer = INVULNERABLE_TIME;
    }

    public boolean isInvulnerable() {
        // Indica si el jugador está actualmente protegido.
        return invulnerable;
    }

    public void knockBackFrom(Rectangle sourceRect, float distance,
                              float worldWidth, float worldHeight, float hudHeight) {

        // Calcula el centro del jugador.
        float playerCenterX = rect.x + rect.width / 2f;
        float playerCenterY = rect.y + rect.height / 2f;

        // Calcula el centro del objeto que ha provocado el empujón, por ejemplo el boss.
        float sourceCenterX = sourceRect.x + sourceRect.width / 2f;
        float sourceCenterY = sourceRect.y + sourceRect.height / 2f;

        // Vector desde el objeto fuente hacia el jugador.
        float dx = playerCenterX - sourceCenterX;
        float dy = playerCenterY - sourceCenterY;

        // Longitud del vector.
        float len = (float) Math.sqrt(dx * dx + dy * dy);

        // Evita división entre cero si ambos centros coinciden exactamente.
        if (len == 0f) {
            dx = 1f;
            dy = 0f;
            len = 1f;
        }

        // Normaliza el vector para quedarse solo con la dirección.
        dx /= len;
        dy /= len;

        // Aplica el rebote alejando al jugador del objeto fuente (el Boss).
        // Se usa más fuerza en X que en Y para que el empujón sea más visible horizontalmente.
        rect.x += dx * distance * 1.5f;
        rect.y += dy * distance * 0.8f;

        // Vuelve a limitar la posición para que el rebote no saque al jugador del mundo.
        if (rect.x < 0f) rect.x = 0f;
        if (rect.x + rect.width > worldWidth) rect.x = worldWidth - rect.width;

        float minY = 0f;
        float maxY = worldHeight - hudHeight - rect.height;

        if (rect.y < minY) rect.y = minY;
        if (rect.y > maxY) rect.y = maxY;
    }

    public void dispose() {
        // Las texturas y sonidos son compartidos y se liberan desde ResourceManager.
    }
}
