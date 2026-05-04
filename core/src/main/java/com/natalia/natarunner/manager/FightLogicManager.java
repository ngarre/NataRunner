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
        // Limpia cualquier InputProcessor anterior.
        // En este nivel la entrada se gestiona manualmente desde input().
        Gdx.input.setInputProcessor(null);

        // Evita inicializar dos veces los elementos del nivel.
        // Es útil si se vuelve desde una pantalla temporal como InstructionsScreen.
        if (!ctx.initialized) {

            // Define el objetivo de puntuación según la dificultad elegida.
            // Los valores concretos están centralizados en GameConfig.
            ctx.scoreBasta = settings.isEasyMode() ? GameConfig.puntosSiFacil : GameConfig.puntosSiDificil;

            // Fuentes usadas para dibujar textos e información del HUD.
            ctx.font = resources.hudFont;
            ctx.smallFont = resources.hudSmallFont;

            // Inicializa elementos de información y control del nivel:
            // menú de pausa, mensajes, barra de puntuación y temporizador.
            ctx.pauseMenu = new PauseMenu(resources.hudFont, resources, audio);
            ctx.flashMessage = new FlashMessage(GameConfig.duracionFlashMensaje);
            ctx.scoreBar = new ScoreBar(
                ctx.scoreBasta,
                220f,
                20f,
                20f
            );
            ctx.countdownTimer = new CountdownTimer(60f, ctx.font);


            // Prepara la música del nivel 2.
            // Se configura en bucle, pero no se reproduce aquí.
            // Empieza a sonar cuando el jugador inicia el nivel desde INTRO.
            music = resources.fightMusic;
            music.setLooping(true); // La música se repetirá en bucle.
            music.stop(); // Asegura que la música no suene al cargar el nivel, se reproducirá al iniciar el juego.

            // Asigna las texturas y sonidos necesarios para los disparos y NPCs.
            bulletTexture = resources.bulletPlayer;
            bulletBossTexture = resources.bulletBoss;
            chillidoSound = resources.chillidoChica;

            // Crea el PERSONAJE PRINCIPAL del nivel 2.
            // Recibe las dimensiones del mundo, una escala/altura de referencia,
            // el audio y los recursos necesarios para sus sprites y sonidos.
            ctx.player = new PlayerBladecar(
                12.28f,
                7.68f,
                1f,
                audio,
                resources
            );

            // Crea el primer tipo de NPC lateral.
            // Se le pasan varias texturas para animación, una textura de impacto,
            // su sonido y las dimensiones del mundo.
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

            // Crea el segundo tipo de NPC lateral, con sprites y sonido diferentes.
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

            // Crea el boss del nivel 2.
            // Al inicio existe como objeto, pero su visibilidad/aparición se controla después.
            ctx.boss = new NpcBoss(12.28f, 7.68f, audio, resources);

            // Marca el nivel como inicializado para no recrear jugador, NPCs, boss y HUD.
            ctx.initialized = true;
        }
    }

    public void input() {

        // Guarda la posición actual del ratón en coordenadas de pantalla.
        // Más adelante se convertirá al sistema de coordenadas del HUD si hace falta.
        ctx.mouseHud.set(Gdx.input.getX(), Gdx.input.getY());

        // Atajo de presentación: pausa o reanuda solo la música.
        // No pausa la partida, solo permite escuchar mejor los efectos de sonido.
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.M)) {
            ctx.pauseOnlyMusic = !ctx.pauseOnlyMusic;
            if (ctx.pauseOnlyMusic) music.pause(); else music.play();
            return;
        }

        // Atajo de pruebas/presentación: fuerza la puntuación necesaria
        // para activar el final del nivel.
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.L)) {
            ctx.score = ctx.scoreBasta;
            return;
        }

        // FREEZE MODE: Atajo de pruebas/presentación: activa o desactiva el modo congelado.
        // Aquí solo se cambia la bandera; la lógica se detiene realmente en update().
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.F)) {
            ctx.freezeMode = !ctx.freezeMode;
            if (ctx.freezeMode) audio.pauseMusic(); else audio.playMusic(music);
            return;
        }

        // Si el modo congelado está activo, no se procesa más entrada.
        if (ctx.freezeMode) {
            return;
        }

        // Estado INTRO: pantalla previa antes de empezar el nivel 2.
        if (ctx.state == FightGameContext.GameState.INTRO) {
            boolean startGame = false;

            // ENTER inicia el nivel
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
                startGame = true;
            }

            // Convierte la posición del ratón a las coordenadas donde está definido
            // el rectángulo clicable de inicio del nivel.
            renderManager.getHudViewport().unproject(ctx.mouseHud);

            // También se puede iniciar haciendo click en el rectángulo de LEVEL 2.
            if (Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT)
                && ctx.level2Rectangulo.contains(ctx.mouseHud)) {
                startGame = true;
            }

            // Al empezar, se cambia el estado a PLAYING y comienza la música del nivel.
            if (startGame) {
                ctx.state = FightGameContext.GameState.PLAYING;
                audio.playMusic(music);
            }

            return;
        }

        // Estado GAMEOVER: la partida ha terminado por derrota.
        if (ctx.state == FightGameContext.GameState.GAMEOVER) {
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
                game.setScreen(new com.natalia.natarunner.screens.MenuScreen(game, audio, resources, settings));
            }
            return;
        }

        // Estado YOUWIN: el jugador ha superado el nivel 2.
        if (ctx.state == FightGameContext.GameState.YOUWIN) {
            // ENTER o click llevan a la pantalla de puntuaciones.
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)
                || Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT))
            {
                game.setScreen(new com.natalia.natarunner.screens.ScoresScreen(game, audio, resources, settings));
            }
            return;
        }

        // Estado PLAYING: entrada durante la partida.
        if (ctx.state == FightGameContext.GameState.PLAYING) {

            // ESCAPE pausa la partida y detiene la música.
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
                ctx.state = FightGameContext.GameState.PAUSED;
                if (music != null) {
                    music.pause();
                }
                return;
            }

            // SPACE dispara una bala del jugador.
            // Solo se permite disparar cuando el PlayerBladecar ya ha terminado su entrada inicial.
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)
                && ctx.player.hasArrived()) {
                ctx.bullets.add(ctx.player.shoot(bulletTexture));
            }

            // Control opcional con ratón, según la configuración elegida por el usuario.
            if (settings.isMouseEnabled()) {

                // Al pulsar click izquierdo, se comprueba si el click cae sobre el jugador.
                if (Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
                    ctx.mouseWorld.set(Gdx.input.getX(), Gdx.input.getY());
                    renderManager.getViewport().unproject(ctx.mouseWorld);

                    // Si el jugador ha pulsado sobre el coche/nave, empieza el arrastre.
                    if (ctx.player.getRect().contains(ctx.mouseWorld)) {
                        ctx.dragging = true;
                    }
                }

                // Al soltar el botón izquierdo, termina el arrastre.
                if (!Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
                    ctx.dragging = false;
                }

                // Mientras se arrastra, se convierte el ratón a coordenadas del mundo
                // y se mueve el jugador a esa posición.
                if (ctx.dragging) {
                    ctx.mouseWorld.set(Gdx.input.getX(), Gdx.input.getY());
                    renderManager.getViewport().unproject(ctx.mouseWorld);
                    ctx.player.handleMouseDrag(ctx.mouseWorld);
                }

            } else {
                // Si el control con ratón está desactivado, se asegura que no quede arrastre activo.
                ctx.dragging = false;
            }

            return;
        }

        // Estado PAUSED: se gestiona el menú de pausa.
        if (ctx.state == FightGameContext.GameState.PAUSED) {

            // El PauseMenu comprueba si el jugador ha elegido alguna opción
            // usando las coordenadas del HUD.
            com.natalia.natarunner.ui.PauseMenu.PauseOption option =
                ctx.pauseMenu.input(renderManager.getHudViewport());

            switch (option) {
                case CONTINUE:
                    // Continúa la partida y reanuda la música.
                    ctx.state = FightGameContext.GameState.PLAYING;
                    audio.playMusic(music);
                    break;

                case INSTRUCTIONS:
                    // Abre la pantalla reutilizable de instrucciones del nivel 2.
                    // Se pasa ownerScreen para poder volver a esta misma pantalla.
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
                    // Sale de la partida actual y vuelve al menú principal.
                    game.setScreen(new com.natalia.natarunner.screens.MenuScreen(game, audio, resources, settings));
                    break;

                default:
                    break;
            }

            // También se puede salir de la pausa pulsando ESCAPE otra vez.
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
                ctx.state = FightGameContext.GameState.PLAYING;
                audio.playMusic(music);
            }
        }
    }

    public void update(float delta) {
        // La lógica del nivel 2 solo avanza si estamos jugando
        // y no está activado el modo congelado de pruebas/presentación.
        if (ctx.state == FightGameContext.GameState.PLAYING && !ctx.freezeMode) {
            logic(delta);
        }
    }

    private void logic(float delta) {

        // Actualiza el estado interno del jugador.
        // Esto incluye, por ejemplo, su entrada inicial en pantalla o animaciones propias.
        ctx.player.update(delta);

        // El jugador solo puede moverse libremente cuando ya ha terminado su entrada inicial.
        if (ctx.player.hasArrived()) {

            // Convierte la altura reservada al HUD a coordenadas del mundo jugable.
            // Así el jugador no invade la zona superior donde se muestra la información.
            float hudHeightWorld =
                (ctx.barHeight / renderManager.getHudViewport().getWorldHeight())
                    * renderManager.getViewport().getWorldHeight();

            // Mueve el jugador dentro de los límites del mundo y respetando la zona del HUD.
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

        // Recorre las balas disparadas por el jugador.
        // Aunque la clase se llame BulletChicas, aquí son disparos del PlayerBladecar.
        for (int i = ctx.bullets.size - 1; i >= 0; i--) {
            BulletChicas b = ctx.bullets.get(i);

            // Actualiza la posición de la bala.
            b.update(delta);

            // Si la bala sale del mundo por los laterales, se elimina.
            if (b.isOutOfWorld(renderManager.getViewport().getWorldWidth())) {
                ctx.bullets.removeIndex(i);
            }
        }

        // =====================
        // IMPACTOS DEL PLAYER AL BOSS
        // =====================
        // Solo se comprueban impactos contra el boss cuando el boss está visible.
        if (ctx.boss.isVisible()) {
            for (int i = ctx.bullets.size - 1; i >= 0; i--) {
                BulletChicas b = ctx.bullets.get(i);

                // Si la hitbox de la bala se solapa con la hitbox del boss, hay impacto.
                if (b.getRect().overlaps(ctx.boss.getRect())) {
                    ctx.bullets.removeIndex(i);

                    // El boss recibe daño.
                    ctx.boss.takeHit();

                    // Suma los puntos del boss, sin superar la puntuación objetivo del nivel.
                    ctx.score += ctx.boss.getPoints();
                    if (ctx.score > ctx.scoreBasta) {
                        ctx.score = ctx.scoreBasta;
                    }

                    // Muestra texto flotante con los puntos obtenidos.
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
        // NPCs LATERALES - AQUÍ EMPIEZA LA IA DE LAS CHICAS
        // ========================================
        for (NpcChicas npc : ctx.sideNPCs) {

            // Actualiza cada NPC lateral.
            // Aquí se delega su IA: el NPC recibe el delta, el jugador y el audio.
            // La clase NpcChicas decide cómo moverse o reaccionar al jugador.
            npc.update(delta, ctx.player, audio);

            // Cuando una chica es golpeada no desaparece sin más: entra en un estado de impacto.  Cuando termina
            // este estado, isReadyToRespawn() indica que puede volver a colocarse en la pantalla, y resetPosition() la
            // recoloca en una nueva posición inicial para que siga participando en el nivel.
            if (npc.isReadyToRespawn()) {
                npc.resetPosition(
                    renderManager.getViewport().getWorldWidth(),
                    renderManager.getViewport().getWorldHeight(),
                    ctx.hudHeight
                );
                continue;
            }

            // COLISIÓN NPC - PLAYER
            // Si el NPC no está muriendo y toca al jugador, penaliza.
            if (!npc.isDying() && npc.getRect().overlaps(ctx.player.getRect())) {
                ctx.score -= PlayerBladecar.PENALTY;

                ctx.flashMessage.show(
                    new Color(1f, 1f, 0f, 1f),
                    "-" + PlayerBladecar.PENALTY
                );

                // El jugador recibe impacto.
                ctx.player.hit();

                npc.resetPosition(
                    renderManager.getViewport().getWorldWidth(),
                    renderManager.getViewport().getWorldHeight(),
                    ctx.hudHeight
                );

                // Comprueba si la puntuación ha llegado a cero y hay que perder vida o hacer game over.
                verSiGameOverCeroPuntos();
            }

            // BALAZOS A LAS CHICAS
            // Comprueba si alguna bala del jugador impacta contra este NPC.
            for (int i = ctx.bullets.size - 1; i >= 0; i--) {
                if (!npc.isDying() && npc.getRect().overlaps(ctx.bullets.get(i).getRect())) {

                    // Suma puntos por golpear al NPC.
                    ctx.score += npc.getPoints();

                    // Sonido de impacto del NPC.
                    audio.playSound(chillidoSound, 1f);

                    // Limita la puntuación para que no pase del objetivo.
                    if (ctx.score > ctx.scoreBasta) {
                        ctx.score = ctx.scoreBasta;
                    }

                    // Muestra texto flotante con los puntos ganados.
                    ctx.floatingTexts.add(new FloatingText(
                        "+" + npc.getPoints(),
                        npc.getX() + npc.getRect().width / 2f,
                        npc.getY() + npc.getRect().height / 2f,
                        0.6f,
                        Color.WHITE
                    ));

                    // Elimina la bala y marca al NPC como golpeado.
                    ctx.bullets.removeIndex(i);
                    npc.takeHit();

                    // Aumenta el contador de NPCs eliminados.
                    ctx.npcKilled++;

                    // Comprueba si ya se ha alcanzado el número necesario para sacar al boss
                    controlSalidaDelBoss();

                    break;
                }
            }

            // Si el NPC sale completamente del mundo, se recoloca.
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
        // Cuando ya se han eliminado suficientes NPCs, el boss no aparece instantáneamente.
        // Primero se activa bossPending y aquí se cuenta un pequeño retraso.
        if (ctx.bossPending) {
            ctx.bossDelayTimer += delta;

            if (ctx.bossDelayTimer >= ctx.bossDelay) {
                // Cuando termina el retraso, el boss aparece desde la izquierda.
                ctx.boss.appearLeft();
                ctx.bossPending = false;
            }
        }

        // =====================
        // BOSS UPDATE + DISPARO - IA / COMPORTAMIENTO DEL BOSS
        // =====================

        // Solo se actualiza el boss cuando ya está visible.
        if (ctx.boss.isVisible()) {

            // Actualiza el movimiento o comportamiento interno del boss.
            ctx.boss.update(delta);

            // Si el boss puede disparar según su temporizador interno,
            // crea una bala dirigida al jugador.
            if (ctx.boss.canShoot(delta)) {
                ctx.bossBullets.add(
                    ctx.boss.shoot(ctx.player, bulletBossTexture)
                );
            }
        }

        // =====================
        // BALAS DEL BOSS
        // =====================
        // Actualiza las balas disparadas por el boss.
        for (int i = ctx.bossBullets.size - 1; i >= 0; i--) {
            BulletBoss b = ctx.bossBullets.get(i);
            b.update(delta);

            // Si la bala sale del mundo, se elimina.
            if (b.isOutOfWorld(
                renderManager.getViewport().getWorldWidth(),
                renderManager.getViewport().getWorldHeight()
            )) {
                ctx.bossBullets.removeIndex(i);
                continue;
            }

            // Si la bala del boss impacta contra el jugador, resta puntos.
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
            // Evita que el boss haga daño continuamente mientras el jugador está tocándolo.
            // Tras recibir un golpe, el jugador queda invulnerable durante un breve tiempo.
            if (!ctx.player.isInvulnerable()
                && ctx.boss.getRect().overlaps(ctx.player.getRect())) {

                ctx.score -= PlayerBladecar.PENALTY;

                ctx.flashMessage.show(
                    new Color(1f, 0.5f, 0f, 1f),
                    "-" + PlayerBladecar.PENALTY
                );

                // El jugador recibe impacto y entra en invulnerabilidad temporal.
                ctx.player.hit();
                ctx.player.startInvulnerability();

                // Convierte la altura del HUD a coordenadas del mundo para calcular límites.
                // Esto es para que el rebote también respete esa zona.
                float hudHeightWorld =
                    (ctx.barHeight / renderManager.getHudViewport().getWorldHeight())
                        * renderManager.getViewport().getWorldHeight();

                // Aplica un rebote al jugador al tocar al boss.
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

        // Actualiza los mensajes grandes temporales.
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

        // Actualiza el temporizador del nivel.
        ctx.countdownTimer.update(delta);

        // Si el tiempo termina, se pierde un corazón o se entra en game over.
        if (ctx.countdownTimer.isFinished()
            && ctx.state != FightGameContext.GameState.GAMEOVER) {
            loseHeartOrGameOverByTime();
        }

        // Comprueba si se ha alcanzado la puntuación necesaria para ganar el nivel.
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
