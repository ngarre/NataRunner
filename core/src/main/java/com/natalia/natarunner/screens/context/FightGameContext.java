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
     * Contexto del nivel 2.
     * Centraliza el estado de la partida para que LogicManager y RenderManager
     * trabajen sobre los mismos datos sin duplicarlos.
     *
     * FightLogicManager modifica este estado:
     * movimiento, disparos, colisiones, puntuación, vidas, boss, etc.
     *
     * FightRenderManager lee este estado:
     * dibuja jugador, NPCs, boss, balas, HUD, mensajes y estados de pantalla.
     */

     /* Forma parte de la separación de responsabilidades aplicada a la arquitectura de la Screen.
     */

    // Texto identificativo del nivel actual.
    // Se usa en el render para mostrar al jugador que está en el nivel 2.
    public String level2Title = "LEVEL 2: FIGHT ZONE";

    // Estados posibles del nivel 2.
    // Permiten controlar si estamos en intro, jugando, pausa, game over o victoria.
    public enum GameState {
        INTRO,
        PLAYING,
        PAUSED,
        GAMEOVER,
        YOUWIN
    }

    // El nivel empieza en INTRO, esperando a que el jugador confirme el inicio.
    public GameState state = GameState.INTRO;

    // Atajos o estados auxiliares de control.
    public boolean freezeMode = false; // Congela la lógica para pruebas/presentación.
    public boolean pauseOnlyMusic = false; // Pausa solo la música.
    public boolean initialized = false; // Evita inicializar el nivel más de una vez.
    public boolean dragging = false; // Indica si el jugador se está arrastrando con ratón

    // Control de aparición del boss.
    public boolean bossPending = false; // Indica que el boss está pendiente de aparecer.

    // Configura si se eliminan los NPCs normales cuando aparece el boss.
    public boolean quitarChicasSiSaleBoss = GameConfig.quitarChicasSiSaleElBoss;

    // Puntuación inicial del nivel y objetivo necesario para completarlo.
    public int score = 100;
    public int scoreBasta;

    // Vidas/corazones del jugador, configuradas desde GameConfig.
    public int hearts = GameConfig.cuantosCorazones;

    // Número de NPCs eliminados y cantidad necesaria para activar el boss.
    public int npcKilled = 0;
    public int npcMaxKilled = GameConfig.chicasAMatar;

    // Medidas reservadas para el HUD y la barra de puntuación.
    public float hudHeight = 1f;
    public float barHeight = 60f;

    // Pequeño retardo antes de que aparezca el boss tras cumplir la condición.
    public float bossDelayTimer = 0f;
    public float bossDelay = 1f;

    // Rectángulo clicable de la pantalla de introducción del nivel 2.
    public Rectangle level2Rectangulo = new Rectangle();

    // Coordenadas del ratón convertidas a distintos sistemas:
    // mouseHud para botones/textos/HUD, mouseWorld para el mundo jugable.
    public Vector2 mouseHud = new Vector2();
    public Vector2 mouseWorld = new Vector2();

    // Entidades principales del nivel 2.
    public PlayerBladecar player;  // Personaje controlado por el jugador.
    public NpcBoss boss; // Boss final del nivel.

    // Listas de entidades activas durante la partida
    public Array<NpcChicas> sideNPCs = new Array<>();
    public Array<BulletChicas> bullets = new Array<>(); // OJO SON LAS BALAS QUE YO DISPARO TANTO A CHICAS COMO A BOSS
    public Array<BulletBoss> bossBullets = new Array<>(); // Balas disparadas por el Boss
    public Array<FloatingText> floatingTexts = new Array<>();

    // Elementos del HUD y mensajes.
    public FlashMessage flashMessage;
    public CountdownTimer countdownTimer;
    public PauseMenu pauseMenu;
    public ScoreBar scoreBar;

    // Fuentes usadas para dibujar texto en pantalla.
    public BitmapFont font;
    public BitmapFont smallFont;
}
