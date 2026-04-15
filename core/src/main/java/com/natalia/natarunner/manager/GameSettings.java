package com.natalia.natarunner.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameSettings {

    private static final String PREFS_NAME = "nataRunnerSettings";
    private static final String KEY_MOUSE_ENABLED = "mouseEnabled";
    private static final String KEY_EASY_MODE = "easyMode";
    private static final String KEY_PLAYER_NAME = "playerName";

    private final Preferences prefs;

    private boolean mouseEnabled;
    private boolean easyMode;
    private String playerName;

    public GameSettings() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);

        mouseEnabled = prefs.getBoolean(KEY_MOUSE_ENABLED, true);
        easyMode = prefs.getBoolean(KEY_EASY_MODE, false);
        playerName = prefs.getString(KEY_PLAYER_NAME, "NONAME");
    }

    public boolean isMouseEnabled() {
        return mouseEnabled;
    }

    public void setMouseEnabled(boolean enabled) {
        mouseEnabled = enabled;
        save();
    }

    public boolean isEasyMode() {
        return easyMode;
    }

    public void setEasyMode(boolean enabled) {
        easyMode = enabled;
        save();
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            this.playerName = "NONAME";
        } else {
            this.playerName = playerName.trim();
        }
        save();
    }

    private void save() {
        prefs.putBoolean(KEY_MOUSE_ENABLED, mouseEnabled);
        prefs.putBoolean(KEY_EASY_MODE, easyMode);
        prefs.putString(KEY_PLAYER_NAME, playerName);
        prefs.flush();
    }
}
