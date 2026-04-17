package com.natalia.natarunner.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;

public class CountdownTimer {

    private float remainingTime;    // en segundos
    private BitmapFont font;
    private boolean finished = false;

    // Constructor
    public CountdownTimer(float startTimeSeconds, BitmapFont font) {
        this.remainingTime = startTimeSeconds;
        this.font = font;
    }

    // Actualiza el timer
    public void update(float delta) {
        if (finished) return;

        remainingTime -= delta;

        if (remainingTime <= 0f) {
            remainingTime = 0f;
            finished = true;
        }
    }

    // Dibuja el timer en pantalla
    public void draw(SpriteBatch batch, float x, float y) {
        String text = "Remaining Time: " + (int) remainingTime;

        font.getData().setScale(0.8f);       // tamaño más pequeño
        font.setColor(Color.ORANGE);            // color rojo
        float offsetX = -50f;                // un poco a la izquierda
        font.draw(batch, text, x + offsetX, y);

        // Vuelvo a reponer estas propiedades para que Score se dibuje con su tamaño y color
        font.setColor(Color.WHITE);
        font.getData().setScale(1f);


    }

    // Saber si el tiempo terminó
    public boolean isFinished() {
        return finished;
    }

    // Obtener tiempo restante (por si quieres mostrarlo en otra parte)
    public float getRemainingTime() {
        return remainingTime;
    }

    public int getRemainingSeconds() {
        return Math.max(0, (int) Math.ceil(remainingTime));
    }

    // Resetear timer si hace falta
    public void reset(float startTimeSeconds) {
        this.remainingTime = startTimeSeconds;
        this.finished = false;
    }
}
