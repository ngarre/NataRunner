package com.natalia.natarunner.screens;

import com.natalia.natarunner.NataRunner;
import com.natalia.natarunner.manager.AudioManager;
import com.natalia.natarunner.manager.GameSettings;
import com.natalia.natarunner.manager.ResourceManager;
import com.natalia.natarunner.model.ScoreEntry;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ScoresScreen implements Screen {

    private final Game game;

    SpriteBatch spriteBatch;
    FitViewport viewport;

    Texture toMenuButton;
    Rectangle rectanguloBack = new Rectangle();
    private Texture fondoTexture;
    private Music music;

    AudioManager audio;
    ResourceManager resources;
    GameSettings settings;

    private Array<ScoreEntry> scores;
    private BitmapFont tableFont;

    public ScoresScreen(Game game,  AudioManager audio,  ResourceManager resources,  GameSettings settings)
    {
        this.game = game;
        this.audio = audio;
        this.resources = resources;
        this.settings = settings;
    }

    private Viewport uiViewport;


    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(12.28f, 7.68f);

        uiViewport = new FitViewport(1228, 768);
        uiViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        //  Cargo el fondo igual que antes
        fondoTexture = resources.fondoScore;
        //  y el único botón de volver al menú principal
        toMenuButton = resources.botonToMenu;

        // Para posicionar el botón toMenu...
        float w = 3f;
        float h = 3f;
        float margin = 0.1f;
        float x = viewport.getWorldWidth() - w - margin;
        float y = margin - 0.8f;  // ajuste obtenido mediante prueba
        rectanguloBack.set(x, y, w, h);

        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        music = resources.scoreMusic;
        music.setLooping(true);
        audio.playMusic(music);

        tableFont = resources.fontMenu;

        // Tema de la puntuación
        NataRunner nataRunner = (NataRunner) game;
        if (!nataRunner.session.isScoreSaved()) {

            String playerName = settings.getPlayerName();
            int finalScore = nataRunner.session.getFinalScore();

            long now = System.currentTimeMillis();
            String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            // ... pero internamente guardo milisegundos por si se acaba una partida en el mismo minuto
            //     por dos jugadores distintos. para que se ordene bien.

            ScoreEntry newEntry = new ScoreEntry(playerName, finalScore, dateTime, now);
            nataRunner.scoreManager.addScore(newEntry);

            nataRunner.session.setScoreSaved(true);

        }
        scores = nataRunner.scoreManager.loadScores();

    }

    @Override
    public void render(float delta) {
        input();
        logic(delta);
        draw();
    }

    private void logic(float delta) {

    }

    private void input() {

        Vector2 mouseWorld = new Vector2(Gdx.input.getX(), Gdx.input.getY());

        viewport.unproject(mouseWorld);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) &&  rectanguloBack.contains(mouseWorld)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
        {
            game.setScreen(new MenuScreen(game, audio, resources,  settings));
        }
    }


    private void drawScoresTable() {
        float startX = 100f;
        float startY = 620f;
        float rowHeight = 42f;

        tableFont.setColor(Color.WHITE);

        tableFont.draw(spriteBatch, "LAST 10 SCORES", startX, startY + 50f);

        tableFont.setColor(Color.YELLOW);
        tableFont.draw(spriteBatch, "NAME", startX, startY);
        tableFont.draw(spriteBatch, "SCORE", startX + 220f, startY);
        tableFont.draw(spriteBatch, "DATE", startX + 380f, startY);
        tableFont.setColor(Color.WHITE);

        for (int i = 0; i < scores.size; i++) {
            ScoreEntry entry = scores.get(i);
            float y = startY - ((i + 1) * rowHeight);

            tableFont.draw(spriteBatch, entry.getPlayerName(), startX, y);
            tableFont.draw(spriteBatch, String.valueOf(entry.getFinalScore()), startX + 220f, y);
            tableFont.draw(spriteBatch, entry.getDateTime(), startX + 380f, y);
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);

        // =========================
        // CAPA MUNDO
        // =========================
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        // Fondo
        spriteBatch.draw(fondoTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        // Botón back
        spriteBatch.draw(toMenuButton, rectanguloBack.x, rectanguloBack.y, rectanguloBack.width, rectanguloBack.height);

        spriteBatch.end();

        // =========================
        // CAPA UI / TABLA
        // =========================
        uiViewport.apply();
        spriteBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        spriteBatch.begin();

        drawScoresTable();



        spriteBatch.end();
    }



    @Override public void resize(int width, int height)
    {
        if(width <= 0 || height <= 0) return;
        viewport.update(width, height, true);

        // Necesario para el escalado correcto de la fuente de las instrucciones
        uiViewport.update(width, height, true);

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

        // no hago dispose de nada más porque los recursos están en ResourceManager.

    }
}
