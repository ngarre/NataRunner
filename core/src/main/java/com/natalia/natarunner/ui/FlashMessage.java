package com.natalia.natarunner.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;

public class FlashMessage {

    private boolean visible = false;
    private float timer = 0f;
    private float duration;

    private Color color = Color.WHITE;
    private String text = "";

    public FlashMessage(float duration) {
        this.duration = duration;
    }

    // Activar flash
    public void show(Color color, String text) {
        this.color = new Color(color); // copia defensiva
        this.text = text;
        this.timer = duration;
        this.visible = true;
    }

    // Actualizar estado
    public void update(float delta) {
        if (!visible) return;

        timer -= delta;
        if (timer <= 0f) {
            visible = false;
        }
    }

    // Dibujar
    public void draw(SpriteBatch batch,
                     Texture background,
                     BitmapFont font,
                     float screenWidth,
                     float flashHeight) {

        if (!visible) return;

        batch.setColor(color.r, color.g, color.b, 0.4f);
        batch.draw(background, 0, 0, screenWidth, flashHeight);
        batch.setColor(1f, 1f, 1f, 1f);

        font.draw(batch, text, 10, flashHeight - 10);
    }

    public boolean isVisible() {
        return visible;
    }
}
