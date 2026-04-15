package com.natalia.natarunner;

import com.badlogic.gdx.Game;
import com.natalia.natarunner.manager.GameSettings;
import com.natalia.natarunner.screens.MenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class NataRunner extends Game {

    public GameSettings settings;

    @Override
    public void create() {
        settings = new GameSettings();
        setScreen(new MenuScreen(this));
    }
}
