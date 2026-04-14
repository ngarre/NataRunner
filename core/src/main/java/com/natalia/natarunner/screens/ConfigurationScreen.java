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
import com.natalia.natarunner.NataRunner;
import com.natalia.natarunner.manager.GameSettings;

public class ConfigurationScreen implements Screen {

    private final Game game;
    private final Screen previousScreen;

    private SpriteBatch spriteBatch;
    private FitViewport viewport;
    private BitmapFont font;

    public ConfigurationScreen(Game game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;
    }

    @Override
    public void show() {
        if (spriteBatch == null) {
            spriteBatch = new SpriteBatch();
            viewport = new FitViewport(1280, 720);
            font = new BitmapFont();
            font.getData().setScale(1.5f);
            font.setColor(Color.WHITE);
        }
    }

    @Override
    public void render(float delta) {
        input();

        GameSettings settings = ((NataRunner) game).settings;

        ScreenUtils.clear(0.07f, 0.07f, 0.10f, 1f);

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        font.draw(spriteBatch, "CONFIGURACION", 420, 620);

        font.draw(spriteBatch, "Pulsa M para cambiar control con raton", 180, 500);
        font.draw(spriteBatch, "Mouse control: " + (settings.isMouseEnabled() ? "ACTIVADO" : "DESACTIVADO"), 220, 440);

        font.draw(spriteBatch, "Pulsa F para cambiar modo facil", 220, 340);
        font.draw(spriteBatch, "Easy mode: " + (settings.isEasyMode() ? "ACTIVADO" : "DESACTIVADO"), 280, 280);

        font.draw(spriteBatch, "Pulsa ESC para volver", 330, 160);
        spriteBatch.end();
    }

    private void input() {
        GameSettings settings = ((NataRunner) game).settings;

        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            settings.setMouseEnabled(!settings.isMouseEnabled());
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            settings.setEasyMode(!settings.isEasyMode());
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
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
