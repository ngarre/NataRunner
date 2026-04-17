package com.natalia.natarunner.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.natalia.natarunner.NataRunner;
import com.natalia.natarunner.config.GameConfig;
import com.natalia.natarunner.model.entities.drops.RedDrop;
import com.natalia.natarunner.model.entities.drops.WhiteDrop;
import com.natalia.natarunner.model.entities.drops.YellowDrop;
import com.natalia.natarunner.model.entities.player.PlayerTears;
import com.natalia.natarunner.screens.FightScreen;
import com.natalia.natarunner.screens.MenuScreen;
import com.natalia.natarunner.screens.context.TearsGameContext;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;

public class TearsLogicManager {

    private final Game game;
    private final AudioManager audio;
    private final ResourceManager resources;
    private final GameSettings settings;
    private final TearsGameContext ctx;
    private final FitViewport viewport;
    private final FitViewport hudViewport;
    private final Vector2 mouseHud = new Vector2();

    private Music music;

    public TearsLogicManager(
        Game game,
        AudioManager audio,
        ResourceManager resources,
        GameSettings settings,
        TearsGameContext ctx,
        FitViewport viewport,
        FitViewport hudViewport
    ) {
        this.game = game;
        this.audio = audio;
        this.resources = resources;
        this.settings = settings;
        this.ctx = ctx;
        this.viewport = viewport;
        this.hudViewport = hudViewport;
    }

    public void show() {
        ctx.playerTears = new PlayerTears(resources.manosTexture, resources.manosCerradasTexture, 5.5f, 1.2f);

        ctx.score = 100;
        ctx.hearts = GameConfig.cuantosCorazones;
        ctx.gotaBlancaTimer = 0f;
        ctx.gotaAmarillaTimer = 0f;
        ctx.gotaRojaTimer = 0f;
        ctx.tiempoTotal = 0f;
        ctx.state = TearsGameContext.GameState.INTRO;

        ctx.scoreBasta = settings.isEasyMode()
            ? GameConfig.puntosSiFacil
            : GameConfig.puntosSiDificil;

        ctx.gotasBlancas.clear();
        ctx.gotasAmarillas.clear();
        ctx.gotasRojas.clear();
        ctx.floatingTexts.clear();

        ctx.scoreBar = new com.natalia.natarunner.ui.ScoreBar(
            ctx.scoreBasta,
            225f,
            18f,
            18f
        );

        ctx.countdownTimer = new com.natalia.natarunner.ui.CountdownTimer(
            60f,
            ctx.font
        );

        ctx.flashMessage = new com.natalia.natarunner.ui.FlashMessage(1.5f);

        music = resources.tearsMusic;
        music.setLooping(true);
        audio.stopMusic();
        audio.playMusic(music);

        ((NataRunner) game).session.reset();
        ((NataRunner) game).session.setTearsScore(ctx.score);
    }

    public void render(float delta) {
        input(delta);

        if (ctx.state == TearsGameContext.GameState.PLAYING) {
            update(delta);
        }
    }

    private void input(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            audio.stopMusic();
            game.setScreen(new MenuScreen(
                game,
                ((NataRunner) game).audioManager,
                ((NataRunner) game).resources,
                ((NataRunner) game).settings
            ));
            return;
        }

        if (ctx.state == TearsGameContext.GameState.INTRO) {
            boolean startGame = false;

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                startGame = true;
            }

            mouseHud.set(Gdx.input.getX(), Gdx.input.getY());
            hudViewport.unproject(mouseHud);

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)
                && ctx.level1Rectangulo.contains(mouseHud)) {
                startGame = true;
            }

            if (startGame) {
                ctx.state = TearsGameContext.GameState.PLAYING;
            }

            return;
        }

        ctx.playerTears.handleKeyboard(delta);

        if (settings.isMouseEnabled() && Gdx.input.isTouched()) {
            Vector2 mouseWorld = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            ctx.playerTears.handleMouseDrag(mouseWorld);
        }
    }

    private void update(float delta) {
        ctx.countdownTimer.update(delta);
        ctx.flashMessage.update(delta);

        ctx.tiempoTotal += delta;

        ctx.playerTears.update(
            delta,
            viewport.getWorldWidth(),
            viewport.getWorldHeight(),
            ctx.hudHeight
        );

        spawnWhiteDrops(delta);
        spawnYellowDrops(delta);
        spawnRedDrops(delta);

        updateWhiteDrops(delta);
        updateYellowDrops(delta);
        updateRedDrops(delta);

        for (int i = ctx.floatingTexts.size - 1; i >= 0; i--) {
            com.natalia.natarunner.ui.FloatingText ft = ctx.floatingTexts.get(i);
            ft.timeLeft -= delta;
            ft.pos.y += 25f * delta;
            ft.alpha = Math.max(0f, ft.timeLeft);

            if (ft.timeLeft <= 0f) {
                ctx.floatingTexts.removeIndex(i);
            }
        }

        checkLevelEnd();

        ((NataRunner) game).session.setTearsScore(ctx.score);
    }

    private void checkLevelEnd() {
        if (ctx.score >= ctx.scoreBasta) {
            ((NataRunner) game).session.setTearsScore(ctx.score);
            audio.stopMusic();
            game.setScreen(new FightScreen(game));
        }
    }

    private void spawnWhiteDrops(float delta) {
        ctx.gotaBlancaTimer += delta;

        if (ctx.gotaBlancaTimer > GameConfig.tiempoCadaCuantoGotaBlanca) {
            ctx.gotaBlancaTimer = 0f;

            float x = MathUtils.random(0f, viewport.getWorldWidth() - 0.5f);
            float y = viewport.getWorldHeight() - ctx.hudHeight;

            ctx.gotasBlancas.add(new WhiteDrop(
                resources.gotaBlanca1,
                resources.gotaBlanca2,
                resources.gotaBlanca3,
                x,
                y
            ));
        }
    }

    private void spawnYellowDrops(float delta) {
        if (ctx.tiempoTotal <= GameConfig.tiempoPrimeraGotaAmarilla) {
            return;
        }

        ctx.gotaAmarillaTimer += delta;

        if (ctx.gotaAmarillaTimer > GameConfig.tiempoCadaCuantoAmarilla) {
            ctx.gotaAmarillaTimer = 0f;

            float x = MathUtils.random(0f, viewport.getWorldWidth() - 0.5f);
            float y = viewport.getWorldHeight() - ctx.hudHeight;

            ctx.gotasAmarillas.add(
                new YellowDrop(resources.gotaAmarillaTexture, resources.gotaAmarillaSableadaTexture, x, y)
            );
        }
    }

    private void spawnRedDrops(float delta) {
        if (ctx.tiempoTotal <= GameConfig.tiempoPrimeraGotaRoja) {
            return;
        }

        ctx.gotaRojaTimer += delta;

        if (ctx.gotaRojaTimer > GameConfig.tiempoCadaCuantoRoja) {
            ctx.gotaRojaTimer = 0f;

            float x = MathUtils.random(0f, viewport.getWorldWidth() - 0.8f);
            float y = viewport.getWorldHeight() - ctx.hudHeight;

            ctx.gotasRojas.add(new RedDrop(resources.gotaRojaTexture, x, y));
            audio.playSound(resources.gotaRojaSound);
        }
    }

    private void updateWhiteDrops(float delta) {
        for (int i = ctx.gotasBlancas.size - 1; i >= 0; i--) {
            WhiteDrop gota = ctx.gotasBlancas.get(i);
            gota.update(delta);

            if (gota.isOutOfScreen()) {
                ctx.gotasBlancas.removeIndex(i);
            } else if (ctx.playerTears.getBounds().overlaps(gota.getBounds())) {
                ctx.gotasBlancas.removeIndex(i);
                ctx.score += WhiteDrop.POINTS;
                ctx.floatingTexts.add(new com.natalia.natarunner.ui.FloatingText(
                    "+10",
                    gota.getBounds().x,
                    gota.getBounds().y,
                    1f,
                    com.badlogic.gdx.graphics.Color.WHITE
                ));
                audio.playSound(resources.gotaBlancaSound);
            }
        }
    }

    private void updateYellowDrops(float delta) {
        for (int i = ctx.gotasAmarillas.size - 1; i >= 0; i--) {
            YellowDrop gota = ctx.gotasAmarillas.get(i);
            gota.update(delta);

            if (gota.consumeTransformEvent()) {
                audio.playSound(resources.sonidoSable);
            }

            if (gota.isOutOfScreen()) {
                ctx.gotasAmarillas.removeIndex(i);
                ctx.score -= YellowDrop.PENALTY;
                ctx.floatingTexts.add(new com.natalia.natarunner.ui.FloatingText(
                    "-25",
                    gota.getBounds().x,
                    gota.getBounds().y,
                    1f,
                    com.badlogic.gdx.graphics.Color.ORANGE
                ));

                if (ctx.score < 0) ctx.score = 0;
                audio.playSound(resources.gotaAmarillaFallSound);

            } else if (ctx.playerTears.getBounds().overlaps(gota.getBounds())) {
                ctx.gotasAmarillas.removeIndex(i);

                if (gota.isLethal()) {
                    ctx.hearts--;
                    if (ctx.hearts < 0) ctx.hearts = 0;
                } else {
                    ctx.score += YellowDrop.POINTS;
                    ctx.floatingTexts.add(new com.natalia.natarunner.ui.FloatingText(
                        "+15",
                        gota.getBounds().x,
                        gota.getBounds().y,
                        1f,
                        com.badlogic.gdx.graphics.Color.YELLOW
                    ));
                    audio.playSound(resources.gotaAmarillaSound);
                }
            }
        }
    }

    private void updateRedDrops(float delta) {
        for (int i = ctx.gotasRojas.size - 1; i >= 0; i--) {
            RedDrop gota = ctx.gotasRojas.get(i);

            gota.update(delta);
            gota.chase(ctx.playerTears.getCenterX(), delta);

            if (gota.isOutOfScreen()) {
                ctx.gotasRojas.removeIndex(i);
            } else if (ctx.playerTears.getBounds().overlaps(gota.getReducedBounds())) {
                ctx.gotasRojas.removeIndex(i);
                ctx.hearts--;
                if (ctx.hearts < 0) ctx.hearts = 0;
                audio.playSound(resources.sonidoMortal);
            }
        }
    }

    public void hide() {
        audio.stopMusic();
    }
}
