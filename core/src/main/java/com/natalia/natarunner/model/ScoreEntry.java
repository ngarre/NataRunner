package com.natalia.natarunner.model;


public class ScoreEntry {

    private String playerName;
    private int finalScore;
    private String dateTime;
    private long createdAt;

    /*
         Esta es la clase para una fila de resultados. Para el tema de las puntuaciones

         Modelo de una fila de resultados:
           nombre
           puntuación final
           fecha
     */


    // Constructor vacío necesario para la deserialización JSON de libGDX
    public ScoreEntry() {
        // necesario para Json de LibGDX
    }

    public ScoreEntry(String playerName, int finalScore, String dateTime, long createdAt) {
        this.playerName = playerName;
        this.finalScore = finalScore;
        this.dateTime = dateTime;
        this.createdAt = createdAt;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public String getDateTime() {
        return dateTime;
    }

    public long getCreatedAt() {
        return createdAt;
    }

}
