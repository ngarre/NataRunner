package com.natalia.natarunner.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

// Esta clase la defino para los textos flotantes. Por ejemplo para
// que aparezca la puntuación cuando se atrapa un objeto, una gota...
public class FloatingText {

    public String text;
    public Vector2 pos = new Vector2();
    public float timeLeft;
    public float alpha = 1f;
    public Color color;

    public FloatingText(String text, float x, float y, float duration, Color color) {
        this.text = text;
        this.pos.set(x, y);
        this.timeLeft = duration;
        this.color = color;
    }
}
