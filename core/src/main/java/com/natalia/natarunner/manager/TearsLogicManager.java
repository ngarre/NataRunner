package com.natalia.natarunner.manager;

import com.natalia.natarunner.screens.context.TearsGameContext;
import com.natalia.natarunner.ui.CountdownTimer;
import com.natalia.natarunner.ui.FlashMessage;
import com.natalia.natarunner.ui.PauseMenu;
import com.natalia.natarunner.ui.ScoreBar;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.natalia.natarunner.NataRunner;
import com.natalia.natarunner.model.entities.drops.RedDrop;
import com.natalia.natarunner.model.entities.drops.WhiteDrop;
import com.natalia.natarunner.model.entities.drops.YellowDrop;
import com.natalia.natarunner.screens.FightScreen;
import com.natalia.natarunner.ui.FloatingText;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.natalia.natarunner.model.entities.projectile.RedDropProjectile;
import com.natalia.natarunner.config.GameConfig;
import com.natalia.natarunner.config.InstructionsConfig;

public class TearsLogicManager {

    private final Game game;
    private final AudioManager audio;
    private final ResourceManager resources;
    private final GameSettings settings;
    private final TearsGameContext ctx;
    private Music music;

    private com.badlogic.gdx.Screen ownerScreen;
    public void setOwnerScreen(com.badlogic.gdx.Screen ownerScreen) {
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
    private static final int RED_PROJECTILE_PENALTY = 25;



    private TearsRenderManager renderManager;
    public void setRenderManager(TearsRenderManager renderManager) {
        this.renderManager = renderManager;
    }

    public TearsLogicManager(Game game,
                             AudioManager audio,
                             ResourceManager resources,
                             GameSettings settings,
                             TearsGameContext ctx) {
        this.game = game;
        this.audio = audio;
        this.resources = resources;
        this.settings = settings;
        this.ctx = ctx;
    }

    public void show() {
        Gdx.input.setInputProcessor(null);

        if (!ctx.initialized) {

            // Ajuste de dificultad
            ctx.scoreBasta = settings.isEasyMode() ? GameConfig.puntosSiFacil : GameConfig.puntosSiDificil;

            ctx.font = resources.hudFont;
            ctx.smallFont = resources.hudSmallFont;

            ctx.pauseMenu = new PauseMenu(resources.hudFont, resources, audio);
            ctx.flashMessage = new FlashMessage(GameConfig.duracionFlashMensaje);
            ctx.scoreBar = new ScoreBar(ctx.scoreBasta, 220f, 20f, 20f);
            ctx.countdownTimer = new CountdownTimer(60f, ctx.font);

            // Música del nivel
            music = resources.tearsMusic;
            music.setLooping(true);
            audio.stopMusic();

            // Player
            ctx.playerTears = new com.natalia.natarunner.model.entities.player.PlayerTears(
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
    }


    public void input() {

        // Atajo para pausar solo música. para durante la presentación poder mostrar bien a Santi
        // los sonidos de las animaciones
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.M)) {
            ctx.pauseOnlyMusic = !ctx.pauseOnlyMusic;
            if (ctx.pauseOnlyMusic) music.pause(); else music.play();
            return;
        }

        // Atajo para saltar al siguiente nivel
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.L)) {
            ctx.score = ctx.scoreBasta;
            return;
        }

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.F)) {
            ctx.freezeMode = !ctx.freezeMode;
            if (ctx.freezeMode) audio.pauseMusic(); else audio.playMusic(music);
            return;
        }

        if (ctx.freezeMode) {
            return;
        }

        com.badlogic.gdx.math.Vector2 mouseHud =
            new com.badlogic.gdx.math.Vector2(Gdx.input.getX(), Gdx.input.getY());
        com.badlogic.gdx.math.Vector2 mouseWorld =
            new com.badlogic.gdx.math.Vector2(Gdx.input.getX(), Gdx.input.getY());

        // ESCAPE
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {

            if (ctx.state == TearsGameContext.GameState.INTRO) {
                game.setScreen(new com.natalia.natarunner.screens.MenuScreen(game, audio, resources, settings));
                return;
            }

            if (ctx.state == TearsGameContext.GameState.PLAYING) {
                ctx.state = TearsGameContext.GameState.PAUSED;
                if (music != null) {
                    music.pause();
                }
                return;
            }
        }

        // INTRO
        if (ctx.state == TearsGameContext.GameState.INTRO) {

            boolean startGame = false;

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
                startGame = true;
            }

            renderManager.getHudViewport().unproject(mouseHud);

            if (Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT)
                && ctx.level1Rectangulo.contains(mouseHud)) {
                startGame = true;
            }

            if (startGame) {
                ctx.state = TearsGameContext.GameState.PLAYING;
                audio.playMusic(music);
            }

            return;
        }

        // GAME OVER
        if (ctx.state == TearsGameContext.GameState.GAMEOVER) {
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
                game.setScreen(new com.natalia.natarunner.screens.MenuScreen(game, audio, resources, settings));
            }
            return;
        }

        // PAUSA
        if (ctx.state == TearsGameContext.GameState.PAUSED) {

            com.natalia.natarunner.ui.PauseMenu.PauseOption option =
                ctx.pauseMenu.input(renderManager.getHudViewport());

            switch (option) {
                case CONTINUE:
                    ctx.state = TearsGameContext.GameState.PLAYING;
                    audio.playMusic(music);
                    break;

                case INSTRUCTIONS:

                    game.setScreen(new com.natalia.natarunner.screens.InstructionsScreen(
                        game,
                        ownerScreen,
                        InstructionsConfig.TEARS.imagePath,
                        InstructionsConfig.TEARS.text,
                        com.badlogic.gdx.utils.Align.left,
                        resources
                    ));
                    break;

                case EXIT:
                    game.setScreen(new com.natalia.natarunner.screens.MenuScreen(game, audio, resources, settings));
                    break;

                default:
                    break;
            }

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
                ctx.state = TearsGameContext.GameState.PLAYING;
                if (music != null) {
                    music.play();
                }
            }

            return;
        }

        // PLAYING
        if (ctx.state == TearsGameContext.GameState.PLAYING) {

            if (settings.isMouseEnabled()) {

                if (Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
                    renderManager.getViewport().unproject(mouseWorld);
                    ctx.playerTears.updateBounds();

                    if (ctx.playerTears.getBounds().contains(mouseWorld)) {
                        ctx.dragging = true;
                    }
                }

                if (!Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
                    ctx.dragging = false;
                }

                if (ctx.dragging) {
                    mouseWorld.set(Gdx.input.getX(), Gdx.input.getY());
                    renderManager.getViewport().unproject(mouseWorld);
                    ctx.playerTears.handleMouseDrag(mouseWorld);
                }
            }
        }
    }

    public void update(float delta) {
        if (ctx.state == TearsGameContext.GameState.PLAYING && !ctx.freezeMode) {
            logic(delta);
        }
    }

    private void logic(float delta) {

        ctx.tiempoTotal += delta;

        playerLogic(delta);
        spawnLogic(delta);
        updateWhiteDrops(delta);
        updateYellowDrops(delta);
        updateRedDrops(delta);
        updateRedProjectiles(delta);
        updateFloatingTexts(delta);

        ctx.flashMessage.update(delta);

        ctx.countdownTimer.update(delta);

        if (ctx.countdownTimer.isFinished() &&
            ctx.state != TearsGameContext.GameState.GAMEOVER) {
            loseHeartOrGameOver(
                false,
                true,
                "¡ TIME OVER !",
                new Color(1f, 0f, 0f, 1f)
            );
        }

        checkLevelEnd();
    }

    private void playerLogic(float delta) {
        ctx.playerTears.handleKeyboard(delta);

        ctx.playerTears.update(
            delta,
            renderManager.getViewport().getWorldWidth(),
            renderManager.getViewport().getWorldHeight(),
            ctx.hudHeight
        );
    }
    private void crearGotaBlanca() {
        float x = MathUtils.random(0f, renderManager.getViewport().getWorldWidth() - 0.5f);
        float y = renderManager.getViewport().getWorldHeight() - ctx.hudHeight;

        ctx.gotasBlancas.add(new WhiteDrop(
            gotaBlanca1,
            gotaBlanca2,
            gotaBlanca3,
            x,
            y
        ));
    }

    private void crearGotaAmarilla() {
        float x = MathUtils.random(0f, renderManager.getViewport().getWorldWidth() - 0.5f);
        float y = renderManager.getViewport().getWorldHeight() - ctx.hudHeight;

        ctx.gotasAmarillas.add(new YellowDrop(
            gotaAmarillaTexture,
            gotaAmarillaSableadaTexture,
            x,
            y
        ));
    }

    private void crearGotaRoja() {
        float x = MathUtils.random(0f, renderManager.getViewport().getWorldWidth() - 0.5f);
        float y = renderManager.getViewport().getWorldHeight() - ctx.hudHeight;

        ctx.gotasRojas.add(new RedDrop(gotaRojaTexture, x, y));
        audio.playSound(gotaRojaSound);
    }

    private void spawnLogic(float delta) {

        // Salida gotas blancas
        ctx.gotaBlancaTimer += delta;
        if (ctx.gotaBlancaTimer > GameConfig.tiempoCadaCuantoGotaBlanca) {
            ctx.gotaBlancaTimer = 0f;
            crearGotaBlanca();
        }

        // Hago aparecer gota amarilla a los 8 segundos
        if (ctx.tiempoTotal > GameConfig.tiempoPrimeraGotaAmarilla) {
            ctx.gotaAmarillaTimer += delta;
            if (ctx.gotaAmarillaTimer > GameConfig.tiempoCadaCuantoAmarilla) {
                ctx.gotaAmarillaTimer = 0f;
                crearGotaAmarilla();
            }
        }

        // Hago aparecer gota roja a los 15 segundos o lo que se programe
        if (ctx.tiempoTotal > GameConfig.tiempoPrimeraGotaRoja) {
            ctx.gotaRojaTimer += delta;
            if (ctx.gotaRojaTimer > GameConfig.tiempoCadaCuantoRoja) {
                ctx.gotaRojaTimer = 0f;
                crearGotaRoja();
            }
        }


    }

    private void updateWhiteDrops(float delta) {

        for (int i = ctx.gotasBlancas.size - 1; i >= 0; i--) {

            WhiteDrop gota = ctx.gotasBlancas.get(i);
            gota.update(delta);

            if (gota.isOutOfScreen()) {
                ctx.gotasBlancas.removeIndex(i);
            }
            else if (ctx.playerTears.getBounds().overlaps(gota.getBounds())) {
                ctx.gotasBlancas.removeIndex(i);
                audio.playSound(gotaBlancaSound);
                ctx.score += WhiteDrop.POINTS;

                ctx.floatingTexts.add(new FloatingText(
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
                if (ctx.score < 0) ctx.score = 0;   // para que no me salgan negativos

                if (ctx.score <= 0) {
                    loseHeartOrGameOver(
                        true,
                        false,
                        "¡ SCORE OUT !",
                        new Color(1f, 0.2f, 0.2f, 1f)
                    );
                } else {
                    ctx.flashMessage.show(
                        new Color(1f, 1f, 0f, 1f),
                        "-" + YellowDrop.PENALTY
                    );

                    ctx.floatingTexts.add(new FloatingText(
                        "-" + YellowDrop.PENALTY,
                        x,
                        y,
                        0.6f,
                        Color.YELLOW
                    ));
                }
            }
            else if (ctx.playerTears.getBounds().overlaps(gota.getBounds())) {

                if (gota.isLethal()) {
                    ctx.gotasAmarillas.removeIndex(i);

                    loseHeartOrGameOver(
                        false,
                        false,
                        "¡ YOU ARE STABBED !",
                        new Color(1f, 0.4f, 0.1f, 1f)
                    );
                } else {
                    ctx.gotasAmarillas.removeIndex(i);
                    audio.playSound(gotaAmarillaSound);
                    ctx.score += YellowDrop.POINTS;

                    ctx.floatingTexts.add(new FloatingText(
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

        boolean anyRedDropShooting = false;

        for (int k = 0; k < ctx.gotasRojas.size; k++) {
            if (ctx.gotasRojas.get(k).isShooting()) {
                anyRedDropShooting = true;
                break;
            }
        }

        for (int i = ctx.gotasRojas.size - 1; i >= 0; i--) {
            RedDrop gota = ctx.gotasRojas.get(i);

            boolean canStartShooting = !anyRedDropShooting || gota.isShooting();

            gota.update(delta);

            gota.updateShooting(
                delta,
                ctx.redProjectiles,
                resources.gotaBlancaTexture,
                resources.shootSound,
                audio,
                canStartShooting
            );

            if (!gota.isShooting()) {
                gota.chase(ctx.playerTears.getCenterX(), delta);
            }

            gota.updateBounds();

            if (gota.getSprite().getY() < -gota.getSprite().getHeight()) {
                ctx.gotasRojas.removeIndex(i);
            }
            else if (
                gota.getSprite().getY() < renderManager.getViewport().getWorldHeight() - ctx.hudHeight - 0.35f
                    && ctx.playerTears.getBounds().overlaps(gota.getReducedBounds())
            ) {
                ctx.gotasRojas.removeIndex(i);

                loseHeartOrGameOver(
                    false,
                    false,
                    "¡ TOUCHED by a Red Tear !",
                    new Color(1f, 0f, 0f, 1f)
                );
            }
        }
    }

    private void updateRedProjectiles(float delta) {

        float worldWidth = renderManager.getViewport().getWorldWidth();
        float worldHeight = renderManager.getViewport().getWorldHeight();

        for (int i = ctx.redProjectiles.size - 1; i >= 0; i--) {
            RedDropProjectile p = ctx.redProjectiles.get(i);
            p.update(delta);

            // Fuera del mundo
            if (p.isOutOfWorld(worldWidth, worldHeight)) {
                ctx.redProjectiles.removeIndex(i);
                continue;
            }

            // Impacto con player -> penalización de 25
            if (ctx.playerTears.getBounds().overlaps(p.getBounds())) {
                float x = p.getX();
                float y = p.getY();

                ctx.redProjectiles.removeIndex(i);
                ctx.score -= RED_PROJECTILE_PENALTY;
                if (ctx.score < 0) ctx.score = 0;   // para que no me salgan negativos

                if (ctx.score <= 0) {
                    loseHeartOrGameOver(
                        true,
                        false,
                        "¡ SCORE OUT !",
                        new Color(1f, 0.2f, 0.2f, 1f)
                    );
                } else {
                    ctx.flashMessage.show(
                        new Color(1f, 0.3f, 0.3f, 1f),
                        "-" + RED_PROJECTILE_PENALTY
                    );

                    ctx.floatingTexts.add(new FloatingText(
                        "-" + RED_PROJECTILE_PENALTY,
                        x,
                        y,
                        0.6f,
                        Color.RED
                    ));
                }
                continue;
            }

            // Impacto con gotas blancas -> se borran
            boolean removedProjectile = false;

            for (int j = ctx.gotasBlancas.size - 1; j >= 0; j--) {
                WhiteDrop blanca = ctx.gotasBlancas.get(j);

                if (blanca.getBounds().overlaps(p.getBounds())) {
                    ctx.gotasBlancas.removeIndex(j);
                    ctx.redProjectiles.removeIndex(i);
                    removedProjectile = true;
                    break;
                }
            }

            if (removedProjectile) continue;

            // Impacto con gotas amarillas -> se borran, estén o no letales
            for (int j = ctx.gotasAmarillas.size - 1; j >= 0; j--) {
                YellowDrop amarilla = ctx.gotasAmarillas.get(j);

                if (amarilla.getBounds().overlaps(p.getBounds())) {
                    ctx.gotasAmarillas.removeIndex(j);
                    ctx.redProjectiles.removeIndex(i);
                    removedProjectile = true;
                    break;
                }
            }

            if (removedProjectile) continue;
        }
    }

    private void updateFloatingTexts(float delta) {

        for (int i = ctx.floatingTexts.size - 1; i >= 0; i--) {
            FloatingText ft = ctx.floatingTexts.get(i);
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

            if (music != null) {
                music.stop();
            }

            NataRunner nataRunner = (NataRunner) game;
            nataRunner.session.setTearsScore(ctx.countdownTimer.getRemainingSeconds());

            game.setScreen(new FightScreen(game, audio, resources, settings));
        }
    }

    // Método para Game Over directo
    private void triggerGameOver(String message, Color color) {
        if (ctx.state == TearsGameContext.GameState.GAMEOVER) return;

        ctx.flashMessage.show(color, message);
        ctx.state = TearsGameContext.GameState.GAMEOVER;

        if (music != null) {
            music.pause();
        }
    }

    // Método general para perder un corazón
    private void loseHeartOrGameOver(boolean resetScore, boolean resetTimer, String message, Color color) {
        if (ctx.state == TearsGameContext.GameState.GAMEOVER) return;

        // Si queda solo un corazón, esta condición ya mata
        if (ctx.hearts <= 1) {
            triggerGameOver(message, color);
            return;
        }

        // Si quedan más, pierde un corazón
        ctx.hearts--;

        // SONIDO DE DAÑO / VIDA PERDIDA
        audio.playSound(resources.sonidoMortal);

        if (resetScore) {
            ctx.score = 100;
        }

        if (resetTimer && ctx.countdownTimer != null) {
            ctx.countdownTimer.reset(60f);
        }

        ctx.flashMessage.show(color, message);

    }


}
