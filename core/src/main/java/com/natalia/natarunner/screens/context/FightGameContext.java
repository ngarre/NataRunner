package com.natalia.natarunner.screens.context;

import com.natalia.natarunner.config.GameConfig;
import com.natalia.natarunner.model.entities.npc.NpcBoss;
import com.natalia.natarunner.model.entities.npc.NpcChicas;
import com.natalia.natarunner.model.entities.player.PlayerBladecar;
import com.natalia.natarunner.model.entities.projectile.BulletBoss;
import com.natalia.natarunner.model.entities.projectile.BulletChicas;
import com.natalia.natarunner.ui.CountdownTimer;
import com.natalia.natarunner.ui.FlashMessage;
import com.natalia.natarunner.ui.FloatingText;
import com.natalia.natarunner.ui.PauseMenu;
import com.natalia.natarunner.ui.ScoreBar;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class FightGameContext {

    /*
     * Clase de contexto que centraliza el estado de la partida (player, score, entidades, timers, etc.).
     * Se utiliza para compartir datos entre LogicManager y RenderManager sin duplicarlos.
     * Evita acoplamiento entre clases y mantiene una única fuente de verdad del estado del juego.
     * Forma parte de la separación de responsabilidades aplicada a la arquitectura de la Screen.
     */

    public String level2Title = "LEVEL 2: FIGHT ZONE";

    public enum GameState {
        INTRO,
        PLAYING,
        PAUSED,
        GAMEOVER,
        YOUWIN
    }

    public GameState state = GameState.INTRO;

    public boolean freezeMode = false;
    public boolean pauseOnlyMusic = false;
    public boolean initialized = false;
    public boolean dragging = false;

    public boolean bossPending = false;
    public boolean quitarChicasSiSaleBoss = GameConfig.quitarChicasSiSaleElBoss;

    public int score = 100;
    public int scoreBasta;

    public int hearts = GameConfig.cuantosCorazones;

    public int npcKilled = 0;
    public int npcMaxKilled = GameConfig.chicasAMatar;

    public float hudHeight = 1f;
    public float barHeight = 60f;

    public float bossDelayTimer = 0f;
    public float bossDelay = 1f;

    public Rectangle level2Rectangulo = new Rectangle();

    public Vector2 mouseHud = new Vector2();
    public Vector2 mouseWorld = new Vector2();

    public PlayerBladecar player;
    public NpcBoss boss;

    public Array<NpcChicas> sideNPCs = new Array<>();
    public Array<BulletChicas> bullets = new Array<>();
    public Array<BulletBoss> bossBullets = new Array<>();
    public Array<FloatingText> floatingTexts = new Array<>();

    public FlashMessage flashMessage;
    public CountdownTimer countdownTimer;
    public PauseMenu pauseMenu;
    public ScoreBar scoreBar;

    public BitmapFont font;
    public BitmapFont smallFont;
}
