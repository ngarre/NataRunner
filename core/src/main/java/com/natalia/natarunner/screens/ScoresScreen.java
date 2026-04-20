package com.natalia.natarunner.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.natalia.natarunner.manager.AudioManager;
import com.natalia.natarunner.manager.GameSettings;
import com.natalia.natarunner.manager.ResourceManager;

public class ScoresScreen implements Screen {

    private final Game game;
    private final AudioManager audio;
    private final ResourceManager resources;
    private final GameSettings settings;

    private SpriteBatch spriteBatch;
    private FitViewport viewport;
    private Viewport uiViewport;

    private Texture fondoTexture;
    private Texture toMenuButton;
    private final Rectangle rectanguloBack = new Rectangle();

    private Music music;
    private BitmapFont tableFont;

    public ScoresScreen(Game game, AudioManager audio, ResourceManager resources, GameSettings settings) {
        this.game = game;
        this.audio = audio;
        this.resources = resources;
        this.settings = settings;
    }

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(12.28f, 7.68f);

        uiViewport = new FitViewport(1228, 768);
        uiViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        fondoTexture = resources.fondoScore;
        toMenuButton = resources.botonToMenu;

        float w = 3f;
        float h = 3f;
        float margin = 0.1f;
        float x = viewport.getWorldWidth() - w - margin;
        float y = margin - 0.8f;
        rectanguloBack.set(x, y, w, h);

        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        music = resources.scoreMusic;
        music.setLooping(true);
        audio.playMusic(music);

        tableFont = resources.fontMenu;
    }

    @Override
    public void render(float delta) {
        input();
        draw();
    }

    private void input() {
        Vector2 mouseWorld = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouseWorld);

        if ((Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && rectanguloBack.contains(mouseWorld))
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game, audio, resources, settings));
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();

        spriteBatch.draw(fondoTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        spriteBatch.draw(
            toMenuButton,
            rectanguloBack.x,
            rectanguloBack.y,
            rectanguloBack.width,
            rectanguloBack.height
        );

        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;

        viewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    @Override public void pause() { }

    @Override public void resume() { }

    @Override public void hide() { }

    @Override
    public void dispose() {
        if (spriteBatch != null) {
            spriteBatch.dispose();
        }
    }
}
