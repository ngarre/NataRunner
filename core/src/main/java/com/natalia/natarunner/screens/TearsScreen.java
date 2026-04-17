package com.natalia.natarunner.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.natalia.natarunner.NataRunner;
import com.natalia.natarunner.config.GameConfig;
import com.natalia.natarunner.manager.AudioManager;
import com.natalia.natarunner.manager.GameSettings;
import com.natalia.natarunner.manager.ResourceManager;
import com.natalia.natarunner.manager.TearsRenderManager;
import com.natalia.natarunner.model.entities.drops.RedDrop;
import com.natalia.natarunner.model.entities.drops.WhiteDrop;
import com.natalia.natarunner.model.entities.drops.YellowDrop;
import com.natalia.natarunner.model.entities.player.PlayerTears;
import com.natalia.natarunner.screens.context.TearsGameContext;

public class TearsScreen implements Screen {

    private final Game game;
    private final AudioManager audio;
    private final ResourceManager resources;
    private final GameSettings settings;

    private SpriteBatch spriteBatch;
    private FitViewport viewport;
    private FitViewport hudViewport;

    private TearsGameContext context;
    private TearsRenderManager renderManager;

    private Music music;
    private final Vector2 mouseHud = new Vector2();

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
        }

        if (context == null) {
            context = new TearsGameContext();
        }

        if (renderManager == null) {
            renderManager = new TearsRenderManager(resources);
        }

        context.font = resources.hudFont;
        context.smallFont = resources.hudSmallFont;

        context.playerTears = new PlayerTears(resources.manosTexture, resources.manosCerradasTexture, 5.5f, 1.2f);

        context.score = 100;
        context.hearts = GameConfig.cuantosCorazones;
        context.gotaBlancaTimer = 0f;
        context.gotaAmarillaTimer = 0f;
        context.gotaRojaTimer = 0f;
        context.tiempoTotal = 0f;
        context.state = TearsGameContext.GameState.INTRO;

        context.scoreBasta = settings.isEasyMode()
            ? GameConfig.puntosSiFacil
            : GameConfig.puntosSiDificil;

        context.gotasBlancas.clear();
        context.gotasAmarillas.clear();
        context.gotasRojas.clear();

        music = resources.tearsMusic;
        music.setLooping(true);
        audio.stopMusic();
        audio.playMusic(music);

        renderManager.updateIntroLayout(context, hudViewport);

        ((NataRunner) game).session.reset();
        ((NataRunner) game).session.setTearsScore(context.score);
    }

    @Override
    public void render(float delta) {
        input(delta);

        if (context.state == TearsGameContext.GameState.PLAYING) {
            update(delta);
        }

        draw();
    }

    private void input(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            audio.stopMusic();
            game.setScreen(new MenuScreen(
                game,
                ((NataRunner) game).audioManager,
                ((NataRunner) game).resources,
                ((NataRunner) game).settings
            ));
            return;
        }

        if (context.state == TearsGameContext.GameState.INTRO) {
            boolean startGame = false;

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                startGame = true;
            }

            mouseHud.set(Gdx.input.getX(), Gdx.input.getY());
            hudViewport.unproject(mouseHud);

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)
                && context.level1Rectangulo.contains(mouseHud)) {
                startGame = true;
            }

            if (startGame) {
                context.state = TearsGameContext.GameState.PLAYING;
            }

            return;
        }

        context.playerTears.handleKeyboard(delta);

        if (settings.isMouseEnabled() && Gdx.input.isTouched()) {
            Vector2 mouseWorld = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            context.playerTears.handleMouseDrag(mouseWorld);
        }
    }

    private void update(float delta) {
        context.tiempoTotal += delta;

        context.playerTears.update(
            delta,
            viewport.getWorldWidth(),
            viewport.getWorldHeight(),
            context.hudHeight
        );

        spawnWhiteDrops(delta);
        spawnYellowDrops(delta);
        spawnRedDrops(delta);

        updateWhiteDrops(delta);
        updateYellowDrops(delta);
        updateRedDrops(delta);

        checkLevelEnd();

        ((NataRunner) game).session.setTearsScore(context.score);
    }

    private void checkLevelEnd() {
        if (context.score >= context.scoreBasta) {
            ((NataRunner) game).session.setTearsScore(context.score);
            audio.stopMusic();
            game.setScreen(new FightScreen(game));
        }
    }

    private void spawnWhiteDrops(float delta) {
        context.gotaBlancaTimer += delta;

        if (context.gotaBlancaTimer > GameConfig.tiempoCadaCuantoGotaBlanca) {
            context.gotaBlancaTimer = 0f;

            float x = MathUtils.random(0f, viewport.getWorldWidth() - 0.5f);
            float y = viewport.getWorldHeight() - context.hudHeight;

            context.gotasBlancas.add(new WhiteDrop(
                resources.gotaBlanca1,
                resources.gotaBlanca2,
                resources.gotaBlanca3,
                x,
                y
            ));
        }
    }

    private void spawnYellowDrops(float delta) {
        if (context.tiempoTotal <= GameConfig.tiempoPrimeraGotaAmarilla) {
            return;
        }

        context.gotaAmarillaTimer += delta;

        if (context.gotaAmarillaTimer > GameConfig.tiempoCadaCuantoAmarilla) {
            context.gotaAmarillaTimer = 0f;

            float x = MathUtils.random(0f, viewport.getWorldWidth() - 0.5f);
            float y = viewport.getWorldHeight() - context.hudHeight;

            context.gotasAmarillas.add(
                new YellowDrop(resources.gotaAmarillaTexture, resources.gotaAmarillaSableadaTexture, x, y)
            );
        }
    }

    private void spawnRedDrops(float delta) {
        if (context.tiempoTotal <= GameConfig.tiempoPrimeraGotaRoja) {
            return;
        }

        context.gotaRojaTimer += delta;

        if (context.gotaRojaTimer > GameConfig.tiempoCadaCuantoRoja) {
            context.gotaRojaTimer = 0f;

            float x = MathUtils.random(0f, viewport.getWorldWidth() - 0.8f);
            float y = viewport.getWorldHeight() - context.hudHeight;

            context.gotasRojas.add(new RedDrop(resources.gotaRojaTexture, x, y));
            audio.playSound(resources.gotaRojaSound);
        }
    }

    private void updateWhiteDrops(float delta) {
        for (int i = context.gotasBlancas.size - 1; i >= 0; i--) {
            WhiteDrop gota = context.gotasBlancas.get(i);
            gota.update(delta);

            if (gota.isOutOfScreen()) {
                context.gotasBlancas.removeIndex(i);
            } else if (context.playerTears.getBounds().overlaps(gota.getBounds())) {
                context.gotasBlancas.removeIndex(i);
                context.score += WhiteDrop.POINTS;
                audio.playSound(resources.gotaBlancaSound);
            }
        }
    }

    private void updateYellowDrops(float delta) {
        for (int i = context.gotasAmarillas.size - 1; i >= 0; i--) {
            YellowDrop gota = context.gotasAmarillas.get(i);
            gota.update(delta);

            if (gota.consumeTransformEvent()) {
                audio.playSound(resources.sonidoSable);
            }

            if (gota.isOutOfScreen()) {
                context.gotasAmarillas.removeIndex(i);
                context.score -= YellowDrop.PENALTY;
                if (context.score < 0) context.score = 0;
                audio.playSound(resources.gotaAmarillaFallSound);
            } else if (context.playerTears.getBounds().overlaps(gota.getBounds())) {
                context.gotasAmarillas.removeIndex(i);

                if (gota.isLethal()) {
                    context.hearts--;
                    if (context.hearts < 0) context.hearts = 0;
                } else {
                    context.score += YellowDrop.POINTS;
                    audio.playSound(resources.gotaAmarillaSound);
                }
            }
        }
    }

    private void updateRedDrops(float delta) {
        for (int i = context.gotasRojas.size - 1; i >= 0; i--) {
            RedDrop gota = context.gotasRojas.get(i);

            gota.update(delta);
            gota.chase(context.playerTears.getCenterX(), delta);

            if (gota.isOutOfScreen()) {
                context.gotasRojas.removeIndex(i);
            } else if (context.playerTears.getBounds().overlaps(gota.getReducedBounds())) {
                context.gotasRojas.removeIndex(i);
                context.hearts--;
                if (context.hearts < 0) context.hearts = 0;
                audio.playSound(resources.sonidoMortal);
            }
        }
    }

    private void draw() {
        renderManager.draw(context, spriteBatch, viewport, hudViewport, settings);
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;

        viewport.update(width, height, true);
        hudViewport.update(width, height, true);

        renderManager.updateIntroLayout(context, hudViewport);
    }

    @Override public void pause() { }

    @Override public void resume() { }

    @Override
    public void hide() {
        audio.stopMusic();
    }

    @Override
    public void dispose() {
        if (spriteBatch != null) spriteBatch.dispose();
    }
}
