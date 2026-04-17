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

    public TearsRenderManager(ResourceManager resources) {
        this.resources = resources;
    }

    public void updateIntroLayout(TearsGameContext context, FitViewport hudViewport) {
        float w = 300f;
        float h = 300f;
        float x = (hudViewport.getWorldWidth() - w) / 2f;
        float y = (hudViewport.getWorldHeight() - h) / 2f;
        context.level1Rectangulo.set(x, y, w, h);
    }

    public void draw(TearsGameContext context,
                     SpriteBatch spriteBatch,
                     FitViewport viewport,
                     FitViewport hudViewport,
                     GameSettings settings) {

        ScreenUtils.clear(Color.BLACK);

        drawWorld(context, spriteBatch, viewport);

        hudViewport.apply();
        spriteBatch.setProjectionMatrix(hudViewport.getCamera().combined);
        spriteBatch.begin();

        if (context.state == TearsGameContext.GameState.INTRO) {
            drawIntro(context, spriteBatch);
        } else {
            drawHud(context, spriteBatch, hudViewport, settings);
        }

        spriteBatch.end();
    }

    private void drawWorld(TearsGameContext context,
                           SpriteBatch spriteBatch,
                           FitViewport viewport) {

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        spriteBatch.draw(
            resources.fondoTears,
            0,
            0,
            viewport.getWorldWidth(),
            viewport.getWorldHeight()
        );

        if (context.state == TearsGameContext.GameState.PLAYING) {
            for (WhiteDrop gota : context.gotasBlancas) {
                gota.draw(spriteBatch);
            }

            for (YellowDrop gota : context.gotasAmarillas) {
                gota.draw(spriteBatch);
            }

            for (RedDrop gota : context.gotasRojas) {
                gota.draw(spriteBatch);
            }

            if (context.playerTears != null) {
                context.playerTears.draw(spriteBatch);
            }
        }

        spriteBatch.end();
    }

    private void drawIntro(TearsGameContext context, SpriteBatch spriteBatch) {
        spriteBatch.draw(
            resources.Level1Texture,
            context.level1Rectangulo.x,
            context.level1Rectangulo.y,
            context.level1Rectangulo.width,
            context.level1Rectangulo.height
        );

        if (context.smallFont != null) {
            context.smallFont.draw(spriteBatch,
                "Pulsa ENTER o haz click para comenzar",
                360,
                180
            );
        }
    }

    private void drawHud(TearsGameContext context,
                         SpriteBatch spriteBatch,
                         FitViewport hudViewport,
                         GameSettings settings) {

        float screenWidth = hudViewport.getWorldWidth();
        float screenHeight = hudViewport.getWorldHeight();

        spriteBatch.setColor(0f, 0f, 0f, 0.5f);
        spriteBatch.draw(
            resources.hudBackground,
            0,
            screenHeight - context.barHeight,
            screenWidth,
            context.barHeight
        );
        spriteBatch.setColor(1f, 1f, 1f, 1f);

        context.font.draw(spriteBatch, context.level1Title, 390, 730);
        context.smallFont.draw(spriteBatch, "SCORE: " + context.score, 20, 745);
        context.smallFont.draw(spriteBatch, "META: " + context.scoreBasta, 180, 745);
        context.smallFont.draw(spriteBatch, "HEARTS: " + context.hearts, 340, 745);
        context.smallFont.draw(spriteBatch, "Jugador: " + settings.getPlayerName(), 500, 745);
        context.smallFont.draw(spriteBatch, "ESC = volver al menu", 930, 745);
    }
}
