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
import com.natalia.natarunner.model.entities.drops.RedDrop;
import com.natalia.natarunner.model.entities.drops.WhiteDrop;
import com.natalia.natarunner.model.entities.drops.YellowDrop;
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
    private Texture gotaAmarilla;
    private Texture gotaAmarillaSableada;
    private Texture gotaRoja;

    private PlayerTears playerTears;
    private Array<WhiteDrop> gotasBlancas;
    private Array<YellowDrop> gotasAmarillas;
    private Array<RedDrop> gotasRojas;

    private float hudHeight = 1f;
    private float barHeight = 60f;
    private float gotaBlancaTimer = 0f;
    private float gotaAmarillaTimer = 0f;
    private float gotaRojaTimer = 0f;
    private float tiempoTotal = 0f;

    private int score = 100;
    private int hearts = GameConfig.cuantosCorazones;
    private int scoreBasta;

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
            gotaAmarilla = new Texture("Tears/Gotas/gota_amarilla.png");
            gotaAmarillaSableada = new Texture("Tears/Gotas/Sableada.png");
            gotaRoja = new Texture("Tears/Gotas/gota_roja.png");

            hudBackground = new Texture("Tears/manoscerradas.png");

            playerTears = new PlayerTears(manosTexture, manosCerradasTexture, 5.5f, 1.2f);
            gotasBlancas = new Array<>();
            gotasAmarillas = new Array<>();
            gotasRojas = new Array<>();
        }

        score = 100;
        hearts = GameConfig.cuantosCorazones;
        gotaBlancaTimer = 0f;
        gotaAmarillaTimer = 0f;
        gotaRojaTimer = 0f;
        tiempoTotal = 0f;

        scoreBasta = ((NataRunner) game).settings.isEasyMode()
            ? GameConfig.puntosSiFacil
            : GameConfig.puntosSiDificil;

        gotasBlancas.clear();
        gotasAmarillas.clear();
        gotasRojas.clear();

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
        tiempoTotal += delta;

        playerTears.update(delta, viewport.getWorldWidth(), viewport.getWorldHeight(), hudHeight);

        spawnWhiteDrops(delta);
        spawnYellowDrops(delta);
        spawnRedDrops(delta);

        updateWhiteDrops(delta);
        updateYellowDrops(delta);
        updateRedDrops(delta);

        checkLevelEnd();

        ((NataRunner) game).session.setTearsScore(score);
    }

    private void checkLevelEnd() {
        if (score >= scoreBasta) {
            ((NataRunner) game).session.setTearsScore(score);
            game.setScreen(new FightScreen(game));
        }
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

    private void spawnYellowDrops(float delta) {
        if (tiempoTotal <= GameConfig.tiempoPrimeraGotaAmarilla) {
            return;
        }

        gotaAmarillaTimer += delta;

        if (gotaAmarillaTimer > GameConfig.tiempoCadaCuantoAmarilla) {
            gotaAmarillaTimer = 0f;

            float x = MathUtils.random(0f, viewport.getWorldWidth() - 0.5f);
            float y = viewport.getWorldHeight() - hudHeight;

            gotasAmarillas.add(new YellowDrop(gotaAmarilla, gotaAmarillaSableada, x, y));
        }
    }

    private void spawnRedDrops(float delta) {
        if (tiempoTotal <= GameConfig.tiempoPrimeraGotaRoja) {
            return;
        }

        gotaRojaTimer += delta;

        if (gotaRojaTimer > GameConfig.tiempoCadaCuantoRoja) {
            gotaRojaTimer = 0f;

            float x = MathUtils.random(0f, viewport.getWorldWidth() - 0.8f);
            float y = viewport.getWorldHeight() - hudHeight;

            gotasRojas.add(new RedDrop(gotaRoja, x, y));
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

    private void updateYellowDrops(float delta) {
        for (int i = gotasAmarillas.size - 1; i >= 0; i--) {
            YellowDrop gota = gotasAmarillas.get(i);
            gota.update(delta);

            if (gota.consumeTransformEvent()) {
                // Más adelante aquí conectaremos sonido de transformación.
            }

            if (gota.isOutOfScreen()) {
                gotasAmarillas.removeIndex(i);
                score -= YellowDrop.PENALTY;
                if (score < 0) score = 0;
            } else if (playerTears.getBounds().overlaps(gota.getBounds())) {
                gotasAmarillas.removeIndex(i);

                if (gota.isLethal()) {
                    hearts--;
                    if (hearts < 0) hearts = 0;
                } else {
                    score += YellowDrop.POINTS;
                }
            }
        }
    }

    private void updateRedDrops(float delta) {
        for (int i = gotasRojas.size - 1; i >= 0; i--) {
            RedDrop gota = gotasRojas.get(i);

            gota.update(delta);
            gota.chase(playerTears.getCenterX(), delta);

            if (gota.isOutOfScreen()) {
                gotasRojas.removeIndex(i);
            } else if (playerTears.getBounds().overlaps(gota.getReducedBounds())) {
                gotasRojas.removeIndex(i);
                hearts--;
                if (hearts < 0) hearts = 0;
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

        for (YellowDrop gota : gotasAmarillas) {
            gota.draw(spriteBatch);
        }

        for (RedDrop gota : gotasRojas) {
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
        smallFont.draw(spriteBatch, "META: " + scoreBasta, 180, 745);
        smallFont.draw(spriteBatch, "HEARTS: " + hearts, 340, 745);
        smallFont.draw(spriteBatch, "Jugador: " + ((NataRunner) game).settings.getPlayerName(), 500, 745);
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
        if (gotaAmarilla != null) gotaAmarilla.dispose();
        if (gotaAmarillaSableada != null) gotaAmarillaSableada.dispose();
        if (gotaRoja != null) gotaRoja.dispose();
        if (hudBackground != null) hudBackground.dispose();
    }
}
