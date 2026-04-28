package com.natalia.natarunner.screens;

import com.natalia.natarunner.manager.ResourceManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class InstructionsScreen implements Screen {


    private final Game game;

    SpriteBatch spriteBatch;
    FitViewport viewport;
    Texture fondoMenu;

    Texture backButton;
    Rectangle rectanguloBack = new Rectangle();


    private BitmapFont instruccionesFont; // ← NUEVA FUENTE PARA INSTRUCCIONES
    private String instrucciones;
    private Screen previousScreen;

    private String imagenFondo;
    private String instruccionesTexto;
    private int alineacion;

    ResourceManager resources;

    public InstructionsScreen(Game game, Screen previousScreen,
                              String imagenFondo, String instruccionesTexto, int alineacion,  ResourceManager resources)
    {
        this.game = game;
        this.previousScreen = previousScreen;
        this.imagenFondo = imagenFondo;
        this.instruccionesTexto = instruccionesTexto;
        this.alineacion = alineacion;
        this.resources = resources;

        // Fijarse que esta pantalla la llamo dede el menú de pausa de Tears y de Fight. es la misma.
        // solo cambia la imagen del fondo, el texto a visualizar y a qué lado se imprime.

    }


    private Viewport uiViewport;


    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(12.28f, 7.68f);

        uiViewport = new FitViewport(1228, 768);
        uiViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        fondoMenu = new Texture(imagenFondo);
        backButton = resources.botonBack;

        // Para posicionar el botón de back...
        float w = 3f;
        float h = 3f;
        float margin = 0.1f;
        float x = viewport.getWorldWidth() - w - margin;
        float y = margin - 0.8f;  // ajuste obtenido mediante prueba
        rectanguloBack.set(x, y, w, h);

        // ---------------------------
        // FUENTE PARA INSTRUCCIONES (esta en ResourceManager)
        // ---------------------------
        instruccionesFont = resources.fontInstructions;

        /*
        INSTRUCTIONS
        Reach 500 points to go to level 2
        White Tears give points and do not penalize you
        Yellow Tears give points but fall to the ground and penalize you
        Be careful with Yellow Tears because they can release knives
        Red Tears will go after you to kill you
        Press ENTER or BACK to return
        */
        // ^ este es el ejemplo de lo que sacaré más abajo y que vendrá de TearsScreen y FightScreen


        // Texto de instrucciones
        instrucciones = instruccionesTexto;

        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

    }

    @Override
    public void render(float delta) {
        input();
        logic(delta);
        draw();
    }

    private void logic(float delta) {

    }

    private void input() {

        Vector2 mouseWorld = new Vector2(Gdx.input.getX(), Gdx.input.getY());

        viewport.unproject(mouseWorld);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) &&  rectanguloBack.contains(mouseWorld)) {
            game.setScreen(previousScreen);
        }


        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ) {
            game.setScreen(previousScreen);

        }


    }
    private void draw() {
        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        // saco el fondo
        spriteBatch.draw(fondoMenu, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        // Saco el botón de back
        spriteBatch.draw(backButton, rectanguloBack.x, rectanguloBack.y, rectanguloBack.width, rectanguloBack.height);

        uiViewport.apply();
        spriteBatch.setProjectionMatrix(uiViewport.getCamera().combined);

        float margin = 50;
        instruccionesFont.draw(
            spriteBatch,
            instrucciones,
            margin,
            uiViewport.getWorldHeight() - margin,
            uiViewport.getWorldWidth() - 100,
            alineacion,true
        );

        spriteBatch.end();
    }



    @Override public void resize(int width, int height)
    {
        if(width <= 0 || height <= 0) return;
        viewport.update(width, height, true);

        // Necesario para el escalado correcto de la fuente de las instrucciones
        uiViewport.update(width, height, true);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

        spriteBatch.dispose();
        fondoMenu.dispose();
        backButton.dispose();
    }
}
