package com.natalia.natarunner;

import com.badlogic.gdx.Game;
import com.natalia.natarunner.manager.AudioManager;
import com.natalia.natarunner.manager.GameSession;
import com.natalia.natarunner.manager.GameSettings;
import com.natalia.natarunner.manager.ResourceManager;
import com.natalia.natarunner.manager.ScoreManager;
import com.natalia.natarunner.screens.MenuScreen;

public class NataRunner extends Game {

    public AudioManager audioManager;
    public ResourceManager resources;
    public GameSettings settings;
    public GameSession session;
    public ScoreManager scoreManager;

    @Override
    public void create() {
        audioManager = new AudioManager();
        resources = new ResourceManager();
        settings = new GameSettings();
        session = new GameSession();
        scoreManager = new ScoreManager();

        setScreen(new MenuScreen(this, audioManager, resources, settings));
    }
}
