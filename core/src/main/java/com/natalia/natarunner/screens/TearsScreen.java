package com.natalia.natarunner.screens;

import com.natalia.natarunner.NataRunner;
import com.natalia.natarunner.config.GameConfig;
import com.natalia.natarunner.manager.AudioManager;
import com.natalia.natarunner.manager.GameSettings;
import com.natalia.natarunner.manager.ResourceManager;
import com.natalia.natarunner.model.entities.drops.RedDrop;
import com.natalia.natarunner.model.entities.drops.WhiteDrop;
import com.natalia.natarunner.model.entities.drops.YellowDrop;
import com.natalia.natarunner.model.entities.player.PlayerTears;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class TearsScreen implements Screen {

    private final Game game;
    private final AudioManager audio;
    private final ResourceManager resources;
    private final GameSettings settings;

    private SpriteBatch spriteBatch;
    private FitViewport viewport;
    private FitViewport hudViewport;
    private BitmapFont font;
    private BitmapFont smallFont;

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

    private Music music;

    public TearsScreen(Game game, AudioManager audio, ResourceManager resources, GameSettings settings) {
        this.game = game;
        this.audio = audio;
        this.resources = resources;
        this.settings = settings;
    }


    @Override
    public void show() {
        if (spriteBatch == null) {
            spriteBatch = new SpriteBatch();
            viewport = new FitViewport(GameConfig.mundoAnchoTears, GameConfig.mundoAltoTears);
            hudViewport = new FitViewport(1228, 768);

            font = resources.hudFont;
            smallFont = resources.hudSmallFont;

            playerTears = new PlayerTears(resources.manosTexture, resources.manosCerradasTexture, 5.5f, 1.2f);
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

        scoreBasta = settings.isEasyMode()
            ? GameConfig.puntosSiFacil
            : GameConfig.puntosSiDificil;

        gotasBlancas.clear();
        gotasAmarillas.clear();
        gotasRojas.clear();

        music = resources.tearsMusic;
        music.setLooping(true);
        audio.stopMusic();
        audio.playMusic(music);

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
            audio.stopMusic();
            game.setScreen(new MenuScreen(game, ((NataRunner) game).audioManager, ((NataRunner) game).resources, ((NataRunner) game).settings));
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
            audio.stopMusic();
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
                resources.gotaBlanca1,
                resources.gotaBlanca2,
                resources.gotaBlanca3,
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

            gotasAmarillas.add(new YellowDrop(resources.gotaAmarillaTexture, resources.gotaAmarillaSableadaTexture, x, y));
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

            gotasRojas.add(new RedDrop(resources.gotaRojaTexture, x, y));
            audio.playSound(resources.gotaRojaSound);
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
                audio.playSound(resources.gotaBlancaSound);
            }
        }
    }

    private void updateYellowDrops(float delta) {
        for (int i = gotasAmarillas.size - 1; i >= 0; i--) {
            YellowDrop gota = gotasAmarillas.get(i);
            gota.update(delta);

            if (gota.consumeTransformEvent()) {
                audio.playSound(resources.sonidoSable);
            }

            if (gota.isOutOfScreen()) {
                gotasAmarillas.removeIndex(i);
                score -= YellowDrop.PENALTY;
                if (score < 0) score = 0;
                audio.playSound(resources.gotaAmarillaFallSound);
            } else if (playerTears.getBounds().overlaps(gota.getBounds())) {
                gotasAmarillas.removeIndex(i);

                if (gota.isLethal()) {
                    hearts--;
                    if (hearts < 0) hearts = 0;
                } else {
                    score += YellowDrop.POINTS;
                    audio.playSound(resources.gotaAmarillaSound);
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
                audio.playSound(resources.sonidoMortal);
            }
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        spriteBatch.draw(resources.fondoTears, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

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
        spriteBatch.draw(resources.hudBackground, 0, screenHeight - barHeight, screenWidth, barHeight);
        spriteBatch.setColor(1f, 1f, 1f, 1f);

        font.draw(spriteBatch, "LEVEL 1: TEARS DISTRICT", 390, 730);
        smallFont.draw(spriteBatch, "SCORE: " + score, 20, 745);
        smallFont.draw(spriteBatch, "META: " + scoreBasta, 180, 745);
        smallFont.draw(spriteBatch, "HEARTS: " + hearts, 340, 745);
        smallFont.draw(spriteBatch, "Jugador: " + ((NataRunner) game).settings.getPlayerName(), 500, 745);
        smallFont.draw(spriteBatch, "ESC = volver al menu", 930, 745);

        spriteBatch.draw(resources.Level1Texture, 460, 210, 300, 300);

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
        audio.stopMusic();
    }

    @Override
    public void dispose() {
        if (spriteBatch != null) spriteBatch.dispose();
    }
}
