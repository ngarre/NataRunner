package com.natalia.natarunner.screens;

import com.natalia.natarunner.NataRunner;
import com.natalia.natarunner.config.GameConfig;
import com.natalia.natarunner.manager.AudioManager;
import com.natalia.natarunner.manager.GameSettings;
import com.natalia.natarunner.manager.ResourceManager;
import com.natalia.natarunner.model.entities.player.PlayerBladecar;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FightScreen implements Screen {

    private final Game game;

    private AudioManager audio;
    private ResourceManager resources;
    private GameSettings settings;

    private SpriteBatch spriteBatch;
    private FitViewport viewport;
    private FitViewport hudViewport;

    private BitmapFont font;
    private BitmapFont smallFont;

    private PlayerBladecar playerBladecar;

    private float hudHeight = 1f;
    private float barHeight = 60f;

    private int fightScore = 0;
    private int hearts = GameConfig.cuantosCorazones;

    private Music music;

    private enum GameState {
        INTRO,
        PLAYING
    }

    private GameState state = GameState.INTRO;

    private Rectangle level2Rectangulo = new Rectangle();
    private Vector2 mouseHud = new Vector2();

    public FightScreen(Game game) {
        this.game = game;
    }

    public FightScreen(Game game, AudioManager audio, ResourceManager resources, GameSettings settings) {
        this(game);
    }

    @Override
    public void show() {
        NataRunner nataRunner = (NataRunner) game;
        audio = nataRunner.audioManager;
        resources = nataRunner.resources;
        settings = nataRunner.settings;

        if (spriteBatch == null) {
            spriteBatch = new SpriteBatch();
            viewport = new FitViewport(GameConfig.mundoAnchoFight, GameConfig.mundoAltoFight);
            hudViewport = new FitViewport(1228, 768);
        }

        font = resources.hudFont;
        smallFont = resources.hudSmallFont;

        playerBladecar = new PlayerBladecar(
            GameConfig.mundoAnchoFight,
            GameConfig.mundoAltoFight,
            1f,
            audio,
            resources
        );

        fightScore = 0;
        hearts = GameConfig.cuantosCorazones;
        state = GameState.INTRO;

        music = resources.fightMusic;
        music.setLooping(true);
        music.stop();

        float w = 300f;
        float h = 300f;
        float x = (hudViewport.getWorldWidth() - w) / 2f;
        float y = (hudViewport.getWorldHeight() - h) / 2f;
        level2Rectangulo.set(x, y, w, h);

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
            audio.stopMusic();
            game.setScreen(new MenuScreen(game, ((NataRunner) game).audioManager, ((NataRunner) game).resources, ((NataRunner) game).settings));
            return;
        }

        if (state == GameState.INTRO) {
            boolean startGame = false;

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                startGame = true;
            }

            mouseHud.set(Gdx.input.getX(), Gdx.input.getY());
            hudViewport.unproject(mouseHud);

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)
                && level2Rectangulo.contains(mouseHud)) {
                startGame = true;
            }

            if (startGame) {
                state = GameState.PLAYING;
                audio.playMusic(music);
            }

            return;
        }

        playerBladecar.move(delta, viewport.getWorldWidth(), viewport.getWorldHeight(), hudHeight);

        if (settings.isMouseEnabled() && Gdx.input.isTouched()) {
            Vector2 mouseWorld = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            playerBladecar.handleMouseDrag(mouseWorld);
        }
    }

    private void update(float delta) {
        if (state == GameState.PLAYING) {
            playerBladecar.update(delta);
        }

        ((NataRunner) game).session.setFightScore(fightScore);
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        spriteBatch.draw(
            resources.fondoFight,
            0,
            0,
            viewport.getWorldWidth(),
            viewport.getWorldHeight()
        );

        if (state == GameState.PLAYING) {
            playerBladecar.draw(spriteBatch);
        }

        spriteBatch.end();

        hudViewport.apply();
        spriteBatch.setProjectionMatrix(hudViewport.getCamera().combined);

        spriteBatch.begin();

        float screenWidth = hudViewport.getWorldWidth();
        float screenHeight = hudViewport.getWorldHeight();

        spriteBatch.setColor(0f, 0f, 0f, 0.5f);
        spriteBatch.draw(resources.hudBackground, 0, screenHeight - barHeight, screenWidth, barHeight);
        spriteBatch.setColor(1f, 1f, 1f, 1f);

        if (state == GameState.INTRO) {
            spriteBatch.draw(resources.level2, level2Rectangulo.x, level2Rectangulo.y,
                level2Rectangulo.width, level2Rectangulo.height);

            smallFont.draw(spriteBatch, "Pulsa ENTER o haz click para comenzar", 360, 180);
        } else {
            font.draw(spriteBatch, "LEVEL 2: FIGHT ZONE", 390, 730);
            smallFont.draw(spriteBatch, "SCORE L1: " + ((NataRunner) game).session.getTearsScore(), 20, 745);
            smallFont.draw(spriteBatch, "SCORE L2: " + fightScore, 220, 745);
            smallFont.draw(spriteBatch, "HEARTS: " + hearts, 380, 745);
            smallFont.draw(spriteBatch, "Jugador: " + settings.getPlayerName(), 540, 745);
            smallFont.draw(spriteBatch, "ESC = volver al menu", 930, 745);
        }

        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);

        float w = 300f;
        float h = 300f;
        float x = (hudViewport.getWorldWidth() - w) / 2f;
        float y = (hudViewport.getWorldHeight() - h) / 2f;
        level2Rectangulo.set(x, y, w, h);
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
