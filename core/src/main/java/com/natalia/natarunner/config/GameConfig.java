package com.natalia.natarunner.config;

public class GameConfig {

    /*
        Aquí voy a meter valores que puedo cambiar a mano según vea la dificultad del juego
        Solo traigo aquí algunos ajustes. no todos. solo los más interesantes.
     */

    // A T A J O S (para el día de la presentación
    /*

             L   = para forzar el final del nivel y pasar al siguiente.
                   se se hace en el nivel2 que es el último, terminas el
                   juego y vas a los scores

             F   = (freeze) durante el juego en los niveles lo detengo/arranco todo
                   por si hay que explicarle a Santi en tiempo de jugo
                   detalles

             M   = otro toggle.  en este caso paro SOLO la música. para oír solo
                   los sonidos de los npc's

             F9  = solo en la pantalla del menú.  Vale para borrar todos los scores

      */


    //  EN GENERAL  ......................................................
    //  ..................................................................

    public static int puntosSiFacil = 300;
    public static int puntosSiDificil = 500;
    public static int cuantosCorazones = 5;
    public static float duracionFlashMensaje = 0.4f;


    //  T E A R S   D I S T R I C T ......................................................
    //  ..................................................................................

    // ************ ROJAS
    // Cada cuanto tiempo en segundos saco una gota roja, para que haya más o menos
    public static final float tiempoCadaCuantoRoja = 8f;
    // Cuánto tarda a salir la primera gota roja
    public static final float tiempoPrimeraGotaRoja = 15f;
    public static final float probabilidadRojaDispara = 0.6f;   // 60%, se puede cambiar

    // ************** AMARILLAS
    // Cuánto tarda a salir la primera gota amarilla
    public static final float tiempoPrimeraGotaAmarilla = 8f;
    // Cada cuánto saco (en segundos siempre) una gota amarilla.
    public static final float tiempoCadaCuantoAmarilla = 0.5f;
    // Probabilidad de que sea una gota amarilla letal
    public static final float probabilidadSable = 0.3f;   // 0.3 sería 30%

    // ************** BLANCAS
    // Cada cuánto aparece una gota blanca
    public static final float tiempoCadaCuantoGotaBlanca = 1f;


    //  F I G H T   Z O N E ......................................................
    //  ..........................................................................

    // Chicas a matar antes del boss
    public static final int chicasAMatar = 10;
    public static boolean quitarChicasSiSaleElBoss = true;


}
