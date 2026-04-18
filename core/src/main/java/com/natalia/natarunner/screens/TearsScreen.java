package com.natalia.natarunner.screens;

import com.natalia.natarunner.manager.AudioManager;
import com.natalia.natarunner.manager.GameSettings;
import com.natalia.natarunner.manager.ResourceManager;
import com.natalia.natarunner.manager.TearsLogicManager;
import com.natalia.natarunner.manager.TearsRenderManager;
import com.natalia.natarunner.screens.context.TearsGameContext;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class TearsScreen implements Screen {

    private final Game game;
    private final AudioManager audio;
    private final ResourceManager resources;
    private final GameSettings settings;

    private final TearsGameContext ctx;
    private final TearsLogicManager logicManager;
    private final TearsRenderManager renderManager;

    public TearsScreen(Game game, AudioManager audio, ResourceManager resources, GameSettings settings) {
        this.game = game;
        this.audio = audio;
        this.resources = resources;
        this.settings = settings;

        this.ctx = new TearsGameContext();
        this.renderManager = new TearsRenderManager(resources, ctx);
        this.logicManager = new TearsLogicManager(game, audio, resources, settings, ctx);

        this.logicManager.setRenderManager(renderManager);
        this.logicManager.setOwnerScreen(this);
    }

    @Override
    public void show() {
        logicManager.show();   //   <---- Inicializa el estado del juego (entidades, lógica, timers, audio)
        renderManager.show();  //   <---- Inicializa elementos de render (SpriteBatch, viewports, layout HUD)
    }

    @Override
    public void render(float delta) {
        logicManager.input();
        logicManager.update(delta);
        renderManager.draw();
    }

    @Override
    public void resize(int width, int height) {
        renderManager.resize(width, height);
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
        renderManager.dispose();
    }
}
