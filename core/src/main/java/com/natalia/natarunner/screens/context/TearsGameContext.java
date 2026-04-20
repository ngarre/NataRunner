package com.natalia.natarunner.screens.context;

import com.natalia.natarunner.config.GameConfig;
import com.natalia.natarunner.model.entities.drops.RedDrop;
import com.natalia.natarunner.model.entities.drops.WhiteDrop;
import com.natalia.natarunner.model.entities.drops.YellowDrop;
import com.natalia.natarunner.model.entities.player.PlayerTears;
import com.natalia.natarunner.ui.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class TearsGameContext {

    /*
     * Clase de contexto que centraliza el estado de la partida (player, score, entidades, timers, etc.).
     * Se utiliza para compartir datos entre LogicManager y RenderManager sin duplicarlos.
     * Evita acoplamiento entre clases y mantiene una única fuente de verdad del estado del juego.
     * Forma parte de la separación de responsabilidades aplicada a la arquitectura de la Screen.
     */

    public String level1Title = "LEVEL 1: TEARS DISTRICT";

    public enum GameState {
        INTRO,
        PLAYING,
        PAUSED,
        GAMEOVER
    }


    public GameState state = GameState.INTRO;

    public boolean freezeMode = false;
    public boolean pauseOnlyMusic = false;
    public boolean dragging = false;
    public boolean initialized = false;

    public float hudHeight = 1f;
    public float barHeight = 60f;

    public float gotaBlancaTimer = 0f;
    public float gotaAmarillaTimer = 0f;
    public float gotaRojaTimer = 0f;
    public float tiempoTotal = 0f;

    public int score = 100;
    public int scoreBasta;

    public int hearts = GameConfig.cuantosCorazones;


    public PlayerTears playerTears;

    public Array<WhiteDrop> gotasBlancas = new Array<>();
    public Array<YellowDrop> gotasAmarillas = new Array<>();
    public Array<RedDrop> gotasRojas = new Array<>();
    public Array<FloatingText> floatingTexts = new Array<>();

    public FlashMessage flashMessage;
    public PauseMenu pauseMenu;
    public ScoreBar scoreBar;
    public CountdownTimer countdownTimer;

    public Rectangle level1Rectangulo = new Rectangle();

    public BitmapFont font;
    public BitmapFont smallFont;

    public com.badlogic.gdx.utils.Array<com.natalia.natarunner.model.entities.projectile.RedDropProjectile> redProjectiles =
        new com.badlogic.gdx.utils.Array<>();
}
