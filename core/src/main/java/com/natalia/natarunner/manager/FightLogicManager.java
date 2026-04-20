package com.natalia.natarunner.manager;


import com.natalia.natarunner.config.GameConfig;
import com.natalia.natarunner.model.entities.npc.NpcBoss;
import com.natalia.natarunner.model.entities.npc.NpcChicas;
import com.natalia.natarunner.model.entities.player.PlayerBladecar;
import com.natalia.natarunner.screens.context.FightGameContext;
import com.natalia.natarunner.ui.CountdownTimer;
import com.natalia.natarunner.ui.FlashMessage;
import com.natalia.natarunner.ui.PauseMenu;
import com.natalia.natarunner.ui.ScoreBar;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.natalia.natarunner.NataRunner;
import com.natalia.natarunner.model.entities.projectile.BulletBoss;
import com.natalia.natarunner.model.entities.projectile.BulletChicas;
import com.natalia.natarunner.ui.FloatingText;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.natalia.natarunner.config.InstructionsConfig;

public class FightLogicManager {

    private final Game game;
    private final AudioManager audio;
    private final ResourceManager resources;
    private final GameSettings settings;
    private final FightGameContext ctx;

    private FightRenderManager renderManager;
    private Screen ownerScreen;

    private Music music;

    private Texture bulletTexture;
    private Texture bulletBossTexture;
    private Sound chillidoSound;

    public FightLogicManager(Game game,
                             AudioManager audio,
                             ResourceManager resources,
                             GameSettings settings,
                             FightGameContext ctx) {
        this.game = game;
        this.audio = audio;
        this.resources = resources;
        this.settings = settings;
        this.ctx = ctx;
    }

    public void setRenderManager(FightRenderManager renderManager) {
        this.renderManager = renderManager;
    }

    public void setOwnerScreen(Screen ownerScreen) {
        this.ownerScreen = ownerScreen;
    }

    public void show() {
        Gdx.input.setInputProcessor(null);

        if (!ctx.initialized) {

            // Ajuste de dificultad
            ctx.scoreBasta = settings.isEasyMode() ? GameConfig.puntosSiFacil : GameConfig.puntosSiDificil;

            //ctx.hearts = GameConfig.cuantosCorazones;

            // HUD
            ctx.font = resources.hudFont;
            ctx.smallFont = resources.hudSmallFont;

            ctx.pauseMenu = new PauseMenu(resources.hudFont, resources, audio);
            ctx.flashMessage = new FlashMessage(GameConfig.duracionFlashMensaje);
            ctx.scoreBar = new ScoreBar(
                ctx.scoreBasta,
                220f,
                20f,
                20f
            );
            ctx.countdownTimer = new CountdownTimer(60f, ctx.font);

            // Música
            music = resources.fightMusic;
            music.setLooping(true);
            music.stop();

            // Sonidos y texturas de disparo
            bulletTexture = resources.bulletPlayer;
            bulletBossTexture = resources.bulletBoss;
            chillidoSound = resources.chillidoChica;

            // Player
            ctx.player = new PlayerBladecar(
                12.28f,
                7.68f,
                1f,
                audio,
                resources
            );

            // NPCs laterales
            ctx.sideNPCs.add(new NpcChicas(
                resources.npcChica1,
                resources.npcChica2,
                resources.npcChica3,
                resources.npcChica4,
                resources.npcChicaTocada,
                resources.risaChica,
                12.28f,
                7.68f
            ));

            ctx.sideNPCs.add(new NpcChicas(
                resources.npcHolandesa1,
                resources.npcHolandesa2,
                resources.npcHolandesa3,
                resources.npcHolandesa4,
                resources.npcHolandesaTocada,
                resources.risaHolandesa,
                12.28f,
                7.68f
            ));

            // Boss
            ctx.boss = new NpcBoss(12.28f, 7.68f, audio, resources);

            ctx.initialized = true;
        }
    }

    public void input() {

        ctx.mouseHud.set(Gdx.input.getX(), Gdx.input.getY());

        // Atajo para pausar solo música. para durante la presentación poder mostrar bien a Santi
        // los sonidos de las animaciones
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.M)) {
            ctx.pauseOnlyMusic = !ctx.pauseOnlyMusic;
            if (ctx.pauseOnlyMusic) music.pause(); else music.play();
            return;
        }

        // Atajo para forzar final
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.L)) {
            ctx.score = ctx.scoreBasta;
            return;
        }

        // Freeze mode
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.F)) {
            ctx.freezeMode = !ctx.freezeMode;
            if (ctx.freezeMode) audio.pauseMusic(); else audio.playMusic(music);
            return;
        }

        if (ctx.freezeMode) {
            return;
        }

        // INTRO
        if (ctx.state == FightGameContext.GameState.INTRO) {
            boolean startGame = false;

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
                startGame = true;
            }

            renderManager.getHudViewport().unproject(ctx.mouseHud);

            if (Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT)
                && ctx.level2Rectangulo.contains(ctx.mouseHud)) {
                startGame = true;
            }

            if (startGame) {
                ctx.state = FightGameContext.GameState.PLAYING;
                audio.playMusic(music);
            }

            return;
        }

        // GAME OVER
        if (ctx.state == FightGameContext.GameState.GAMEOVER) {
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
                game.setScreen(new com.natalia.natarunner.screens.MenuScreen(game, audio, resources, settings));
            }
            return;
        }

        // YOU WIN
        if (ctx.state == FightGameContext.GameState.YOUWIN) {
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)
                || Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT))
            {
                game.setScreen(new com.natalia.natarunner.screens.ScoresScreen(game, audio, resources, settings));
            }
            return;
        }

        // PLAYING
        if (ctx.state == FightGameContext.GameState.PLAYING) {

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
                ctx.state = FightGameContext.GameState.PAUSED;
                if (music != null) {
                    music.pause();
                }
                return;
            }

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)
                && ctx.player.hasArrived()) {
                ctx.bullets.add(ctx.player.shoot(bulletTexture));
            }

            if (settings.isMouseEnabled()) {

                if (Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
                    ctx.mouseWorld.set(Gdx.input.getX(), Gdx.input.getY());
                    renderManager.getViewport().unproject(ctx.mouseWorld);

                    if (ctx.player.getRect().contains(ctx.mouseWorld)) {
                        ctx.dragging = true;
                    }
                }

                if (!Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
                    ctx.dragging = false;
                }

                if (ctx.dragging) {
                    ctx.mouseWorld.set(Gdx.input.getX(), Gdx.input.getY());
                    renderManager.getViewport().unproject(ctx.mouseWorld);
                    ctx.player.handleMouseDrag(ctx.mouseWorld);
                }

            } else {
                ctx.dragging = false;
            }

            return;
        }

        // PAUSED
        if (ctx.state == FightGameContext.GameState.PAUSED) {

            com.natalia.natarunner.ui.PauseMenu.PauseOption option =
                ctx.pauseMenu.input(renderManager.getHudViewport());

            switch (option) {
                case CONTINUE:
                    ctx.state = FightGameContext.GameState.PLAYING;
                    audio.playMusic(music);
                    break;

                case INSTRUCTIONS:

                    game.setScreen(new com.natalia.natarunner.screens.InstructionsScreen(
                        game,
                        ownerScreen,
                        InstructionsConfig.FIGHT.imagePath,
                        InstructionsConfig.FIGHT.text,
                        com.badlogic.gdx.utils.Align.right,
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
                ctx.state = FightGameContext.GameState.PLAYING;
                audio.playMusic(music);
            }
        }
    }

    public void update(float delta) {
        if (ctx.state == FightGameContext.GameState.PLAYING && !ctx.freezeMode) {
            logic(delta);
        }
    }

    private void logic(float delta) {

        ctx.player.update(delta);

        if (ctx.player.hasArrived()) {
            float hudHeightWorld =
                (ctx.barHeight / renderManager.getHudViewport().getWorldHeight())
                    * renderManager.getViewport().getWorldHeight();

            ctx.player.move(
                delta,
                renderManager.getViewport().getWorldWidth(),
                renderManager.getViewport().getWorldHeight(),
                hudHeightWorld
            );
        }

        // =====================
        // BALAS DEL PLAYER
        // =====================
        for (int i = ctx.bullets.size - 1; i >= 0; i--) {
            BulletChicas b = ctx.bullets.get(i);
            b.update(delta);

            if (b.isOutOfWorld(renderManager.getViewport().getWorldWidth())) {
                ctx.bullets.removeIndex(i);
            }
        }

        // =====================
        // BALAZOS AL BOSS
        // =====================
        if (ctx.boss.isVisible()) {
            for (int i = ctx.bullets.size - 1; i >= 0; i--) {
                BulletChicas b = ctx.bullets.get(i);

                if (b.getRect().overlaps(ctx.boss.getRect())) {
                    ctx.bullets.removeIndex(i);
                    ctx.boss.takeHit();

                    ctx.score += ctx.boss.getPoints();
                    if (ctx.score > ctx.scoreBasta) {
                        ctx.score = ctx.scoreBasta;
                    }

                    ctx.floatingTexts.add(new FloatingText(
                        "+" + ctx.boss.getPoints(),
                        ctx.boss.getRect().x + ctx.boss.getRect().width / 2f,
                        ctx.boss.getRect().y + ctx.boss.getRect().height / 2f,
                        0.6f,
                        Color.YELLOW
                    ));
                }
            }
        }

        // ========================================
        // NPCs LATERALES
        // ========================================
        for (NpcChicas npc : ctx.sideNPCs) {
            npc.update(delta, ctx.player, audio);

            if (npc.isReadyToRespawn()) {
                npc.resetPosition(
                    renderManager.getViewport().getWorldWidth(),
                    renderManager.getViewport().getWorldHeight(),
                    ctx.hudHeight
                );
                continue;
            }

            // COLISIÓN NPC - PLAYER
            if (!npc.isDying() && npc.getRect().overlaps(ctx.player.getRect())) {
                ctx.score -= PlayerBladecar.PENALTY;

                ctx.flashMessage.show(
                    new Color(1f, 1f, 0f, 1f),
                    "-" + PlayerBladecar.PENALTY
                );

                ctx.player.hit();

                npc.resetPosition(
                    renderManager.getViewport().getWorldWidth(),
                    renderManager.getViewport().getWorldHeight(),
                    ctx.hudHeight
                );

                verSiGameOverCeroPuntos();
            }

            // BALAZOS A LAS CHICAS
            for (int i = ctx.bullets.size - 1; i >= 0; i--) {
                if (!npc.isDying() && npc.getRect().overlaps(ctx.bullets.get(i).getRect())) {
                    ctx.score += npc.getPoints();

                    audio.playSound(chillidoSound, 1f);

                    if (ctx.score > ctx.scoreBasta) {
                        ctx.score = ctx.scoreBasta;
                    }

                    ctx.floatingTexts.add(new FloatingText(
                        "+" + npc.getPoints(),
                        npc.getX() + npc.getRect().width / 2f,
                        npc.getY() + npc.getRect().height / 2f,
                        0.6f,
                        Color.WHITE
                    ));

                    ctx.bullets.removeIndex(i);
                    npc.takeHit();

                    ctx.npcKilled++;
                    controlSalidaDelBoss();

                    break;
                }
            }

            if (npc.isOutOfWorld(
                renderManager.getViewport().getWorldWidth(),
                renderManager.getViewport().getWorldHeight()
            )) {
                npc.resetPosition(
                    renderManager.getViewport().getWorldWidth(),
                    renderManager.getViewport().getWorldHeight(),
                    ctx.hudHeight
                );
            }
        }

        // =====================
        // RETRASO DE SALIDA DEL BOSS
        // =====================
        if (ctx.bossPending) {
            ctx.bossDelayTimer += delta;

            if (ctx.bossDelayTimer >= ctx.bossDelay) {
                ctx.boss.appearLeft();
                ctx.bossPending = false;
            }
        }

        // =====================
        // BOSS UPDATE + DISPARO
        // =====================

        if (ctx.boss.isVisible()) {
            ctx.boss.update(delta);

            if (ctx.boss.canShoot(delta)) {
                ctx.bossBullets.add(
                    ctx.boss.shoot(ctx.player, bulletBossTexture)
                );
            }
        }

        // =====================
        // BALAS DEL BOSS
        // =====================
        for (int i = ctx.bossBullets.size - 1; i >= 0; i--) {
            BulletBoss b = ctx.bossBullets.get(i);
            b.update(delta);

            if (b.isOutOfWorld(
                renderManager.getViewport().getWorldWidth(),
                renderManager.getViewport().getWorldHeight()
            )) {
                ctx.bossBullets.removeIndex(i);
                continue;
            }

            if (b.getRect().overlaps(ctx.player.getRect())) {
                ctx.score -= PlayerBladecar.PENALTY;
                ctx.flashMessage.show(new Color(1f, 0.5f, 0f, 1f), "-25");
                ctx.player.hit();
                ctx.bossBullets.removeIndex(i);

                verSiGameOverCeroPuntos();
            }
        }

        // =====================
        // COLISIÓN BOSS - PLAYER
        // =====================
        if (ctx.boss.isVisible()) {
            if (!ctx.player.isInvulnerable()
                && ctx.boss.getRect().overlaps(ctx.player.getRect())) {

                ctx.score -= PlayerBladecar.PENALTY;

                ctx.flashMessage.show(
                    new Color(1f, 0.5f, 0f, 1f),
                    "-" + PlayerBladecar.PENALTY
                );

                ctx.player.hit();
                ctx.player.startInvulnerability();

                float hudHeightWorld =
                    (ctx.barHeight / renderManager.getHudViewport().getWorldHeight())
                        * renderManager.getViewport().getWorldHeight();

                // El rebote si toco al boss
                ctx.player.knockBackFrom(
                    ctx.boss.getRect(),
                    2.0f,
                    renderManager.getViewport().getWorldWidth(),
                    renderManager.getViewport().getWorldHeight(),
                    hudHeightWorld
                );

                verSiGameOverCeroPuntos();
            }
        }

        // FLASH
        ctx.flashMessage.update(delta);

        // TEXTOS FLOTANTES
        for (int i = ctx.floatingTexts.size - 1; i >= 0; i--) {
            FloatingText ft = ctx.floatingTexts.get(i);
            ft.timeLeft -= delta;
            ft.pos.y += 0.8f * delta;
            ft.alpha = MathUtils.clamp(ft.timeLeft / 0.6f, 0f, 1f);

            if (ft.timeLeft <= 0f) {
                ctx.floatingTexts.removeIndex(i);
            }
        }

        // TIMER
        ctx.countdownTimer.update(delta);

        if (ctx.countdownTimer.isFinished()
            && ctx.state != FightGameContext.GameState.GAMEOVER) {
            loseHeartOrGameOverByTime();
        }

        checkWinCondition();
    }

    private void verSiGameOverCeroPuntos() {
        if (ctx.score <= 0) {
            loseHeartOrGameOverByScore();
        }
    }

    private void loseHeartOrGameOverByScore() {
        if (ctx.state == FightGameContext.GameState.GAMEOVER) return;

        if (ctx.hearts <= 1) {
            ctx.score = 0;
            ctx.flashMessage.show(new Color(1f, 0f, 0f, 1f), "¡ GAME OVER !");
            ctx.state = FightGameContext.GameState.GAMEOVER;

            if (music != null) {
                music.stop();
            }
            return;
        }

        // Pierde un corazón y reinicia score
        ctx.hearts--;
        ctx.score = 100;

        audio.playSound(resources.sonidoMortal);

        ctx.flashMessage.show(new Color(1f, 0f, 0f, 1f), "¡ LIFE LOST !");
    }


    private void loseHeartOrGameOverByTime() {
        if (ctx.state == FightGameContext.GameState.GAMEOVER) return;

        if (ctx.hearts <= 1) {
            ctx.flashMessage.show(new Color(1f, 0f, 0f, 1f), "¡ TIME OVER !");
            ctx.state = FightGameContext.GameState.GAMEOVER;

            if (music != null) {
                music.stop();
            }
            return;
        }

        // Pierde un corazón y reinicia tiempo
        ctx.hearts--;
        ctx.countdownTimer.reset(60f);

        audio.playSound(resources.sonidoMortal);

        ctx.flashMessage.show(new Color(1f, 0f, 0f, 1f), "¡ TIME OVER !");
    }

    private void checkWinCondition() {
        if (ctx.score >= ctx.scoreBasta
            && ctx.state == FightGameContext.GameState.PLAYING) {

            ctx.score = ctx.scoreBasta;

            NataRunner nataRunner = (NataRunner) game;
            nataRunner.session.setFightScore(ctx.countdownTimer.getRemainingSeconds());

            ctx.state = FightGameContext.GameState.YOUWIN;

            if (music != null) {
                music.pause();
            }
        }
    }

    private void controlSalidaDelBoss() {
        if (ctx.npcKilled >= ctx.npcMaxKilled && !ctx.boss.isVisible()) {

            if (ctx.quitarChicasSiSaleBoss) {
                ctx.sideNPCs.clear();
            }

            ctx.bossPending = true;
            ctx.bossDelayTimer = 0f;
        }
    }


}
