package com.natalia.natarunner.manager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.natalia.natarunner.model.entities.drops.RedDrop;
import com.natalia.natarunner.model.entities.drops.WhiteDrop;
import com.natalia.natarunner.model.entities.drops.YellowDrop;
import com.natalia.natarunner.model.entities.projectile.RedDropProjectile;
import com.natalia.natarunner.screens.context.TearsGameContext;
import com.badlogic.gdx.utils.Align;


public class TearsRenderManager {

    private final ResourceManager resources;
    private final TearsGameContext ctx;
    private FitViewport viewport;
    private FitViewport hudViewport;

    private SpriteBatch spriteBatch;

    public TearsRenderManager(ResourceManager resources, TearsGameContext ctx) {
        this.resources = resources;
        this.ctx = ctx;
    }

    public void show() {
        spriteBatch = new SpriteBatch();

        viewport = new FitViewport(12.28f, 7.68f);
        hudViewport = new FitViewport(1228, 768);

        float w = 300f;
        float h = 300f;
        float x = (hudViewport.getWorldWidth() - w) / 2f;
        float y = (hudViewport.getWorldHeight() - h) / 2f;
        ctx.level1Rectangulo.set(x, y, w, h);

        if (ctx.pauseMenu != null) {
            ctx.pauseMenu.layout(hudViewport.getWorldWidth(), hudViewport.getWorldHeight());
        }
    }

    public void draw() {
        ScreenUtils.clear(Color.BLACK);

        // =========================
        // DIBUJO DEL MUNDO
        // =========================
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(resources.fondoTears, 0, 0, worldWidth, worldHeight);

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

        for (RedDropProjectile p : ctx.redProjectiles) {
            p.draw(spriteBatch);
        }

        spriteBatch.end();

        // =========================
        // DIBUJO DEL HUD
        // =========================
        hudViewport.apply();
        spriteBatch.setProjectionMatrix(hudViewport.getCamera().combined);

        spriteBatch.begin();

        float screenWidth = hudViewport.getWorldWidth();
        float screenHeight = hudViewport.getWorldHeight();

        if (ctx.state == TearsGameContext.GameState.INTRO) {
            sacarLevel1(screenWidth, screenHeight);
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

        // SCORE BAR
        if (ctx.scoreBar != null) {
            ctx.scoreBar.draw(
                spriteBatch,
                resources.hudBackground,
                ctx.score,
                screenWidth,
                727f
            );
        }

        // SCORE
        ctx.font.draw(spriteBatch, "SCORE: " + ctx.score, 15, 745);

        // HEARTS
        renderHearts(screenHeight);

        // TIMER
        if (ctx.countdownTimer != null) {
            ctx.countdownTimer.draw(spriteBatch, 610f, 745f);
        }

        // FLASH MESSAGE
        float flashHeight = 40f;
        if (ctx.flashMessage != null && ctx.smallFont != null) {
            ctx.flashMessage.draw(
                spriteBatch,
                resources.hudBackground,
                ctx.smallFont,
                screenWidth,
                flashHeight
            );
        }

        // GAME OVER
        if (ctx.state == TearsGameContext.GameState.GAMEOVER) {
            sacarGameOver(screenWidth, screenHeight);
        }

        // FLOATING TEXTS
        if (ctx.smallFont != null) {
            for (com.natalia.natarunner.ui.FloatingText ft : ctx.floatingTexts) {
                com.badlogic.gdx.math.Vector2 screenPos =
                    new com.badlogic.gdx.math.Vector2(ft.pos.x, ft.pos.y);
                viewport.project(screenPos);

                ctx.smallFont.setColor(ft.color.r, ft.color.g, ft.color.b, ft.alpha);
                ctx.smallFont.draw(spriteBatch, ft.text, screenPos.x, screenPos.y);
            }

            ctx.smallFont.setColor(1f, 1f, 1f, 1f);
        }

        // PAUSE MENU
        if (ctx.state == TearsGameContext.GameState.PAUSED && ctx.pauseMenu != null) {
            ctx.pauseMenu.draw(
                spriteBatch,
                hudViewport.getWorldWidth(),
                hudViewport.getWorldHeight()
            );
        }

        // LEVEL TITLE
        renderLevelTitle(spriteBatch);

        spriteBatch.end();
    }

    private void sacarGameOver(float screenWidth, float screenHeight) {
        float imgWidth = 400f;
        float imgHeight = 400f;
        float x = (screenWidth - imgWidth) / 2f;
        float y = (screenHeight - imgHeight) / 2f;

        spriteBatch.draw(resources.gameOver, x, y, imgWidth, imgHeight);
    }

    private void sacarLevel1(float w, float h) {
        spriteBatch.setColor(0f, 0f, 0f, 0.75f);
        spriteBatch.draw(resources.hudBackground, 0, 0, w, h);
        spriteBatch.setColor(1f, 1f, 1f, 1f);

        spriteBatch.draw(
            resources.Level1Texture,
            ctx.level1Rectangulo.x,
            ctx.level1Rectangulo.y,
            ctx.level1Rectangulo.width,
            ctx.level1Rectangulo.height
        );
    }

    private void renderLevelTitle(SpriteBatch spriteBatch) {
        float marginRight = 20f;
        float marginBottom = 20f;

        float x = hudViewport.getWorldWidth() - marginRight;
        float y = marginBottom + resources.fontMenu.getCapHeight();

        resources.fontMenu.draw(
            spriteBatch,
            ctx.level1Title,
            x,
            y,
            0,
            Align.right,
            false
        );
    }

    private void renderHearts(float screenHeight) {
        if (resources.minicorazon == null) return;

        float heartWidth = 28f;
        float heartHeight = 28f;
        float spacing = 8f;

        float startX = 280f;
        float y = screenHeight - 50f;

        for (int i = 0; i < ctx.hearts; i++) {
            float x = startX + i * (heartWidth + spacing);
            spriteBatch.draw(resources.minicorazon, x, y, heartWidth, heartHeight);
        }
    }

    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;

        viewport.update(width, height, true);
        hudViewport.update(width, height, true);

        if (ctx.pauseMenu != null) {
            ctx.pauseMenu.layout(hudViewport.getWorldWidth(), hudViewport.getWorldHeight());
        }

        float w = 300f;
        float h = 300f;
        float x = (hudViewport.getWorldWidth() - w) / 2f;
        float y = (hudViewport.getWorldHeight() - h) / 2f;
        ctx.level1Rectangulo.set(x, y, w, h);
    }

    public void dispose() {
        if (spriteBatch != null) {
            spriteBatch.dispose();
        }
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public FitViewport getViewport() {
        return viewport;
    }

    public FitViewport getHudViewport() {
        return hudViewport;
    }
}
