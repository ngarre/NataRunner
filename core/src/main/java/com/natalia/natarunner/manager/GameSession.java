package com.natalia.natarunner.manager;

public class GameSession {

    private int tearsScore;
    private int fightScore;
    private boolean scoreSaved;

    public GameSession() {
        reset();
    }

    public void reset() {
        tearsScore = 0;
        fightScore = 0;
        scoreSaved = false;
    }

    public void setTearsScore(int tearsScore) {
        this.tearsScore = tearsScore;
    }

    public void setFightScore(int fightScore) {
        this.fightScore = fightScore;
    }

    public int getTearsScore() {
        return tearsScore;
    }

    public int getFightScore() {
        return fightScore;
    }

    public int getFinalScore() {
        return tearsScore + fightScore;
    }

    public boolean isScoreSaved() {
        return scoreSaved;
    }

    public void setScoreSaved(boolean scoreSaved) {
        this.scoreSaved = scoreSaved;
    }
}
