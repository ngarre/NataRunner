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
        // Desactiva cualquier InputProcessor anterior.
        // En este nivel la entrada se gestiona manualmente desde el metodo input() no con Stage.
        Gdx.input.setInputProcessor(null);

        // Evita reinicializar el nivel si la pantalla ya había sido preparada antes.
        if (!ctx.initialized) {

            // Define el objetivo de puntuación según la dificultad elegida en ConfigurationScreen.
            // Los valores concretos están centralizados en GameConfig.
            ctx.scoreBasta = settings.isEasyMode() ? GameConfig.puntosSiFacil : GameConfig.puntosSiDificil;

            // Asigna las fuentes que se usarán para dibujar información en pantalla.
            ctx.font = resources.hudFont;
            ctx.smallFont = resources.hudSmallFont;

            // Inicializa elementos de información y control del nivel:
            // menú de pausa, mensajes, barra de puntuación y temporizador.
            ctx.pauseMenu = new PauseMenu(resources.hudFont, resources, audio);
            ctx.flashMessage = new FlashMessage(GameConfig.duracionFlashMensaje);
            ctx.scoreBar = new ScoreBar(ctx.scoreBasta, 220f, 20f, 20f);
            ctx.countdownTimer = new CountdownTimer(60f, ctx.font);

            // Prepara la música del nivel.
            // Se configura en bucle y se detiene cualquier música anterior.
            music = resources.tearsMusic;
            music.setLooping(true);
            audio.stopMusic();

            // Crea el personaje principal del nivel 1.
            // Se le pasan sus texturas y su posición inicial en la parte inferior central.
            ctx.playerTears = new com.natalia.natarunner.model.entities.player.PlayerTears(
                resources.manosTexture,
                resources.manosCerradasTexture,
                12.28f / 2f - 0.5f, // Posición inicial en X: centro del mundo menos la mitad del ancho del sprite (0.5f)
                0.2f // Posición inicial en Y: un poco por encima del borde inferior para que no se vea cortado (0.2f)
            );

            // Asigna las texturas de las gotas que se usarán durante el nivel.
            gotaBlanca1 = resources.gotaBlanca1;
            gotaBlanca2 = resources.gotaBlanca2;
            gotaBlanca3 = resources.gotaBlanca3;

            gotaAmarillaTexture = resources.gotaAmarillaTexture;
            gotaAmarillaSableadaTexture = resources.gotaAmarillaSableadaTexture;
            gotaRojaTexture = resources.gotaRojaTexture;

            // Asigna los sonidos asociados a las acciones de las gotas.
            gotaBlancaSound = resources.gotaBlancaSound;
            gotaAmarillaSound = resources.gotaAmarillaSound;
            gotaAmarillaFallSound = resources.gotaAmarillaFallSound;
            sonidoSable = resources.sonidoSable;
            gotaRojaSound = resources.gotaRojaSound;

            // Marca el contexto como inicializado para no crear estos objetos más de una vez.
            // Así, si vuelvo desde pantalla de pausa, no se reinicia el nivel, sino que se mantiene el estado.
            ctx.initialized = true;
        }
    }


    public void input() {

        // Atajo de presentación: pausa o reanuda solo la música.
        // Permite mostrar los efectos de sonido del nivel sin que la música los tape.
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.M)) {
            ctx.pauseOnlyMusic = !ctx.pauseOnlyMusic; // Alterna el estado de pausa solo para la música
            if (ctx.pauseOnlyMusic) music.pause(); else music.play(); // Pausa o reanuda la música según el nuevo estado
            return; // Salimos del metodo input() para evitar procesar otras entradas en el mismo frame
        }

        // Atajo de presentación/pruebas: fuerza la puntuación necesaria
        // para superar el nivel y pasar directamente al siguiente.
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.L)) {
            ctx.score = ctx.scoreBasta;
            return;
        }

        // Atajo de presentación/pruebas: activa o desactiva el modo congelado.
        // Aquí solo se cambia la bandera freezeMode y se pausa/reanuda la música.
        // La lógica se detiene realmente en update(), porque allí no se llama a logic(delta)
        // mientras ctx.freezeMode sea true.
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.F)) {
            ctx.freezeMode = !ctx.freezeMode;
            if (ctx.freezeMode) audio.pauseMusic(); else audio.playMusic(music);
            return;
        }

        // Si el modo congelado está activo, no se procesa más entrada del nivel.
        if (ctx.freezeMode) {
            return;
        }

        // Coordenadas del ratón para zonas de HUD/interfaz y para el mundo del juego.
        // Se crean a partir de la posición real del ratón en pantalla.
        com.badlogic.gdx.math.Vector2 mouseHud =
            new com.badlogic.gdx.math.Vector2(Gdx.input.getX(), Gdx.input.getY());
        com.badlogic.gdx.math.Vector2 mouseWorld =
            new com.badlogic.gdx.math.Vector2(Gdx.input.getX(), Gdx.input.getY());

        // Gestión de la tecla ESCAPE según el estado actual del nivel.
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {

            // Si estamos en la introducción, ESCAPE vuelve al menú principal.
            if (ctx.state == TearsGameContext.GameState.INTRO) {
                game.setScreen(new com.natalia.natarunner.screens.MenuScreen(game, audio, resources, settings));
                return;
            }

            // Si estamos jugando, ESCAPE pausa la partida y detiene la música.
            if (ctx.state == TearsGameContext.GameState.PLAYING) {
                ctx.state = TearsGameContext.GameState.PAUSED;
                if (music != null) {
                    music.pause();
                }
                return;
            }
        }

        // Estado INTRO: pantalla previa al inicio real del nivel.
        if (ctx.state == TearsGameContext.GameState.INTRO) {

            boolean startGame = false;

            // ENTER inicia el nivel.
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
                startGame = true;
            }

            // Convierte la posición del ratón a las coordenadas donde está definido
            // el rectángulo clicable de inicio del nivel.
            renderManager.getHudViewport().unproject(mouseHud);

            // También se puede iniciar haciendo clic sobre la zona definida para el nivel 1.
            if (Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT)
                && ctx.level1Rectangulo.contains(mouseHud)) {
                startGame = true;
            }

            // Cuando el jugador confirma el inicio, el nivel pasa a PLAYING
            // y comienza la música.
            if (startGame) {
                ctx.state = TearsGameContext.GameState.PLAYING;
                audio.playMusic(music);
            }

            return;
        }

        // Estado GAMEOVER: la partida ha terminado.

        // ENTER devuelve al menú principal para poder iniciar otra partida.
        if (ctx.state == TearsGameContext.GameState.GAMEOVER) {
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
                game.setScreen(new com.natalia.natarunner.screens.MenuScreen(game, audio, resources, settings));
            }
            return;
        }

        // Estado PAUSED: se muestra y gestiona el menú de pausa.
        if (ctx.state == TearsGameContext.GameState.PAUSED) {

            // Comprueba si el jugador ha pulsado alguna opción del menú de pausa.
            // Se usa el hudViewport porque el menú está dibujado en coordenadas de pantalla,
            // no en coordenadas del mundo del juego.
            com.natalia.natarunner.ui.PauseMenu.PauseOption option =
                ctx.pauseMenu.input(renderManager.getHudViewport());

            switch (option) {
                case CONTINUE:
                    // Continúa la partida y reanuda la música.
                    ctx.state = TearsGameContext.GameState.PLAYING;
                    audio.playMusic(music);
                    break;

                case INSTRUCTIONS:
                    // Abre la pantalla reutilizable de instrucciones.
                    // Se le pasa esta pantalla como pantalla anterior para poder volver.
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
                    // Sale de la partida actual y vuelve al menú principal.
                    game.setScreen(new com.natalia.natarunner.screens.MenuScreen(game, audio, resources, settings));
                    break;

                default:
                    break;
            }

            // También se puede salir de la pausa pulsando ESCAPE otra vez.
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
                ctx.state = TearsGameContext.GameState.PLAYING;
                if (music != null) {
                    music.play();
                }
            }

            return;
        }

        // Estado PLAYING: entrada durante la partida.
        if (ctx.state == TearsGameContext.GameState.PLAYING) {

            // El control con ratón solo se permite si está activado en configuración.
            if (settings.isMouseEnabled()) {

                // Al pulsar el botón izquierdo, se comprueba si el clic cae sobre el jugador.
                if (Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
                    renderManager.getViewport().unproject(mouseWorld);
                    ctx.playerTears.updateBounds();

                    // Si el jugador ha pulsado sobre las manos, empieza el arrastre.
                    if (ctx.playerTears.getBounds().contains(mouseWorld)) {
                        ctx.dragging = true;
                    }
                }

                // Si se suelta el botón izquierdo, se deja de arrastrar al jugador.
                if (!Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
                    ctx.dragging = false;
                }

                // Mientras se está arrastrando, se convierte la posición del ratón
                // a coordenadas del mundo y se mueve el jugador.
                if (ctx.dragging) {
                    mouseWorld.set(Gdx.input.getX(), Gdx.input.getY());
                    renderManager.getViewport().unproject(mouseWorld);
                    ctx.playerTears.handleMouseDrag(mouseWorld);
                }
            }
        }
    }

    public void update(float delta) {
        // La lógica del nivel solo avanza cuando el juego está en estado PLAYING
        // y no está activado el modo congelado de presentación/pruebas.
        if (ctx.state == TearsGameContext.GameState.PLAYING && !ctx.freezeMode) {
            logic(delta);
        }
    }

    private void logic(float delta) {

        // Acumula el tiempo total transcurrido desde que empezó el nivel.
        // Se usa para controlar cuándo empiezan a aparecer ciertos tipos de gotas.
        ctx.tiempoTotal += delta;

        // Actualiza el movimiento y límites del jugador.
        playerLogic(delta);

        // Controla la aparición progresiva de gotas según los temporizadores.
        spawnLogic(delta);

        // Actualiza gotas blancas y comprueba si se recogen o salen de pantalla.
        updateWhiteDrops(delta);

        // Actualiza gotas amarillas y gestiona puntos, penalizaciones o daño.
        updateYellowDrops(delta);

        // Actualiza gotas rojas, su persecución, disparos y colisiones con el jugador.
        updateRedDrops(delta);

        // Actualiza los proyectiles de las gotas rojas y sus colisiones.
        updateRedProjectiles(delta);

        // Actualiza los textos flotantes de puntos positivos o negativos.
        updateFloatingTexts(delta);

        // Actualiza los textos flotantes de puntos positivos o negativos.
        ctx.flashMessage.update(delta);

        // Actualiza el temporizador principal del nivel.
        ctx.countdownTimer.update(delta);

        // Si el temporizador llega a cero y todavía no estamos en GAMEOVER,
        // se pierde un corazón o termina la partida.
        if (ctx.countdownTimer.isFinished() &&
            ctx.state != TearsGameContext.GameState.GAMEOVER) {
            loseHeartOrGameOver(
                false,
                true,
                "¡ TIME OVER !",
                new Color(1f, 0f, 0f, 1f)
            );
        }

        // Comprueba si se ha alcanzado la puntuación necesaria para pasar al nivel 2.
        checkLevelEnd();
    }

    private void playerLogic(float delta) {
        // Movimiento continuo por teclado.
        // El control por ratón se gestiona en input(), porque depende de clics
        // y del estado de arrastre del jugador.
        ctx.playerTears.handleKeyboard(delta);

        // Actualiza el estado del jugador y limita su movimiento
        // dentro del mundo visible del nivel.
        ctx.playerTears.update(
            delta, // tiempo real para movimiento fluido
            renderManager.getViewport().getWorldWidth(), // límite horizontal: ancho del mundo
            renderManager.getViewport().getWorldHeight(), // límite vertical: alto del mundo menos la altura del HUD
            ctx.hudHeight // altura reservada para la zona superior de información.
        );
    }
    private void crearGotaBlanca() {
        // Calcula una posición X aleatoria dentro del ancho del mundo.
        // Se resta 0.5f para evitar que la gota aparezca parcialmente fuera de la pantalla.
        float x = MathUtils.random(0f, renderManager.getViewport().getWorldWidth() - 0.5f);

        // La gota aparece justo debajo del HUD, en la parte superior de la zona jugable.
        float y = renderManager.getViewport().getWorldHeight() - ctx.hudHeight;

        // Crea una nueva gota blanca y la añade a la lista de gotas blancas activas.
        // Se le pasan varias texturas porque la gota blanca tiene animación/cambio visual.
        ctx.gotasBlancas.add(new WhiteDrop(
            gotaBlanca1,
            gotaBlanca2,
            gotaBlanca3,
            x,
            y
        ));
    }

    private void crearGotaAmarilla() {
        // Calcula una posición X aleatoria dentro del ancho del mundo
        float x = MathUtils.random(0f, renderManager.getViewport().getWorldWidth() - 0.5f);

        // La gota amarilla aparece desde la parte superior de la zona jugable.
        float y = renderManager.getViewport().getWorldHeight() - ctx.hudHeight;

        // Crea una nueva gota amarilla y la añade a su lista.
        // Recibe textura normal y textura sableada porque puede transformarse en peligrosa.
        ctx.gotasAmarillas.add(new YellowDrop(
            gotaAmarillaTexture,
            gotaAmarillaSableadaTexture,
            x,
            y
        ));
    }

    private void crearGotaRoja() {
        // Calcula una posición X aleatoria dentro del ancho del mundo.
        float x = MathUtils.random(0f, renderManager.getViewport().getWorldWidth() - 0.5f);

        // La gota roja aparece desde la parte superior de la zona jugable.
        float y = renderManager.getViewport().getWorldHeight() - ctx.hudHeight;

        // Crea una nueva gota roja enemiga y la añade a la lista de gotas rojas activas.
        ctx.gotasRojas.add(new RedDrop(gotaRojaTexture, x, y));

        // Reproduce el sonido asociado a la aparición de la gota roja.
        audio.playSound(gotaRojaSound);
    }

    // Metodo que decide cuándo aparecen gotas nuevas
    private void spawnLogic(float delta) {

        // Salida gotas blancas
        // Temporizador de gotas blancas.
        // Las gotas blancas aparecen desde el inicio del nivel cada cierto tiempo.
        ctx.gotaBlancaTimer += delta;
        if (ctx.gotaBlancaTimer > GameConfig.tiempoCadaCuantoGotaBlanca) {
            ctx.gotaBlancaTimer = 0f;
            crearGotaBlanca();
        }

        // Las gotas amarillas no aparecen desde el primer segundo.
        // Primero se espera a que el tiempo total supere el valor configurado en GameConfig.
        // Hago aparecer gota amarilla a los 8 segundos
        if (ctx.tiempoTotal > GameConfig.tiempoPrimeraGotaAmarilla) {
            ctx.gotaAmarillaTimer += delta;

            // Una vez activadas, aparecen periódicamente según su propio temporizador.
            if (ctx.gotaAmarillaTimer > GameConfig.tiempoCadaCuantoAmarilla) {
                ctx.gotaAmarillaTimer = 0f;
                crearGotaAmarilla();
            }
        }

        // Las gotas rojas aparecen más tarde para aumentar la dificultad progresivamente.
        // Hago aparecer gota roja a los 15 segundos o lo que se programe
        if (ctx.tiempoTotal > GameConfig.tiempoPrimeraGotaRoja) {
            ctx.gotaRojaTimer += delta;

            // Una vez activadas, aparecen cada cierto tiempo configurado.
            if (ctx.gotaRojaTimer > GameConfig.tiempoCadaCuantoRoja) {
                ctx.gotaRojaTimer = 0f;
                crearGotaRoja();
            }
        }
    }

    // Metodo que gestiona todas las gotas blancas que están activas en pantalla: las mueve, elimina las que salen
    // de la pantalla y suma puntos si el jugador las recoge
    private void updateWhiteDrops(float delta) {

        // Recorro la lista de gotas blancas de atrás hacia delante.
        // Así puedo eliminar elementos de la lista sin saltarme ninguna gota.
        for (int i = ctx.gotasBlancas.size - 1; i >= 0; i--) {

            // Obtengo la gota blanca actual.
            WhiteDrop gota = ctx.gotasBlancas.get(i);

            // Actualiza la posición/animación de la gota según el tiempo transcurrido.
            gota.update(delta);

            // Si la gota sale por la parte inferior de la pantalla, se elimina.
            if (gota.isOutOfScreen()) {
                ctx.gotasBlancas.removeIndex(i);
            }

            // Si la hitbox del jugador se solapa con la hitbox de la gota,
            // significa que el jugador la ha recogido.
            else if (ctx.playerTears.getBounds().overlaps(gota.getBounds())) {
                // Se elimina la gota recogida para que no pueda volver a sumar puntos.
                ctx.gotasBlancas.removeIndex(i);

                // Reproduce el sonido de recoger gota blanca.
                audio.playSound(gotaBlancaSound);

                // Suma la puntuación definida en la propia clase WhiteDrop.
                ctx.score += WhiteDrop.POINTS;

                // Crea un texto flotante visual en la posición de la gota,
                // mostrando los puntos obtenidos.
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

        // Recorro la lista de gotas amarillas de atrás hacia delante
        // porque dentro del bucle puedo eliminar gotas de la lista.

        for (int i = ctx.gotasAmarillas.size - 1; i >= 0; i--) {

            // Obtengo la gota amarilla actual.
            YellowDrop gota = ctx.gotasAmarillas.get(i);

            // Actualiza la posición y el estado interno de la gota.
            // Aquí puede avanzar su animación o cambiar a estado letal.
            gota.update(delta);

            // Si la gota acaba de transformarse, reproduce el sonido de sable.
            // consumeTransformEvent() devuelve true solo una vez por transformación,
            // para que el sonido no se repita en cada frame.
            if (gota.consumeTransformEvent()) {
                audio.playSound(sonidoSable);
            }

            // Si la gota amarilla sale de la pantalla por abajo, penaliza al jugador.
            if (gota.isOutOfScreen()) {

                // Guardo la posición antes de eliminarla para poder mostrar ahí el texto flotante.
                float x = gota.getSprite().getX();
                float y = Math.max(gota.getSprite().getY(), 0.2f);

                // Elimino la gota porque ya ha salido de la zona jugable.
                ctx.gotasAmarillas.removeIndex(i);

                // Reproduzco el sonido de caída de la gota amarilla.
                audio.playSound(gotaAmarillaFallSound);

                // Resto puntos al jugador.
                ctx.score -= YellowDrop.PENALTY;

                // Evito que la puntuación quede por debajo de cero.
                if (ctx.score < 0) ctx.score = 0;   // para que no me salgan negativos

                // Si la puntuación llega a cero, el jugador pierde un corazón
                // o entra en GAMEOVER si ya no le quedan vidas.
                if (ctx.score <= 0) {
                    loseHeartOrGameOver(
                        true,
                        false,
                        "¡ SCORE OUT !",
                        new Color(1f, 0.2f, 0.2f, 1f)
                    );
                } else {
                    // Si todavía queda puntuación, muestro un aviso visual de penalización.
                    ctx.flashMessage.show(
                        new Color(1f, 1f, 0f, 1f),
                        "-" + YellowDrop.PENALTY
                    );

                    // También creo un texto flotante en la posición donde cayó la gota.
                    ctx.floatingTexts.add(new FloatingText(
                        "-" + YellowDrop.PENALTY,
                        x,
                        y,
                        0.6f,
                        Color.YELLOW
                    ));
                }
            }

            // Si la gota no ha salido de pantalla, compruebo si choca con el jugador.
            else if (ctx.playerTears.getBounds().overlaps(gota.getBounds())) {

                // Si la gota está en estado letal, daña al jugador.
                if (gota.isLethal()) {
                    ctx.gotasAmarillas.removeIndex(i);

                    loseHeartOrGameOver(
                        false,
                        false,
                        "¡ YOU ARE STABBED !",
                        new Color(1f, 0.4f, 0.1f, 1f)
                    );

                // Si la gota no es letal, funciona como recompensa.
                } else {
                    ctx.gotasAmarillas.removeIndex(i);
                    audio.playSound(gotaAmarillaSound);

                    // Suma los puntos definidos en YellowDrop.
                    ctx.score += YellowDrop.POINTS;

                    // Muestra un texto flotante con los puntos ganados.
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
