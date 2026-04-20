package com.natalia.natarunner.manager;

import com.natalia.natarunner.manager.ResourceManager;
import com.natalia.natarunner.screens.context.FightGameContext;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.natalia.natarunner.model.entities.npc.NpcChicas;
import com.natalia.natarunner.model.entities.projectile.BulletBoss;
import com.natalia.natarunner.model.entities.projectile.BulletChicas;
import com.natalia.natarunner.ui.FloatingText;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

public class FightRenderManager {

    private final ResourceManager resources;
    private final FightGameContext ctx;

    private SpriteBatch spriteBatch;
    private FitViewport viewport;
    private FitViewport hudViewport;

    public FightRenderManager(ResourceManager resources, FightGameContext ctx) {
        this.resources = resources;
        this.ctx = ctx;
    }

    public void show() {
        spriteBatch = new SpriteBatch();

        // Mundo del juego
        viewport = new FitViewport(12.28f, 7.68f);

        // HUD en píxeles lógicos
        hudViewport = new FitViewport(1228, 768);

        // Rectángulo del cartel LEVEL 2
        float w = 300f;
        float h = 300f;
        float x = (hudViewport.getWorldWidth() - w) / 2f;
        float y = (hudViewport.getWorldHeight() - h) / 2f;
        ctx.level2Rectangulo.set(x, y, w, h);

        // Si el PauseMenu ya existe, se le aplica layout aquí
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

        spriteBatch.draw(
            resources.fondoFight,
            0,
            0,
            viewport.getWorldWidth(),
            viewport.getWorldHeight()
        );

        if (ctx.state == FightGameContext.GameState.PLAYING) {

            if (ctx.player != null) {
                ctx.player.draw(spriteBatch);
            }

            for (BulletChicas b : ctx.bullets) {
                b.draw(spriteBatch);
            }

            for (BulletBoss b : ctx.bossBullets) {
                b.draw(spriteBatch);
            }

            for (NpcChicas npc : ctx.sideNPCs) {
                npc.draw(spriteBatch);
            }

            if (ctx.boss != null && ctx.boss.isVisible()) {
                ctx.boss.draw(spriteBatch);
            }
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

        // INTRO
        if (ctx.state == FightGameContext.GameState.INTRO) {
            sacarLevel2(screenWidth, screenHeight);
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
        float yBar = screenHeight - ctx.barHeight + (ctx.barHeight - 20f) / 2f;

        if (ctx.scoreBar != null) {
            ctx.scoreBar.draw(
                spriteBatch,
                resources.hudBackground,
                ctx.score,
                screenWidth,
                yBar
            );
        }

        if (ctx.font != null) {
            ctx.font.draw(
                spriteBatch,
                "SCORE: " + ctx.score,
                20,
                screenHeight - 20
            );
        }

        renderHearts(screenHeight);

        // TIMER
        if (ctx.countdownTimer != null) {
            float middleX = hudViewport.getWorldWidth() / 2f - 60f;
            float yPos = hudViewport.getWorldHeight() - 20f;
            ctx.countdownTimer.draw(spriteBatch, middleX, yPos);
        }

        // FLASH
        if (ctx.flashMessage != null && ctx.smallFont != null) {
            float flashHeight = 40f;

            ctx.flashMessage.draw(
                spriteBatch,
                resources.hudBackground,
                ctx.smallFont,
                screenWidth,
                flashHeight
            );
        }

        // TEXTOS FLOTANTES
        if (ctx.smallFont != null) {
            for (FloatingText ft : ctx.floatingTexts) {
                Vector2 screenPos = new Vector2(ft.pos.x, ft.pos.y);
                viewport.project(screenPos);

                ctx.smallFont.setColor(ft.color.r, ft.color.g, ft.color.b, ft.alpha);
                ctx.smallFont.draw(spriteBatch, ft.text, screenPos.x, screenPos.y);
            }

            ctx.smallFont.setColor(1f, 1f, 1f, 1f);
        }

        // GAME OVER
        if (ctx.state == FightGameContext.GameState.GAMEOVER) {
            sacarGameOver(screenWidth, screenHeight);
        }

        // YOU WIN
        if (ctx.state == FightGameContext.GameState.YOUWIN) {
            sacarYouWin(screenWidth, screenHeight);
        }

        // PAUSE
        if (ctx.state == FightGameContext.GameState.PAUSED && ctx.pauseMenu != null) {
            ctx.pauseMenu.draw(
                spriteBatch,
                hudViewport.getWorldWidth(),
                hudViewport.getWorldHeight()
            );
        }


        // Llamo a pintar el nombre del nivel
        renderLevelTitle(spriteBatch);

        spriteBatch.end();
    }

    private void sacarLevel2(float w, float h) {
        spriteBatch.setColor(0f, 0f, 0f, 0.75f);
        spriteBatch.draw(resources.hudBackground, 0, 0, w, h);
        spriteBatch.setColor(1f, 1f, 1f, 1f);

        spriteBatch.draw(
            resources.level2,
            ctx.level2Rectangulo.x,
            ctx.level2Rectangulo.y,
            ctx.level2Rectangulo.width,
            ctx.level2Rectangulo.height
        );
    }


    // Esto saca el título del nivel abajo.
    private void renderLevelTitle(SpriteBatch spriteBatch) {

        float marginRight = 20f;
        float marginBottom = 20f;

        float x = hudViewport.getWorldWidth() - marginRight;
        float y = marginBottom + resources.fontMenu.getCapHeight();

        resources.fontMenu.draw(
            spriteBatch,
            ctx.level2Title,    // <--- Está en TearsGameContext
            x,
            y,
            0,
            Align.right,
            false
        );
    }

    private void sacarGameOver(float screenWidth, float screenHeight) {
        float imgWidth = 400f;
        float imgHeight = 400f;
        float x = (screenWidth - imgWidth) / 2f;
        float y = (screenHeight - imgHeight) / 2f;

        spriteBatch.draw(resources.gameOver, x, y, imgWidth, imgHeight);
    }

    private void renderHearts(float screenHeight) {
        if (resources.minicorazon == null) return;

        float heartWidth = 28f;
        float heartHeight = 28f;
        float spacing = 8f;

        float startX = 280f;             // <--- posición horizontal de los corazones
        float y = screenHeight - 50f;    // <---          vertical de los corazones.

        for (int i = 0; i < ctx.hearts; i++) {
            float x = startX + i * (heartWidth + spacing);
            spriteBatch.draw(resources.minicorazon, x, y, heartWidth, heartHeight);
        }
    }

    private void sacarYouWin(float screenWidth, float screenHeight) {
        float imgWidth = 500f;
        float imgHeight = 500f;
        float x = (screenWidth - imgWidth) / 2f;
        float y = (screenHeight - imgHeight) / 2f;

        spriteBatch.draw(resources.youWin, x, y, imgWidth, imgHeight);
    }

    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;

        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
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
