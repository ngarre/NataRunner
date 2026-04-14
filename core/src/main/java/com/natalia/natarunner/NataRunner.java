package com.natalia.natarunner;

import com.badlogic.gdx.Game;
import com.natalia.natarunner.screens.MenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class NataRunner extends Game {
    @Override
    public void create() {
        setScreen(new MenuScreen(this));
    }
}
