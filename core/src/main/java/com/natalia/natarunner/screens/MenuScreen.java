package com.natalia.natarunner.screens;

import com.natalia.natarunner.NataRunner;
import com.natalia.natarunner.manager.AudioManager;
import com.natalia.natarunner.manager.GameSettings;
import com.natalia.natarunner.manager.ResourceManager;
import com.natalia.natarunner.ui.LogoIntroAnimation;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;

public class MenuScreen implements Screen {

    private final Game game;
    private final AudioManager audio;
    private final ResourceManager resources;
    private final GameSettings settings;

    private SpriteBatch spriteBatch;
    private FitViewport viewport;

    private Texture fondoMenu;
    private Texture logoTexture;
    private LogoIntroAnimation logoIntro;

    private Music music;

    private Texture startTexture;
    private Texture setupTexture;
    private Texture quitTexture;

    private final Rectangle startRectangulo = new Rectangle();
    private final Rectangle setupRectangulo = new Rectangle();
    private final Rectangle quitRectangulo = new Rectangle();

    private boolean initialized = false;

    private Stage stage;
    private VisTextField nameField;
    private VisLabel nameLabel;
    private VisTable table;

    public MenuScreen(Game game, AudioManager audio, ResourceManager resources, GameSettings settings) {
        this.game = game;
        this.audio = audio;
        this.resources = resources;
        this.settings = settings;
    }

    @Override
    public void show() {
        if (!initialized) {
            music = resources.menuMusic;
            music.setLooping(true);
            audio.playMusic(music);

            spriteBatch = new SpriteBatch();
            viewport = new FitViewport(12.28f, 7.68f);

            fondoMenu = resources.fondoMenu;
            logoTexture = resources.logoTexture;
            logoIntro = new LogoIntroAnimation(logoTexture, viewport);

            startTexture = resources.startTexture;
            setupTexture = resources.setupTexture;
            quitTexture = resources.quitTexture;

            initialized = true;

            if (!VisUI.isLoaded()) {
                VisUI.load();
            }

            stage = new Stage(new ScreenViewport());
            stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

            table = new VisTable();
            table.bottom().right();
            table.setFillParent(true);
            table.setVisible(false);

            Label.LabelStyle labelStyle = new Label.LabelStyle();
            labelStyle.font = resources.fontMenu;
            labelStyle.fontColor = Color.WHITE;

            nameLabel = new VisLabel("Player Name:", labelStyle);

            VisTextField.VisTextFieldStyle textFieldStyle =
                new VisTextField.VisTextFieldStyle(
                    VisUI.getSkin().get(VisTextField.VisTextFieldStyle.class)
                );
            textFieldStyle.font = resources.fontMenu;

            nameField = new VisTextField(settings.getPlayerName(), textFieldStyle);
            nameField.setMaxLength(12);

            table.add(nameLabel).padRight(10);
            table.add(nameField).width(180).height(40);
            table.pad(20);

            stage.addActor(table);

            InputMultiplexer multiplexer = new InputMultiplexer();
            multiplexer.addProcessor(stage);
            Gdx.input.setInputProcessor(multiplexer);
        }
    }

    private void input() {
        if (!logoIntro.isFinished()) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.F9)) {
            ((NataRunner) game).scoreManager.clearScores();
            System.out.println("Scores borrados.");
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(mouse);

            if (startRectangulo.contains(mouse)) {
                String playerName = nameField.getText().trim();
                if (playerName.isEmpty()) {
                    playerName = "NONAME";
                }
                settings.setPlayerName(playerName);

                NataRunner nataRunner = (NataRunner) game;
                nataRunner.session.reset();

                audio.stopMusic();
                game.setScreen(new TearsScreen(game));
            }

            if (setupRectangulo.contains(mouse)) {
                game.setScreen(new ConfigurationScreen(game, this));
            }

            if (quitRectangulo.contains(mouse)) {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void render(float delta) {
        input();
        logic(delta);
        draw();

        stage.act(delta);
        stage.draw();
    }

    private void logic(float delta) {
        logoIntro.update(delta);
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        spriteBatch.draw(fondoMenu, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        logoIntro.draw(spriteBatch, viewport);

        if (logoIntro.isFinished()) {
            sacarBotones();
            table.setVisible(true);
        }

        spriteBatch.end();
    }

    private void sacarBotones() {
        float width = 3f;
        float height = 1f;
        float spacing = 0.1f;

        float x = viewport.getWorldWidth() / 2f - width / 2f;
        float y = viewport.getWorldHeight() / 2f - height / 2f;

        startRectangulo.set(x, y, width, height);
        setupRectangulo.set(x, y - height - spacing, width, height);
        quitRectangulo.set(x, y - (height + spacing) * 2, width, height);

        spriteBatch.draw(startTexture, startRectangulo.x, startRectangulo.y, width, height);
        spriteBatch.draw(setupTexture, setupRectangulo.x, setupRectangulo.y, width, height);
        spriteBatch.draw(quitTexture, quitRectangulo.x, quitRectangulo.y, width, height);
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;

        viewport.update(width, height, true);

        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override public void pause() { }
    @Override public void resume() { }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (spriteBatch != null) spriteBatch.dispose();
    }
}
