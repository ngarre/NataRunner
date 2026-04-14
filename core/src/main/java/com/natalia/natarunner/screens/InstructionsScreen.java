package com.natalia.natarunner.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class InstructionsScreen implements Screen {

    private final Game game;
    private final Screen previousScreen;

    private SpriteBatch spriteBatch;
    private FitViewport viewport;
    private BitmapFont font;

    public InstructionsScreen(Game game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;
    }

    @Override
    public void show() {
        if (spriteBatch == null) {
            spriteBatch = new SpriteBatch();
            viewport = new FitViewport(1280, 720);
            font = new BitmapFont();
            font.getData().setScale(1.6f);
            font.setColor(Color.WHITE);
        }
    }

    @Override
    public void render(float delta) {
        input();

        ScreenUtils.clear(0.08f, 0.06f, 0.10f, 1f);

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        font.draw(spriteBatch, "INSTRUCCIONES", 430, 620);
        font.draw(spriteBatch, "Este es un prototipo inicial del juego.", 260, 500);
        font.draw(spriteBatch, "Desde aqui luego mostraremos las reglas reales", 180, 430);
        font.draw(spriteBatch, "y el objetivo de cada nivel.", 350, 360);
        font.draw(spriteBatch, "Pulsa ENTER o ESC para volver al menu.", 220, 240);
        spriteBatch.end();
    }

    private void input() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(previousScreen);
        }
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (spriteBatch != null) spriteBatch.dispose();
        if (font != null) font.dispose();
    }
}
