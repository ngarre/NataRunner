package com.natalia.natarunner.ui;

import com.natalia.natarunner.manager.AudioManager;
import com.natalia.natarunner.manager.ResourceManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PauseMenu {

    public enum PauseOption {
        NONE,
        CONTINUE,
        INSTRUCTIONS,
        EXIT
    }

    private final ResourceManager resources;
    private final BitmapFont font;
    private final AudioManager audioManager;

    private final Rectangle continueRect = new Rectangle();
    private final Rectangle soundRect = new Rectangle();
    private final Rectangle instructionsRect = new Rectangle();
    private final Rectangle exitRect = new Rectangle();

    private String soundText;
    private int selectedIndex = 0;

    public PauseMenu(BitmapFont font, ResourceManager resources, AudioManager audioManager) {
        this.font = font;
        this.resources = resources;
        this.audioManager = audioManager;
    }

    public void layout(float w, float h) {
        float buttonWidth = 320;
        float buttonHeight = 60;

        float x = (w - buttonWidth) / 2f;
        float startY = h / 2f + 100;

        continueRect.set(x, startY, buttonWidth, buttonHeight);
        instructionsRect.set(x, startY - 90, buttonWidth, buttonHeight);
        soundRect.set(x, startY - 180, buttonWidth, buttonHeight);
        exitRect.set(x, startY - 270, buttonWidth, buttonHeight);
    }

    public PauseOption input(Viewport viewport) {

        // =========================
        // TECLADO
        // =========================
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedIndex--;
            if (selectedIndex < 0) {
                selectedIndex = 3;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIndex++;
            if (selectedIndex > 3) {
                selectedIndex = 0;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            return executeSelectedOption();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            return PauseOption.CONTINUE;
        }

        // =========================
        // RATÓN
        // =========================
        Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouse);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {

            if (continueRect.contains(mouse)) {
                selectedIndex = 0;
                return PauseOption.CONTINUE;
            }

            if (instructionsRect.contains(mouse)) {
                selectedIndex = 1;
                return PauseOption.INSTRUCTIONS;
            }

            if (soundRect.contains(mouse)) {
                selectedIndex = 2;
                return executeSelectedOption();
            }

            if (exitRect.contains(mouse)) {
                selectedIndex = 3;
                return PauseOption.EXIT;
            }
        }

        return PauseOption.NONE;
    }

    private PauseOption executeSelectedOption() {
        switch (selectedIndex) {
            case 0:
                return PauseOption.CONTINUE;
            case 1:
                return PauseOption.INSTRUCTIONS;
            case 2:
                toggleAllAudio();
                return PauseOption.NONE;
            case 3:
                return PauseOption.EXIT;
            default:
                return PauseOption.NONE;
        }
    }

    private void toggleAllAudio() {
        boolean anyEnabled = audioManager.isMusicEnabled() || audioManager.isSoundEnabled();

        if (anyEnabled) {
            audioManager.setMusicEnabled(false);
            audioManager.setSoundEnabled(false);
        } else {
            audioManager.setMusicEnabled(true);
            audioManager.setSoundEnabled(true);
        }
    }

    public void draw(SpriteBatch batch, float w, float h) {

        // Fondo oscuro
        batch.setColor(0, 0, 0, 0.75f);
        batch.draw(resources.hudBackground, 0, 0, w, h);
        batch.setColor(1, 1, 1, 1);

        GlyphLayout layout = new GlyphLayout();

        // =========================
        // TÍTULO
        // =========================
        font.getData().setScale(1.5f);

        String title = "PAUSED";
        layout.setText(font, title);

        float titleGap = 100f;
        float titleY = continueRect.y + continueRect.height + titleGap;

        font.setColor(Color.WHITE);
        font.draw(batch, title, (w - layout.width) / 2f, titleY);

        font.getData().setScale(1f);

        // =========================
        // BOTONES
        // =========================
        soundText = audioManager.isMusicEnabled() ? "Disable Sound" : "Enable Sound";

        font.getData().setScale(1f);

        String[] buttons = {
            "Continue",
            "Instructions",
            soundText,
            "Exit to Menu"
        };

        float buttonGap = 20f;
        float currentY = continueRect.y + continueRect.height;

        for (int i = 0; i < buttons.length; i++) {

            String marker = (i == selectedIndex) ? "> " : "  ";
            String textToDraw = marker + buttons[i];

            layout.setText(font, textToDraw);

            if (i == selectedIndex) {
                font.setColor(Color.YELLOW);
            } else {
                font.setColor(Color.WHITE);
            }

            float textX = (w - layout.width) / 2f;
            font.draw(batch, textToDraw, textX, currentY);

            Rectangle rect = null;
            if (i == 0) rect = continueRect;
            else if (i == 1) rect = instructionsRect;
            else if (i == 2) rect = soundRect;
            else if (i == 3) rect = exitRect;

            rect.set(textX, currentY - layout.height, layout.width, layout.height);

            currentY -= layout.height + buttonGap;
        }
    }
}

