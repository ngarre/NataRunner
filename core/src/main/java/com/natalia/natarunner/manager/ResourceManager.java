package com.natalia.natarunner.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class ResourceManager {

    public static AssetManager assets = new AssetManager();   // <--- No voy a usarlo.
    // ********************

    /*
      En este proyecto no se utiliza AssetManager()).
      En su lugar he implementado un ResourceManager propio que centraliza la carga de texturas,
      sonidos y música al iniciar el juego. Dado que el número de recursos es reducido y el juego tiene pocos niveles,
      no es necesario implementar un sistema de carga asíncrona ni pantallas de carga.
      Esta solución simplifica el código y resulta suficiente para el alcance del proyecto.
    */

    // T E X T U R A S

    // --- Ambos Niveles
    public Texture minicorazon;

    // *******************************************************
    // MENUSCREEN
    // *******************************************************
    public Texture fondoMenu;
    public Texture logoTexture;
    public Texture startTexture;
    public Texture setupTexture;
    public Texture quitTexture;
    public Music menuMusic;

    // *******************************************************
    // TEARSSCREEN
    // *******************************************************
    public Texture fondoTears;
    public Texture Level1Texture;
    public Texture manosTexture;
    public Texture manosCerradasTexture;
    public Texture gotaBlancaTexture;
    public Texture gotaBlanca1;
    public Texture gotaBlanca2;
    public Texture gotaBlanca3;
    public Texture gotaAmarillaTexture;
    public Texture gotaAmarillaSableadaTexture;
    public Texture gotaRojaTexture;
    public Sound gotaBlancaSound;
    public Sound gotaAmarillaSound;
    public Sound gotaAmarillaFallSound;
    public Sound sonidoSable;
    public Sound gotaRojaSound;
    public Music tearsMusic;

    // *******************************************************
    // FONTS
    // *******************************************************

    //... para la pantalla del menú de configuración de opciones
    public BitmapFont fontMenuTitle;
    public BitmapFont fontMenu;

    //... para la pantalla de instrucciones
    public BitmapFont fontInstructions;

    // HUD
    public Texture hudBackground;
    public BitmapFont hudFont;
    public BitmapFont hudSmallFont;



    // *******************************************************
    // FIGHTSCREEN
    // *******************************************************
    public Texture fondoFight;
    public Texture level2;
    public Texture gameOver;
    public Texture youWin;
    public Texture bulletPlayer;
    public Texture bulletBoss;
    public Texture npcChica1;
    public Texture npcChica2;
    public Texture npcChica3;
    public Texture npcChica4;
    public Texture npcChicaTocada;
    public Texture npcHolandesa1;
    public Texture npcHolandesa2;
    public Texture npcHolandesa3;
    public Texture npcHolandesa4;
    public Texture naveRoy;
    public Texture naveRoySonriendo;
    public Texture naveRoyTocada;
    public Texture fireBeam;
    public Texture npcHolandesaTocada;
    public Texture cocheBladecarDispara;
    public Music fightMusic;
    public Sound chillidoChica;
    public Sound hurtSound;
    public Sound roarSound;
    public Sound risaChica;
    public Sound risaHolandesa;



    // *******************************************************
    // PLAYERBLADECAR
    // *******************************************************
    public Sound giroSound;
    public Sound aterrizajeSound;
    public Sound reciboImpacto;
    public Sound shootSound;
    public Texture cocheBladecar1;
    public Texture cocheBladecar2;
    public Texture cocheBladecar3;


    // *******************************************************
    // SCORESCREEN
    // *******************************************************
    public Texture fondoScore;
    public Texture botonToMenu;
    public Music scoreMusic;


    // *******************************************************
    // INSTRUCTIONCREEN
    // *******************************************************
    public Texture botonBack;

    // --- Ambos Niveles
    public Sound sonidoMortal;


    // El constructor de la clase
    public ResourceManager() {
        loadTextures();
        loadSounds();
        loadMusic();
        loadFonts();
    }

    private void loadTextures() {

        // de MenuScreen
        // *************
        fondoMenu = new Texture("Menu/FondoMenu.jpg");
        logoTexture = new Texture("Menu/NataRunner-01.png");
        startTexture = new Texture("Menu/start.png");
        setupTexture = new Texture("Menu/setup.png");
        quitTexture = new Texture("Menu/quit.png");


        // de TearsScreen
        // **************
        fondoTears = new Texture("Tears/lagrimas_fondo.jpg");
        Level1Texture = new Texture("Tears/Level1.png");
        manosTexture = new Texture("Tears/manos.png");
        manosCerradasTexture = new Texture("Tears/manoscerradas.png");
        gotaBlancaTexture = new Texture("Tears/Gotas/gota_blanca.png");
        gotaBlanca1 = new Texture("Tears/Gotas/gota_blanca_1.png");
        gotaBlanca2 = new Texture("Tears/Gotas/gota_blanca_2.png");
        gotaBlanca3 = new Texture("Tears/Gotas/gota_blanca_3.png");
        gotaAmarillaTexture = new Texture("Tears/Gotas/gota_amarilla.png");
        gotaAmarillaSableadaTexture = new Texture("Tears/Gotas/Sableada.png");
        gotaRojaTexture = new Texture("Tears/Gotas/gota_roja.png");

        // de FightScreen
        // **************
        fondoFight = new Texture("Fight/FondoFight.jpg");
        npcChica1 = new Texture("Fight/Npc/nave-chica-1.png");
        npcChica2 = new Texture("Fight/Npc/nave-chica-2.png");
        npcChica3 = new Texture("Fight/Npc/nave-chica-3.png");
        npcChica4 = new Texture("Fight/Npc/nave-chica-4.png");
        npcChicaTocada = new Texture("Fight/Npc/nave-chica-tocada.png");
        npcHolandesa1 = new Texture("Fight/Npc/nave-holandesa-1.png");
        npcHolandesa2 = new Texture("Fight/Npc/nave-holandesa-2.png");
        npcHolandesa3 = new Texture("Fight/Npc/nave-holandesa-3.png");
        npcHolandesa4 = new Texture("Fight/Npc/nave-holandesa-4.png");
        npcHolandesaTocada = new Texture("Fight/Npc/nave-holandesa-tocada.png");
        naveRoy = new Texture("Fight/Npc/nave-roy.png");
        naveRoySonriendo = new Texture("Fight/Npc/nave-roy-sonriendo.png");
        naveRoyTocada = new Texture("Fight/Npc/nave-roy-tocada.png");
        fireBeam = new Texture("Fight/Npc/FireBeam.png");
        level2 = new Texture("Fight/Level2.png");
        gameOver = new Texture("GameOver.png");
        bulletPlayer = new Texture("Fight/Bullets/bala-coche.png");
        bulletBoss = new Texture("Fight/Bullets/bala-boss.png");
        youWin = new Texture("youwin.png");


        // de Playerbladecar
        // *****************
        cocheBladecar1 = new Texture(Gdx.files.internal("Fight/Player/coche-1.png"));
        cocheBladecar2 = new Texture(Gdx.files.internal("Fight/Player/coche-2.png"));
        cocheBladecar3 = new Texture(Gdx.files.internal("Fight/Player/coche-3.png"));
        cocheBladecarDispara = new Texture(Gdx.files.internal("Fight/Player/coche-dispara.png"));


        // del Hud
        // *****************
        // Generación de textura base (1x1) para UI:
        // Se utiliza un Pixmap blanco como textura mínima que actúa como primitiva.
        // Al escalarla y aplicar tintado dinámico, permite construir fondos,
        // overlays y elementos HUD sin depender de assets gráficos adicionales.
        // Mejora rendimiento y flexibilidad en el renderizado.
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        hudBackground = new Texture(pixmap);
        pixmap.dispose();
        // Se utiliza en TearsScreen y FightScreen para renderizar fondos del HUD,
        // que es donde saldrá el score, el tiempo restante y la barra de vida
        // Estoy separando renderizado del mundo y renderizado de UI (HUD).

        minicorazon = new Texture("minicorazon.png");

        // de ScoreScreen
        fondoScore = new Texture("Scores/scores_fondo.jpg");
        botonToMenu = new Texture("Scores/button_toMenu.png");

        // de InstructionsScore
        botonBack = new Texture("Instructions/backbutton.png");


    }

    private void loadSounds() {

        // Ambos niveles
        sonidoMortal = Gdx.audio.newSound(Gdx.files.internal("claxon.ogg"));

        // de TearsScreen
        gotaBlancaSound = Gdx.audio.newSound(Gdx.files.internal("Tears/Sonidos/drop.mp3"));
        gotaAmarillaSound = Gdx.audio.newSound(Gdx.files.internal("Tears/Sonidos/sonido_atrapo_amarilla.mp3"));
        gotaAmarillaFallSound = Gdx.audio.newSound(Gdx.files.internal("Tears/Sonidos/error_amarillo.mp3"));
        sonidoSable = Gdx.audio.newSound(Gdx.files.internal("Tears/Sonidos/sonido_sable.ogg"));
        gotaRojaSound = Gdx.audio.newSound(Gdx.files.internal("Tears/Sonidos/redtear.mp3"));

        // de FightScreen
        chillidoChica = Gdx.audio.newSound(Gdx.files.internal("Fight/Sonidos/chillido.ogg"));
        risaChica = Gdx.audio.newSound(Gdx.files.internal("Fight/Sonidos/risa-joven.ogg"));
        risaHolandesa = Gdx.audio.newSound(Gdx.files.internal("Fight/Sonidos/risa-mayor.ogg"));
        hurtSound = Gdx.audio.newSound(Gdx.files.internal("Fight/Sonidos/ouchBoss.ogg"));
        roarSound = Gdx.audio.newSound(Gdx.files.internal("Fight/Sonidos/rugidoBoss.ogg"));


        // de Playerbladecar
        giroSound = Gdx.audio.newSound(Gdx.files.internal("Fight/Sonidos/girocoche.ogg"));
        aterrizajeSound = Gdx.audio.newSound(Gdx.files.internal("Fight/Sonidos/aterrizaje.ogg"));
        reciboImpacto = Gdx.audio.newSound(Gdx.files.internal("Fight/Sonidos/impactoNpc.ogg"));
        shootSound = Gdx.audio.newSound(Gdx.files.internal("Fight/Sonidos/disparo.ogg"));

    }

    private void loadMusic() {
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("Menu/Sonidos/RachelTheme.mp3"));
        tearsMusic = Gdx.audio.newMusic(Gdx.files.internal("Tears/Sonidos/Tears In Rain.mp3"));
        fightMusic = Gdx.audio.newMusic(Gdx.files.internal("Fight/Sonidos/EndTitles.mp3"));
        scoreMusic = Gdx.audio.newMusic(Gdx.files.internal("Scores/Sonidos/MesaBD2049.ogg"));
    }


    private void loadFonts() {

        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(Gdx.files.internal("ARIAL.TTF"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        // Fuente título menú
        parameter.size = 40;
        fontMenuTitle = generator.generateFont(parameter);

        // Fuente normal del menú
        parameter.size = 24;
        fontMenu = generator.generateFont(parameter);

        // Fuente instrucciones
        FreeTypeFontGenerator.FreeTypeFontParameter instructionsParam =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        instructionsParam.size = 30;
        instructionsParam.color = com.badlogic.gdx.graphics.Color.WHITE;
        instructionsParam.borderWidth = 2f;
        instructionsParam.borderColor = com.badlogic.gdx.graphics.Color.BLACK;
        fontInstructions = generator.generateFont(instructionsParam);
        fontInstructions.getData().setScale(1f);


        // Fuente HUD principal
        FreeTypeFontGenerator.FreeTypeFontParameter hudParam =
            new FreeTypeFontGenerator.FreeTypeFontParameter();
        hudParam.size = 36;
        hudParam.color = Color.YELLOW;
        hudParam.borderWidth = 2f;
        hudParam.borderColor = Color.BLACK;

        hudFont = generator.generateFont(hudParam);
        hudFont.setUseIntegerPositions(false);

        // Fuente HUD pequeña
        FreeTypeFontGenerator.FreeTypeFontParameter hudSmallParam =
            new FreeTypeFontGenerator.FreeTypeFontParameter();
        hudSmallParam.size = 22;
        hudSmallParam.color = Color.WHITE;
        hudSmallParam.borderWidth = 2f;
        hudSmallParam.borderColor = Color.BLACK;

        hudSmallFont = generator.generateFont(hudSmallParam);
        hudSmallFont.setUseIntegerPositions(false);

        generator.dispose();
    }

    public void dispose() {

        // Texturas menú
        fondoMenu.dispose();
        logoTexture.dispose();
        startTexture.dispose();
        setupTexture.dispose();
        quitTexture.dispose();

        // Texturas TearsScreen
        fondoTears.dispose();
        Level1Texture.dispose();
        manosTexture.dispose();
        manosCerradasTexture.dispose();
        gotaBlancaTexture.dispose();
        gotaBlanca1.dispose();
        gotaBlanca2.dispose();
        gotaBlanca3.dispose();
        gotaAmarillaTexture.dispose();
        gotaAmarillaSableadaTexture.dispose();
        gotaRojaTexture.dispose();

        // Texturas FightScreen
        fondoFight.dispose();
        level2.dispose();
        gameOver.dispose();
        bulletPlayer.dispose();
        bulletBoss.dispose();
        youWin.dispose();

        npcChica1.dispose();
        npcChica2.dispose();
        npcChica3.dispose();
        npcChica4.dispose();
        npcChicaTocada.dispose();

        npcHolandesa1.dispose();
        npcHolandesa2.dispose();
        npcHolandesa3.dispose();
        npcHolandesa4.dispose();
        npcHolandesaTocada.dispose();

        naveRoy.dispose();
        naveRoySonriendo.dispose();
        naveRoyTocada.dispose();
        fireBeam.dispose();

        // Texturas Player
        cocheBladecar1.dispose();
        cocheBladecar2.dispose();
        cocheBladecar3.dispose();
        cocheBladecarDispara.dispose();

        // HUD
        hudBackground.dispose();

        // Score / Instructions
        fondoScore.dispose();
        botonToMenu.dispose();
        botonBack.dispose();

        // Sonidos Tears
        gotaBlancaSound.dispose();
        gotaAmarillaSound.dispose();
        gotaAmarillaFallSound.dispose();
        sonidoSable.dispose();
        gotaRojaSound.dispose();

        // Sonidos Fight
        chillidoChica.dispose();
        hurtSound.dispose();
        roarSound.dispose();


        // Sonidos Player
        giroSound.dispose();
        aterrizajeSound.dispose();
        reciboImpacto.dispose();
        shootSound.dispose();

        // Música
        menuMusic.dispose();
        tearsMusic.dispose();
        fightMusic.dispose();
        scoreMusic.dispose();

        // Fuentes
        fontMenu.dispose();
        fontMenuTitle.dispose();
        fontInstructions.dispose();
        hudFont.dispose();
        hudSmallFont.dispose();
    }
}

