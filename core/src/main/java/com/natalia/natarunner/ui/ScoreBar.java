package com.natalia.natarunner.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Texture;

public class ScoreBar {

    private int scoreMax;

    private float width;
    private float height;
    private float marginRight;

    public ScoreBar(int scoreMax, float width, float height, float marginRight) {
        this.scoreMax = scoreMax;
        this.width = width;
        this.height = height;
        this.marginRight = marginRight;
    }

    private float getPercent(int score) {
        return MathUtils.clamp((float) score / (float) scoreMax, 0f, 1f);
    }

    public void draw(SpriteBatch batch,
                     Texture pixel,
                     int score,
                     float screenWidth,
                     float yBar) {

        float percent = getPercent(score);

        float xBar = screenWidth - width - marginRight;

        // fondo negro
        batch.setColor(0f,0f,0f,1f);
        batch.draw(pixel, xBar, yBar, width, height);

        // color según puntuación
        if (score <= 100) {
            batch.setColor(1f,0f,0f,1f);
        } else if (score <= 300) {
            batch.setColor(1f,1f,0f,1f);
        } else {
            batch.setColor(0f,1f,0f,1f);
        }

        // relleno
        batch.draw(pixel, xBar, yBar, width * percent, height);

        // marco blanco
        batch.setColor(1f,1f,1f,1f);

        batch.draw(pixel, xBar - 2, yBar - 2, width + 4, 2);
        batch.draw(pixel, xBar - 2, yBar + height, width + 4, 2);
        batch.draw(pixel, xBar - 2, yBar, 2, height);
        batch.draw(pixel, xBar + width, yBar, 2, height);
    }
}
