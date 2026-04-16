package com.natalia.natarunner;

import com.badlogic.gdx.Game;
import com.natalia.natarunner.manager.AudioManager;
import com.natalia.natarunner.manager.GameSession;
import com.natalia.natarunner.manager.GameSettings;
import com.natalia.natarunner.screens.MenuScreen;

public class NataRunner extends Game {

    public GameSettings settings;
    public AudioManager audioManager;
    public GameSession session;

    @Override
    public void create() {
        settings = new GameSettings();
        audioManager = new AudioManager();
        session = new GameSession();
        setScreen(new MenuScreen(this));
    }
}
