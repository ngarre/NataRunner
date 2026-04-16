package com.natalia.natarunner.manager;

import com.natalia.natarunner.model.ScoreEntry;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class ScoreManager {

    private static final String PREFS_NAME = "nata_runner_scores";
    private static final String KEY_SCORES = "scores_json";
    private static final int MAX_SCORES = 10;

    private final Preferences prefs;
    private final Json json;

    public ScoreManager() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        json = new Json();
    }

    public Array<ScoreEntry> loadScores() {
        String scoresJson = prefs.getString(KEY_SCORES, "");

        if (scoresJson == null || scoresJson.isEmpty()) {
            return new Array<>();
        }

        Array<ScoreEntry> scores = json.fromJson(Array.class, ScoreEntry.class, scoresJson);

        if (scores == null) {
            return new Array<>();
        }

        scores.sort((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));

        return scores;
    }

    public void saveScores(Array<ScoreEntry> scores) {
        String scoresJson = json.toJson(scores);
        prefs.putString(KEY_SCORES, scoresJson);
        prefs.flush();
    }

    public void addScore(ScoreEntry newEntry) {
        Array<ScoreEntry> scores = loadScores();

        scores.add(newEntry);
        scores.sort((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));

        while (scores.size > MAX_SCORES) {
            scores.removeIndex(scores.size - 1);
        }

        saveScores(scores);
    }

    public void clearScores() {
        prefs.remove(KEY_SCORES);
        prefs.flush();
    }
}
