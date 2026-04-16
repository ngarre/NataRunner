package com.natalia.natarunner.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.natalia.natarunner.NataRunner;
import com.natalia.natarunner.config.GameConfig;
import com.natalia.natarunner.model.entities.player.PlayerBladecar;

public class FightScreen implements Screen {

    private final Game game;

    private SpriteBatch spriteBatch;
    private FitViewport viewport;
    private FitViewport hudViewport;
    private BitmapFont font;
    private BitmapFont smallFont;

    private Texture backgroundTexture;
    private Texture levelTexture;
    private Texture coche1;
    private Texture coche2;
    private Texture coche3;
    private Texture hudBackground;

    private PlayerBladecar playerBladecar;

    private float hudHeight = 1f;
    private float barHeight = 60f;

    private int fightScore = 0;
    private int hearts = GameConfig.cuantosCorazones;

    public FightScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        if (spriteBatch == null) {
            spriteBatch = new SpriteBatch();
            viewport = new FitViewport(GameConfig.mundoAnchoFight, GameConfig.mundoAltoFight);
            hudViewport = new FitViewport(1280, 720);

            font = new BitmapFont();
            font.getData().setScale(1.5f);
            font.setColor(Color.WHITE);

            smallFont = new BitmapFont();
            smallFont.getData().setScale(1.1f);
            smallFont.setColor(Color.WHITE);

            backgroundTexture = new Texture("Fight/FondoFight.jpg");
            levelTexture = new Texture("Fight/Level2.png");

            coche1 = new Texture("Fight/Player/coche-1.png");
            coche2 = new Texture("Fight/Player/coche-2.png");
            coche3 = new Texture("Fight/Player/coche-3.png");

            hudBackground = new Texture("Fight/Player/coche-1.png");

            playerBladecar = new PlayerBladecar(coche1, coche2, coche3, 5.8f, 1.0f);
        }

        fightScore = 0;
        hearts = GameConfig.cuantosCorazones;

        ((NataRunner) game).session.setFightScore(fightScore);
    }

    @Override
    public void render(float delta) {
        input(delta);
        update(delta);
        draw();
    }

    private void input(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            return;
        }

        playerBladecar.handleKeyboard(delta);

        if (((NataRunner) game).settings.isMouseEnabled() && Gdx.input.isTouched()) {
            Vector2 mouseWorld = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            playerBladecar.handleMouseDrag(mouseWorld);
        }
    }

    private void update(float delta) {
        playerBladecar.update(delta, viewport.getWorldWidth(), viewport.getWorldHeight(), hudHeight);
        ((NataRunner) game).session.setFightScore(fightScore);
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        playerBladecar.draw(spriteBatch);
        spriteBatch.end();

        hudViewport.apply();
        spriteBatch.setProjectionMatrix(hudViewport.getCamera().combined);

        spriteBatch.begin();

        float screenWidth = hudViewport.getWorldWidth();
        float screenHeight = hudViewport.getWorldHeight();

        spriteBatch.setColor(0f, 0f, 0f, 0.5f);
        spriteBatch.draw(hudBackground, 0, screenHeight - barHeight, screenWidth, barHeight);
        spriteBatch.setColor(1f, 1f, 1f, 1f);

        font.draw(spriteBatch, "LEVEL 2: FIGHT DISTRICT", 400, 685);
        smallFont.draw(spriteBatch, "SCORE L1: " + ((NataRunner) game).session.getTearsScore(), 20, 700);
        smallFont.draw(spriteBatch, "SCORE L2: " + fightScore, 200, 700);
        smallFont.draw(spriteBatch, "HEARTS: " + hearts, 360, 700);
        smallFont.draw(spriteBatch, "Jugador: " + ((NataRunner) game).settings.getPlayerName(), 520, 700);
        smallFont.draw(spriteBatch, "ESC = volver al menu", 1020, 700);

        spriteBatch.draw(levelTexture, 480, 170, 320, 320);

        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
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
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (levelTexture != null) levelTexture.dispose();
        if (coche1 != null) coche1.dispose();
        if (coche2 != null) coche2.dispose();
        if (coche3 != null) coche3.dispose();
        if (hudBackground != null) hudBackground.dispose();
    }
}
