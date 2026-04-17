package com.natalia.natarunner.manager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.natalia.natarunner.model.entities.drops.RedDrop;
import com.natalia.natarunner.model.entities.drops.WhiteDrop;
import com.natalia.natarunner.model.entities.drops.YellowDrop;
import com.natalia.natarunner.screens.context.TearsGameContext;

public class TearsRenderManager {

    private final ResourceManager resources;
    private final TearsGameContext ctx;
    private final FitViewport viewport;
    private final FitViewport hudViewport;

    private SpriteBatch spriteBatch;

    public TearsRenderManager(ResourceManager resources, TearsGameContext ctx, FitViewport viewport, FitViewport hudViewport) {
        this.resources = resources;
        this.ctx = ctx;
        this.viewport = viewport;
        this.hudViewport = hudViewport;
    }

    public void show() {
        spriteBatch = new SpriteBatch();

        float w = 300f;
        float h = 300f;
        float x = (hudViewport.getWorldWidth() - w) / 2f;
        float y = (hudViewport.getWorldHeight() - h) / 2f;
        ctx.level1Rectangulo.set(x, y, w, h);
    }

    public void draw() {
        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(resources.fondoTears, 0, 0, worldWidth, worldHeight);

        if (ctx.state == TearsGameContext.GameState.PLAYING) {
            if (ctx.playerTears != null) {
                ctx.playerTears.draw(spriteBatch);
            }

            for (WhiteDrop gota : ctx.gotasBlancas) {
                gota.draw(spriteBatch);
            }

            for (YellowDrop gota : ctx.gotasAmarillas) {
                gota.draw(spriteBatch);
            }

            for (RedDrop gota : ctx.gotasRojas) {
                gota.draw(spriteBatch);
            }
        }

        spriteBatch.end();

        hudViewport.apply();
        spriteBatch.setProjectionMatrix(hudViewport.getCamera().combined);

        spriteBatch.begin();

        float screenWidth = hudViewport.getWorldWidth();
        float screenHeight = hudViewport.getWorldHeight();

        if (ctx.state == TearsGameContext.GameState.INTRO) {
            sacarLevel1();
            spriteBatch.end();
            return;
        }

        spriteBatch.setColor(0f, 0f, 0f, 0.5f);
        spriteBatch.draw(
            resources.hudBackground,
            0,
            screenHeight - ctx.barHeight,
            screenWidth,
            ctx.barHeight
        );
        spriteBatch.setColor(1f, 1f, 1f, 1f);

        ctx.font.draw(spriteBatch, ctx.level1Title, 390, 730);
        ctx.smallFont.draw(spriteBatch, "SCORE: " + ctx.score, 20, 745);
        ctx.smallFont.draw(spriteBatch, "META: " + ctx.scoreBasta, 180, 745);
        ctx.smallFont.draw(spriteBatch, "HEARTS: " + ctx.hearts, 340, 745);
        ctx.smallFont.draw(spriteBatch, "ESC = volver al menu", 930, 745);

        spriteBatch.end();
    }

    private void sacarLevel1() {
        spriteBatch.draw(
            resources.Level1Texture,
            ctx.level1Rectangulo.x,
            ctx.level1Rectangulo.y,
            ctx.level1Rectangulo.width,
            ctx.level1Rectangulo.height
        );

        if (ctx.smallFont != null) {
            ctx.smallFont.draw(
                spriteBatch,
                "Pulsa ENTER o haz click para comenzar",
                360,
                180
            );
        }
    }

    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;

        viewport.update(width, height, true);
        hudViewport.update(width, height, true);

        float w = 300f;
        float h = 300f;
        float x = (hudViewport.getWorldWidth() - w) / 2f;
        float y = (hudViewport.getWorldHeight() - h) / 2f;
        ctx.level1Rectangulo.set(x, y, w, h);
    }

    public void dispose() {
        if (spriteBatch != null) spriteBatch.dispose();
    }
}
