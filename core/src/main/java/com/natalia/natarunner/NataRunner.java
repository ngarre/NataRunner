package com.natalia.natarunner;

import com.natalia.natarunner.manager.*;
import com.natalia.natarunner.screens.MenuScreen;
import com.badlogic.gdx.Game;


public class NataRunner extends Game {

    public AudioManager audio;
    public ResourceManager resources;
    public GameSettings settings;
    public GameSession session;
    public ScoreManager scoreManager;

    @Override
    public void create() {

        audio = new AudioManager();
        resources = new ResourceManager();
        settings = new GameSettings();
        session = new GameSession();
        scoreManager = new ScoreManager();

        /*
        Para permitir que la pantalla de juego (TearsScreen) controle la navegación a otras pantallas,
        como el menú principal, se pasa una referencia al objeto Game al constructor de la pantalla.
        Esto permite que, desde la propia pantalla de juego, se pueda invocar game.setScreen(new MenuScreen(game))
         cuando sea necesario (por ejemplo, al pulsar ESC y confirmar salida).
         */

        //setScreen(new FightScreen(this, audio));
        //setScreen(new TearsScreen(this, audio));
        setScreen(new MenuScreen(this, audio,  resources, settings));

    }
}
