package com.natalia.natarunner.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;

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

    private Screen ownerScreen;
    public void setOwnerScreen(Screen ownerScreen) {
        this.ownerScreen = ownerScreen;
    }

    private Texture gotaBlanca1;
    private Texture gotaBlanca2;
    private Texture gotaBlanca3;

    private Texture gotaAmarillaTexture;
    private Texture gotaAmarillaSableadaTexture;
    private Texture gotaRojaTexture;

    private Sound gotaBlancaSound;
    private Sound gotaAmarillaSound;
    private Sound gotaAmarillaFallSound;
    private Sound sonidoSable;
    private Sound gotaRojaSound;

    private TearsRenderManager renderManager;
    public void setRenderManager(TearsRenderManager renderManager) {
        this.renderManager = renderManager;
    }

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
        Gdx.input.setInputProcessor(null);

        if (!ctx.initialized) {

            ctx.scoreBasta = settings.isEasyMode()
                ? GameConfig.puntosSiFacil
                : GameConfig.puntosSiDificil;

            ctx.font = resources.hudFont;
            ctx.smallFont = resources.hudSmallFont;

            ctx.flashMessage = new com.natalia.natarunner.ui.FlashMessage(1.5f);
            ctx.scoreBar = new com.natalia.natarunner.ui.ScoreBar(ctx.scoreBasta, 220f, 20f, 20f);
            ctx.countdownTimer = new com.natalia.natarunner.ui.CountdownTimer(60f, ctx.font);

            music = resources.tearsMusic;
            music.setLooping(true);
            audio.stopMusic();

            ctx.playerTears = new PlayerTears(
                resources.manosTexture,
                resources.manosCerradasTexture,
                12.28f / 2f - 0.5f,
                0.2f
            );

            gotaBlanca1 = resources.gotaBlanca1;
            gotaBlanca2 = resources.gotaBlanca2;
            gotaBlanca3 = resources.gotaBlanca3;

            gotaAmarillaTexture = resources.gotaAmarillaTexture;
            gotaAmarillaSableadaTexture = resources.gotaAmarillaSableadaTexture;
            gotaRojaTexture = resources.gotaRojaTexture;

            gotaBlancaSound = resources.gotaBlancaSound;
            gotaAmarillaSound = resources.gotaAmarillaSound;
            gotaAmarillaFallSound = resources.gotaAmarillaFallSound;
            sonidoSable = resources.sonidoSable;
            gotaRojaSound = resources.gotaRojaSound;

            ctx.initialized = true;
        }

        ctx.score = 100;
        ctx.hearts = GameConfig.cuantosCorazones;
        ctx.gotaBlancaTimer = 0f;
        ctx.gotaAmarillaTimer = 0f;
        ctx.gotaRojaTimer = 0f;
        ctx.tiempoTotal = 0f;
        ctx.state = TearsGameContext.GameState.INTRO;

        ctx.gotasBlancas.clear();
        ctx.gotasAmarillas.clear();
        ctx.gotasRojas.clear();
        ctx.floatingTexts.clear();

        if (ctx.countdownTimer != null) {
            ctx.countdownTimer.reset(60f);
        }

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

            if (ctx.state == TearsGameContext.GameState.GAMEOVER) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    game.setScreen(new MenuScreen(
                        game,
                        ((NataRunner) game).audioManager,
                        ((NataRunner) game).resources,
                        ((NataRunner) game).settings
                    ));
                }
                return;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                startGame = true;
            }

            mouseHud.set(Gdx.input.getX(), Gdx.input.getY());
            renderManager.getHudViewport().unproject(mouseHud);

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)
                && ctx.level1Rectangulo.contains(mouseHud)) {
                startGame = true;
            }

            if (startGame) {
                ctx.state = TearsGameContext.GameState.PLAYING;
                audio.playMusic(music);
            }

            return;
        }

        ctx.playerTears.handleKeyboard(delta);

        if (settings.isMouseEnabled() && Gdx.input.isTouched()) {
            Vector2 mouseWorld = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            renderManager.getViewport().unproject(mouseWorld);
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

        updateFloatingTexts(delta);

        checkLevelEnd();

        ((NataRunner) game).session.setTearsScore(ctx.score);
    }

    private void updateFloatingTexts(float delta) {
        for (int i = ctx.floatingTexts.size - 1; i >= 0; i--) {
            com.natalia.natarunner.ui.FloatingText ft = ctx.floatingTexts.get(i);
            ft.timeLeft -= delta;
            ft.pos.y += 0.8f * delta;
            ft.alpha = MathUtils.clamp(ft.timeLeft / 0.6f, 0f, 1f);

            if (ft.timeLeft <= 0f) {
                ctx.floatingTexts.removeIndex(i);
            }
        }
    }

    private void checkLevelEnd() {
        if (ctx.score >= ctx.scoreBasta) {
            audio.stopMusic();
            ((NataRunner) game).session.setTearsScore(ctx.countdownTimer.getRemainingSeconds());
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
                audio.playSound(gotaBlancaSound);
                ctx.score += WhiteDrop.POINTS;

                ctx.floatingTexts.add(new com.natalia.natarunner.ui.FloatingText(
                    "+" + WhiteDrop.POINTS,
                    gota.getSprite().getX(),
                    gota.getSprite().getY(),
                    0.6f,
                    Color.WHITE
                ));
            }
        }
    }

    private void updateYellowDrops(float delta) {
        for (int i = ctx.gotasAmarillas.size - 1; i >= 0; i--) {
            YellowDrop gota = ctx.gotasAmarillas.get(i);
            gota.update(delta);

            if (gota.consumeTransformEvent()) {
                audio.playSound(sonidoSable);
            }

            if (gota.isOutOfScreen()) {
                float x = gota.getSprite().getX();
                float y = Math.max(gota.getSprite().getY(), 0.2f);

                ctx.gotasAmarillas.removeIndex(i);
                audio.playSound(gotaAmarillaFallSound);
                ctx.score -= YellowDrop.PENALTY;
                if (ctx.score < 0) ctx.score = 0;

                ctx.floatingTexts.add(new com.natalia.natarunner.ui.FloatingText(
                    "-" + YellowDrop.PENALTY,
                    x,
                    y,
                    0.6f,
                    Color.YELLOW
                ));

            } else if (ctx.playerTears.getBounds().overlaps(gota.getBounds())) {
                ctx.gotasAmarillas.removeIndex(i);

                if (gota.isLethal()) {
                    loseHeartOrGameOver(
                        "¡ YOU ARE STABBED !",
                        new Color(1f, 0.4f, 0.1f, 1f)
                    );
                } else {
                    audio.playSound(gotaAmarillaSound);
                    ctx.score += YellowDrop.POINTS;

                    ctx.floatingTexts.add(new com.natalia.natarunner.ui.FloatingText(
                        "+" + YellowDrop.POINTS,
                        gota.getSprite().getX(),
                        gota.getSprite().getY(),
                        0.6f,
                        Color.YELLOW
                    ));
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
                loseHeartOrGameOver(
                    "¡ TOUCHED by a Red Tear !",
                    new Color(1f, 0f, 0f, 1f)
                );
            }
        }
    }

    public void hide() {
        audio.stopMusic();
    }

    private void triggerGameOver(String message, Color color) {
        if (ctx.state == TearsGameContext.GameState.GAMEOVER) return;

        if (ctx.flashMessage != null) {
            ctx.flashMessage.show(color, message);
        }

        ctx.state = TearsGameContext.GameState.GAMEOVER;

        if (music != null) {
            music.pause();
        }
    }

    private void loseHeartOrGameOver(String message, Color color) {
        if (ctx.state == TearsGameContext.GameState.GAMEOVER) return;

        if (ctx.hearts <= 1) {
            audio.playSound(resources.sonidoMortal);
            triggerGameOver(message, color);
            return;
        }

        ctx.hearts--;
        audio.playSound(resources.sonidoMortal);

        if (ctx.flashMessage != null) {
            ctx.flashMessage.show(color, message);
        }
    }
}
