package com.natalia.natarunner.model.entities.drops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class WhiteDrop extends Drop {

    public static final int POINTS = 10;

    private final Animation<TextureRegion> animation;
    private float stateTime = 0f;

    public WhiteDrop(Texture tex1, Texture tex2, Texture tex3, float x, float y) {
        super(tex1, x, y, 0.5f, 0.5f, -1f);

        TextureRegion[] frames = new TextureRegion[3];
        frames[0] = new TextureRegion(tex1);
        frames[1] = new TextureRegion(tex2);
        frames[2] = new TextureRegion(tex3);

        animation = new Animation<>(0.12f, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        stateTime += delta;
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion frame = animation.getKeyFrame(stateTime, true);

        float worldScale = 0.5f;

        float width = frame.getRegionWidth() * worldScale / 64f;
        float height = frame.getRegionHeight() * worldScale / 64f;

        float centerX = sprite.getX() + sprite.getWidth() / 2f;
        float centerY = sprite.getY() + sprite.getHeight() / 2f;

        float drawX = centerX - width / 2f;
        float drawY = centerY - height / 2f;

        batch.draw(frame, drawX, drawY, width, height);
    }
}
