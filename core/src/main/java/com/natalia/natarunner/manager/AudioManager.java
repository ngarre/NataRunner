package com.natalia.natarunner.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {

    private static final String PREFS_NAME = "nataRunnerAudio";
    private static final String KEY_MUSIC_ENABLED = "musicEnabled";
    private static final String KEY_SOUND_ENABLED = "soundEnabled";
    private static final String KEY_MUSIC_VOLUME = "musicVolume";
    private static final String KEY_SOUND_VOLUME = "soundVolume";

    private boolean soundEnabled;
    private boolean musicEnabled;

    // volumen global: 0f a 1f
    private float musicVolume;
    private float soundVolume;

    private Music currentMusic;
    private final Preferences prefs;

    public AudioManager() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);

        musicEnabled = prefs.getBoolean(KEY_MUSIC_ENABLED, true);
        soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true);

        musicVolume = prefs.getFloat(KEY_MUSIC_VOLUME, 1f);
        soundVolume = prefs.getFloat(KEY_SOUND_VOLUME, 1f);

        Gdx.app.log("AudioManager",
            "INIT musicVolume=" + musicVolume + ", soundVolume=" + soundVolume);
    }

    public void playMusic(Music music) {
        if (currentMusic != null && currentMusic != music) {
            currentMusic.stop();
        }

        currentMusic = music;
        currentMusic.setLooping(true);
        currentMusic.setVolume(musicEnabled ? musicVolume : 0f);

        if (musicEnabled) {
            currentMusic.play();
        }
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    public void pauseMusic() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }



    public void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;

        if (currentMusic != null) {
            if (enabled) {
                currentMusic.setVolume(musicVolume);
                currentMusic.play();
            } else {
                currentMusic.pause();
            }
        }

        savePreferences();
    }

    public void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
        savePreferences();
    }



    // Metodo unificado para el slider único
    public void setMasterVolume(float volume) {
        float v = clamp(volume);

        musicVolume = v;
        soundVolume = v;

        if (currentMusic != null) {
            currentMusic.setVolume(musicEnabled ? musicVolume : 0f);
        }

        savePreferences();
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public float getMasterVolume() {
        return musicVolume;
    }

    public long playSound(Sound sound) {
        if (!soundEnabled) return -1;
        return sound.play(soundVolume);
    }

    public long playSound(Sound sound, float localVolume) {
        if (!soundEnabled) return -1;
        return sound.play(clamp(localVolume) * soundVolume);
    }

    private float clamp(float value) {
        return Math.max(0f, Math.min(1f, value));
    }

    private void savePreferences() {
        prefs.putBoolean(KEY_MUSIC_ENABLED, musicEnabled);
        prefs.putBoolean(KEY_SOUND_ENABLED, soundEnabled);
        prefs.putFloat(KEY_MUSIC_VOLUME, musicVolume);
        prefs.putFloat(KEY_SOUND_VOLUME, soundVolume);
        prefs.flush();
    }
}
