package com.natalia.natarunner.screens;

import com.natalia.natarunner.manager.AudioManager;
import com.natalia.natarunner.manager.GameSettings;
import com.natalia.natarunner.manager.ResourceManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

public class ConfigurationScreen implements Screen {

    private final Screen menuScreen;
    private final ResourceManager resources;
    private final AudioManager audioManager;
    private final GameSettings settings;

    private Stage stage;

    private VisCheckBox mouseCheck;
    private VisCheckBox easyModeCheck;
    private VisSlider volumeSlider;
    private VisLabel volumeValue;

    public ConfigurationScreen(Screen menuScreen, ResourceManager resources,
                               AudioManager audioManager, GameSettings settings) {
        this.menuScreen = menuScreen;
        this.resources = resources;
        this.audioManager = audioManager;
        this.settings = settings;
    }

    @Override
    public void show() {

        if (!VisUI.isLoaded()) {
            VisUI.load();
        }

        stage = new Stage(new ScreenViewport());

        VisTable table = new VisTable(true);
        table.setFillParent(true);
        stage.addActor(table);

        // =========================
        // ESTILOS
        // =========================
        Label.LabelStyle titleStyle = new Label.LabelStyle(resources.fontMenuTitle, Color.WHITE);
        Label.LabelStyle labelStyle = new Label.LabelStyle(resources.fontMenu, Color.WHITE);

        // =========================
        // CONTROLES
        // =========================
        mouseCheck = new VisCheckBox("  Enable Mouse Control");
        mouseCheck.getLabel().setStyle(labelStyle);
        mouseCheck.setChecked(settings.isMouseEnabled());

        easyModeCheck = new VisCheckBox("  Mode Easy Game");
        easyModeCheck.getLabel().setStyle(labelStyle);
        easyModeCheck.setChecked(settings.isEasyMode());

        volumeSlider = new VisSlider(0, 10, 1, false);
        volumeSlider.setValue(Math.round(audioManager.getMasterVolume() * 10f));

        volumeValue = new VisLabel(String.valueOf(Math.round(volumeSlider.getValue())));
        volumeValue.setStyle(labelStyle);

        // =========================
        // LISTENERS
        // =========================
        easyModeCheck.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settings.setEasyMode(easyModeCheck.isChecked());
            }
        });

        mouseCheck.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settings.setMouseEnabled(mouseCheck.isChecked());
            }
        });

        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int value = Math.round(volumeSlider.getValue());
                float realVolume = value / 10f;

                volumeValue.setText(String.valueOf(value));
                audioManager.setMasterVolume(realVolume);
            }
        });

        VisTextButton quitButton = new VisTextButton("QUIT");
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                // Forzar guardado del valor visible del slider antes de salir
                int value = Math.round(volumeSlider.getValue());
                float realVolume = value / 10f;
                audioManager.setMasterVolume(realVolume);

                ((Game) Gdx.app.getApplicationListener()).setScreen(menuScreen);
            }
        });

        // =========================
        // LABELS
        // =========================
        VisLabel title = new VisLabel("CONFIGURATION");
        title.setStyle(titleStyle);

        VisLabel volumeLabel = new VisLabel("Volume");
        volumeLabel.setStyle(labelStyle);

        VisLabel valueLabel = new VisLabel("Value");
        valueLabel.setStyle(labelStyle);

        // =========================
        // LAYOUT
        // =========================
        table.defaults().pad(20);

        table.row();
        table.add(title).colspan(2).center();

        table.row();
        table.add(mouseCheck).left().colspan(2);

        table.row();
        table.add(easyModeCheck).left().colspan(2);

        table.row();
        table.add(volumeLabel).right();
        table.add(volumeSlider).width(350).height(40);

        table.row();
        table.add(valueLabel).left();
        table.add(volumeValue).left();

        table.row();
        table.add().height(40);

        table.row();
        table.add(quitButton).colspan(2).width(300).height(80);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(dt);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}

    @Override public void hide() {
        if (Gdx.input.getInputProcessor() == stage) {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
    }
}
