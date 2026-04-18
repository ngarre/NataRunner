package com.natalia.natarunner.config;

public class InstructionsConfig {

    /*
     * Centraliza los textos e imágenes de las pantallas de instrucciones.
     * Evita duplicación en los LogicManager y mantiene el contenido desacoplado
     * de la lógica del juego.
     */

    public static class Data {
        public final String imagePath;
        public final String text;

        public Data(String imagePath, String text) {
            this.imagePath = imagePath;
            this.text = text;
        }
    }

    // =========================
    // FIGHT SCREEN
    // =========================
    public static final Data FIGHT = new Data(
        "Instructions/Leon.jpg",
        "INSTRUCTIONS\n\n" +
            "Reach 500 points to finish the game\n" +
            "but 300 when Easy Mode\n" +
            "The replicant girls are crazy, avoid being touched by them\n" +
            "When you finish her off, the boss will appear; try to dodge his shots\n" +
            "Be careful, time is running out and it's not easy\n" +
            "Try to become a real blade runner\n\n" +
            "Press ENTER or BACK to return"
    );

    // =========================
    // TEARS SCREEN
    // =========================
    public static final Data TEARS = new Data(
        "Instructions/Rachel.jpg",
        "INSTRUCTIONS\n\n" +
            "Score 500 points to progress to level 2\n" +
            "or 300 point in Easy Mode\n" +
            "White Tears give points and do not penalize you\n" +
            "Yellow Tears give points but fall to the ground and penalize you\n" +
            "Be careful with Yellow Tears because they can release knives\n" +
            "Red Tears will go after you to kill you and they can fire projectiles\n\n" +
            "Press ENTER or BACK to return"
    );
}
