package com.natalia.natarunner.screens;

import com.natalia.natarunner.manager.*;
import com.natalia.natarunner.screens.context.FightGameContext;
import com.natalia.natarunner.ui.*;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class FightScreen implements Screen {

    private final Game game;

    AudioManager audio;
    ResourceManager resources;
    GameSettings settings;

    private final FightGameContext ctx;
    private final FightLogicManager logicManager;
    private final FightRenderManager renderManager;

    public FightScreen(Game game, AudioManager audio,  ResourceManager resources, GameSettings settings) {
        this.game = game;
        this.audio = audio;
        this.resources = resources;
        this.settings = settings;

        this.ctx = new FightGameContext();
        this.renderManager = new FightRenderManager(resources, ctx);
        this.logicManager = new FightLogicManager(game, audio, resources, settings, ctx);
        this.logicManager.setRenderManager(renderManager);
        this.logicManager.setOwnerScreen(this);
    }




    // *************************************************************************************************************
    // SHOW....
    // *************************************************************************************************************
    @Override
    public void show() {
        logicManager.show();   //   <---- Inicializa el estado del juego (entidades, lógica, timers, audio)
        renderManager.show();  //   <---- Inicializa elementos de render (SpriteBatch, viewports, layout HUD)
    }


    // *************************************************************************************************************
    // RENDER....
    // *************************************************************************************************************
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

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        renderManager.dispose();
    }
}
