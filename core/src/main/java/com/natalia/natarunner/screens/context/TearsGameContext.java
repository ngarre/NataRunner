package com.natalia.natarunner.screens.context;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.natalia.natarunner.config.GameConfig;
import com.natalia.natarunner.model.entities.drops.RedDrop;
import com.natalia.natarunner.model.entities.drops.WhiteDrop;
import com.natalia.natarunner.model.entities.drops.YellowDrop;
import com.natalia.natarunner.model.entities.player.PlayerTears;

public class TearsGameContext {

    public String level1Title = "LEVEL 1: TEARS DISTRICT";

    public enum GameState {
        INTRO,
        PLAYING,
        PAUSED,
        GAMEOVER
    }

    public GameState state = GameState.INTRO;

    public boolean initialized = false;
    public boolean dragging = false;

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

    public Rectangle level1Rectangulo = new Rectangle();

    public BitmapFont font;
    public BitmapFont smallFont;
}
