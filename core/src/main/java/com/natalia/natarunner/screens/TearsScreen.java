package com.natalia.natarunner.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.natalia.natarunner.config.GameConfig;
import com.natalia.natarunner.manager.AudioManager;
import com.natalia.natarunner.manager.GameSettings;
import com.natalia.natarunner.manager.ResourceManager;
import com.natalia.natarunner.manager.TearsLogicManager;
import com.natalia.natarunner.manager.TearsRenderManager;
import com.natalia.natarunner.screens.context.TearsGameContext;

public class TearsScreen implements Screen {

    private final ResourceManager resources;
    private final TearsRenderManager renderManager;
    private final TearsLogicManager logicManager;
    private final TearsGameContext ctx;

    public TearsScreen(Game game, AudioManager audio, ResourceManager resources, GameSettings settings) {
        this.ctx = new TearsGameContext();

        FitViewport viewport = new FitViewport(GameConfig.mundoAnchoTears, GameConfig.mundoAltoTears);
        FitViewport hudViewport = new FitViewport(1228, 768);

        this.resources = resources;
        this.renderManager = new TearsRenderManager(resources, ctx, viewport, hudViewport);
        this.logicManager = new TearsLogicManager(game, audio, resources, settings, ctx, viewport, hudViewport);
    }

    @Override
    public void show() {
        ctx.font = resources.hudFont;
        ctx.smallFont = resources.hudSmallFont;
        renderManager.show();
        logicManager.show();
    }

    @Override
    public void render(float delta) {
        logicManager.render(delta);
        renderManager.draw();
    }

    @Override
    public void resize(int width, int height) {
        renderManager.resize(width, height);
    }

    @Override public void pause() { }
    @Override public void resume() { }

    @Override
    public void hide() {
        logicManager.hide();
    }

    @Override
    public void dispose() {
        renderManager.dispose();
    }
}
