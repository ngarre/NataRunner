package com.natalia.natarunner.model;

public class ScoreEntry {

    private String playerName;
    private int score;
    private long createdAt;

    public ScoreEntry() {
    }

    public ScoreEntry(String playerName, int score, long createdAt) {
        this.playerName = playerName;
        this.score = score;
        this.createdAt = createdAt;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
