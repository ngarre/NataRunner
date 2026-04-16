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

public class FightScreen implements Screen {

    private final Game game;

    private SpriteBatch spriteBatch;
    private FitViewport viewport;
    private BitmapFont font;
    private BitmapFont smallFont;

    public FightScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        if (spriteBatch == null) {
            spriteBatch = new SpriteBatch();
            viewport = new FitViewport(1280, 720);

            font = new BitmapFont();
            font.getData().setScale(2f);
            font.setColor(Color.WHITE);

            smallFont = new BitmapFont();
            smallFont.getData().setScale(1.2f);
            smallFont.setColor(Color.WHITE);
        }
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
        }

        ScreenUtils.clear(0.10f, 0.04f, 0.04f, 1f);

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        font.draw(spriteBatch, "LEVEL 2: FIGHT DISTRICT", 320, 620);
        smallFont.draw(spriteBatch, "Pantalla provisional del segundo nivel.", 350, 480);
        smallFont.draw(spriteBatch, "Puntuacion nivel 1: " + ((NataRunner) game).session.getTearsScore(), 350, 400);
        smallFont.draw(spriteBatch, "Aqui construiremos el combate del proyecto final.", 270, 320);
        smallFont.draw(spriteBatch, "ESC = volver al menu", 430, 220);
        spriteBatch.end();
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
        if (smallFont != null) smallFont.dispose();
    }
}
