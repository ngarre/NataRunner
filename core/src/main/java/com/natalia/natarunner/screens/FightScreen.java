package com.natalia.natarunner.screens;

import com.natalia.natarunner.manager.*;
import com.natalia.natarunner.screens.context.FightGameContext;
import com.natalia.natarunner.ui.*;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class FightScreen implements Screen {

    private final Game game;

    // Managers compartidos recibidos por constructor.
    // Se crean en la clase principal del juego (NataRunner) y se reutilizan en las pantallas
    // para mantener el mismo audio, recursos y configuración.
    AudioManager audio;
    ResourceManager resources;
    GameSettings settings;

    // Piezas principales del nivel 2.
    // Contexto: guarda el estado.
    // LogicManager: controla reglas, input, colisiones y cambios de pantalla.
    // RenderManager: dibuja lo visible.
    private final FightGameContext ctx;
    private final FightLogicManager logicManager;
    private final FightRenderManager renderManager;

    public FightScreen(Game game, AudioManager audio,  ResourceManager resources, GameSettings settings) {
        this.game = game;
        this.audio = audio;
        this.resources = resources;
        this.settings = settings;

        // Crea el contexto propio del nivel 2.
        // Aquí se guardarán jugador, enemigos, boss, balas, puntuación, vidas, timer, etc.
        this.ctx = new FightGameContext();

        // Crea el manager de renderizado, que necesitará los recursos y el estado del contexto para dibujar.
        this.renderManager = new FightRenderManager(resources, ctx);

        // Crea el manager de lógica, que necesitará acceso al juego, audio, recursos,
        // configuración y contexto para controlar el funcionamiento del nivel.
        this.logicManager = new FightLogicManager(game, audio, resources, settings, ctx);

        // Conecta lógica y render.
        // La lógica necesita consultar el viewport del render para coordenadas, límites y entrada.
        this.logicManager.setRenderManager(renderManager);

        // Guarda esta pantalla como pantalla propietaria.
        // Se usa, por ejemplo, para volver desde InstructionsScreen a esta misma pantalla.
        this.logicManager.setOwnerScreen(this);
    }




    // *************************************************************************************************************
    // SHOW....
    // *************************************************************************************************************
    @Override
    public void show() {
        logicManager.show();   //   <---- Inicializa el estado del juego (entidades, lógica, timers, audio)
        renderManager.show();  //   <---- Inicializa elementos de render (SpriteBatch, viewports, layout HUD)
    }


    // *************************************************************************************************************
    // RENDER....
    // *************************************************************************************************************
    @Override
    public void render(float delta) {
        // En cada frame se sigue el orden:
        // 1. Leer entrada del jugador.
        // 2. Actualizar lógica del nivel.
        // 3. Dibujar el resultado.
        logicManager.input();
        logicManager.update(delta);
        renderManager.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Si cambia el tamaño de la ventana, el render ajusta sus viewports.
        renderManager.resize(width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        renderManager.dispose();
    }
}
