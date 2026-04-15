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
import com.natalia.natarunner.model.entities.player.PlayerTears;

public class TearsScreen implements Screen {

    private final Game game;

    private SpriteBatch spriteBatch;
    private FitViewport viewport;
    private FitViewport hudViewport;
    private BitmapFont font;
    private BitmapFont smallFont;

    private Texture backgroundTexture;
    private Texture levelTexture;
    private Texture manosTexture;
    private Texture manosCerradasTexture;
    private Texture hudBackground;

    private PlayerTears playerTears;

    private float hudHeight = 1f;
    private float barHeight = 60f;

    private int score = 100;
    private int hearts = GameConfig.cuantosCorazones;

    public TearsScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        if (spriteBatch == null) {
            spriteBatch = new SpriteBatch();
            viewport = new FitViewport(GameConfig.mundoAnchoTears, GameConfig.mundoAltoTears);
            hudViewport = new FitViewport(1228, 768);

            font = new BitmapFont();
            font.getData().setScale(1.5f);
            font.setColor(Color.WHITE);

            smallFont = new BitmapFont();
            smallFont.getData().setScale(1.1f);
            smallFont.setColor(Color.WHITE);

            backgroundTexture = new Texture("Tears/lagrimas_fondo.jpg");
            levelTexture = new Texture("Tears/Level1.png");
            manosTexture = new Texture("Tears/manos.png");
            manosCerradasTexture = new Texture("Tears/manoscerradas.png");

            hudBackground = new Texture("Tears/manoscerradas.png");

            playerTears = new PlayerTears(manosTexture, manosCerradasTexture, 5.5f, 1.2f);
        }

        ((NataRunner) game).session.reset();
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

        playerTears.handleKeyboard(delta);

        if (((NataRunner) game).settings.isMouseEnabled() && Gdx.input.isTouched()) {
            Vector2 mouseWorld = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            playerTears.handleMouseDrag(mouseWorld);
        }
    }

    private void update(float delta) {
        playerTears.update(delta, viewport.getWorldWidth(), viewport.getWorldHeight(), hudHeight);
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        playerTears.draw(spriteBatch);
        spriteBatch.end();

        hudViewport.apply();
        spriteBatch.setProjectionMatrix(hudViewport.getCamera().combined);

        spriteBatch.begin();

        float screenWidth = hudViewport.getWorldWidth();
        float screenHeight = hudViewport.getWorldHeight();

        spriteBatch.setColor(0f, 0f, 0f, 0.5f);
        spriteBatch.draw(hudBackground, 0, screenHeight - barHeight, screenWidth, barHeight);
        spriteBatch.setColor(1f, 1f, 1f, 1f);

        font.draw(spriteBatch, "LEVEL 1: TEARS DISTRICT", 390, 730);
        smallFont.draw(spriteBatch, "SCORE: " + score, 20, 745);
        smallFont.draw(spriteBatch, "HEARTS: " + hearts, 180, 745);
        smallFont.draw(spriteBatch, "Jugador: " + ((NataRunner) game).settings.getPlayerName(), 340, 745);
        smallFont.draw(spriteBatch, "ESC = volver al menu", 930, 745);

        spriteBatch.draw(levelTexture, 460, 210, 300, 300);

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
        if (manosTexture != null) manosTexture.dispose();
        if (manosCerradasTexture != null) manosCerradasTexture.dispose();
        if (hudBackground != null) hudBackground.dispose();
    }
}
