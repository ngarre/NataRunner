package com.natalia.natarunner.model.entities.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PlayerTears {

      /*
        Personaje principal del nivel TearsScreen.

        Este jugador se representa con un Sprite de manos.
        Tiene dos texturas: manos abiertas y manos cerradas.
        La animación se consigue alternando esas dos texturas cada cierto tiempo.

        La clase también gestiona:
        - movimiento con teclado
        - movimiento por arrastre con ratón
        - límites de movimiento dentro del mundo
        - hitbox para colisiones
        - dibujo del sprite
     */


    // Sprite visual del jugador. Guarda textura, posición, tamaño y permite dibujarlo.
    private Sprite sprite;

    // Rectángulo invisible usado como hitbox para detectar colisiones.
    private Rectangle bounds = new Rectangle();

    // Textura de las manos abiertas.
    private final Texture manosTexture;

    // Textura de las manos cerradas.
    private final Texture manosCerradasTexture;

    // Velocidad de movimiento del jugador en unidades del mundo por segundo.
    private float speed = 15f;

    // Temporizador usado para cambiar entre manos abiertas y cerradas.
    private float animationTimer = 0f;

    // Tiempo que dura cada frame de la animación.
    private final float frameDuration = 0.5f;

    // Frame actual de la animación.
    // 0 = manos abiertas, 1 = manos cerradas.
    private int currentFrame = 0;

    public PlayerTears(Texture manosTexture, Texture manosCerradasTexture, float x, float y) {
        // Guarda las dos texturas que se usarán para animar las manos.
        this.manosTexture = manosTexture;
        this.manosCerradasTexture = manosCerradasTexture;

        // Crea el sprite inicial usando la textura de manos abiertas.
        sprite = new Sprite(manosTexture);
        // Define el tamaño del personaje en coordenadas del mundo.
        sprite.setSize(1f, 1f);
        // Coloca al jugador en la posición inicial recibida por parámetro.
        sprite.setPosition(x, y);
    }

    // =========================
    // INPUT TECLADO
    // =========================
    public void handleKeyboard(float delta) {
        // Variables que indican la dirección de movimiento.
        float moveX = 0f;
        float moveY = 0f;

        // Si se pulsa derecha, el jugador se mueve en X positiva.
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) moveX += 1f;
        // Si se pulsa izquierda, el jugador se mueve en X negativa.
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  moveX -= 1f;
        // Si se pulsa arriba, el jugador se mueve en Y positiva.
        if (Gdx.input.isKeyPressed(Input.Keys.UP))    moveY += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  moveY -= 1f;
        // Si se pulsa abajo, el jugador se mueve en Y negativa.

        // Si se mueve en diagonal, se reduce la velocidad para que no vaya más rápido
        // que cuando se mueve solo horizontal o verticalmente.
        if (moveX != 0f && moveY != 0f) {
            float inv = 0.7071f;
            moveX *= inv;
            moveY *= inv;
        }

        // Calcula el desplazamiento real usando velocidad y delta.
        // delta hace que el movimiento dependa del tiempo real y no de los FPS.
        float dx = moveX * speed * delta;
        float dy = moveY * speed * delta;

        // Mueve el sprite del jugador.
        sprite.translate(dx, dy);
    }

    // =========================
    // INPUT RATÓN
    // =========================
    public void handleMouseDrag(Vector2 mouseWorld) {

        // Calcula la posición para que el centro del sprite coincida con el ratón.
        float targetX = mouseWorld.x - sprite.getWidth() / 2f;
        float targetY = mouseWorld.y - sprite.getHeight() / 2f;

        // Calcula la posición para que el centro del sprite coincida con el ratón.
        sprite.setPosition(targetX, targetY);
    }

    // =========================
    // UPDATE
    // =========================
    public void update(float delta, float worldWidth, float worldHeight, float hudHeight) {

        // La X del jugador no puede ser menor que 0 ni mayor que el ancho del mundo menos el ancho del sprite.
        float clampedX = MathUtils.clamp(
            sprite.getX(),
            0,
            worldWidth - sprite.getWidth()
        );

        // La Y del jugador no puede bajar de 0 ni subir hasta invadir la zona del HUD.
        float clampedY = MathUtils.clamp(
            sprite.getY(),
            0,
            worldHeight - hudHeight - sprite.getHeight()
        );

        // Aplica la posición limitada al sprite.
        sprite.setPosition(clampedX, clampedY);

        // Actualiza la animación de manos abiertas/cerradas.
        updateAnimation(delta);

        // Actualiza la hitbox para que coincida con la nueva posición del sprite.
        updateBounds();
    }

    private void updateAnimation(float delta) {
        // Suma el tiempo transcurrido desde el último frame.
        // Cuando el temporizador llega a 0,5 segundos, se cambia de imagen
        animationTimer += delta;

        // Si ya ha pasado el tiempo que debe durar una imagen,
        // cambiamos a la otra textura de las manos.
        if (animationTimer >= frameDuration) {
            // Restamos la duración del frame para empezar a contar el siguiente cambio.
            animationTimer -= frameDuration;

            // Alterna entre 0 y 1.
            // 0 = manos abiertas, 1 = manos cerradas.
            // % es el operador módulo, que devuelve el resto de la división.
            // Al sumar 1 y luego hacer módulo 2, se alterna entre 0 y 1.
            currentFrame = (currentFrame + 1) % 2;
        }

        // Si el frame actual es 0, se muestran las manos abiertas.
        if (currentFrame == 0) {
            sprite.setTexture(manosTexture);

        // Si el frame actual es 1, se muestran las manos cerradas.
        } else {
            sprite.setTexture(manosCerradasTexture);
        }
    }

    public float getCenterX() {
        // Devuelve el centro horizontal del jugador.
        // Se usa, por ejemplo, para que la gota roja persiga al jugador.
        return sprite.getX() + sprite.getWidth() / 2f;
    }

    public void updateBounds() {
        // Actualiza la hitbox usando la posición y tamaño actuales del sprite.
        bounds.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }

    public Rectangle getBounds() {
        // Devuelve la hitbox del jugador para comprobar colisiones.
        return bounds;
    }

    public Sprite getSprite() {
        // Devuelve el sprite por si otra clase necesita consultar su posición o tamaño.
        return sprite;
    }

    public void draw(SpriteBatch batch) {
        // Dibuja el sprite usando el SpriteBatch recibido.
        sprite.draw(batch);
    }
}
