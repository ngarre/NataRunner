package com.natalia.natarunner.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.natalia.natarunner.NataRunner;
import com.natalia.natarunner.manager.GameSettings;

public class MenuScreen implements Screen {

    private final Game game;

    private SpriteBatch spriteBatch;
    private FitViewport viewport;
    private BitmapFont font;

    private boolean editingName;
    private StringBuilder nameBuffer;
    private InputAdapter textInputProcessor;

    public MenuScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        if (spriteBatch == null) {
            spriteBatch = new SpriteBatch();
            viewport = new FitViewport(1280, 720);
            font = new BitmapFont();
            font.getData().setScale(2f);
            font.setColor(Color.WHITE);
        }

        GameSettings settings = ((NataRunner) game).settings;
        nameBuffer = new StringBuilder(settings.getPlayerName()); // El nombre se edita aquí y al pulsar enter se guarda definitivamente

        textInputProcessor = new InputAdapter() { // Escucha caracteres escritos
            @Override
            public boolean keyTyped(char character) { // keyTyped() escucha caracteres escritos
                if (!editingName) return false; // Si no estamos editando el nombre este metodo no hace nada

                if (character == '\b') {
                    if (nameBuffer.length() > 0) {
                        nameBuffer.deleteCharAt(nameBuffer.length() - 1);
                    }
                    return true; // Para indicar que esta tecla ya se ha gestionado
                }

                if (character == '\r' || character == '\n') { // Se detecta ENTER
                    savePlayerName();
                    return true;
                }

                if (character == 27) { // Código de Escape en este contexto --> no guarda y deja nombre preexistente
                    cancelEditingName();
                    return true;
                }

                // Para añadir letras, números o símbolos normales.  El límite son 12 caracteres
                if (character >= 32 && character <= 126 && nameBuffer.length() < 12) {
                    nameBuffer.append(character);
                    return true;
                }

                return false;
            }
        };

        Gdx.input.setInputProcessor(textInputProcessor);
    }

    @Override
    public void render(float delta) {
        input();

        GameSettings settings = ((NataRunner) game).settings;

        ScreenUtils.clear(0.05f, 0.05f, 0.08f, 1f);

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        font.draw(spriteBatch, "NataRunner", 470, 640);

        if (editingName) {
            font.draw(spriteBatch, "Editando nombre: " + nameBuffer + "_", 220, 540);
            font.draw(spriteBatch, "ENTER = guardar", 360, 450);
            font.draw(spriteBatch, "ESC = cancelar", 380, 380);
            font.draw(spriteBatch, "BACKSPACE = borrar", 300, 310);
        } else {
            font.draw(spriteBatch, "Jugador: " + settings.getPlayerName(), 360, 540);
            font.draw(spriteBatch, "Pulsa N para cambiar nombre", 320, 470);
            font.draw(spriteBatch, "Pulsa ENTER para empezar", 360, 390);
            font.draw(spriteBatch, "Pulsa C para configuracion", 340, 320);
            font.draw(spriteBatch, "Pulsa I para ver instrucciones", 320, 250);
            font.draw(spriteBatch, "Pulsa ESC para salir", 390, 180);
        }

        spriteBatch.end();
    }

    private void input() {
        if (editingName) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) { // Si estás editando y pulsas ENTER guardas nombre
                savePlayerName();
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                cancelEditingName();
            }

            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new TearsScreen(game));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            game.setScreen(new ConfigurationScreen(game, this));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            game.setScreen(new InstructionsScreen(game, this));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            startEditingName();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private void startEditingName() {
        GameSettings settings = ((NataRunner) game).settings;
        editingName = true;
        nameBuffer.setLength(0);
        nameBuffer.append(settings.getPlayerName());
    }

    private void savePlayerName() {
        GameSettings settings = ((NataRunner) game).settings;
        settings.setPlayerName(nameBuffer.toString());
        editingName = false;
    }

    private void cancelEditingName() {
        editingName = false;
        GameSettings settings = ((NataRunner) game).settings;
        nameBuffer.setLength(0);
        nameBuffer.append(settings.getPlayerName());
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        if (spriteBatch != null) spriteBatch.dispose();
        if (font != null) font.dispose();
    }
}
