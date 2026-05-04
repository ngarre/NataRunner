package com.natalia.natarunner.manager;

public class GameSession {

    private int tearsScore;
    private int fightScore;
    private boolean scoreSaved;

    /*
      Esto es importante.
      No guardo la puntuación parcial del nivel 1 en Preferences, porque eso sería persistencia permanente y no corresponde.
      Es mejor una clase de sesión actual.

      Guarda los datos temporales de la partida actual:
      score del nivel 1
      score del nivel 2
      score final
      control para no guardar dos veces

                                              La puntuación final es la suma de los segundos
                                              restantes de cada nivel. Cuanto más tiempo tardes
                                              en acabar, pues menos puntos, claro

    */

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
