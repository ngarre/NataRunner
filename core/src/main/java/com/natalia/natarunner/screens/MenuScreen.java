package com.natalia.natarunner.screens;

import com.natalia.natarunner.NataRunner;
import com.natalia.natarunner.manager.AudioManager;
import com.natalia.natarunner.manager.GameSettings;
import com.natalia.natarunner.manager.ResourceManager;
import com.natalia.natarunner.ui.LogoIntroAnimation;
import com.badlogic.gdx.*;
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
    AudioManager audio;
    ResourceManager resources;
    GameSettings settings;


    public MenuScreen(Game game,  AudioManager audio,  ResourceManager resources , GameSettings settings) {
        this.game = game;
        this.audio = audio;
        this.resources = resources;
        this.settings = settings;
    }

    SpriteBatch spriteBatch;
    FitViewport viewport;
    Texture fondoMenu;

    Texture logoTexture;
    LogoIntroAnimation logoIntro;

    Music music;

    // para el cuadro START
    Texture startTexture;
    // Con este rectángulo haré que pueda hacer click sobre el botón de start
    Rectangle startRectangulo = new Rectangle();

    // para el cuadro SETUP
    Texture setupTexture;
    Rectangle setupRectangulo = new Rectangle();

    // para el cuadro QUIT
    Texture quitTexture;
    Rectangle quitRectangulo = new Rectangle();

    private boolean initialized = false;

    // Atributos para el campo del nombre del jugador
    private Stage stage;
    private VisTextField nameField;
    private VisLabel nameLabel;
    private VisTable table;


    @Override
    public void show() {


        if (!initialized) {

            // Música de fondo
            music = resources.menuMusic;
            music.setLooping(true);
            audio.playMusic(music);

            spriteBatch = new SpriteBatch();
            viewport = new FitViewport(12.28f, 7.68f);
            fondoMenu = resources.fondoMenu;

            logoTexture = resources.logoTexture;
            logoIntro = new LogoIntroAnimation(logoTexture, viewport);

            // Cartelito de Start
            startTexture = resources.startTexture;

            // Cartelito de Start
            setupTexture = resources.setupTexture;

            // Cartelito de Quit
            quitTexture = resources.quitTexture;

            // para que no se vuelva a reiniciar el show
            initialized = true;

            // Código para implementar el campo donde el player pondrá su nombre
            // ******************************************************************************************************
            if (!VisUI.isLoaded()) {
                VisUI.load();
            }
            stage = new Stage(new ScreenViewport());
            // la siguiente línea es para que salga alineado SIEMPRE el Player Name con el textfield,
            // que a veces sale raro.  Así refresco posicionamientos.
            stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

            table = new VisTable();

            // colocar la tabla abajo a la derecha
            table.bottom().right();
            table.setFillParent(true);

            // oculto al inicio
            table.setVisible(false);


            // ==============================
            // ESTILO DEL LABEL (uso mi font)
            // ==============================
            Label.LabelStyle labelStyle = new Label.LabelStyle();
            labelStyle.font = resources.fontMenu;
            labelStyle.fontColor = Color.WHITE;

            nameLabel = new VisLabel("Player Name:", labelStyle);


            // ==============================
            // ESTILO DEL TEXTFIELD
            // ==============================
            VisTextField.VisTextFieldStyle textFieldStyle =
                new VisTextField.VisTextFieldStyle(
                    VisUI.getSkin().get(VisTextField.VisTextFieldStyle.class)
                );
            textFieldStyle.font = resources.fontMenu;
            nameField = new VisTextField(settings.getPlayerName(), textFieldStyle);
            nameField.setMaxLength(12);

            // ==============================
            // AÑADIR A LA TABLA
            // ==============================
            table.add(nameLabel).padRight(10);
            table.add(nameField).width(180).height(40);

            // margen respecto al borde de pantalla
            table.pad(20);

            stage.addActor(table);

            InputMultiplexer multiplexer = new InputMultiplexer();
            multiplexer.addProcessor(stage);
            Gdx.input.setInputProcessor(multiplexer);
            // ******************************************************************************************************

        }

    }


    private void input() {

        // Si el logo sigue moviéndose, la intro, no se hace nada
        if (!logoIntro.isFinished()) return;

        // Esto del F9 es un atajo que me pongo para llamar a un método que borre las scores
        // tema depuración, comprobaciones y tal
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

                // Cada vez que se empiece una partida nueva:
                // el nombre actual del jugador queda guardado
                // la partida anterior se limpia
                // tearsScore y fightScore vuelven a 0
                // scoreSaved vuelve a false
                NataRunner nataRunner = (NataRunner) game;
                nataRunner.session.reset();

                audio.stopMusic();
                game.setScreen(new TearsScreen(game, audio, resources, settings));
            }

            if (setupRectangulo.contains(mouse)) {
                game.setScreen(new ConfigurationScreen(this, resources, audio, settings));
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


        // hago que aparezca el cuadro para meter el nombre del usuario
        if (logoIntro.isFinished()) {

            SacarBotones();

            // el objeto VisUI para sacar la solicitud del nombre del jugador
            table.setVisible(true);
        }

        spriteBatch.end();
    }


    private void SacarBotones() {

        // =========================
        // BOTONES
        // =========================

        float width = 3f;
        float height = 1f;
        float spacing = 0.1f;

        float x = viewport.getWorldWidth() / 2f - width / 2f;
        float y = viewport.getWorldHeight() / 2f - height / 2f;

        startRectangulo.set(x, y, width, height);
        setupRectangulo.set(x, y - height - spacing, width, height);
        quitRectangulo.set(x, y - (height + spacing) * 2, width, height);

        // Botón Start...
        spriteBatch.draw(startTexture, startRectangulo.x, startRectangulo.y, width, height);
        // Botón Setup...
        spriteBatch.draw(setupTexture, setupRectangulo.x, setupRectangulo.y, width, height);
        // Botón Quit
        spriteBatch.draw(quitTexture, quitRectangulo.x, quitRectangulo.y, width, height);

            /*
                   Podría sacar esto a una clase MenuButton PERO NO LO VOY A HACER por claridad
                   eso supone un nivel de abstracción innecesario y OverKill (o sea complicarme la vida)
                   no vamos a tener más botones aquí y se ve rápido.
            */

    }



    @Override public void resize(int width, int height)
    {
        if (width <= 0 || height <= 0) return;

        viewport.update(width, height, true);

        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        //music.stop();

    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        if (stage != null) {
            stage.dispose();
        }

    }
}
