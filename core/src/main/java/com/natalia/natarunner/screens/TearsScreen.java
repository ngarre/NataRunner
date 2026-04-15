package com.natalia.natarunner.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.natalia.natarunner.NataRunner;
import com.natalia.natarunner.config.GameConfig;
import com.natalia.natarunner.model.entities.drops.WhiteDrop;
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

    private Texture gotaBlanca1;
    private Texture gotaBlanca2;
    private Texture gotaBlanca3;

    private PlayerTears playerTears;
    private Array<WhiteDrop> gotasBlancas;

    private float hudHeight = 1f;
    private float barHeight = 60f;
    private float gotaBlancaTimer = 0f;

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

            gotaBlanca1 = new Texture("Tears/Gotas/gota_blanca_1.png");
            gotaBlanca2 = new Texture("Tears/Gotas/gota_blanca_2.png");
            gotaBlanca3 = new Texture("Tears/Gotas/gota_blanca_3.png");

            hudBackground = new Texture("Tears/manoscerradas.png");

            playerTears = new PlayerTears(manosTexture, manosCerradasTexture, 5.5f, 1.2f);
            gotasBlancas = new Array<>();
        }

        score = 100;
        hearts = GameConfig.cuantosCorazones;
        gotaBlancaTimer = 0f;
        gotasBlancas.clear();

        ((NataRunner) game).session.reset();
        ((NataRunner) game).session.setTearsScore(score);
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

        spawnWhiteDrops(delta);
        updateWhiteDrops(delta);

        ((NataRunner) game).session.setTearsScore(score);
    }

    private void spawnWhiteDrops(float delta) {
        gotaBlancaTimer += delta;

        if (gotaBlancaTimer > GameConfig.tiempoCadaCuantoGotaBlanca) {
            gotaBlancaTimer = 0f;

            float x = MathUtils.random(0f, viewport.getWorldWidth() - 0.5f);
            float y = viewport.getWorldHeight() - hudHeight;

            gotasBlancas.add(new WhiteDrop(
                gotaBlanca1,
                gotaBlanca2,
                gotaBlanca3,
                x,
                y
            ));
        }
    }

    private void updateWhiteDrops(float delta) {
        for (int i = gotasBlancas.size - 1; i >= 0; i--) {
            WhiteDrop gota = gotasBlancas.get(i);
            gota.update(delta);

            if (gota.isOutOfScreen()) {
                gotasBlancas.removeIndex(i);
            } else if (playerTears.getBounds().overlaps(gota.getBounds())) {
                gotasBlancas.removeIndex(i);
                score += WhiteDrop.POINTS;
            }
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        for (WhiteDrop gota : gotasBlancas) {
            gota.draw(spriteBatch);
        }

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
        if (gotaBlanca1 != null) gotaBlanca1.dispose();
        if (gotaBlanca2 != null) gotaBlanca2.dispose();
        if (gotaBlanca3 != null) gotaBlanca3.dispose();
        if (hudBackground != null) hudBackground.dispose();
    }
}
